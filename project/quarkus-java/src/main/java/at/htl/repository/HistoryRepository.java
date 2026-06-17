package at.htl.repository;

import at.htl.model.History;
import at.htl.model.Seat;
import at.htl.repository.dto.HistoryDTO;
import at.htl.repository.dto.SeatOccupancyDTO;
import at.htl.sockets.SeatWebSocket;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@ApplicationScoped
public class HistoryRepository {

    private static final Random RANDOM = new Random();

    @Inject
    EntityManager em;

    @Inject
    SeatWebSocket ws;

    public long addHistories(HistoryDTO data) {

        if (data == null) {
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
                History history = new History();
                history.setSeat(seat);
                history.setEndedAt(data.dateTime());
                history.setTimePassed(
                        RANDOM.nextLong(
                                data.minSeconds(),
                                data.maxSeconds()
                        )
                );

                em.persist(history);
            ws.broadcastSeatUpdate();
            return 200;

        } catch (Exception e) {
            e.printStackTrace();
            return 500;
        }
    }

    public List<SeatOccupancyDTO> getOccupancyForDate(LocalDate date) {

        LocalDateTime startDay = date.atStartOfDay();
        LocalDateTime endDay = date.plusDays(1).atStartOfDay();

        List<History> histories = em.createQuery("""
            select h
            from History h
            where h.endedAt >= :start
            and h.endedAt < :end
            """, History.class)
                .setParameter("start", startDay)
                .setParameter("end", endDay)
                .getResultList();

        double[][] occupancies = new double[5][24];

        for (History h : histories) {

            int seatIndex = Math.toIntExact(h.getSeat().getId() - 1);

            LocalDateTime end = h.getEndedAt();

            LocalDateTime start =
                    end.minusSeconds(h.getTimePassed());

            LocalDateTime current = start;

            while (current.isBefore(end)) {

                int hour = current.getHour();

                LocalDateTime nextHour =
                        current.withMinute(0)
                                .withSecond(0)
                                .withNano(0)
                                .plusHours(1);

                LocalDateTime border =
                        nextHour.isBefore(end)
                                ? nextHour
                                : end;

                long seconds =
                        java.time.Duration
                                .between(current, border)
                                .toSeconds();

                double value = (double) seconds / 3600.0;

                occupancies[seatIndex][hour] =
                        Math.max(occupancies[seatIndex][hour], value);

                current = border;
            }
        }

        List<SeatOccupancyDTO> result = new ArrayList<>();

        for (int seat = 0; seat < 5; seat++) {

            for (int hour = 0; hour < 24; hour++) {

                result.add(
                        new SeatOccupancyDTO(
                                seat + 1,
                                "Koje " + (seat + 1),
                                String.format("%02d:00", hour),
                                Math.round(
                                        occupancies[seat][hour] * 100
                                ) / 100.0
                        )
                );
            }
        }

        return result;
    }
}