package at.htl.repository;

import at.htl.model.Duration;
import at.htl.model.History;
import at.htl.model.Seat;
import at.htl.model.SensorMessage;
import at.htl.repository.dto.SeatInformationDTO;
import at.htl.repository.dto.SeatRenameDTO;
import at.htl.repository.dto.SeatTimeAverageDTO;
import at.htl.sockets.SeatWebSocket;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

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

    private final ConcurrentHashMap<Long, LocalDateTime> inactiveCheckMap =
            new ConcurrentHashMap<>();


    @Transactional
    public void updateSeatFromSensor(SensorMessage msg) {

        Seat seat = em.find(Seat.class, msg.getId());

        if (seat == null) {
            return;
        }

        boolean oldStatus = seat.getStatus();
        boolean newStatus = msg.getStatus();

        LocalDateTime now = LocalDateTime.now();

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

        LocalDateTime now = LocalDateTime.now();

        List<Long> seatsToRemove = new ArrayList<>();

        for (var entry : inactiveCheckMap.entrySet()) {

            Long seatId = entry.getKey();
            LocalDateTime lastSeen = entry.getValue();

            if (lastSeen.plusSeconds(durationSeconds).isBefore(now)) {

                Seat seat = em.find(Seat.class, seatId);

                if (seat != null && !seat.getStatus()) {

                    long seconds = java.time.Duration
                            .between(seat.getOccupiedSince(), now)
                            .toSeconds();

                    History history = new History();
                    history.setSeat(seat);
                    history.setTimePassed(seconds);

                    em.persist(history);

                    seat.setStatus(true);
                    seat.setOccupiedSince(null);
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
        return em.createQuery("""
                        select new at.htl.repository.dto.SeatInformationDTO(
                            c.id, c.name, c.status, se.floor, se.wing, c.occupiedSince
                        )
                        from Seat c
                        join SeatLocation se on se.id = c.location.id
                        order by c.id desc
                        """, SeatInformationDTO.class)
                .getResultList();
    }

    @Transactional
    public boolean changeStatus(Long id) {

        if (id <= 5 && id >= 1) {

            try {

                Seat seat = em.find(Seat.class, id);

                boolean newStatus = !seat.getStatus();

                seat.setStatus(newStatus);

                if (newStatus) {
                    inactiveCheckMap.remove(id);
                    seat.setOccupiedSince(null);
                } else {
                    LocalDateTime now = LocalDateTime.now();
                    inactiveCheckMap.put(id, now);
                    seat.setOccupiedSince(now);
                }

            } catch (Exception e) {
                return false;
            }

            return true;
        }

        return false;
    }

    public List<SeatInformationDTO> getSeatByFloor(String floor) {

        var query = em.createQuery("""
                select new at.htl.repository.dto.SeatInformationDTO(
                    c.id,
                    c.name,
                    c.status,
                    se.floor,
                    se.wing,
                    c.occupiedSince
                )
                from Seat c
                join SeatLocation se on c.location.id = se.id
                where lower(se.floor) like lower(:floor)
                order by c.id desc
                """, SeatInformationDTO.class);

        query.setParameter("floor", floor);

        return query.getResultList();
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

    public int checkNameExistence(String name) {

        var query = em.createQuery("""
                select s
                from Seat s
                where s.name = :name
                """, Seat.class);

        query.setParameter("name", name);

        return query.getResultList().size();
    }

    @Transactional
    public List<SeatInformationDTO> renameSeat(SeatRenameDTO seatRenameDTO) {

        if (checkNameExistence(seatRenameDTO.name()) == 0) {

            int updated = em.createQuery("""
                            update Seat s
                            set s.name = :newName
                            where s.id = :id
                            """)
                    .setParameter("newName", seatRenameDTO.name())
                    .setParameter("id", seatRenameDTO.id())
                    .executeUpdate();

            if (updated > 0) {

                ws.broadcastSeatUpdate();

                return getAllSeats();
            }
        }

        return new ArrayList<>();
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

    //</editor-fold>
}