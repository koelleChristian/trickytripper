package de.koelle.christian.trickytripper.activitysupport;

public interface Updatable {
    // Alternative to this construct would be to use setUserVisibleHint() which really seems to be called whenever tab-swiped to view.
    // Following solutions had no positive effect
    // a.) setOffscreenPageLimit(0)
    // b.) Using the FragmentStateAdapter instead of FragmentStatePagerAdapter
    void update();
}