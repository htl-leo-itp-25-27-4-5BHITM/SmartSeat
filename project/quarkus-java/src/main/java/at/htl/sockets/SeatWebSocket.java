package at.htl.sockets;

import at.htl.repository.SeatRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.websockets.next.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@WebSocket(path = "/ws/seats")
@ApplicationScoped
public class SeatWebSocket {

    @Inject
    SeatRepository seatRepository;

    @Inject
    ObjectMapper mapper;

    private final Set<WebSocketConnection> connections = Collections.newSetFromMap(new ConcurrentHashMap<>());

    @OnOpen
    public void onOpen(WebSocketConnection connection) {
        connections.add(connection);
        sendAllSeats(connection);
    }

    @OnClose
    public void onClose(WebSocketConnection connection, CloseReason reason) {
        connections.remove(connection);
    }

    @OnError
    public void onError(WebSocketConnection connection, Throwable throwable) {
        connections.remove(connection);
        throwable.printStackTrace();
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
