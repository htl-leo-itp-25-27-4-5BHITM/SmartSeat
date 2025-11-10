package at.htl.model;
import io.quarkus.runtime.Startup;
import jakarta.persistence.*;
import org.h2.api.DatabaseEventListener;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.CurrentTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Date;
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
    @CreationTimestamp
    @Column(name = "created_Date")
    private Date timeStamp;

    @UpdateTimestamp
    @Column(name ="last_change_date")
    private Date lastUse;

    public Seat (String location, String name, SeatStatus status) {
        setLocation(location);
        setStatus(status);
        setName(name);
    }
    public Seat () {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
