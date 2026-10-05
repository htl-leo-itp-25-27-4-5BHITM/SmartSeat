package at.htl.model;
import jakarta.persistence.*;

import java.time.Instant;

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

    private Instant occupiedSince;

    private Double mapX;

    private Double mapY;

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

    public void setId(Long id) {
        this.id = id;
    }

    public SeatLocation getLocation() {
        return location;
    }

    public void setLocation(SeatLocation location) {
        this.location = location;
    }

    public Instant getOccupiedSince() {
        return occupiedSince;
    }

    public void setOccupiedSince(Instant occupiedSince) {
        this.occupiedSince = occupiedSince;
    }

    public Double getMapX() {
        return mapX;
    }

    public void setMapX(Double mapX) {
        this.mapX = mapX;
    }

    public Double getMapY() {
        return mapY;
    }

    public void setMapY(Double mapY) {
        this.mapY = mapY;
    }

    //</editor-fold>
}
