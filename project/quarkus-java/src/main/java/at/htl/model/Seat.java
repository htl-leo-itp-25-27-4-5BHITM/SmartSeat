package at.htl.model;
import jakarta.persistence.*;
@Entity
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @OneToOne
    private SeatLocation location;

    private String name;
    private SeatStatus status;


    public Seat ( String name, SeatStatus status, SeatLocation location) {
        setStatus(status);
        setName(name);
        setLocation(location);
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


    public Long getId() {
        return id;
    }

    public SeatLocation getLocation() {
        return location;
    }

    public void setLocation(SeatLocation location) {
        this.location = location;
    }
    //</editor-fold>
}
