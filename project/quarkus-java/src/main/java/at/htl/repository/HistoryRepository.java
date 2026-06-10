package at.htl.repository;

import at.htl.model.History;
import at.htl.model.Seat;
import at.htl.repository.dto.HistoryDTO;
import at.htl.sockets.SeatWebSocket;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

import java.time.LocalDateTime;
import java.util.Random;

@ApplicationScoped
public class HistoryRepository {

    private static final Random RANDOM = new Random();

    @Inject
    EntityManager em;

    @Inject
    SeatWebSocket ws;

    public long addHistories(HistoryDTO data) {

        if (data == null || data.n() <= 0) {
            return 400;
        }

        if (data.minSeconds() >= data.maxSeconds()) {
            return 400;
        }

        Seat seat = em.find(Seat.class, data.seat_id());

        if (seat == null) {
            return 404;
        }

        try {

            for (int i = 0; i < data.n(); i++) {

                History history = new History();
                history.setSeat(seat);
                history.setEndedAt(LocalDateTime.now());
                history.setTimePassed(
                        RANDOM.nextLong(
                                data.minSeconds(),
                                data.maxSeconds()
                        )
                );

                em.persist(history);
            }

            ws.broadcastSeatUpdate();
            return 200;

        } catch (Exception e) {
            e.printStackTrace();
            return 500;
        }
    }
}