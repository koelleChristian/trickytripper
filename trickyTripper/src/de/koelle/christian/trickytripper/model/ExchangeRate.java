package de.koelle.christian.trickytripper.model;

import java.util.Currency;
import java.util.Date;

public class ExchangeRate {

    private long id;
    private Currency currencyFrom;
    private Currency currencyTo;
    private Double exchangeRate;
    private String description;
    private Date updateDate;
    private ImportOrigin importOrigin;

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

}
