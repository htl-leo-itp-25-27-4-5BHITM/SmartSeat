package at.htl.repository;

import at.htl.model.Duration;
import at.htl.model.History;
import at.htl.model.Seat;
import at.htl.model.SensorMessage;
import at.htl.sockets.SeatWebSocket;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SeatRepositoryLifecycleTest {

    @Test
    void preservesStartOnRepeatedObservationAndReleasesAcrossDstBoundary() {
        EntityManager entityManager = mock(EntityManager.class);
        SeatWebSocket webSocket = mock(SeatWebSocket.class);
        Clock clock = mock(Clock.class);
        SeatRepository repository = new SeatRepository();
        repository.em = entityManager;
        repository.ws = webSocket;
        repository.clock = clock;

        Seat seat = new Seat("Testplatz", true);
        seat.setId(42L);
        Duration timeout = new Duration(35);
        Instant firstObservation = Instant.parse("2026-10-25T00:59:30Z");
        Instant repeatedObservation = Instant.parse("2026-10-25T01:00:30Z");
        Instant release = Instant.parse("2026-10-25T01:01:06Z");

        when(entityManager.find(Seat.class, 42L)).thenReturn(seat);
        when(entityManager.find(Duration.class, 1)).thenReturn(timeout);
        when(clock.instant()).thenReturn(firstObservation, repeatedObservation, release);
        when(clock.getZone()).thenReturn(ZoneId.of("Europe/Vienna"));

        repository.updateSeatFromSensor(new SensorMessage(42L, false));
        assertFalse(seat.getStatus());
        assertEquals(firstObservation, seat.getOccupiedSince());

        repository.updateSeatFromSensor(new SensorMessage(42L, false));
        assertEquals(firstObservation, seat.getOccupiedSince(),
                "A repeated occupied observation must not restart the interval");

        repository.checkInactiveSeats();

        assertTrue(seat.getStatus());
        assertNull(seat.getOccupiedSince());
        ArgumentCaptor<History> history = ArgumentCaptor.forClass(History.class);
        verify(entityManager).persist(history.capture());
        assertEquals(96, history.getValue().getTimePassed(),
                "Elapsed time must remain continuous while Vienna leaves daylight-saving time");
        verify(webSocket, times(2)).broadcastSeatUpdate();
    }
}
