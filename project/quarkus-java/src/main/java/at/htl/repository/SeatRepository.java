package at.htl.repository;

import at.htl.model.ScanHistory;
import at.htl.model.Seat;
import at.htl.model.SeatLocation;
import at.htl.model.SeatStatus;
import io.quarkus.runtime.Startup;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class SeatRepository {
    @Inject
    EntityManager em;

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
    public List<Seat> getAllSeats () {
        var query = em.createQuery("select c.name, c.status, se.floor, se.wing" +
                " from Seat c " +
                "join SeatLocation se on se.id = c.location.id " +
                " order by c.id desc",Seat.class);

        return  query.getResultList();
    }
    public List<Seat> getSeatByFloor (String floor) {
        var query = em.createQuery("select c.name, c.status, se.floor, se.wing from Seat c" +
                " join SeatLocation se on c.location.id = se.id" +
                " where lower(se.floor) like lower(:floor) order by c.id desc", Seat.class);
        query.setParameter("floor",floor);
        return query.getResultList();
    }
    public boolean changeStatusToOccupiedAfterTime () {
        //Scanhistory -> Timestamp
        var query = em.createQuery("select c from Seat c", Seat.class);
        var seatList =query.getResultList();
        seatList.forEach(e -> e.setStatus(SeatStatus.OCCUPIED));

        return false;
    }
    public boolean changeStatusToOccupied (Long id) {
        if (id <= 5 && id >= 1) {
            try {
                em.find(Seat.class, id).setStatus(SeatStatus.OCCUPIED);

            } catch (Exception e) {
                return false;
            }
            return true;
        }



        return false;
    }
    public boolean changeStatusToUnoccupied (Long id) {
        if (id <= 5 && id >= 1) {
            try {
                em.find(Seat.class, id).setStatus(SeatStatus.UNOCCUPIED);

            } catch (Exception e) {
                return false;
            }
            return true;
        }
        return false;
    }

    @Startup
    @Transactional
    public void init() {
        //LOCATION
//        var seatLoc1 = new SeatLocation("1OG","left");
//        var seatLoc2 = new SeatLocation("1OG","right");
//        var seatLoc3 = new SeatLocation("2OG","left");
//        var seatLoc4 = new SeatLocation("2OG","right");
//
////        em.persist(seatLoc1);
////        em.persist(seatLoc2);
////        em.persist(seatLoc3);
////        em.persist(seatLoc4);

        //SEATS
//        em.persist(new Seat("Koje 1", SeatStatus.UNOCCUPIED,seatLoc1));
//        em.persist(new Seat("Koje 2", SeatStatus.UNOCCUPIED,seatLoc2));
//        em.persist(new Seat("Koje 3", SeatStatus.UNOCCUPIED,seatLoc2));
//        em.persist(new Seat("Koje 4", SeatStatus.UNOCCUPIED,seatLoc3));
//        em.persist(new Seat("Koje 5", SeatStatus.UNOCCUPIED,seatLoc4));





    }
    @PreDestroy
    @Transactional
    public void clear () {
        em.clear();
    }

}
