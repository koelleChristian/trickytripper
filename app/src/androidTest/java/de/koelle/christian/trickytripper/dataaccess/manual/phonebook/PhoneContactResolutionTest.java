package de.koelle.christian.trickytripper.dataaccess.manual.phonebook;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SmallTest;

import org.junit.Test;

import java.util.ArrayList;

import de.koelle.christian.trickytripper.dataaccess.PhoneContactResolver;
import de.koelle.christian.trickytripper.model.PhoneContact;

@SmallTest
public class PhoneContactResolutionTest{

    private Context context;

    @Test
    public void testQuery() {
        context = InstrumentationRegistry.getTargetContext();
        PhoneContactResolver resolver = new PhoneContactResolver(context.getContentResolver());

        ArrayList<PhoneContact> result = resolver.findContactByNameString2("ch");
        System.out.println(result);
    }

}

