package de.koelle.christian.trickytripper.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import de.koelle.christian.common.abs.ActionBarSupport;
import de.koelle.christian.common.options.OptionConstraintsInflater;
import de.koelle.christian.trickytripper.R;
import de.koelle.christian.trickytripper.TrickyTripperApp;

/**
 * <p>
 * +++++++++++++++++++++++++++++++++++++++++++++++++++++
 * </p>
 * The original version of this class has been provided by Brad Greco via his
 * homepage: <a href="http://www.bgreco.net/directorypicker/">
 * http://www.bgreco.net/directorypicker/</a>. It is licensed under MIT license.
 * <p/>
 * 
 * <p>
 * +++++++++++++++++++++++++++++++++++++++++++++++++++++
 * </p>
 * Copyright (C) 2011 by Brad Greco <brad@bgreco.net>
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

public class DirectoryPickerActivity extends AppCompatActivity {

    public static final String EXTRA_START_DIR = "startDir";
    public static final String EXTRA_ONLY_DIRS = "onlyDirs";
    public static final String EXTRA_SHOW_HIDDEN = "showHidden";
    public static final String EXTRA_TITLE = "extraTitle";
    public static final String EXTRA_CHOOSE_TXT_PREFIX = "extraChooseText";

    public static final String EXTRA_RESULT_CHOSEN_DIRECTORY = "chosenDir";
    public static final int REQUEST_CODE = 49300;

    private File dir;
    private boolean showHidden = false;
    private boolean onlyDirs = true;
    private String title;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Bundle extras = getIntent().getExtras();
        dir = Environment.getExternalStorageDirectory();
        if (extras != null) {
            String preferredStartDir = extras.getString(EXTRA_START_DIR);
            showHidden = extras.getBoolean(EXTRA_SHOW_HIDDEN, false);
            onlyDirs = extras.getBoolean(EXTRA_ONLY_DIRS, true);
            title = extras.getString(EXTRA_TITLE);
            if (preferredStartDir != null) {
                File startDir = new File(preferredStartDir);
                if (startDir.isDirectory()) {
                    dir = startDir;
                }
            }
        }

        setContentView(R.layout.picker_chooser_list);
        
        setTitle(title);
        
        TextView textViewPath = (TextView) findViewById(R.id.dirPickerPath);
        textViewPath.setText(dir.getAbsolutePath());

        Button btnChoose = (Button) findViewById(R.id.dirPickerButtonChoose);
        String name = dir.getName();
        if (name.length() == 0)
            name = "/";
        btnChoose.setText(extras.getString(EXTRA_CHOOSE_TXT_PREFIX) + " " + "'" + name + "'");
        btnChoose.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                returnDir(dir.getAbsolutePath());
            }
        });

        ListView lv = (ListView) findViewById(android.R.id.list);
        lv.setTextFilterEnabled(true);

        if (!dir.canRead()) {
            Context context = getApplicationContext();
            String msg = getString(R.string.directory_picker_folder_unreadable);
            Toast toast = Toast.makeText(context, msg, Toast.LENGTH_LONG);
            toast.show();
            return;
        }

        final ArrayList<File> files = filter(dir.listFiles(), onlyDirs, showHidden);
        String[] names = names(files);
        lv.setAdapter(new ArrayAdapter<>(this, R.layout.picker_list_item, names));

        lv.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!files.get(position).isDirectory())
                    return;
                String path = files.get(position).getAbsolutePath();
                Intent intent = new Intent(DirectoryPickerActivity.this, DirectoryPickerActivity.class);
                intent.putExtra(DirectoryPickerActivity.EXTRA_START_DIR, path);
                intent.putExtra(DirectoryPickerActivity.EXTRA_SHOW_HIDDEN, showHidden);
                intent.putExtra(DirectoryPickerActivity.EXTRA_ONLY_DIRS, onlyDirs);
                intent.putExtra(DirectoryPickerActivity.EXTRA_TITLE, title);
                intent.putExtra(DirectoryPickerActivity.EXTRA_CHOOSE_TXT_PREFIX,
                        extras.getString(EXTRA_CHOOSE_TXT_PREFIX));
                startActivityForResult(intent, REQUEST_CODE);
            }
        });
        ActionBarSupport.addBackButton(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            String path = (String) extras.get(DirectoryPickerActivity.EXTRA_RESULT_CHOSEN_DIRECTORY);
            returnDir(path);
        }
    }

    private void returnDir(String path) {
        Intent result = new Intent();
        result.putExtra(EXTRA_RESULT_CHOSEN_DIRECTORY, path);
        setResult(RESULT_OK, result);
        finish();
    }

    public ArrayList<File> filter(File[] file_list, boolean onlyDirs, boolean showHidden) {
        ArrayList<File> files = new ArrayList<>();
        for (File file : file_list) {
            if (onlyDirs && !file.isDirectory())
                continue;
            if (!showHidden && file.isHidden())
                continue;
            files.add(file);
        }
        Collections.sort(files);
        return files;
    }

    public String[] names(ArrayList<File> files) {
        String[] names = new String[files.size()];
        int i = 0;
        for (File file : files) {
            names[i] = file.getName();
            i++;
        }
        return names;
    }
    
    private TrickyTripperApp getApp() {
        return (TrickyTripperApp)getApplication();
    }

    /* ============== Options Shit [BGN] ============== */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return getApp().getMiscController().getOptionSupport().populateOptionsMenu(
                new OptionConstraintsInflater().activity(getMenuInflater()).menu(menu)
                        .options(new int[] {
                                R.id.option_help
                        }));
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.option_help:
            getApp().getViewController().openHelp(getSupportFragmentManager());
            return true;
        case android.R.id.home:
            onBackPressed();
            return true;                  
        default:
            return super.onOptionsItemSelected(item);
        }
    }
    /* ============== Options Shit [END] ============== */
    

}
