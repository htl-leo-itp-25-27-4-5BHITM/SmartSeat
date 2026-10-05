package at.htl.repository;

import at.htl.model.Duration;
import at.htl.model.History;
import at.htl.model.Seat;
import at.htl.model.SensorMessage;
import at.htl.repository.dto.HistorySeatCountDTO;
import at.htl.repository.dto.SeatInformationDTO;
import at.htl.repository.dto.SeatRenameDTO;
import at.htl.repository.dto.SeatRenameResult;
import at.htl.repository.dto.SeatTimeAverageDTO;
import at.htl.sockets.SeatWebSocket;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class SeatRepository {

    @Inject
    EntityManager em;

    @Inject
    SeatWebSocket ws;

    @Inject
    Clock clock;

    private final ConcurrentHashMap<Long, Instant> inactiveCheckMap =
            new ConcurrentHashMap<>();


    @Transactional
    public void updateSeatFromSensor(SensorMessage msg) {

        Seat seat = em.find(Seat.class, msg.getId());

        if (seat == null) {
            return;
        }

        boolean oldStatus = seat.getStatus();
        boolean newStatus = msg.getStatus();

        Instant now = clock.instant();

        if (oldStatus != newStatus) {

            seat.setStatus(newStatus);

            if (!newStatus) {
                seat.setOccupiedSince(now);
                inactiveCheckMap.put(seat.getId(), now);
            } else {
                seat.setOccupiedSince(null);
                inactiveCheckMap.remove(seat.getId());
            }

            ws.broadcastSeatUpdate();
        } else {
            if (!newStatus) {
                inactiveCheckMap.put(seat.getId(), now);
            }
        }
    }


    @Scheduled(every = "1s")
    @Transactional
    void checkInactiveSeats() {

        int durationSeconds = em.find(Duration.class, 1).getSeconds();

        Instant now = clock.instant();

        List<Long> seatsToRemove = new ArrayList<>();

        for (var entry : inactiveCheckMap.entrySet()) {

            Long seatId = entry.getKey();
            Instant lastSeen = entry.getValue();

            if (!lastSeen.plusSeconds(durationSeconds).isAfter(now)) {

                Seat seat = em.find(Seat.class, seatId);

                if (seat != null && !seat.getStatus()) {

                    completeOccupancy(seat, now);
                }

                seatsToRemove.add(seatId);
            }
        }

        seatsToRemove.forEach(inactiveCheckMap::remove);

        if (!seatsToRemove.isEmpty()) {
            ws.broadcastSeatUpdate();
        }
    }


    //<editor-fold desc="Basic Functions">
    public List<SeatInformationDTO> getAllSeats() {
        return em.createQuery("select s from Seat s join fetch s.location order by s.id", Seat.class)
                .getResultStream()
                .map(this::toDto)
                .toList();
    }

    public List<String> getAllFloors() {
        return em.createQuery("select distinct location.floor from SeatLocation location order by location.floor", String.class)
                .getResultList();
    }

    @Transactional
    public boolean changeStatus(Long id) {
        Seat seat = em.find(Seat.class, id);
        if (seat == null) {
            return false;
        }

        Instant now = clock.instant();
        boolean becomingFree = !seat.getStatus();

        if (becomingFree) {
            completeOccupancy(seat, now);
            inactiveCheckMap.remove(id);
        } else {
            seat.setStatus(false);
            seat.setOccupiedSince(now);
            inactiveCheckMap.put(id, now);
        }

        return true;
    }

    public List<SeatInformationDTO> getSeatByFloor(String floor) {
        var query = em.createQuery("""
                select c
                from Seat c
                join fetch c.location se
                where lower(se.floor) = lower(:floor)
                order by c.id
                """, Seat.class);

        query.setParameter("floor", floor);

        return query.getResultStream().map(this::toDto).toList();
    }

    public long getUnoccupiedCount() {

        return em.createQuery("""
                select count(s)
                from Seat s
                where s.status = true
                """, Long.class)
                .getSingleResult();
    }

    public long getUnoccupiedByFloor(String floor) {

        var query = em.createQuery("""
                select count(c)
                from Seat c
                join SeatLocation se on c.location.id = se.id
                where lower(se.floor) like lower(:floor)
                and c.status = true
                """, Long.class);

        query.setParameter("floor", floor);

        return query.getSingleResult();
    }

    @Transactional
    public SeatRenameResult renameSeat(SeatRenameDTO seatRenameDTO) {
        if (seatRenameDTO == null || seatRenameDTO.name() == null) {
            return new SeatRenameResult(SeatRenameResult.Status.INVALID, List.of());
        }

        String normalizedName = seatRenameDTO.name().trim();
        if (normalizedName.isBlank()) {
            return new SeatRenameResult(SeatRenameResult.Status.INVALID, List.of());
        }

        Seat target = em.find(Seat.class, seatRenameDTO.id());
        if (target == null) {
            return new SeatRenameResult(SeatRenameResult.Status.NOT_FOUND, List.of());
        }

        if (normalizedName.equals(target.getName().trim())) {
            return new SeatRenameResult(SeatRenameResult.Status.SUCCESS, getAllSeats());
        }

        long otherOwners = em.createQuery("""
                        select count(s)
                        from Seat s
                        where s.id <> :id and s.name = :name
                        """, Long.class)
                .setParameter("id", seatRenameDTO.id())
                .setParameter("name", normalizedName)
                .getSingleResult();

        if (otherOwners > 0) {
            return new SeatRenameResult(SeatRenameResult.Status.CONFLICT, List.of());
        }

        target.setName(normalizedName);
        em.flush();
        ws.broadcastSeatUpdate();
        return new SeatRenameResult(SeatRenameResult.Status.SUCCESS, getAllSeats());
    }

    public int getDuration() {

        return em.find(Duration.class, 1).getSeconds();
    }

    @Transactional
    public boolean changeDuration(int newDuration) {

        int updated = em.createQuery("""
                        update Duration d
                        set d.seconds = :newDuration
                        """)
                .setParameter("newDuration", newDuration)
                .executeUpdate();

        return updated > 0;
    }

    public Double getAverageTimePassed() {

        var query = em.createQuery("""
                select avg(h.timePassed)
                from History h
                """, Double.class);

        return query.getSingleResult();
    }

    public List<SeatTimeAverageDTO> getAverageWaitingTimesBySeat() {

        return em.createQuery("""
            select new at.htl.repository.dto.SeatTimeAverageDTO(
                h.seat.id,
                h.seat.name,
                avg(h.timePassed)
            )
            from History h
            group by h.seat.id, h.seat.name
            """, SeatTimeAverageDTO.class)
                .getResultList();
    }

    public SeatTimeAverageDTO getAverageWaitingTimesForId(long id) {

        var query = em.createQuery("""
                select new at.htl.repository.dto.SeatTimeAverageDTO(
                    h.seat.id,
                    h.seat.name,
                    avg(h.timePassed)
                )
                from History h
                where h.seat.id = :id
                group by h.seat.id, h.seat.name
                """, SeatTimeAverageDTO.class)
                .setParameter("id", id);

        return query.getSingleResult();
    }

    public long countHistoryForDate(LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay();

        return em.createQuery("""
            select count(h)
            from History h
            where h.endedAt >= :start
              and h.endedAt < :end
            """, Long.class)
                .setParameter("start", start)
                .setParameter("end", end)
                .getSingleResult();
    }

    public List<HistorySeatCountDTO> countHistoryForDateAndSeat(LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay();

        return em.createQuery("""
            select new at.htl.repository.dto.HistorySeatCountDTO(
            h.seat.id, h.seat.name, count(h)
            )
            from History h
            where h.endedAt >= :start
              and h.endedAt < :end
              group by seat.id, seat.name
            """, HistorySeatCountDTO.class)
                .setParameter("start", start)
                .setParameter("end", end)
                .getResultList();
    }

    private SeatInformationDTO toDto(Seat seat) {
        return new SeatInformationDTO(
                seat.getId(),
                seat.getName(),
                seat.getStatus(),
                seat.getStatus() ? "FREE" : "OCCUPIED",
                seat.getLocation().getFloor(),
                seat.getLocation().getWing(),
                seat.getMapX(),
                seat.getMapY(),
                seat.getOccupiedSince()
        );
    }

    private void completeOccupancy(Seat seat, Instant endedAt) {
        if (seat.getOccupiedSince() != null) {
            History history = new History();
            history.setSeat(seat);
            history.setTimePassed(Math.max(0, java.time.Duration
                    .between(seat.getOccupiedSince(), endedAt)
                    .toSeconds()));
            history.setEndedAt(LocalDateTime.ofInstant(endedAt, clock.getZone()));
            em.persist(history);
        }

        seat.setStatus(true);
        seat.setOccupiedSince(null);
    }

    //</editor-fold>
}
