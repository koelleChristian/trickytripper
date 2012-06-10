package de.koelle.christian.trickytripper.decoupling.impl;

import android.app.Activity;
import de.koelle.christian.trickytripper.decoupling.ActivityResolver;

public class ActivityResolverImpl implements ActivityResolver {

    private final Activity activity;

    public ActivityResolverImpl(Activity activity) {
        this.activity = activity;
    }

    public Object getActivity() {
        return activity;
    }

}
