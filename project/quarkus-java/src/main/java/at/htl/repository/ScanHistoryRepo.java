package at.htl.repository;

import at.htl.model.ScanHistory;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

import java.util.List;

@ApplicationScoped
public class ScanHistoryRepo {

    @Inject
    EntityManager em;


    public void addEntry(long seatId) {
        em.persist(seatId);
    }

    public List<ScanHistory> getAllEntries() {
         return em.createQuery("select s from ScanHistory s").getResultList();
    }
}
