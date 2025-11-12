package at.htl.model;

import java.util.Date;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;

//Saves the history of the usage
@Entity
public class ScanHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @OneToOne
    Seat seat;

    @CreationTimestamp
    Date scanned;

    public ScanHistory() {}
    public ScanHistory(Seat seat) {
        setSeat(seat);
    }

    //<editor-fold desc="Getter Setter">
    public Seat getSeat() {
        return seat;
    }

    public void setSeat(Seat seat) {
        this.seat = seat;
    }

    public Long getId() {
        return id;
    }

    public Date getScanned() {
        return scanned;
    }
    //</editor-fold>
}
