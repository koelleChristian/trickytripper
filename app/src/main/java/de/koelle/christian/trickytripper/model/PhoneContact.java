package de.koelle.christian.trickytripper.model;

public class PhoneContact {

    public String phone = "";
    public String displayName = "";
    public String email = "";
    public String id = "";

    @Override
    public String toString() {
        return displayName + " " + phone + " " + email;
    }

}
