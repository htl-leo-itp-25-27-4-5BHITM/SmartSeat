package at.htl.model;

import jakarta.persistence.*;

import java.time.Duration;

@Entity
public class History {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    private Seat seat;

    private long timePassed;

    public History(Seat seat, long timePassed) {
        this.seat = seat;
        this.timePassed = timePassed;
    }

    public History() {
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Seat getSeat() {
        return seat;
    }

    public void setSeat(Seat seat) {
        this.seat = seat;
    }

    public long getTimePassed() {
        return timePassed;
    }

    public void setTimePassed(long timePassed) {
        this.timePassed = timePassed;
    }
}
