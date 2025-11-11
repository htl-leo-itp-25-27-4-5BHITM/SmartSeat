package at.htl.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

//Saves the history of the usage
@Entity
public class SeatUsage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @OneToOne
    Seat seat;

    @CreationTimestamp
    Date lastUsed;

    public SeatUsage () {}


}
