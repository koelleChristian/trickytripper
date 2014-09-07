package de.koelle.christian.trickytripper.dataaccess.impl;

import java.util.List;

public interface Dao<T> {

    long create(T type);

    void update(T type);

    void deleteWithRelations(T type);

    T get(long id);

    List<T> getAll();

}
