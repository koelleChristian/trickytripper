package de.koelle.christian.common.io;

import java.io.File;

public interface FileWriter {

    File write(String fileName, StringBuilder contents);

}
