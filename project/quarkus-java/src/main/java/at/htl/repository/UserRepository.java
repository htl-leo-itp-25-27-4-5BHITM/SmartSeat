package at.htl.repository;

import at.htl.model.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

@ApplicationScoped
public class UserRepository {

    @Inject
    EntityManager em;

    public User getUserByName(String name) {
        try {
            return em.createQuery(
                            "select u from User u where u.username = :name",
                            User.class
                    )
                    .setParameter("name", name)
                    .getSingleResult();

        } catch (NoResultException e) {
            return null;
        }
    }
}
