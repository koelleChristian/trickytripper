package de.koelle.christian.trickytripper.exchangerates.impl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.koelle.christian.common.utils.NumberUtils;

public class ExchangeRateResultExtractorJsonGoogleImpl implements
        ExchangeRateResultExtractor {
    private static final String NBS = new String(new char[] { 0xA0 });

    /* to parse strings like: '0.618850176 British pounds' or '1 British pounds' */
    public static final String REGEX_NUMBER_PATTERN_STRING = "^(\\d+(\\.\\d+)?).*$";
    public static final Pattern REGEX_NUMBER_PATTERN = Pattern
            .compile(REGEX_NUMBER_PATTERN_STRING);

    public Double extractValue(String exchangeRateString) {
        if (exchangeRateString == null) {
            return null;
        }
        exchangeRateString = clean(exchangeRateString);
        Matcher matcher = REGEX_NUMBER_PATTERN.matcher(exchangeRateString);
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
