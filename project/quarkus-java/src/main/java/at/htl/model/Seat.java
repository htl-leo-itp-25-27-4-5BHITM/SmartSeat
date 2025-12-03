package at.htl.model;
import jakarta.persistence.*;
@Entity
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private SeatLocation location;

    private String name;

    @Column(name = "unoccupied")
    private boolean status;


    public Seat ( String name, boolean status) {
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


    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
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
