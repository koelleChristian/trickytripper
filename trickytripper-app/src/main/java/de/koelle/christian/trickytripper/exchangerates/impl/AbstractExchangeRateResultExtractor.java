package de.koelle.christian.trickytripper.exchangerates.impl;

import de.koelle.christian.common.utils.NumberUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AbstractExchangeRateResultExtractor implements
        ExchangeRateResultExtractor {
    private static final String NBS = new String(new char[] { 0xA0 });

    /* to parse strings like: '<div id=currency_converter_result>1 EUR = <span class=bld>1.3591 USD</span>' */
    public static final String REGEX_NUMBER_PATTERN_STRING = "^.*<span class=bld>(\\d+(\\.\\d+)?).*$";
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
