package de.koelle.christian.trickytripper.exchangerates.impl;



public interface ExchangeRateImporterResultCallback {

    public enum ExchangeRateImporterResultState {
        /**
         * 
         */
        SUCCESS,
        /**
         * 
         */
        CURRENCY_NOT_ALIVE,
        /**
         * 
         */
        NON_PARSABLE_JSON_RESULT,
        /**
         * 
         */
        TECHNICAL_ERROR,
        /**/
        ;
    }

    void deliverResult(ExchangeRateImporterResultContainer resultContainer);
}
