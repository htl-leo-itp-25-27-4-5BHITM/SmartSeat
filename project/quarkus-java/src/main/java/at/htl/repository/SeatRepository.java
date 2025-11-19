package at.htl.repository;

import at.htl.model.Seat;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

import java.util.List;

@ApplicationScoped
public class SeatRepository {
    @Inject
    EntityManager en;

    public boolean addSeat (Long seatID) {
        return false;
    }
    public boolean deleateSeat (Long seatId) {
        return false;
    }
    public List<Seat> getAllSeats () {
        return  null;
    }
    public List<Seat> getSeatByFloor (String floor) {
        return null;
    }
    public boolean changeStatusToOccupiedAfterTime () {
        return false;
    }
    public boolean changeStatusToOccupied (Long id) {
        return false;
    }
    public boolean changeStatusToUnoccupied (Long id) {
        return false;
    }



}
