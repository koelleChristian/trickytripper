package de.koelle.christian.trickytripper.model;

import java.io.Serializable;
import java.util.Currency;
import java.util.Date;

import de.koelle.christian.common.utils.NumberUtils;

public class ExchangeRate implements Serializable {

    private static final long serialVersionUID = 2017174474860551532L;

    private long id;
    private Currency currencyFrom;
    private Currency currencyTo;
    private Double exchangeRate;
    private String description;
    private Date updateDate;
    private Date creationDate;
    private ImportOrigin importOrigin;
    private boolean inversion;
    private transient boolean isSelected;

    public boolean isImported() {
        return ImportOrigin.GOOGLE.equals(getImportOrigin());
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public ImportOrigin getImportOrigin() {
        return importOrigin;
    }

    public void setImportOrigin(ImportOrigin importOrigin) {
        this.importOrigin = importOrigin;
    }

    public Currency getCurrencyFrom() {
        return currencyFrom;
    }

    public void setCurrencyFrom(Currency currencyFrom) {
        this.currencyFrom = currencyFrom;
    }

    public Currency getCurrencyTo() {
        return currencyTo;
    }

    public void setCurrencyTo(Currency currencyTo) {
        this.currencyTo = currencyTo;
    }

    public Double getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(Double exchangeRate) {
        this.exchangeRate = exchangeRate;
    }

    @Override
    public String toString() {
        return "ExchangeRate [id=" + id + ", currencyFrom=" + currencyFrom + ", currencyTo=" + currencyTo
                + ", exchangeRate=" + exchangeRate + ", description=" + description + ", updateDate=" + updateDate
                + ", importOrigin=" + importOrigin + "]";
    }

    public String getSortString() {
        return currencyFrom.getCurrencyCode() + currencyTo.getCurrencyCode();
    }

    public Double getInvertedExchangeRate() {
        return NumberUtils.invertExchangeRateDouble(exchangeRate);
    }

    public boolean isInversion() {
        return inversion;
    }

    public void setInversion(boolean inversion) {
        this.inversion = inversion;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public ExchangeRate cloneToInversion() {
        return cloneInternal(true);
    }

    public ExchangeRate doClone() {
        return cloneInternal(false);
    }

    private ExchangeRate cloneInternal(boolean toInversion) {
        ExchangeRate result = new ExchangeRate();
        result.setCurrencyFrom((toInversion) ? getCurrencyTo() : getCurrencyFrom());
        result.setCurrencyTo((toInversion) ? getCurrencyFrom() : getCurrencyTo());
        result.setDescription(getDescription());
        result.setExchangeRate((toInversion) ? getInvertedExchangeRate() : getExchangeRate());
        result.setId(id);
        result.setImportOrigin(getImportOrigin());
        result.setUpdateDate(getUpdateDate());
        result.setInversion((toInversion) ? !isInversion() : isInversion());
        return result;
    }

    public boolean isNew() {
        return id == 0;
    }

    public boolean equalsFromImportPointOfView(ExchangeRate rate) {
        return equalsStraight(rate);
    }

    private boolean equalsStraight(ExchangeRate other) {
        if (currencyFrom == null) {
            if (other.currencyFrom != null)
                return false;
        }
        else if (!currencyFrom.equals(other.currencyFrom))
            return false;
        if (currencyTo == null) {
            if (other.currencyTo != null)
                return false;
        }
        else if (!currencyTo.equals(other.currencyTo))
            return false;
        if (exchangeRate == null) {
            if (other.exchangeRate != null)
                return false;
        }
        else if (!exchangeRate.equals(other.exchangeRate))
            return false;
        return true;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

}
