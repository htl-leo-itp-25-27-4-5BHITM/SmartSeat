package at.htl.sockets;

import at.htl.repository.SeatRepository;
import jakarta.inject.Inject;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@ServerEndpoint("/ws/seats")
public class SeatWebSocket {

    @Inject
    SeatRepository seatRepository;

    private static Set<Session> sessions = new CopyOnWriteArraySet<>();

    @OnOpen
    public void onOpen(Session session) {
        sessions.add(session);
        System.out.println("Neue SeatWebSocket-Verbindung: " + session.getId());
    }

    @OnClose
    public void onClose(Session session) {
        sessions.remove(session);
        System.out.println("SeatWebSocket geschlossen: " + session.getId());
    }

    @OnMessage
    public void onMessage(Session session, String message) throws IOException {
        var floor = message.replaceAll(".*\"floor\"\\s*:\\s*\"(\\w+)\".*", "$1");

        var seats = seatRepository.getSeatByFloor(floor);
        session.getBasicRemote().sendText("{\"type\":\"seatsUpdate\", \"data\":" + seats + "}");
    }

    public void broadcastSeats(String floor) {
        var seats = seatRepository.getSeatByFloor(floor);
        String payload = "{\"type\":\"seatsUpdate\", \"data\":" + seats + "}";
        sessions.forEach(session -> {
            try {
                session.getBasicRemote().sendText(payload);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
