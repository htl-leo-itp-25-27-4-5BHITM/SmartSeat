package at.htl.repository;

import at.htl.model.ScanHistory;
import at.htl.model.Seat;
import at.htl.model.SeatLocation;
import at.htl.model.SeatStatus;
import at.htl.repository.dto.SeatInformationDTO;
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
    public boolean changeStatusToOccupiedAfterTime () {
        //Scanhistory -> Timestamp
        var query = em.createQuery("select c from Seat c", Seat.class);
        var seatList =query.getResultList();
        seatList.forEach(e -> e.setStatus(false));

        return false;
    }
    public boolean changeStatusToOccupied (Long id) {
        if (id <= 5 && id >= 1) {
            try {
                em.find(Seat.class, id).setStatus(false);

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
                em.find(Seat.class, id).setStatus(false);

            } catch (Exception e) {
                return false;
            }
            return true;
        }
        return false;
    }
}
