package de.koelle.christian.trickytripper.model;

import java.io.Serializable;

public class Participant implements Serializable, Comparable<Participant> {

    private static final long serialVersionUID = -3692563524783113399L;

    private String name = "";
    private long id;
    private String externalId;
    private boolean active = true;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return "Participant [name=" + name + ", id=" + id + ", externalId="
                + externalId + ", active=" + active + "]";
    }

    public int compareTo(Participant another) {
        return getName().compareTo(another.getName());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (id ^ (id >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Participant other = (Participant) obj;
        return id == other.id;
    }

}
