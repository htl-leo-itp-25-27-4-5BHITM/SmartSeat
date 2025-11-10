package at.htl.repository;

import at.htl.model.Seat;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;


@ApplicationScoped
public class Repository {
    public boolean addSeat(Seat seat) {
        return  false;
    }
    public boolean updateSeat (Seat seat) {
        return false;
    }
    public List<Seat> getAll () {
        return null;
    }
    public Seat getSeatById (Long id) {
        return null;
    }

    public String timeSinceLastUse (long id) {
        return "";
    }
    public boolean changeStatusToOccupied (long id) {
        return false;
    }
    public boolean saveChatMessage (String schoolClass) {
        return false;
    }
    public List<String> returnChatMessages(String schoolClass) { // Eventuell eigene Klasse für chatMessages
        return null;
    }


}
