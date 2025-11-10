package at.htl.repository;

import at.htl.model.Seat;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import java.util.List;


@ApplicationScoped
public class Repository {
    @Inject
    EntityManager en;

    @Transactional
    public boolean addSeat(Seat seat) {
        en.persist(seat);
        return  en.find(Seat.class,seat) != null;
    }
    public boolean updateSeat (Seat seat) {
        if (en.find(Seat.class, seat) != null) {
            en.merge(seat);
            return true;
        }
        return false;
    }
    public List<Seat> getAll () {
        var query = en.createQuery("select c from seatUse c");

        return query.getResultList();
    }
    public Seat getSeatById (Long id) {
        return en.find(Seat.class,id);
    }

    public List<Seat> getFilteredSeats (String filter) {
        var query = en.createQuery("select c from seatUse c where lower(name) like lower('%'||:filter||'%')" +
                " or lower(location) like lower('%'||:filter||'%')");
        query.setParameter("filter",filter);

        return query.getResultList();
    }
    public List<Seat> getLastUsedSeats() {
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
