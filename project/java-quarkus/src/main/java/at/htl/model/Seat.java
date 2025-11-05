package at.htl.model;
import jakarta.persistence.*;
import org.hibernate.annotations.CurrentTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name="seatUse")
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)

    private Long id;
    private String location;
    private String name;
    private SeatStatus status;
    //@CurrentTimestamp
    private LocalDateTime timeStamp;
    private int floor;

    public Seat (int floor,String location, String name, SeatStatus status, LocalDateTime timeStamp) {
        setLocation(location);
        setStatus(status);
        setTimeStamp(timeStamp);
        setName(name);
        setFloor(floor);
    }
    public Seat () {
    }

    //<editor-fold desc="Getter Setter">
    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(LocalDateTime timeStamp) {
        this.timeStamp = timeStamp;
    }

    public SeatStatus getStatus() {
        return status;
    }

    public void setStatus(SeatStatus status) {
        this.status = status;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
    //</editor-fold>
}
