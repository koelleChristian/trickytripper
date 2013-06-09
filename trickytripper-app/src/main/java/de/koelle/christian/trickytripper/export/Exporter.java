package de.koelle.christian.trickytripper.export;

import java.io.File;
import java.util.List;

import de.koelle.christian.trickytripper.decoupling.ActivityResolver;
import de.koelle.christian.trickytripper.decoupling.ResourceResolver;
import de.koelle.christian.trickytripper.factories.AmountFactory;
import de.koelle.christian.trickytripper.model.ExportSettings;
import de.koelle.christian.trickytripper.model.Participant;
import de.koelle.christian.trickytripper.model.Trip;

public interface Exporter {

    List<File> exportReport(ExportSettings settings, List<Participant> participants, Trip trip,
            ResourceResolver resourceResolver, ActivityResolver activityResolver, AmountFactory amountFactory);

}
