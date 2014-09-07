package de.koelle.christian.trickytripper.dataaccess.impl.tecbeans;

import de.koelle.christian.trickytripper.model.Participant;

public class ParticipantReference {

    private long trip_id;
    private long id;
    private String name;
    private final boolean active;

    public ParticipantReference(long trip_id, Participant p) {
        this.trip_id = trip_id;
        this.id = p.getId();
        this.name = p.getName();
        this.active = p.isActive();
    }

    public long getTrip_id() {
        return trip_id;
    }

    public void setTrip_id(long trip_id) {
        this.trip_id = trip_id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isActive() {
        return active;
    }

}
