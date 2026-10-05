package at.htl.repository;

import at.htl.model.History;
import at.htl.model.Seat;
import at.htl.repository.dto.SeatOccupancyDTO;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HistoryRepositoryTest {

    @Test
    void sumsHourOverlapCapsUtilizationAndKeepsCatalogIdentifiers() {
        Seat first = seat(2L, "Fenster");
        Seat nonContiguous = seat(42L, "Ruhezone");
        LocalDate date = LocalDate.of(2026, 10, 2);

        List<SeatOccupancyDTO> result = HistoryRepository.calculateOccupancy(
                List.of(first, nonContiguous),
                List.of(
                        history(nonContiguous, "2026-10-02T10:15:00", 900),
                        history(nonContiguous, "2026-10-02T11:15:00", 2700),
                        history(nonContiguous, "2026-10-02T10:50:00", 3600)
                ),
                date
        );

        assertEquals(48, result.size());
        assertEquals(1.0, value(result, 42L, "10:00"));
        assertEquals(0.25, value(result, 42L, "11:00"));
        assertTrue(result.stream().anyMatch(value ->
                value.seatId() == 2L && value.seatName().equals("Fenster")));
        assertTrue(result.stream().anyMatch(value ->
                value.seatId() == 42L && value.seatName().equals("Ruhezone")));
    }

    @Test
    void returnsExplicitEmptyResultForDateWithoutOverlap() {
        Seat seat = seat(42L, "Ruhezone");
        List<SeatOccupancyDTO> result = HistoryRepository.calculateOccupancy(
                List.of(seat),
                List.of(history(seat, "2026-10-01T10:00:00", 60)),
                LocalDate.of(2026, 10, 2)
        );
        assertTrue(result.isEmpty());
    }

    private static Seat seat(long id, String name) {
        Seat seat = new Seat(name, true);
        seat.setId(id);
        return seat;
    }

    private static History history(Seat seat, String endedAt, long seconds) {
        return new History(seat, seconds, LocalDateTime.parse(endedAt));
    }

    private static double value(List<SeatOccupancyDTO> values, long seatId, String hour) {
        return values.stream()
                .filter(value -> value.seatId() == seatId && value.hour().equals(hour))
                .findFirst()
                .orElseThrow()
                .occupancy();
    }
}
