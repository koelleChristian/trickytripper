package de.koelle.christian.trickytripper.decoupling.impl;

import java.util.Locale;

import android.content.res.Resources;
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
