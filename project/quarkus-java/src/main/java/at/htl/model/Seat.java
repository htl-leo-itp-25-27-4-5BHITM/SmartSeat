package at.htl.model;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Date;
import java.time.Instant;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

@Entity
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;


    private String location;
    private String name;
    private SeatStatus status;

    //@CurrentTimestamp
//    @CreationTimestamp
//    @Column(name = "created_Date")
//    private Date timeStamp = Date.valueOf(LocalDate.now());
//
//    @UpdateTimestamp
//    @Column(name ="last_change_date")
//    private Date lastUse;

    public Seat (String location, String name, SeatStatus status) {
        setLocation(location);
        setStatus(status);
        setName(name);
    }
    public Seat () {
    }

    //<editor-fold desc="Getter Setter">
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

//    public Date getTimeStamp() {
//        return timeStamp;
//    }
//
//    public void setTimeStamp(Date timeStamp) {
//        this.timeStamp = timeStamp;
//    }
//
//    public Date getLastUse() {
//        return lastUse;
//    }
//
//    public void setLastUse(Date lastUse) {
//        this.lastUse = lastUse;
//    }

    public Long getId() {
        return id;
    }
    //</editor-fold>
}
