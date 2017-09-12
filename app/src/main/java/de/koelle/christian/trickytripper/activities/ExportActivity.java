package de.koelle.christian.trickytripper.activities;

import android.os.Bundle;
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
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.koelle.christian.common.abs.ActionBarSupport;
import de.koelle.christian.common.options.OptionConstraintsInflater;
import de.koelle.christian.common.utils.UiUtils;
import de.koelle.christian.trickytripper.R;
import de.koelle.christian.trickytripper.TrickyTripperApp;
import de.koelle.christian.trickytripper.activitysupport.SpinnerViewSupport;
import de.koelle.christian.trickytripper.constants.Rc;
import de.koelle.christian.trickytripper.controller.ExportController;
import de.koelle.christian.trickytripper.model.ExportSettings;
import de.koelle.christian.trickytripper.model.ExportSettings.ExportOutputChannel;
import de.koelle.christian.trickytripper.model.Participant;
import de.koelle.christian.trickytripper.ui.model.RowObject;

public class ExportActivity extends AppCompatActivity {


    private List<Participant> participantsInSpinner;
    private Participant participantSelected;
    private ExportSettings exportSettings;
    private List<ExportOutputChannel> supportedOutputChannels;
    private boolean exportEnabled;
    private CheckBox checkboxFormatCsv;
    private CheckBox checkboxFormatHtml;
    private CheckBox checkboxFormatTxt;
    private RadioButton radioFormatCsv;
    private RadioButton radioFormatHtml;
    private RadioButton radioFormatTxt;

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
    private void initAndBindOutputChannelSpinner(ExportOutputChannel previousSelection,
                                                 final List<ExportOutputChannel> enabledOnes) {
        final Spinner spinner = (Spinner) findViewById(R.id.exportViewSpinnerChannel);

        int visibility = View.VISIBLE;
        if (enabledOnes.size() == 1) {
            visibility = View.GONE;
            ExportActivity.this.exportSettings.setOutputChannel(enabledOnes.get(0));
        }
        findViewById(R.id.exportViewSpinnerChannelHeadingTableRow).setVisibility(visibility);
        findViewById(R.id.exportViewSpinnerChannelTableRow).setVisibility(visibility);

        if (visibility == View.GONE) {
            return;
        }

        previousSelection = (previousSelection == null) ? enabledOnes.get(0) : previousSelection;
        List<RowObject> spinnerObjects = SpinnerViewSupport.createSpinnerObjects(previousSelection, false,
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
        SpinnerViewSupport.setSelection(spinner, previousSelection, adapter);

        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

            @SuppressWarnings("unchecked")
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (position >= 0) {
                    Object o = spinner.getSelectedItem();
                    ExportOutputChannel spinnerSelection = ((RowObject<ExportOutputChannel>) o).getRowObject();
                    ExportActivity.this.exportSettings.setOutputChannel(spinnerSelection);
                    updateFormatDependingVisibilityAndState();
                    updateExportState();
                }
            }

            public void onNothingSelected(AdapterView<?> parentView) {
                // intentionally blank
            }

        });
    }

    private void bindCheckBoxes() {
        checkboxFormatCsv = (CheckBox) findViewById(R.id.exportViewCheckboxFormatCsv);
        checkboxFormatHtml = (CheckBox) findViewById(R.id.exportViewCheckboxFormatHtml);
        checkboxFormatTxt = (CheckBox) findViewById(R.id.exportViewCheckboxFormatTxt);
        radioFormatCsv = (RadioButton) findViewById(R.id.exportViewRadioButtonFormatCsv);
        radioFormatHtml = (RadioButton) findViewById(R.id.exportViewRadioButtonFormatHtml);
        radioFormatTxt = (RadioButton) findViewById(R.id.exportViewRadioButtonFormatTxt);

        updateFormatDependingVisibilityAndState();

        CheckBox contentContentPayments = (CheckBox) findViewById(R.id.exportViewCheckboxContentPayments);
        CheckBox contentContentTransfers = (CheckBox) findViewById(R.id.exportViewCheckboxContentTransfers);
        CheckBox contentContentSpendingReport = (CheckBox) findViewById(R.id.exportViewCheckboxContentSpendingReport);
        CheckBox contentContentOwingDebts = (CheckBox) findViewById(R.id.exportViewCheckboxContentOwingDebts);
        CheckBox contentSeparateFilesForIndividuals = (CheckBox) findViewById(R.id.exportViewCheckboxSeparateFilesForIndividuals);
        CheckBox contentShowGlobalSumsOnIndividualSpendingReports = (CheckBox) findViewById(R.id.exportViewCheckboxShowTripSumOnIndividualSpendingReport);

        contentContentPayments.setChecked(exportSettings.isExportPayments());
        contentContentTransfers.setChecked(exportSettings.isExportTransfers());
        contentContentSpendingReport.setChecked(exportSettings.isExportSpending());
        contentContentOwingDebts.setChecked(exportSettings.isExportDebts());

        contentSeparateFilesForIndividuals.setChecked(exportSettings.isSeparateFilesForIndividuals());
        contentShowGlobalSumsOnIndividualSpendingReports.setChecked(exportSettings
                .isShowGlobalSumsOnIndividualSpendingReport());

        checkboxFormatCsv.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                exportSettings.setFormatCsv(isChecked);
                updateExportState();
            }
        });

        checkboxFormatHtml.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                exportSettings.setFormatHtml(isChecked);
                updateExportState();
            }
        });

        checkboxFormatTxt.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                exportSettings.setFormatTxt(isChecked);
                updateExportState();
            }
        });
        radioFormatCsv.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                exportSettings.setFormatCsv(isChecked);
                if (isChecked) {
                    radioFormatHtml.setChecked(false);
                    radioFormatTxt.setChecked(false);
                }
                updateExportState();
            }
        });

        radioFormatHtml.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                exportSettings.setFormatHtml(isChecked);
                if (isChecked) {
                    radioFormatCsv.setChecked(false);
                    radioFormatTxt.setChecked(false);
                }
                updateExportState();
            }
        });

        radioFormatTxt.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                exportSettings.setFormatTxt(isChecked);
                if (isChecked) {
                    radioFormatCsv.setChecked(false);
                    radioFormatHtml.setChecked(false);
                }
                updateExportState();
            }
        });


        /*-------------------*/

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

    private void updateFormatDependingVisibilityAndState() {
        boolean supportingMultipleFiles = exportSettings.getOutputChannel().isSupportingMultipleFiles();

        int visibilityCheckbox;
        int visibilityRadio;

        if (supportingMultipleFiles) {
            visibilityCheckbox = View.VISIBLE;
            visibilityRadio = View.GONE;

            checkboxFormatCsv.setChecked(exportSettings.isFormatCsv());
            checkboxFormatHtml.setChecked(exportSettings.isFormatHtml());
            checkboxFormatTxt.setChecked(exportSettings.isFormatTxt());
        } else {
            visibilityCheckbox = View.GONE;
            visibilityRadio = View.VISIBLE;

            ExportController exportController = getApp().getExportController();

            boolean osSupportsOpenCsv = exportController.osSupportsOpenCsv();
            boolean osSupportsOpenHtml = exportController.ossSupportsOpenHtml();
            boolean osSupportsOpenTxt = exportController.osSupportsOpenTxt();


            if(exportSettings.isFormatHtml() && osSupportsOpenHtml ){
                exportSettings.setFormatCsv(false);
                exportSettings.setFormatTxt(false);
            } else if(exportSettings.isFormatCsv() && osSupportsOpenCsv){
                exportSettings.setFormatHtml(false);
                exportSettings.setFormatTxt(false);
            } else if(exportSettings.isFormatTxt() && osSupportsOpenTxt){
                exportSettings.setFormatCsv(false);
                exportSettings.setFormatHtml(false);
            } else {
                exportSettings.setFormatCsv(false);
                exportSettings.setFormatHtml(false);
                exportSettings.setFormatTxt(false);

            }
            radioFormatCsv.setChecked(exportSettings.isFormatCsv());
            radioFormatHtml.setChecked(exportSettings.isFormatHtml());
            radioFormatTxt.setChecked(exportSettings.isFormatTxt());
            radioFormatCsv.setEnabled(osSupportsOpenCsv);
            radioFormatHtml.setEnabled(osSupportsOpenHtml);
            radioFormatTxt.setEnabled(osSupportsOpenTxt);
        }

        checkboxFormatCsv.setVisibility(visibilityCheckbox);
        checkboxFormatHtml.setVisibility(visibilityCheckbox);
        checkboxFormatTxt.setVisibility(visibilityCheckbox);
        radioFormatCsv.setVisibility(visibilityRadio);
        radioFormatHtml.setVisibility(visibilityRadio);
        radioFormatTxt.setVisibility(visibilityRadio);

    }

    private void updateExportState() {
        exportEnabled = deriveEnableButtonStateFromSettings(exportSettings);
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

    private boolean deriveEnableButtonStateFromSettings(ExportSettings exportSettings2) {
        return (
                exportSettings2.isExportDebts()
                        || exportSettings2.isExportPayments()
                        || exportSettings2.isExportTransfers()
                        || exportSettings2.isExportSpending()
        ) && (
                exportSettings2.isFormatTxt()
                        || exportSettings2.isFormatHtml()
                        || exportSettings2.isFormatCsv()
        );
    }

    public void doExport() {
        getApp().getExportController().exportReport(exportSettings, participantSelected, this);
    }

}
