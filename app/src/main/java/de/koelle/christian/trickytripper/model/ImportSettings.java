package de.koelle.christian.trickytripper.model;

public class ImportSettings {

    private boolean createNewRateOnValueChange;

    public void setCreateNewRateOnValueChange(boolean replaceImportedRecordWhenAlreadyImported) {
        this.createNewRateOnValueChange = replaceImportedRecordWhenAlreadyImported;
    }

    public boolean isCreateNewRateOnValueChange() {
        return createNewRateOnValueChange;
    }

}
