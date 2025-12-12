package at.htl.repository;

import at.htl.model.Seat;
import at.htl.repository.dto.SeatInformationDTO;
import at.htl.sockets.SeatWebSocket;
import io.quarkus.scheduler.Scheduler;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@ApplicationScoped
public class SeatRepository {
    @Inject
    EntityManager em;

    @Inject
    Scheduler scheduler;

    @Inject
    Logger logger;

    @Inject
    SeatWebSocket ws;

    @Transactional
    public boolean addSeat (Seat seat) {
        if (seat.getId() <= 5 && seat.getId() >= 1) {
            try {
                em.persist(seat);
            } catch (Exception e) {
                return false;
            }
            return true;
        }

        return false;

    }

    @Transactional
    public boolean deleteSeat (Long seatId) {
        if (seatId <= 5 && seatId >= 1) {
            try {
                em.remove(seatId);
            } catch (Exception e) {
                return false;
            }
            return true;
        }

        return false;
    }
    public List<SeatInformationDTO> getAllSeats () {
        var query = em.createQuery("select new at.htl.repository.dto.SeatInformationDTO(c.name, c.status, se.floor, se.wing)" +
                " from Seat c " +
                "join SeatLocation se on se.id = c.location.id " +
                " order by c.id desc", SeatInformationDTO.class);

        return  query.getResultList();
    }
    public List<SeatInformationDTO> getSeatByFloor (String floor) {
        var query = em.createQuery("select new at.htl.repository.dto.SeatInformationDTO(c.name, c.status, se.floor, se.wing)" +
                "from Seat c" +
                " join SeatLocation se on c.location.id = se.id " +
                " where lower(se.floor) like lower(:floor) order by c.id desc", SeatInformationDTO.class);
        query.setParameter("floor",floor);
        return query.getResultList();
    }
    public long getUnoccupiedByFloor (String floor) {
        var query = em.createQuery("select count(c)" +
                " from Seat c " +
                " join SeatLocation se on c.location.id = se.id" +
                " where lower(se.floor) like lower(:floor)" +
                "and c.status = true", Long.class);
        query.setParameter("floor", floor);

        return query.getSingleResult();
    }
    public String getFloorByID (Long seatId) {

        var query = em.createQuery(""" 
            select sl.floor from Seat se
                 join SeatLocation sl on se.location.id = sl.id
                 where se.id = :seatId
            """,String.class)
                .setParameter("seatId", seatId);

        return query.getResultList().getFirst();
    }
    @Transactional
    public void changeStatusToUnoccupiedAfterTime () {
        // scheduler
        //String cron = "0 8 * * *" --> Startwert für den normalen Betrieb =  get cron;
        AtomicReference<String> cron = new AtomicReference<>(getCron());
        logger.infof("%s --> Endzeit", cron.get());


        scheduler.newJob("setSeatsToUnoccupiedJob")
                .setCron(cron.get())
                .setTask(scheduledExecution -> {

                    em.createQuery("""
                                select c from Seat c
                                """, Seat.class)
                            .getResultList().forEach(e -> changeStatusToUnoccupied(e.getId()));

                ws.broadcastSeatUpdate();

//                    scheduler.pause("setSeatsToUnoccupiedJob");
                    cron.set(getCron());

                    logger.infof("%s --> neue Endzeit", cron.get());
//                    scheduler.resume();

                }).schedule();

    }
    @Transactional
    public boolean changeStatus (Long id) {
        if (id <= 5 && id >= 1) {
            try {
                em.find(Seat.class, id).setStatus(!em.find(Seat.class,id).getStatus());
            } catch (Exception e) {
                return false;
            }
            return true;
        }
        return false;
    }
    @Transactional
    public void changeStatusToUnoccupied (Long id) {
        em.find(Seat.class, id).setStatus(true);
    }

    private String getCron() {
        LocalTime now = LocalTime.now();


        List<LocalTime> allEndTimes = em.createQuery(
                        "SELECT e.endTime FROM EndTimes e ORDER BY e.endTime", LocalTime.class)
                .getResultList();

        LocalTime nextEndTime = allEndTimes.stream()
                .filter(t -> !t.isBefore(now))
                .findFirst()
                .orElse(null);

        if (nextEndTime == null) {
            return "0 0 8 1/1 * ? *";
        }

        int hours = nextEndTime.getHour();
        int minutes = nextEndTime.getMinute();

        return String.format("0 %d %d 1/1 * ? *", minutes, hours);
    }
}
