package de.koelle.christian.trickytripper.model;

public class ImportSettings {

    private boolean replaceImportedRecordWhenAlreadyImported;

    public void setReplaceImportedRecordWhenAlreadyImported(boolean replaceImportedRecordWhenAlreadyImported) {
        this.replaceImportedRecordWhenAlreadyImported = replaceImportedRecordWhenAlreadyImported;
    }

    public boolean isReplaceImportedRecordWhenAlreadyImported() {
        return replaceImportedRecordWhenAlreadyImported;
    }

}
