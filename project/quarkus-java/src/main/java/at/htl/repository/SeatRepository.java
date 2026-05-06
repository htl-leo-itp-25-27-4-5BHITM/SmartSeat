package at.htl.repository;

import at.htl.model.Duration;
import at.htl.model.Seat;
import at.htl.model.SensorMessage;
import at.htl.repository.dto.SeatInformationDTO;
import at.htl.repository.dto.SeatRenameDTO;
import at.htl.sockets.SeatWebSocket;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class SeatRepository {

    @Inject
    EntityManager em;

    @Inject
    SeatWebSocket ws;

    @Transactional
    public void updateSeatFromSensor(SensorMessage msg) {

        Seat seat = em.createQuery("""
                        select s from Seat s where s.id = :id
                        """, Seat.class)
                .setParameter("id", msg.getId())
                .getSingleResult();

        boolean oldStatus = seat.getStatus();
        boolean newStatus = msg.getStatus();

        seat.setLastUpdate(LocalDateTime.now());

        if (oldStatus != newStatus) {
            seat.setStatus(newStatus);
            seat.setOccupiedSince(LocalDateTime.now());

            ws.broadcastSeatUpdate();
        }
    }

    @Transactional
    public void resetInactiveSeats() {

        var query = em.createQuery("""
                select d from Duration d
                """, Duration.class);


        LocalDateTime threshold = LocalDateTime.now().minusSeconds(query.getResultList().getFirst().getSeconds());

        int updated = em.createQuery("""
                        update Seat s
                        set s.status = true, s.occupiedSince = null
                        where s.lastUpdate < :threshold
                          and s.status = false
                        """)
                .setParameter("threshold", threshold)
                .executeUpdate();

        if (updated > 0) {
            ws.broadcastSeatUpdate();
        }
    }

    @Scheduled(every = "10s")
    void checkInactiveSeats() {
        resetInactiveSeats();
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
                em.find(Seat.class, id).setStatus(!em.find(Seat.class, id).getStatus());
            } catch (Exception e) {
                return false;
            }
            return true;
        }
        return false;
    }

    public List<SeatInformationDTO> getSeatByFloor(String floor) {
        var query = em.createQuery("select new at.htl.repository.dto.SeatInformationDTO(c.id, c.name, c.status, se.floor, se.wing, c.occupiedSince)" +
                "from Seat c" +
                " join SeatLocation se on c.location.id = se.id " +
                " where lower(se.floor) like lower(:floor) order by c.id desc", SeatInformationDTO.class);
        query.setParameter("floor", floor);
        return query.getResultList();
    }

    public long getUnoccupiedCount() {
        return em.createQuery(
                "select count(s) from Seat s where s.status = true",
                Long.class
        ).getSingleResult();
    }

    public long getUnoccupiedByFloor(String floor) {
        var query = em.createQuery("select count(c)" +
                " from Seat c " +
                " join SeatLocation se on c.location.id = se.id" +
                " where lower(se.floor) like lower(:floor)" +
                "and c.status = true", Long.class);
        query.setParameter("floor", floor);

        return query.getSingleResult();
    }

    public int checkNameExistence(String name) {
        var query = em.createQuery("""
                select s from Seat s where s.name = :name
                """, Seat.class);

        query.setParameter("name", name);

        return query.getResultList().size();
    }

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
                return getAllSeats();
            }

            ws.broadcastSeatUpdate();
        }


        return new ArrayList<>();
    }

    public int getDuration() {
        return em.find(Duration.class, 1).getSeconds();
    }

    public boolean changeDuration(int newDuration) {
        int updated = em.createQuery("""
                        update Duration d
                        set d.seconds = :newDuration
                        """)
                .setParameter("newDuration", newDuration)
                .executeUpdate();

        return updated > 0;
    }

    //</editor-fold>
}