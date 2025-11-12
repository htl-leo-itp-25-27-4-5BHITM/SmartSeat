package at.htl.repository;

import at.htl.model.ScanHistory;
import io.quarkus.runtime.Startup;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
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
         return em.createQuery("select s from ScanHistory s order by s.scannedAt desc").getResultList();
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
