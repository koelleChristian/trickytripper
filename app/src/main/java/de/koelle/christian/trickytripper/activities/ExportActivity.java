package de.koelle.christian.trickytripper.activities;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import de.koelle.christian.common.abs.ActionBarSupport;
import de.koelle.christian.common.options.OptionConstraintsInflater;
import de.koelle.christian.common.utils.UiUtils;
import de.koelle.christian.trickytripper.R;
import de.koelle.christian.trickytripper.TrickyTripperApp;
import de.koelle.christian.trickytripper.activitysupport.SpinnerViewSupport;
import de.koelle.christian.trickytripper.constants.Rc;
import de.koelle.christian.trickytripper.model.ExportSettings;
import de.koelle.christian.trickytripper.model.ExportSettings.ExportOutputChannel;
import de.koelle.christian.trickytripper.model.Participant;
import de.koelle.christian.trickytripper.ui.model.RowObject;

public class ExportActivity extends AppCompatActivity {

    public static final String SYSTEM_PERMISSION = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 88;

    private List<Participant> participantsInSpinner;
    private Participant participantSelected;
    private ExportSettings exportSettings;
    private List<ExportOutputChannel> supportedOutputChannels;
    private boolean exportEnabled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.export_view);
        initPanel();
        ActionBarSupport.addBackButton(this);
        getSupportActionBar().setTitle(this.getTitle() + ": " + getApp().getTripController().getTripLoaded().getName());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return getApp().getMiscController().getOptionSupport().populateOptionsMenu(
                new OptionConstraintsInflater().activity(getMenuInflater()).menu(menu)
                        .options(new int[]{
                                R.id.option_upload,
                                R.id.option_help
                        }));
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.option_upload).setEnabled(exportEnabled);
        menu.findItem(R.id.option_upload).getIcon().setAlpha((exportEnabled) ? 255 : 64);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.option_help:
                getApp().getViewController().openHelp(getSupportFragmentManager());
                return true;
            case R.id.option_upload:
                doExport();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private TrickyTripperApp getApp() {
        return (TrickyTripperApp) getApplication();
    }

    private void initPanel() {
        final TrickyTripperApp app = getApp();
        exportSettings = app.getExportController().getDefaultExportSettings();
        initAndBindSpinner(app);
        supportedOutputChannels = app.getExportController().getEnabledExportOutputChannel();
        initAndBindOutputChannelSpinner(exportSettings.getOutputChannel(), supportedOutputChannels);
        bindCheckBoxes();
        updateAllCheckboxStates();
        updateExportState();
    }

    private void initAndBindSpinner(final TrickyTripperApp app) {
        participantsInSpinner = new ArrayList<Participant>();
        participantsInSpinner.add(null);
        participantsInSpinner.addAll(app.getTripController().getAllParticipants(false, true));

        Spinner spinner = (Spinner) findViewById(R.id.reportViewBaseSpinner);
        SpinnerViewSupport.configureReportSelectionSpinner(this, participantsInSpinner, spinner);

        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                participantSelected = participantsInSpinner.get(position);
                if (Rc.debugOn) {
                    Log.d(Rc.LT_INPUT,
                            "selected=" + ((participantSelected == null) ? null : participantSelected.getName()));
                }
                updateAllCheckboxStates();
            }

            public void onNothingSelected(AdapterView<?> parentView) {
                // intentionally blank
            }

        });
    }

    @SuppressWarnings("rawtypes")
    private void initAndBindOutputChannelSpinner(ExportOutputChannel selection,
                                                 final List<ExportOutputChannel> enabledOnes) {
        final Spinner spinner = (Spinner) findViewById(R.id.exportViewSpinnerChannel);

        List<RowObject> spinnerObjects = SpinnerViewSupport.createSpinnerObjects(selection, false,
                null, getResources(), getApp().getMiscController().getDefaultStringCollator());
        ArrayAdapter<RowObject> adapter = new ArrayAdapter<RowObject>(this, android.R.layout.simple_spinner_item,
                spinnerObjects) {
            @Override
            public boolean isEnabled(int position) {
                return enabledOnes.contains(this.getItem(position).getRowObject());
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView result = (TextView) super.getView(position, convertView, parent);
                int resid = result.getTextColors().getDefaultColor();
                UiUtils.setActiveOrInactive(isEnabled(position), result, R.string.exportViewSpinnerNotAvailable,
                        getResources(), resid);
                return result;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                TextView result = (TextView) super.getDropDownView(position, convertView, parent);
                UiUtils.setActiveOrInactive(isEnabled(position), result, R.string.exportViewSpinnerNotAvailable,
                        getResources(), getResources().getColor(R.color.black));
                return result;
            }

        };
        adapter.setDropDownViewResource(R.layout.selection_list_medium);
        spinner.setPromptId(R.string.exportViewSpinnerPromptChannel);
        spinner.setAdapter(adapter);
        SpinnerViewSupport.setSelection(spinner, selection, adapter);

        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

            @SuppressWarnings("unchecked")
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (position >= 0) {
                    Object o = spinner.getSelectedItem();
                    ExportOutputChannel spinnerSelection = ((RowObject<ExportOutputChannel>) o).getRowObject();
                    ExportActivity.this.exportSettings.setOutputChannel(spinnerSelection);
                    updateExportState();
                }
            }

            public void onNothingSelected(AdapterView<?> parentView) {
                // intentionally blank
            }

        });
    }

    private void bindCheckBoxes() {
        CheckBox contentContentPayments = (CheckBox) findViewById(R.id.exportViewCheckboxContentPayments);
        CheckBox contentContentTransfers = (CheckBox) findViewById(R.id.exportViewCheckboxContentTransfers);
        CheckBox contentContentSpendingReport = (CheckBox) findViewById(R.id.exportViewCheckboxContentSpendingReport);
        CheckBox contentContentOwingDebts = (CheckBox) findViewById(R.id.exportViewCheckboxContentOwingDebts);
        CheckBox contentFormatCsv = (CheckBox) findViewById(R.id.exportViewCheckboxFormatCsv);
        CheckBox contentFormatHtml = (CheckBox) findViewById(R.id.exportViewCheckboxFormatHtml);
        CheckBox contentFormatTxt = (CheckBox) findViewById(R.id.exportViewCheckboxFormatTxt);
        CheckBox contentSeparateFilesForIndividuals = (CheckBox) findViewById(R.id.exportViewCheckboxSeparateFilesForIndividuals);
        CheckBox contentShowGlobalSumsOnIndividualSpendingReports = (CheckBox) findViewById(R.id.exportViewCheckboxShowTripSumOnIndividualSpendingReport);

        contentContentPayments.setChecked(exportSettings.isExportPayments());
        contentContentTransfers.setChecked(exportSettings.isExportTransfers());
        contentContentSpendingReport.setChecked(exportSettings.isExportSpending());
        contentContentOwingDebts.setChecked(exportSettings.isExportDebts());
        contentFormatCsv.setChecked(exportSettings.isFormatCsv());
        contentFormatHtml.setChecked(exportSettings.isFormatHtml());
        contentFormatTxt.setChecked(exportSettings.isFormatTxt());
        contentSeparateFilesForIndividuals.setChecked(exportSettings.isSeparateFilesForIndividuals());
        contentShowGlobalSumsOnIndividualSpendingReports.setChecked(exportSettings
                .isShowGlobalSumsOnIndividualSpendingReport());

        contentContentPayments.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                exportSettings.setExportPayments(isChecked);
                updateExportState();
            }
        });
        contentContentTransfers.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                exportSettings.setExportTransfers(isChecked);
                updateExportState();
            }
        });

        contentContentSpendingReport.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                exportSettings.setExportSpending(isChecked);
                updateExportState();
                updateCheckboxStateShowGlobalSumsOnIndividualSpending();
            }
        });

        contentContentOwingDebts.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                exportSettings.setExportDebts(isChecked);
                updateExportState();
            }
        });

        contentFormatCsv.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                exportSettings.setFormatCsv(isChecked);
                updateExportState();
            }
        });

        contentFormatHtml.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                exportSettings.setFormatHtml(isChecked);
                updateExportState();
            }
        });

        contentFormatTxt.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                exportSettings.setFormatTxt(isChecked);
                updateExportState();
            }
        });
        contentSeparateFilesForIndividuals.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                exportSettings.setSeparateFilesForIndividuals(isChecked);
                updateCheckboxStateShowGlobalSumsOnIndividualSpending();
            }
        });

        contentShowGlobalSumsOnIndividualSpendingReports
                .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        exportSettings.setShowGlobalSumsOnIndividualSpendingReport(isChecked);
                    }
                });

    }

    private void updateExportState() {
        exportEnabled = deriveEnableButtonStateFromSettings(exportSettings, supportedOutputChannels);
        supportInvalidateOptionsMenu();
    }

    private void updateAllCheckboxStates() {
        updateCheckboxStateIndividualFiles();
        updateCheckboxStateShowGlobalSumsOnIndividualSpending();
    }

    private void updateCheckboxStateIndividualFiles() {
        CheckBox checkbox = (CheckBox) findViewById(R.id.exportViewCheckboxSeparateFilesForIndividuals);
        boolean enabledToBe = deriveEnabledCheckboxStateFromSettings(participantSelected, exportSettings, true);
        checkbox.setEnabled(enabledToBe);
    }

    private void updateCheckboxStateShowGlobalSumsOnIndividualSpending() {
        CheckBox checkbox = (CheckBox) findViewById(R.id.exportViewCheckboxShowTripSumOnIndividualSpendingReport);
        boolean enabledToBe = deriveEnabledCheckboxStateFromSettings(participantSelected, exportSettings, false);
        checkbox.setEnabled(enabledToBe);
    }

    private boolean deriveEnabledCheckboxStateFromSettings(Participant participantSelected2,
                                                           ExportSettings exportSettings2,
                                                           boolean individualFilesNotHideSums) {
        if (individualFilesNotHideSums) {
            return participantSelected2 == null;
        } else {
            return exportSettings2.isExportSpending() &&
                    (participantSelected2 != null
                            || (participantSelected2 == null && exportSettings2.isSeparateFilesForIndividuals()));
        }
    }

    private boolean deriveEnableButtonStateFromSettings(ExportSettings exportSettings2,
                                                        List<ExportOutputChannel> supportedOutputChannels2) {
        return (
                exportSettings2.isExportDebts()
                        || exportSettings2.isExportPayments()
                        || exportSettings2.isExportTransfers()
                        || exportSettings2.isExportSpending()
        ) && (
                exportSettings2.isFormatTxt()
                        || exportSettings2.isFormatHtml()
                        || exportSettings2.isFormatCsv()
        ) && (
                supportedOutputChannels2.contains(exportSettings2.getOutputChannel())
        );
    }

    public void doExport() {
        if (ExportOutputChannel.SD_CARD.equals(exportSettings.getOutputChannel()) && !isSdCardPermissionGranted()) {
            requestSdCardPermissions();
        } else {
        /* participant selected is null, if nobody is selected. */
        /*
         * Files will be deleted on application's termination as usually files
         * have not be sent on resume here.
         */
            getApp().getExportController().exportReport(exportSettings, participantSelected, this);
        }

    }

    private boolean isSdCardPermissionGranted() {
        Activity thisActivity = this;
        return ContextCompat.checkSelfPermission(thisActivity, SYSTEM_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED;
    }

    boolean permPopShown = false;

    private void requestSdCardPermissions() {
        Activity thisActivity = this;
        if (!isSdCardPermissionGranted()) {
            doRequestPhonebookPermissions();
        }
    }


    private void doRequestPhonebookPermissions() {
        // The OS popup will show up, unless 'don't ask again' had been choosen.
        Activity thisActivity = this;
        ActivityCompat.requestPermissions(thisActivity,
                new String[]{SYSTEM_PERMISSION},
                MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                // This will be called, even when 'don't ask again' has been choosen and no popup appears.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    doExport();
                } else if (!permPopShown) {
                    Toast.makeText(this, R.string.permission_write_ext_storage_permanently_revoked, Toast.LENGTH_LONG).show();
                }
                permPopShown = false;
            }
        }
    }
}
