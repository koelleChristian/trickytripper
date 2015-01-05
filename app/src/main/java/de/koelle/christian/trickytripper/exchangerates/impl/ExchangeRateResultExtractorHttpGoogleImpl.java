package de.koelle.christian.trickytripper.exchangerates.impl;

import java.util.regex.Pattern;

public class ExchangeRateResultExtractorHttpGoogleImpl extends
        AbstractExchangeRateResultExtractor {


    /* to parse strings like: '<div id=currency_converter_result>1 EUR = <span class=bld>1.3591 USD</span>' */
    public static final String REGEX_NUMBER_PATTERN_STRING = "^.*<div id=currency_converter_result>.*?<span class=bld>(\\d+(\\.\\d+)?).*?</span>.*$";
    public static final Pattern REGEX_NUMBER_PATTERN = Pattern.compile(REGEX_NUMBER_PATTERN_STRING, Pattern.DOTALL);

    @Override
    Pattern getPattern() {
        return REGEX_NUMBER_PATTERN;
    }
}
