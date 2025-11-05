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

    public Seat (String location, SeatStatus status, LocalDateTime timeStamp) {
        setLocation(location);
        setStatus(status);
        setTimeStamp(timeStamp);
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
}
