package de.koelle.christian.trickytripper.exchangerates.impl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

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
//        System.out.println(new File(".").getAbsolutePath());
        if (System.getProperty("os.name").startsWith("Windows")) {
            sample2Expected.put(".\\src\\test\\resources\\de\\koelle\\christian\\trickytripper\\sample_google_http_exchange_rate_response_1.txt", Double.valueOf(0.9308));
            sample2Expected.put(".\\src\\test\\resources\\de\\koelle\\christian\\trickytripper\\sample_google_http_exchange_rate_response_2.txt", Double.valueOf(0.0014));
            sample2Expected.put(".\\src\\test\\resources\\de\\koelle\\christian\\trickytripper\\sample_google_http_exchange_rate_response_3.txt", Double.valueOf(642.2531));
        } else {
            sample2Expected.put("./src/test/resources/de/koelle/christian/trickytripper/sample_google_http_exchange_rate_response_1.txt", Double.valueOf(0.9308));
            sample2Expected.put("./src/test/resources/de/koelle/christian/trickytripper/sample_google_http_exchange_rate_response_2.txt", Double.valueOf(0.0014));
            sample2Expected.put("./src/test/resources/de/koelle/christian/trickytripper/sample_google_http_exchange_rate_response_3.txt", Double.valueOf(642.2531));
        }

    }
//    de/koelle/christian/trickytripper/sample_google_http_exchange_rate_response_1.txt

    @Test
    public void testStringParsing() {
        for (Map.Entry<String, Double> entry : sample2Expected.entrySet()) {
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
