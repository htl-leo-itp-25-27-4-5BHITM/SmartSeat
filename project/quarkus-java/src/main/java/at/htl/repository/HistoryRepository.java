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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
        List<Seat> seats = em.createQuery("select s from Seat s order by s.id", Seat.class)
                .getResultList();

        List<History> histories = em.createQuery("""
            select h
            from History h
            join fetch h.seat
            where h.endedAt > :start
            """, History.class)
                .setParameter("start", startDay)
                .getResultList();

        return calculateOccupancy(seats, histories, date);
    }

    static List<SeatOccupancyDTO> calculateOccupancy(
            List<Seat> seats, List<History> histories, LocalDate date) {
        LocalDateTime startDay = date.atStartOfDay();
        LocalDateTime endDay = date.plusDays(1).atStartOfDay();
        Map<Long, long[]> occupancySeconds = new LinkedHashMap<>();
        seats.forEach(seat -> occupancySeconds.put(seat.getId(), new long[24]));
        boolean hasData = false;

        for (History h : histories) {
            LocalDateTime end = h.getEndedAt();
            LocalDateTime intervalStart = end.minusSeconds(Math.max(0, h.getTimePassed()));
            LocalDateTime current = intervalStart.isBefore(startDay) ? startDay : intervalStart;
            LocalDateTime effectiveEnd = end.isAfter(endDay) ? endDay : end;
            long[] buckets = occupancySeconds.get(h.getSeat().getId());

            if (buckets == null || !current.isBefore(effectiveEnd)) {
                continue;
            }

            while (current.isBefore(effectiveEnd)) {

                int hour = current.getHour();

                LocalDateTime nextHour =
                        current.withMinute(0)
                                .withSecond(0)
                                .withNano(0)
                                .plusHours(1);

                LocalDateTime border =
                        nextHour.isBefore(effectiveEnd)
                                ? nextHour
                                : effectiveEnd;

                long seconds =
                        java.time.Duration
                                .between(current, border)
                                .toSeconds();

                if (seconds > 0) {
                    buckets[hour] = Math.min(3600, buckets[hour] + seconds);
                    hasData = true;
                }

                current = border;
            }
        }

        if (!hasData) {
            return List.of();
        }

        List<SeatOccupancyDTO> result = new ArrayList<>();
        for (Seat seat : seats) {
            long[] buckets = occupancySeconds.get(seat.getId());

            for (int hour = 0; hour < 24; hour++) {

                result.add(
                        new SeatOccupancyDTO(
                                seat.getId(),
                                seat.getName(),
                                String.format("%02d:00", hour),
                                Math.round(
                                        ((double) buckets[hour] / 3600.0) * 100
                                ) / 100.0
                        )
                );
            }
        }

        return result;
    }
}
