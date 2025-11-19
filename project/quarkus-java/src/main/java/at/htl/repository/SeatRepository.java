package at.htl.repository;

import at.htl.model.ScanHistory;
import at.htl.model.Seat;
import at.htl.model.SeatStatus;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

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
        var query = em.createQuery("select c from Seat c order by id desc");
        return  query.getResultList();
    }
    public List<Seat> getSeatByFloor (String floor) {
        var query = em.createQuery("select c from Seat c join SeatLocation se on c.location = se.id where lower(se.floor) like lower(:floor) order by c.id desc");
        query.setParameter("floor",floor);
        return query.getResultList();
    }
    public boolean changeStatusToOccupiedAfterTime () {
        //Scanhistory -> Timestamp
        var query = em.createQuery("select c from Seat c ");
        var seatList =query.getResultList();

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



}
