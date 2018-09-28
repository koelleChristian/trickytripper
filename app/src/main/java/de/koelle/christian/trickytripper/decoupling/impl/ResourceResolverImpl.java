package de.koelle.christian.trickytripper.decoupling.impl;

import android.content.res.Resources;

import java.util.Locale;

import de.koelle.christian.trickytripper.decoupling.ResourceResolver;

public class ResourceResolverImpl implements ResourceResolver {

    private final Resources resources;

    public ResourceResolverImpl(Resources resources) {
        this.resources = resources;
    }

    public String resolve(int id) {
        return resources.getString(id);
    }

    public Locale getLocale() {
        return resources.getConfiguration().locale;
    }

}
