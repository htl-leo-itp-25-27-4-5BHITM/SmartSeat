package at.htl.repository;

import at.htl.model.Seat;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import org.jboss.logging.Logger;

import java.util.List;


@ApplicationScoped
public class Repository {
    @Inject
    Logger logger;
    @Inject
    EntityManager en;
    //TODO
    public boolean changeStatusToOccupied (long id) {
        return false;
    }
    public List<Seat> getAll () {
        return null;
    }
    public boolean removeSeat (long id) {
    return false;
    }
    public List<Seat> getSeatFromFloor (long floor) {
        return null;
    }
    public List<Seat> getFilteredSeats(String filter) {
        return null;
    }

    public boolean saveNewSeat (Seat seat) {
        return false;
    }
    public String timeSinceLastUse (long id) {
        return "";
    }
    public boolean saveChatMessage (String schoolClass) {
        return false;
    }


}
