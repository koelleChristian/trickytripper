package de.koelle.christian.trickytripper.model;

import java.io.Serializable;
import java.util.Currency;

import de.koelle.christian.common.utils.NumberUtils;

public class Amount implements Serializable {

    private static final long serialVersionUID = 7992193936275624979L;

    private Double value = (double) 0;
    private Currency unit = Currency.getInstance("EUR");

    public void addAmount(Amount amountToBeAdded) {
        if (unit != null && unit.equals(amountToBeAdded.unit)) {
            value = NumberUtils.round(value + amountToBeAdded.getValue());
        }
    }

    public void addValue(Double floatValueToBeAdded) {
        value = NumberUtils.round(value + floatValueToBeAdded);
    }

    public Amount doClone() {
        Amount clone;
        clone = new Amount();
        clone.setUnit(this.getUnit());
        clone.setValue((double) this.getValue());
        return clone;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public Currency getUnit() {
        return unit;
    }

    public void setUnit(Currency unit) {
        this.unit = unit;
    }

    @Override
    public String toString() {
        return "Amount [value=" + value + ", currency=" + unit + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((unit == null) ? 0 : unit.hashCode());
        result = prime * result + ((value == null) ? 0 : value.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Amount other = (Amount) obj;
        if (unit == null) {
            if (other.unit != null)
                return false;
        }
        else if (!unit.equals(other.unit))
            return false;
        if (value == null) {
            if (other.value != null)
                return false;
        }
        else if (!value.equals(other.value))
            return false;
        return true;
    }

}
