package at.htl.model;

public class SensorMessage {

    private long id;
    private Boolean status;

    public SensorMessage() {
    }

    public SensorMessage(long id, Boolean status) {
        this.id = id;
        this.status = status;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }
}
