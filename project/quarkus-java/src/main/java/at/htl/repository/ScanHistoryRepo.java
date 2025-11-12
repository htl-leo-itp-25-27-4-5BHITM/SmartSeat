package at.htl.repository;

import at.htl.model.ScanHistory;
import at.htl.model.Seat;
import at.htl.model.SeatStatus;
import io.quarkus.runtime.Startup;
import io.quarkus.vertx.web.Route;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.FileSystemAccess;
import io.vertx.ext.web.handler.StaticHandler;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import java.nio.file.Path;
import java.util.List;

@ApplicationScoped
public class ScanHistoryRepo {

    @Inject
    EntityManager em;


    @Transactional
    public boolean addEntry(long seatId) {
        if (seatId <= 5 && seatId >= 1) {
            try {
                em.persist(new ScanHistory(seatId));
            } catch (Exception e) {
                return false;
            }
            return true;
        }

        return false;
    }

    public List<ScanHistory> getAllEntries() {
         return em.createQuery("select s from ScanHistory s").getResultList();
    }


    @Startup
    @Transactional
    public void init () {
        System.out.println("Starting application");
        em.persist(new ScanHistory(1));
        em.persist(new ScanHistory(2));
        em.persist(new ScanHistory(3));
        em.persist(new ScanHistory(4));
        em.persist(new ScanHistory(5));
        em.persist(new ScanHistory(6));


    }

    @PreDestroy
    public void destroy () {
        em.getProperties().values().forEach(System.out::println);
        em.clear();
    }
}
