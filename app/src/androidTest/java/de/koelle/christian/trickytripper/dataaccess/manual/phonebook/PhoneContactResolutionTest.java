package de.koelle.christian.trickytripper.dataaccess.manual.phonebook;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Test;

import java.util.ArrayList;

import de.koelle.christian.trickytripper.dataaccess.PhoneContactResolver;
import de.koelle.christian.trickytripper.model.PhoneContact;

public class PhoneContactResolutionTest {

    @Test
    public void testQuery() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        PhoneContactResolver resolver = new PhoneContactResolver(context.getContentResolver());

        ArrayList<PhoneContact> result = resolver.findContactByNameString2("ch");
        System.out.println(result);
    }

}

