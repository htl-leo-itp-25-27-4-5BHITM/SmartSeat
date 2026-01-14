package at.htl.model;

public class SensorMessage {

    private String name;
    private Boolean status;

    public SensorMessage() {
    }

    public SensorMessage(String name, Boolean status) {
        this.name = name;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }
}
