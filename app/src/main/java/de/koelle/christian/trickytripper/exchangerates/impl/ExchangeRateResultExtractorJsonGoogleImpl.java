package de.koelle.christian.trickytripper.exchangerates.impl;

import java.util.regex.Pattern;

public class ExchangeRateResultExtractorJsonGoogleImpl extends AbstractExchangeRateResultExtractor {

    /* to parse strings like: '0.618850176 British pounds' or '1 British pounds' */
    public static final String REGEX_NUMBER_PATTERN_STRING = "^(\\d+(\\.\\d+)?).*$";
    public static final Pattern REGEX_NUMBER_PATTERN = Pattern
            .compile(REGEX_NUMBER_PATTERN_STRING);

    @Override
    Pattern getPattern() {
        return REGEX_NUMBER_PATTERN;
    }
}
