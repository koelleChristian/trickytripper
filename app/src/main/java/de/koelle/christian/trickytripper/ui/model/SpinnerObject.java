package de.koelle.christian.trickytripper.ui.model;

public class SpinnerObject {

    private String stringToDisplay;
    private long id;

    public String getStringToDisplay() {
        return stringToDisplay;
    }

    public void setStringToDisplay(String stringToDisplay) {
        this.stringToDisplay = stringToDisplay;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return stringToDisplay;
    }

}
