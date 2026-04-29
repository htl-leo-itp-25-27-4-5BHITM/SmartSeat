package at.htl.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Duration {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private int seconds;

    public Duration(int seconds) {
        this.seconds = seconds;
    }

    public Duration() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getSeconds() {
        return seconds;
    }

    public void setSeconds(int millis) {
        this.seconds = millis;
    }
}
