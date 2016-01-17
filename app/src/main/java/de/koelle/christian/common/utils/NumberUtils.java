package de.koelle.christian.common.utils;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import de.koelle.christian.common.primitives.DivisionResult;

public class NumberUtils {

    private static final int DEFAULT_SCALE = 2;
    private static final Double EXCHANGE_RATE_MAX = 9999999999.999;
    private static final Double EXCHANGE_RATE_MIN = 0.0000000001;

    private static final Double AMOUNT_MAX = 9999999999.99;
    private static final Double AMOUNT_MIN = 0.01;

    public static Double neg(Double value) {
        if (value == null) {
            return null;
        }
        return value * -1;
    }

    public static Double getStringToDoubleNonRounded(Locale locale,
                                                     String stringToBeParsed) {
        boolean doRound = false;
        return getStringToDouble(locale, stringToBeParsed, doRound);
    }

    public static Double getStringToDoubleRounded(Locale locale,
            String stringToBeParsed) {
        boolean doRound = true;
        return getStringToDouble(locale, stringToBeParsed, doRound);
    }

    private static Double getStringToDouble(Locale locale,
            String stringToBeParsed, boolean doRound) {
        if (stringToBeParsed == null || stringToBeParsed.length() < 1) {
            return (double) 0;
        }
        String input = stringToBeParsed;

        if (",".equals(stringToBeParsed) || ".".equals(stringToBeParsed)) {
            input = "0";
        }
        NumberFormat nf = NumberFormat.getNumberInstance(locale);
        Number number;
        try {
            number = nf.parse(input);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        double nonRounded = number.doubleValue();
        return (doRound) ? round(nonRounded) : nonRounded;
    }

    public static Double divide(Double dividend, Integer divisor) {
        return divide(dividend, Double.valueOf(divisor + ""));
    }

    public static DivisionResult divideWithLoss(Double dividend,
            Integer divisor, boolean resultToBeNegative) {
        DivisionResult result = new DivisionResult();
        Double dividendHere = Math.abs(dividend);
        Double divisionResult = NumberUtils.divide(dividendHere,
                Integer.valueOf(divisor));
        Double loss = NumberUtils.round(dividendHere
                - NumberUtils.multiply(divisionResult, divisor));
        if (resultToBeNegative) {
            divisionResult = NumberUtils.neg(divisionResult);
            loss = NumberUtils.neg(loss);
        }
        result.setLoss(loss);
        result.setResult(divisionResult);
        return result;
    }

    public static Double multiply(Double factorA, Integer factorB) {
        return multiply(factorA, Double.valueOf(factorB + ""));
    }

    public static Double multiply(Double factorA, Double factorB) {
        if (factorA == null || factorB == null || factorA == 0 || factorB == 0) {
            return (double) 0;
        }
        BigDecimal result;
        BigDecimal a = new BigDecimal(factorA, MathContext.DECIMAL128);
        BigDecimal b = new BigDecimal(factorB, MathContext.DECIMAL128);
        result = a.multiply(b);

        return round(result.doubleValue());
    }

    public static Double divide(Double dividend, Double divisor) {
        return divide(dividend, divisor, DEFAULT_SCALE);
    }

    public static Double divideForExchangeRates(Double dividend, Double divisor) {
        Double double1 =
                (Double.valueOf(0d).equals(divisor)) ?
                        Double.valueOf(0d) :
                        divide(dividend, divisor, 10);
        return ensureExchangeRateMinMax(double1);
    }

    public static Double ensureExchangeRateMinMax(Double double1) {
        return (double1 != null && Double.valueOf(0.0).equals(double1)) ?
                double1 :
                Double.valueOf(Math.max(Math.min(EXCHANGE_RATE_MAX, double1), EXCHANGE_RATE_MIN));
    }

    public static Double ensureAmountMinMax(Double double1) {
        return Math.max(Math.min(EXCHANGE_RATE_MAX, double1), EXCHANGE_RATE_MIN);
    }

    public static boolean isExceedingAmountLimit(Double double1) {
        return double1 < AMOUNT_MIN || double1 > AMOUNT_MAX;
    }

    public static Double invertExchangeRateDouble(Double exchangeRate) {
        return (exchangeRate == null) ? null : divideForExchangeRates(
                1.0, exchangeRate);
    }

    public static Double divide(Double dividend, Double divisor, int scale) {
        if (dividend == null) {
            return null;
        }
        else if (dividend == 0) {
            return (double) 0;
        }
        BigDecimal result;
        BigDecimal dividendBd = new BigDecimal(dividend, MathContext.DECIMAL128);
        dividendBd.setScale(scale, RoundingMode.HALF_EVEN); // TODO(ckoelle)
        BigDecimal divisorBd = new BigDecimal(divisor, MathContext.DECIMAL128);
        divisorBd.setScale(scale, RoundingMode.HALF_EVEN); // TODO(ckoelle)
        result = dividendBd.divide(divisorBd, scale, RoundingMode.HALF_EVEN);
        return Double.valueOf(result.toString());
    }

    public static double round(double nonRounded) {
        return ((double) (Math.round(nonRounded * 100))) / 100;
    }

}
