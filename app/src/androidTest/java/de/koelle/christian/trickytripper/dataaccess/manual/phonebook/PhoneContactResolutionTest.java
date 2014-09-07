package de.koelle.christian.trickytripper.dataaccess.manual.phonebook;

import java.util.ArrayList;

import android.test.ApplicationTestCase;
import de.koelle.christian.trickytripper.TrickyTripperApp;
import de.koelle.christian.trickytripper.dataaccess.PhoneContactResolver;
import de.koelle.christian.trickytripper.model.PhoneContact;

public class PhoneContactResolutionTest extends ApplicationTestCase<TrickyTripperApp> {

    public PhoneContactResolutionTest() {
        super(TrickyTripperApp.class);
    }

    public void testQuery() {
        PhoneContactResolver resolver = new PhoneContactResolver(getContext().getContentResolver());

        ArrayList<PhoneContact> result = resolver.findContactByNameString2("ch");
        System.out.println(result);
    }

}

