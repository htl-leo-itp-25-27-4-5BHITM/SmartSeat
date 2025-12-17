package at.htl.sockets;

import at.htl.repository.SeatRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.websockets.next.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@WebSocket(path = "/ws/seats")
@ApplicationScoped
public class SeatWebSocket {

    private static final Logger LOG = Logger.getLogger(SeatWebSocket.class);

    @Inject
    SeatRepository seatRepository;

    @Inject
    ObjectMapper mapper;

    private final Set<WebSocketConnection> connections = Collections.newSetFromMap(new ConcurrentHashMap<>());

    @OnOpen
    public void onOpen(WebSocketConnection connection) {
        connections.add(connection);
        LOG.infof("New WebSocket connection: %s at %s",
                connection.id(), java.time.LocalTime.now());

        sendAllSeats(connection);
    }

    @OnClose
    public void onClose(WebSocketConnection connection, CloseReason reason) {
        connections.remove(connection);
        LOG.infof("WebSocket closed: %s | Reason: %s | Time: %s",
                connection.id(), reason, java.time.LocalTime.now());
    }

    @OnError
    public void onError(WebSocketConnection connection, Throwable throwable) {
        connections.remove(connection);
        LOG.errorf(throwable,
                "WebSocket error on %s at %s",
                connection.id(), java.time.LocalTime.now());
    }

    private void sendAllSeats(WebSocketConnection connection) {
        try {
            var seats = seatRepository.getAllSeats();
            String json = mapper.writeValueAsString(seats);
            connection.sendText(json)
                    .subscribe().with(
                            unused -> {},
                            Throwable::printStackTrace
                    );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void broadcastSeatUpdate() {
        LOG.debugf("Broadcast seat update to %d connections at %s",
                connections.size(), java.time.LocalTime.now());

        try {
            var seats = seatRepository.getAllSeats();
            String json = mapper.writeValueAsString(seats);

            for (WebSocketConnection connection : connections) {
                connection.sendText(json)
                        .subscribe().with(
                                unused -> {},
                                Throwable::printStackTrace
                        );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
