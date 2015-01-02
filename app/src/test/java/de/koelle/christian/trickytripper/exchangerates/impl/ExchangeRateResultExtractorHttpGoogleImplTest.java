package de.koelle.christian.trickytripper.exchangerates.impl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;
import org.junit.rules.Timeout;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

public class ExchangeRateResultExtractorHttpGoogleImplTest {


    Map<String, Double> sample2Expected;

    @Before
    public void setup() {
        sample2Expected = new LinkedHashMap<String, Double>();
        sample2Expected.put("C:\\trickytripper\\trickytripper-unittest\\src\\test\\resources\\de\\koelle\\christian\\trickytripper\\exchangerates\\impl\\sample_google_http_exchange_rate_response_1.txt", Double.valueOf(0.3593));
        sample2Expected.put("C:\\trickytripper\\trickytripper-unittest\\src\\test\\resources\\de\\koelle\\christian\\trickytripper\\exchangerates\\impl\\sample_google_http_exchange_rate_response_2.txt", Double.valueOf(2.7832));
        sample2Expected.put("C:\\trickytripper\\trickytripper-unittest\\src\\test\\resources\\de\\koelle\\christian\\trickytripper\\exchangerates\\impl\\sample_google_http_exchange_rate_response_3.txt", Double.valueOf(0.0011));
    }


    @Test
    public void testStringParsing() {
        for(Map.Entry<String, Double> entry : sample2Expected.entrySet()){
            String input = null;
            try {
                input = new Scanner(new File(entry.getKey()), "UTF-8").useDelimiter("\\A").next();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            Assert.assertNotNull(input);
            ExchangeRateResultExtractorHttpGoogleImpl testAspect = new ExchangeRateResultExtractorHttpGoogleImpl();
            Double result = testAspect.extractValue(input);
            Assert.assertNotNull(result);
            Assert.assertEquals(entry.getValue(), result);
        }
    }
}
