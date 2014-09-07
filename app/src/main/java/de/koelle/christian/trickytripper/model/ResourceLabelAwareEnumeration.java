package de.koelle.christian.trickytripper.model;

import java.util.List;

public interface ResourceLabelAwareEnumeration {
    
    int getResourceStringId();
    
    List<ResourceLabelAwareEnumeration> getAllValues();

}
