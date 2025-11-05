package at.htl.repository;

import at.htl.model.Seat;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;


@ApplicationScoped
public class Repository {
    public boolean changeStatusToOccupied (long id) {
        return false;
    }
    public List<Seat> getAll () {
        return null;
    }
    public String timeSinceLastUse (long id) {
        return "";
    }
    public boolean saveChatMessage (String schoolClass) {
        return false;
    }


}
