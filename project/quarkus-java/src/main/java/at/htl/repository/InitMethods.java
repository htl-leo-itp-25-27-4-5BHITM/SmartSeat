package at.htl.repository;

import io.quarkus.runtime.Startup;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class InitMethods {
    @Inject
    SeatRepository repository;

    @Startup
    public void init () {
        repository.changeStatusToUnoccupiedAfterTime();
        repository.changeStatusEveryThreeMinutes();
    }
}
