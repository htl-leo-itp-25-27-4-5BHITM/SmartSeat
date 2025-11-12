package at.htl.repository;

import at.htl.model.Seat;
import at.htl.model.SeatStatus;
import io.quarkus.runtime.Startup;
import jakarta.annotation.PreDestroy;
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
    @Transactional
    public boolean removeSeat (Long id) {
        en.remove(id);
        return en.find(Seat.class,id) != null;
    }

    public boolean updateSeat (Seat seat) {
        if (en.find(Seat.class, seat) != null) {
            en.merge(seat);
            return true;
        }
        return false;
    }
    public List<Seat> getAll () {
        var query = en.createQuery("select c from Seat c");

        return query.getResultList();
    }
    public Seat getSeatById (Long id) {
        return en.find(Seat.class,id);
    }

    public List<Seat> getFilteredSeats (String filter) {
        var query = en.createQuery("select c from Seat c where lower(name) like lower('%'||:filter||'%')" +
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

    //Adds the Seats at the start of the application
    @Startup
    @Transactional
    public void init () {
        System.out.println("Starting application");
        en.persist(new Seat("1OG Rechter Flügel","Koje 1", SeatStatus.UNOCCOPIED));
        en.persist(new Seat("1OG Linker Flügel","Koje 2", SeatStatus.UNOCCOPIED));
        en.persist(new Seat("1OG Linker Flügel","Koje 3", SeatStatus.UNOCCOPIED));
        en.persist(new Seat("2OG Rechter Flügel","Koje 4", SeatStatus.UNOCCOPIED));
        en.persist(new Seat("2OG Rechter Flügel","Koje 5", SeatStatus.UNOCCOPIED));
        en.persist(new Seat("2OG Linker Flügel","Koje 6", SeatStatus.UNOCCOPIED));
        en.persist(new Seat("2OG Linker Flügel","Koje 7", SeatStatus.UNOCCOPIED));

    }

    @PreDestroy
    public void destroy () {
        en.getProperties().values().forEach(System.out::println);
        en.clear();
    }

}
