package de.koelle.christian.trickytripper.exchangerates.impl;

import java.util.ArrayList;
import java.util.Currency;
import java.util.Date;
import java.util.List;
import java.util.Set;

import de.koelle.christian.common.utils.CurrencyUtil;
import de.koelle.christian.trickytripper.exchangerates.ExchangeRateImporter;
import de.koelle.christian.trickytripper.exchangerates.impl.ExchangeRateImporterResultCallback.ExchangeRateImporterResultState;
import de.koelle.christian.trickytripper.model.ExchangeRate;
import de.koelle.christian.trickytripper.model.ImportOrigin;

public class ExchangeRateImporterImpl implements ExchangeRateImporter {

    private AsyncExchangeRateResolver asyncExchangeRateResolver;
    private ExchangeRateResultExtractor exchangeRateResultExtractor;
    private int chunkSize = 500;
    private int chunkDelay = 2000;
    private boolean stopped;
    

    public void cancelRunningRequests() {
        stopped = true;
        asyncExchangeRateResolver.cancelRunningRequests();
    }

    public void importExchangeRates(Set<Currency> currencies, ExchangeRateImporterResultCallback callback) {
        stopped = false;
        List<FromToCurrencyPair> permutations = new ArrayList<>();
        if (currencies.size() >= 2) {
            Currency[] currencyArray = new Currency[currencies.size()];
            currencies.toArray(currencyArray);

            Currency from;
            Currency to;

            for (int i = 0; i < currencyArray.length - 1; i++) {
                from = currencyArray[i];
                for (int j = i + 1; j < currencyArray.length; j++) {
                    to = currencyArray[j];
                    if (!CurrencyUtil.isAlive(to.getCurrencyCode()) || !CurrencyUtil.isAlive(from.getCurrencyCode())) {
                        callback.deliverResult(new ExchangeRateImporterResultContainer(null, from, to,
                                ExchangeRateImporterResultState.CURRENCY_NOT_ALIVE, null));
                        continue;
                    }
                    permutations.add(new FromToCurrencyPair(from, to));

                }
            }
            sendRequest(callback, permutations);

        }
    }

    private void sendRequest(ExchangeRateImporterResultCallback callback, List<FromToCurrencyPair> permutations) {
        if (permutations.size() <= chunkSize) {
            for (FromToCurrencyPair pair : permutations) {
                if(stopped){
                    return;
                }
                asyncExchangeRateResolver.getExchangeRate(pair.getFrom(), pair.getTo(),
                        new MyAsyncExchangeRateResolverResult(pair.getFrom(), pair.getTo(),
                                exchangeRateResultExtractor, callback));
            }
        }
        else {
            int noOfChunks = permutations.size() / chunkSize;
            if (permutations.size() % chunkSize > 0) {
                noOfChunks++;
            }

            for (int i = 0; i < noOfChunks; i++) {
                int sublistStart = chunkSize * i;
                int sublistEnd = Math.min(sublistStart + chunkSize, permutations.size());
                sendRequest(callback, permutations.subList(sublistStart, sublistEnd));
                idle();
            }

        }
    }

    @SuppressWarnings("static-access")
    private void idle() {
        if (chunkDelay > 0) {
            try {
                Thread.currentThread().sleep(chunkDelay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    private static final class MyAsyncExchangeRateResolverResult implements
            AsyncExchangeRateResolverResultCallback {

        private final Currency from;
        private final Currency to;
        private final ExchangeRateResultExtractor exchangeRateResultExtractor;
        private final ExchangeRateImporterResultCallback exchangeRateImporterCallback;

        private MyAsyncExchangeRateResolverResult(final Currency from, final Currency to,
                                                  final ExchangeRateResultExtractor exchangeRateResultExtractor,
                                                  ExchangeRateImporterResultCallback exchangeRateImporterCallback) {
            super();
            this.from = from;
            this.to = to;
            this.exchangeRateResultExtractor = exchangeRateResultExtractor;
            this.exchangeRateImporterCallback = exchangeRateImporterCallback;
        }

        public void deliverResult(String result) {
            if (result == null || result.length() <= 0) {
                exchangeRateImporterCallback.deliverResult(new ExchangeRateImporterResultContainer(null, from, to,
                        ExchangeRateImporterResultState.TECHNICAL_ERROR, null));
            }
            Double rate = exchangeRateResultExtractor.extractValue(result);
            if (rate == null) {
                String msg = "Could not extract exchange rate from retrieved json result. from="
                        + from
                        + " to=" + to + " exchangeRate retrieved >" +
                        result + "<";
                exchangeRateImporterCallback.deliverResult(new ExchangeRateImporterResultContainer(null, from, to,
                        ExchangeRateImporterResultState.NON_PARSABLE_JSON_RESULT, msg));
            }
            else {
                ExchangeRate resultRecord = assembleResult(from, to, rate);
                exchangeRateImporterCallback.deliverResult(new ExchangeRateImporterResultContainer(resultRecord, from,
                        to, ExchangeRateImporterResultState.SUCCESS, null));
            }

        }

        private ExchangeRate assembleResult(Currency from, Currency to, Double rate) {
            ExchangeRate result;
            result = new ExchangeRate();
            result.setCurrencyFrom(from);
            result.setCurrencyTo(to);
            result.setDescription(null);
            result.setExchangeRate(rate);
            result.setImportOrigin(ImportOrigin.GOOGLE);
            result.setUpdateDate(new Date());
            result.setCreationDate(result.getUpdateDate());
            return result;
        }
    }

    private static final class FromToCurrencyPair {
        private final Currency from;
        private final Currency to;

        private FromToCurrencyPair(Currency from, Currency to) {
            super();
            this.from = from;
            this.to = to;
        }

        public Currency getFrom() {
            return from;
        }

        public Currency getTo() {
            return to;
        }

    }

    /* ============ setter for injection =========== */

    public void setAsyncExchangeRateResolver(AsyncExchangeRateResolver asyncExchangeRateResolver) {
        this.asyncExchangeRateResolver = asyncExchangeRateResolver;
    }

    public void setExchangeRateResultExtractor(ExchangeRateResultExtractor exchangeRateResultExtractor) {
        this.exchangeRateResultExtractor = exchangeRateResultExtractor;
    }

    public void setChunkSize(int chunkSize) {
        this.chunkSize = chunkSize;
    }

    public void setChunkDelay(int chunkDelay) {
        this.chunkDelay = chunkDelay;
    }

}
