package at.htl.repository;

import at.htl.model.Seat;
import at.htl.model.SensorMessage;
import at.htl.sockets.SeatWebSocket;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.reactive.messaging.Incoming;

import java.io.IOException;

@ApplicationScoped
public class SensorService {

    private long lastMovementTime = 0;

    @Inject
    SeatRepository seatRepository;

    @Inject
    SeatWebSocket seatWebSocket;

    @Transactional
    @Incoming("pico-data")
    public void handleIncoming(byte[] raw) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();

        SensorMessage sensorMessage = objectMapper.readValue(raw, SensorMessage.class);

        System.out.println("<===Übertragung===>");
        System.out.println(sensorMessage.getName());
        System.out.println(sensorMessage.getStatus());


        if (!sensorMessage.getStatus()) {
            System.out.println(seatRepository.changeSensorStatus(sensorMessage));
        }


    }
}
