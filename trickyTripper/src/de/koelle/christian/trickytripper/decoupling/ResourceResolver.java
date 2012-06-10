package de.koelle.christian.trickytripper.decoupling;

import java.util.Locale;

/**
 * This has been introduced to allow android independent testing.
 * 
 * @author ckoelle
 * 
 */
public interface ResourceResolver {

    String resolve(int id);

    Locale getLocale();

}
