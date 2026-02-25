package at.htl.repository;

import at.htl.model.SensorMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.reactive.messaging.Incoming;

import java.io.IOException;

@ApplicationScoped
public class SensorService {

    @Inject
    SeatRepository seatRepository;

    @Transactional
    @Incoming("pico-data")
    public void handleIncoming(byte[] raw) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();

        SensorMessage sensorMessage = objectMapper.readValue(raw, SensorMessage.class);

        if (!sensorMessage.getStatus()) {
            System.out.println("<===Übertragung===>");
            System.out.println(sensorMessage.getName());
            System.out.println(sensorMessage.getStatus());
        }

        if (!sensorMessage.getStatus()) {
            System.out.println(seatRepository.changeSensorStatus(sensorMessage));
        }
    }
}
