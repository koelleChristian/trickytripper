package de.koelle.christian.trickytripper.model;

public class PhoneContact {

    public final String phone = "";
    public String displayName = "";
    public final String email = "";
    public String id = "";

    @Override
    public String toString() {
        return displayName + " " + phone + " " + email;
    }

}
