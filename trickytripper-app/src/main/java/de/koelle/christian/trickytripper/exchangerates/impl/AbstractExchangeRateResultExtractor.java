package de.koelle.christian.trickytripper.exchangerates.impl;

import de.koelle.christian.common.utils.NumberUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AbstractExchangeRateResultExtractor implements
        ExchangeRateResultExtractor {
    private static final String NBS = new String(new char[] { 0xA0 });

    abstract Pattern getPattern();

    public Double extractValue(String exchangeRateString) {
        if (exchangeRateString == null) {
            return null;
        }
        exchangeRateString = clean(exchangeRateString);
        Matcher matcher = getPattern().matcher(exchangeRateString);
        Double rate = null;
        if (matcher.matches()) {
            String rateAsString = matcher.group(1);
            try {
                rate = NumberUtils.ensureExchangeRateMinMax(Double
                        .valueOf(rateAsString));
            } catch (RuntimeException e) {
                // intentionally blank;
            }
        }
        return rate;
    }

    private String clean(String exchangeRateString) {
        return exchangeRateString.replace(NBS, "");
    }
}
