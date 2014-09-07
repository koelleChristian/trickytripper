package de.koelle.christian.trickytripper.dataaccess;

import java.util.ArrayList;

import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.ContactsContract;
import de.koelle.christian.trickytripper.model.PhoneContact;

public class PhoneContactResolver {

    private final ContentResolver mResolver;

    public PhoneContactResolver(ContentResolver cr) {
        mResolver = cr;
    }

    public ArrayList<PhoneContact> findContactByNameString2(String nameSubstr) {
        String[] projection = { ContactsContract.Data._ID, ContactsContract.Data.DISPLAY_NAME };
        String selection = null;
        String[] selectionArgs = null;
        if (nameSubstr != null) {
            selection = ContactsContract.Data.DISPLAY_NAME + " LIKE ?";
            selectionArgs = new String []{ "%" + nameSubstr + "%" };
        } 
        Cursor phoneCursor = null;
        ArrayList<PhoneContact> contacts = new ArrayList<PhoneContact>();
        try {
            phoneCursor = mResolver.query(ContactsContract.Contacts.CONTENT_URI,
                    projection,
                    selection,
                    selectionArgs,
                    ContactsContract.Data.DISPLAY_NAME);
            int idCol = phoneCursor.getColumnIndex(ContactsContract.Data._ID);
            int nameCol = phoneCursor.getColumnIndex(ContactsContract.Data.DISPLAY_NAME);
            while (phoneCursor.moveToNext()) {
                long id = phoneCursor.getLong(idCol);
                String displayName = phoneCursor.getString(nameCol);
                if (displayName == null || displayName.length() < 2) {
                    continue;
                }
                PhoneContact contact = new PhoneContact();
                contact.displayName = displayName;
                contact.id = String.valueOf(id);
                contacts.add(contact);
            }
        }
        finally {
            if (phoneCursor != null)
                phoneCursor.close();
        }
        return contacts;
    }
}
