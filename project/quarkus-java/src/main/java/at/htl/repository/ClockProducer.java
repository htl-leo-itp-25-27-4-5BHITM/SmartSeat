package at.htl.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.time.Clock;
import java.time.ZoneId;

@ApplicationScoped
public class ClockProducer {

    @Produces
    @ApplicationScoped
    Clock applicationClock(
            @ConfigProperty(name = "smartseat.time-zone", defaultValue = "Europe/Vienna") String timeZone) {
        return Clock.system(ZoneId.of(timeZone));
    }
}
