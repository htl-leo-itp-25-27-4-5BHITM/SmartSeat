package at.htl.repository;

import at.htl.model.Seat;
import at.htl.repository.dto.SeatInformationDTO;
import io.quarkus.runtime.Startup;
import io.quarkus.scheduler.Scheduler;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
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
    //Returns a String in the cron format, with the time of the next closes endTime.
    private String getCron () {
        var time = LocalTime.now();
        logger.info(time);

        var endtimeCronList = em.createQuery(""" 
                        select e.endTime from EndTimes e
                                                 where
                                                  DATEADD('MINUTE', 55,(cast(:currentTime as time))) <= CAST(e.endTime as time)
                               order by e.endTime
                                """
                        , LocalTime.class)
                .setParameter("currentTime", time.toString()).getResultList().getFirst();

        if (endtimeCronList == null) {
            return "0 0 8 1/1 * ? *";
        }

        int hours = endtimeCronList.getHour();
        int minutes = endtimeCronList.getMinute();

        return String.format("0 %d %d 1/1 * ? *",minutes,hours);

    }


}
