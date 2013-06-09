package de.koelle.christian.trickytripper.ui.model;

public class RowObject<T> {
    private String stringToDisplay;
    private T rowObject;
    private RowObjectCallback<T> callback;

    public RowObject() {

    }

    public RowObject(RowObjectCallback<T> callback, T rowObject) {
        this.callback = callback;
        this.rowObject = rowObject;
    }

    public String getStringToDisplay() {
        return stringToDisplay;
    }

    public void setStringToDisplay(String stringToDisplay) {
        this.stringToDisplay = stringToDisplay;
    }

    public T getRowObject() {
        return rowObject;
    }

    public void setRowObject(T rowObject) {
        this.rowObject = rowObject;
    }

    @Override
    public String toString() {
        return (callback != null) ? callback.getStringToDisplay(rowObject) : stringToDisplay;
    }
}
