package de.koelle.christian.trickytripper.controller;

import de.koelle.christian.trickytripper.factories.AmountFactory;
import de.koelle.christian.trickytripper.model.Trip;

public interface TripResolver {

    Trip getTripInEditing();

    AmountFactory getAmountFactory();

}
