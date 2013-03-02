package de.koelle.christian.common.utils;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import de.koelle.christian.trickytripper.activitysupport.DivisionResult;

public class NumberUtils {

    private static final int DEFAULT_SCALE = 2;

    public static Double neg(Double value) {
        if (value == null) {
            return null;
        }
        return Double.valueOf(value * -1);
    }

    public static Double getStringToDoubleUnrounded(Locale locale, String stringToBeParsed) {
        boolean doRound = false;
        return getStringToDouble(locale, stringToBeParsed, doRound);
    }

    public static Double getStringToDoubleRounded(Locale locale, String stringToBeParsed) {
        boolean doRound = true;
        return getStringToDouble(locale, stringToBeParsed, doRound);
    }

    private static Double getStringToDouble(Locale locale, String stringToBeParsed, boolean doRound) {
        if (stringToBeParsed == null || stringToBeParsed.length() < 1) {
            return Double.valueOf(0);
        }
        String input = stringToBeParsed;

        if (",".equals(stringToBeParsed) || ".".equals(stringToBeParsed)) {
            input = "0";
        }
        NumberFormat nf = NumberFormat.getNumberInstance(locale);
        Number number;
        try {
            number = nf.parse(input);
        }
        catch (ParseException e) {
            throw new RuntimeException(e);
        }
        double unrounded = number.doubleValue();
        return Double.valueOf((doRound) ? round(unrounded) : unrounded);
    }

    public static Double divide(Double divident, Integer divisor) {
        return divide(divident, Double.valueOf(divisor + ""));
    }

    public static DivisionResult divideWithLoss(Double divident, Integer divisor, boolean resultToBeNegative) {
        DivisionResult result = new DivisionResult();
        Double dividentHere = Math.abs(divident);
        Double divisionResult = NumberUtils.divide(dividentHere, Integer.valueOf(divisor));
        Double loss = NumberUtils.round(dividentHere - NumberUtils.multiply(divisionResult, divisor));
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
        if (factorA == 0 || factorB == 0) {
            return Double.valueOf(0);
        }
        BigDecimal result = null;
        BigDecimal a = new BigDecimal(factorA, MathContext.DECIMAL128);
        BigDecimal b = new BigDecimal(factorB, MathContext.DECIMAL128);
        result = a.multiply(b);

        return round(result.doubleValue());
    }

    public static Double divide(Double divident, Double divisor) {
        return divide(divident, divisor, DEFAULT_SCALE);
    }

    public static Double divideForExchangeRates(Double divident, Double divisor) {
        return (Double.valueOf(0d).equals(divisor)) ?
                Double.valueOf(0d) :
                divide(divident, divisor, divisor.toString().length() - 2);
    }

    public static Double invertExchangeRateDouble(Double exchangeRate) {
        return (exchangeRate == null) ? null : divideForExchangeRates(Double.valueOf(1.0), exchangeRate);
    }

    public static Double divide(Double divident, Double divisor, int scale) {
        if (divident == null) {
            return null;
        }
        else if (divident == 0) {
            return Double.valueOf(0);
        }
        BigDecimal result = null;
        BigDecimal dividentBd = new BigDecimal(divident, MathContext.DECIMAL128);
        dividentBd.setScale(scale, RoundingMode.HALF_EVEN);
        BigDecimal divisorBd = new BigDecimal(divisor, MathContext.DECIMAL128);
        divisorBd.setScale(scale, RoundingMode.HALF_EVEN);
        result = dividentBd.divide(divisorBd, scale, RoundingMode.HALF_EVEN);
        return Double.valueOf(result.toString());
    }

    public static double round(double unrounded) {
        return ((double) (Math.round(unrounded * 100))) / 100;
    }

}
