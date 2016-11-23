package de.koelle.christian.trickytripper.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import de.koelle.christian.common.abs.ActionBarSupport;
import de.koelle.christian.trickytripper.R;

public class PreferencesActivity extends AppCompatActivity {

    /*
      The PreferencesActivity does not have ActionBarCompat support.
      Solution is to use an ordinary ActionBarCompat activity. Instead of using the suggested PreferencesFragment
      which would only work v11 + I am using a PreferenceFragment from an external compatibility library.
    */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preferences_frame);
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame,
                new PreferencesFragment()).commit();

        ActionBarSupport.addBackButton(this);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
