package at.htl.model;

import jakarta.persistence.*;

@Entity
//@Table(name = "location")
public class SeatLocation {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    Long id;
    String floor;
    String wing;

    public SeatLocation () {

    }
    public SeatLocation (String floor, String wing) {
        setFloor(floor);
        setWing(wing);
    }

    //<editor-fold desc="Getter Setter">
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public String getWing() {
        return wing;
    }

    public void setWing(String wing) {
        this.wing = wing;
    }
    //</editor-fold>
}
