package de.koelle.christian.trickytripper.activities;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnShowListener;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import de.koelle.christian.common.abs.ActionBarSupport;
import de.koelle.christian.common.options.OptionContraintsAbs;
import de.koelle.christian.common.ui.filter.DecimalNumberInputUtil;
import de.koelle.christian.common.utils.NumberUtils;
import de.koelle.christian.common.utils.ObjectUtils;
import de.koelle.christian.common.utils.StringUtils;
import de.koelle.christian.common.utils.UiUtils;
import de.koelle.christian.trickytripper.R;
import de.koelle.christian.trickytripper.TrickyTripperApp;
import de.koelle.christian.trickytripper.activitysupport.CurrencyCalculatorResultSupport;
import de.koelle.christian.trickytripper.activitysupport.DivisionResult;
import de.koelle.christian.trickytripper.activitysupport.MathUtils;
import de.koelle.christian.trickytripper.activitysupport.PaymentEditActivityState;
import de.koelle.christian.trickytripper.activitysupport.PopupFactory;
import de.koelle.christian.trickytripper.activitysupport.SpinnerViewSupport;
import de.koelle.christian.trickytripper.constants.Rc;
import de.koelle.christian.trickytripper.constants.Rd;
import de.koelle.christian.trickytripper.constants.Rx;
import de.koelle.christian.trickytripper.constants.ViewMode;
import de.koelle.christian.trickytripper.controller.TripController;
import de.koelle.christian.trickytripper.factories.AmountFactory;
import de.koelle.christian.trickytripper.model.Amount;
import de.koelle.christian.trickytripper.model.Participant;
import de.koelle.christian.trickytripper.model.Payment;
import de.koelle.christian.trickytripper.model.PaymentCategory;
import de.koelle.christian.trickytripper.modelutils.AmountViewUtils;
import de.koelle.christian.trickytripper.ui.model.RowObject;
import de.koelle.christian.trickytripper.ui.model.RowObjectCallback;
import de.koelle.christian.trickytripper.ui.utils.UiAmountViewUtils;

public class PaymentEditActivity extends SherlockActivity {

    private static final int DIALOG_SELECT_PAYERS = 1;
    private static final int DIALOG_SELECT_DEBITORS = 2;

    private static final String DIALOG_PARAM_PARTICIPANTS_IN_USE = "dialogParamMapInUse";
    private static final String DIALOG_PARAM_TOTAL_PAYMENT_AMOUNT = "dialogParamTotalPaymentAmount";
    private static final String DIALOG_PARAM_IS_PAYMENT = "dialogParamIsPayment";

    private ViewMode viewMode;

    private Payment payment;
    private boolean divideEqually;

    private Amount amountTotalPayments;
    private Amount amountTotalDebits;

    private boolean selectParticipantMakesSense = false;
    private final boolean spendingInputInitialized = false;

    private List<Participant> allRelevantParticipants;

    private final List<View> paymentRows = new ArrayList<View>();
    private final List<View> debitRows = new ArrayList<View>();

    private final Map<Participant, EditText> amountPayedParticipantToWidget = new HashMap<Participant, EditText>();
    private final Map<Participant, EditText> amountDebitorParticipantToWidget = new HashMap<Participant, EditText>();

    @Override
    public Object onRetainNonConfigurationInstance() {
        return new PaymentEditActivityState(payment, divideEqually, spendingInputInitialized);
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_edit_view);

        viewMode = (ViewMode) getIntent().getExtras().get(Rc.ACTIVITY_PARAM_KEY_VIEW_MODE);

        if (ViewMode.CREATE.equals(viewMode)) {
            long participantId = getIntent().getLongExtra(Rc.ACTIVITY_PARAM_KEY_PARTICIPANT_ID, -1L);
            payment = getFktnController().prepareNewPayment(participantId);
            allRelevantParticipants = getApp().getTripController().getAllParticipants(true);
            sortParticipants(allRelevantParticipants);
            for (Participant p : allRelevantParticipants) {
                payment.getParticipantToSpending().put(p, getAmountFac().createAmount());
            }
            setTitle(R.string.payment_view_heading_create_payment);
            ((Button) findViewById(R.id.paymentView_buttonCreate)).setVisibility(ViewGroup.VISIBLE);
            ((Button) findViewById(R.id.paymentView_buttonSave)).setVisibility(ViewGroup.GONE);
            divideEqually = true;
        }
        else {
            long paymentId = getIntent().getLongExtra(Rc.ACTIVITY_PARAM_KEY_PAYMENT_ID, -1L);
            payment = ObjectUtils.cloneDeep(getFktnController().loadPayment(paymentId));
            allRelevantParticipants = addAncientInactive(getApp().getTripController().getAllParticipants(true), payment);
            sortParticipants(allRelevantParticipants);
            setTitle(R.string.payment_view_heading_edit_payment);
            ((Button) findViewById(R.id.paymentView_buttonCreate)).setVisibility(ViewGroup.GONE);
            ((Button) findViewById(R.id.paymentView_buttonSave)).setVisibility(ViewGroup.VISIBLE);
            divideEqually = false;
        }

        Object o = getLastNonConfigurationInstance();
        if (o != null) {
            PaymentEditActivityState state = (PaymentEditActivityState) o;
            payment = state.getPayment();
            divideEqually = state.isDivideEqually();
        }

        selectParticipantMakesSense = allRelevantParticipants.size() > 1;
        setViewVisibility(R.id.paymentView_button_add_further_payees, selectParticipantMakesSense);

        initAndBindSpinner(payment.getCategory());
        bindPlainTextInput();
        bindParticipantSelectionButtons();
        addRadioListener();

        buildDebitorInput();
        buildPaymentInput();

        setVisibilitySpendingTable(!divideEqually);
        
        ActionBarSupport.addBackButton(this);
    }

    private void sortParticipants(List<Participant> allRelevantParticipants2) {
        final Collator collator = getApp().getMiscController().getDefaultStringCollator();
        Collections.sort(allRelevantParticipants2, new Comparator<Participant>() {
            public int compare(Participant object1, Participant object2) {
                return collator.compare(object1.getName(), object2.getName());
            }

        });
    }

    private List<Participant> addAncientInactive(List<Participant> allParticipants, Payment payment2) {
        List<Participant> result = new ArrayList<Participant>(allParticipants);
        Set<Entry<Participant, Amount>> entrySet;
        entrySet = payment2.getParticipantToPayment().entrySet();
        addInactiveOnes(result, entrySet);
        entrySet = payment2.getParticipantToSpending().entrySet();
        addInactiveOnes(result, entrySet);
        return result;
    }

    private void addInactiveOnes(List<Participant> result, Set<Entry<Participant, Amount>> entrySet) {
        for (Entry<Participant, Amount> entry : entrySet) {
            Participant p = entry.getKey();
            if (!p.isActive() && !result.contains(p)) {
                result.add(p);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return getApp().getMiscController().getOptionSupport().populateOptionsMenu(
                new OptionContraintsAbs().activity(getSupportMenuInflater()).menu(menu)
                        .options(new int[] {
                                R.id.option_help
                        }));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.option_help:
            showDialog(Rd.DIALOG_HELP);
            return true;
        case android.R.id.home:
            onBackPressed();
            return true;              
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected Dialog onCreateDialog(int id, Bundle args) {
        Dialog dialog;
        switch (id) {
        case DIALOG_SELECT_PAYERS:
            dialog = createDialogPayerSelection();
            break;
        case DIALOG_SELECT_DEBITORS:
            dialog = createDialogDebitorSelection();
            break;
        case Rd.DIALOG_HELP:
            dialog = PopupFactory.createHelpDialog(this, getApp().getMiscController(), Rd.DIALOG_HELP);
            break;
            
        default:
            dialog = null;
        }

        return dialog;
    }

    @Override
    protected void onPrepareDialog(int id, Dialog dialog, Bundle args) {
        switch (id) {
        case DIALOG_SELECT_PAYERS:
            updateParticipantSelectionDialog(dialog, args);
            break;
        case DIALOG_SELECT_DEBITORS:
            updateParticipantSelectionDialog(dialog, args);
            break;
        case Rd.DIALOG_HELP:
            // intentionally blank
            break;
        default:
            dialog = null;
        }
        super.onPrepareDialog(id, dialog, args);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        CurrencyCalculatorResultSupport.onActivityResult(requestCode, resultCode, data, this, getLocale(),
                getDecimalNumberInputUtil());
    }

    private void updateParticipantSelectionDialog(Dialog dialog, Bundle args) {
        ListView listView = (ListView) dialog.findViewById(R.id.payment_edit_selection_dialog_list_view);
        CheckBox checkbox = (CheckBox) dialog.findViewById(R.id.payment_edit_selection_dialog_checkbox);

        List<Participant> participantsInUse = getParamFromBundleParticipantsInUse(args);
        Amount totalAmount = getParamFromBundleAmount(args);

        if (isAmountBiggerZero(totalAmount)) {
            checkbox.setVisibility(View.VISIBLE);
            boolean isPayment = getParamFromBundleIsPayment(args);
            checkbox.setText(getDivisionCheckboxOnParticipantSelectionText(totalAmount));
            checkbox.setChecked(isPayment);
        }
        else {
            checkbox.setVisibility(View.GONE);
        }
        SparseBooleanArray selection = listView.getCheckedItemPositions();
        for (int i = 0; i < allRelevantParticipants.size(); i++) {
            boolean selected = participantsInUse.contains(allRelevantParticipants.get(i));
            selection.put(i, selected);
        }
    }

    private void buildPaymentInput() {
        TableLayout tableLayout = (TableLayout) findViewById(R.id.paymentView_createPaymentPayerTableLayout);

        Map<Participant, Amount> amountMap = payment.getParticipantToPayment();
        Map<Participant, EditText> widgetMap = amountPayedParticipantToWidget;
        List<View> rowHolder = paymentRows;

        refreshRows(tableLayout, amountMap, widgetMap, rowHolder, true);

        updatePayerSum();
    }

    private void buildDebitorInput() {
        TableLayout tableLayout = (TableLayout) findViewById(R.id.paymentView_createSpendingTableLayout);

        Map<Participant, Amount> amountMap = payment.getParticipantToSpending();
        Map<Participant, EditText> widgetMap = amountDebitorParticipantToWidget;
        List<View> rowHolder = debitRows;

        refreshRows(tableLayout, amountMap, widgetMap, rowHolder, false);

        setViewVisibility(R.id.paymentView_button_payee_add_further_payees, selectParticipantMakesSense);

        updateSpentSum();

    }

    private void refreshRows(TableLayout tableLayout, Map<Participant, Amount> amountMap,
            Map<Participant, EditText> widgetMap, List<View> previousRows, final boolean isPayment) {
        removePreviouslyCreatedRows(tableLayout, previousRows);
        previousRows.clear();
        widgetMap.clear();

        TableRow row;
        Participant p;
        Amount amount;

        int dynViewId = (isPayment) ? Rx.DYN_ID_PAYMENT_EDIT_PAYER : Rx.DYN_ID_PAYMENT_EDIT_DEBITED_TO;

        for (int i = 0; i < allRelevantParticipants.size(); i++) {
            p = allRelevantParticipants.get(i);
            if (!amountMap.containsKey(p)) {
                continue;
            }

            row = (TableRow) inflate(R.layout.payment_edit_payer_row_view);
            amount = amountMap.get(p);

            EditText editText = (EditText) row.findViewById(R.id.payment_edit_payer_row_view_input_amount);
            editText.setId(dynViewId);
            TextView textView = (TextView) row.findViewById(R.id.payment_edit_payer_row_view_output_name);

            Button buttonCurrency = (Button) row.findViewById(R.id.payment_edit_payer_row_view_button_currency);
            buttonCurrency.setText(getFktnController().getLodadedTripCurrencySymbol(false));

            UiUtils.makeProperNumberInput(editText, getDecimalNumberInputUtil().getInputPatternMatcher());
            UiAmountViewUtils.writeAmountToEditText(amount, editText, getLocale(), getDecimalNumberInputUtil());

            textView.setText(p.getName());

            bindAmountInput(editText, amount, isPayment);
            bindCurrencyCalculatorAction(buttonCurrency, amount, editText.getId());

            widgetMap.put(p, editText);
            previousRows.add(row);

            tableLayout.addView(row, tableLayout.getChildCount());

            dynViewId++;

        }
    }

    private void bindCurrencyCalculatorAction(final Button buttonCurrency, final Amount sourceAndTargetAmountReference,
            final int viewIdForResult) {
        buttonCurrency.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getApp().getViewController().openMoneyCalculatorView(sourceAndTargetAmountReference, viewIdForResult,
                        PaymentEditActivity.this);
            }
        });
    }

    private void removePreviouslyCreatedRows(TableLayout tableLayout, List<View> payerRows2) {
        for (View v : payerRows2) {
            tableLayout.removeView(v);
        }
    }

    private View inflate(int layoutId) {
        LayoutInflater inflater = getLayoutInflater();
        final View viewInf = inflater.inflate(layoutId, null);
        return viewInf;
    }

    /**
     * View method.
     * 
     * TODO(ckoelle) Remove?
     * 
     * @param view
     *            Required parameter.
     */
    public void notPartOfThisRelease(View view) {
        Toast.makeText(
                getApplicationContext(),
                R.string.common_toast_currency_not_part_of_this_release,
                Toast.LENGTH_SHORT).show();
    }

    private void addRadioListener() {
        int idToEnable = (divideEqually) ?
                R.id.paymentView_radioTravellersChargedSplitEvenly :
                R.id.paymentView_radioTravellersChargedCustom;

        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.paymentView_radioGroupTravelllersCharged);
        RadioButton radioButton = (RadioButton) findViewById(idToEnable);
        radioButton.setChecked(true);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.paymentView_radioTravellersChargedSplitEvenly) {
                    divideEqually = true;
                    setVisibilitySpendingTable(false);
                    updateSaveButtonState();
                    updateDivideRestButtonState();
                }
                else if (checkedId == R.id.paymentView_radioTravellersChargedCustom) {
                    divideEqually = false;
                    setVisibilitySpendingTable(true);
                    updateSaveButtonState();
                    updateDivideRestButtonState();
                }
            }

        });

    }

    private void setVisibilitySpendingTable(boolean visible) {
        TableLayout spendingTable = (TableLayout) findViewById(R.id.paymentView_createSpendingTableLayout);
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.payment_edit_view_total_spending_sum_layout);

        int visibility = (visible) ? View.VISIBLE : View.GONE;
        spendingTable.setVisibility(visibility);
        relativeLayout.setVisibility(visibility);
    }

    private void setViewVisibility(int viewId, boolean visible) {
        View view = findViewById(viewId);
        UiUtils.setViewVisibility(view, visible);
    }

    private void bindParticipantSelectionButtons() {
        ImageButton button;

        button = (ImageButton) findViewById(R.id.paymentView_button_add_further_payees);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ArrayList<Participant> participantsInUse = new ArrayList<Participant>(payment
                        .getParticipantToPayment().keySet());
                showDialog(DIALOG_SELECT_PAYERS,
                        createBundleForParticipantSelection(participantsInUse, amountTotalPayments, true));
            }
        });
        button = (ImageButton) findViewById(R.id.paymentView_button_payee_add_further_payees);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ArrayList<Participant> participantsInUse = new ArrayList<Participant>(payment
                        .getParticipantToSpending().keySet());
                showDialog(DIALOG_SELECT_DEBITORS,
                        createBundleForParticipantSelection(participantsInUse, amountTotalPayments, false));
            }
        });

    }

    /**
     * View method.
     * 
     * @param view
     *            Required parameter.
     */
    public void saveEdit(View view) {
        Amount amountTotal = calculateTotalSumPayer();

        if (amountTotal.getValue() <= 0) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle(R.string.payment_view_msg_cannot_save_title);
            dialog.setMessage(R.string.payment_view_msg_cannot_save_msg);
            dialog.setIcon(android.R.drawable.ic_dialog_alert);
            dialog.setPositiveButton(getString(android.R.string.ok), new OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    PaymentEditActivity.this.finish();
                }
            });
            dialog.show();
            return;
        }

        divideEquallyToPaymentIfRequired(amountTotal);
        getFktnController().persistPayment(payment);
        finish();
    }

    private void divideEquallyToPaymentIfRequired(Amount amountTotal) {
        if (!divideEqually) {
            return; // values already in payment
        }
        List<Participant> participants = allRelevantParticipants;
        Map<Participant, Amount> targetMap = payment.getParticipantToSpending();
        MathUtils.divideAndSetOnMap(amountTotal, participants, targetMap, true, getAmountFac());

    }

    /**
     * View method.
     * 
     * @param view
     *            Required parameter.
     */
    public void cancelEdit(View view) {
        finish();
    }

    /**
     * View method.
     * 
     * @param view
     *            Required parameter.
     */
    public void divideRest(View view) {
        Double rest = NumberUtils.round(amountTotalPayments.getValue()
                - Math.abs(amountTotalDebits.getValue()));

        int countBlanks = 0;
        List<Participant> participantsWithBlanks = new ArrayList<Participant>();
        for (Entry<Participant, Amount> entry : payment.getParticipantToSpending().entrySet()) {
            if (entry.getValue().getValue() == null || entry.getValue().getValue().doubleValue() == 0) {
                countBlanks = countBlanks + 1;
                participantsWithBlanks.add(entry.getKey());
            }
        }
        DivisionResult divisionResult = NumberUtils.divideWithLoss(rest, countBlanks, true);
        int lossIndex = -1;
        if (divisionResult.getLoss() != 0) {
            lossIndex = new Random().nextInt(countBlanks - 1);
        }

        for (int i = 0; i < participantsWithBlanks.size(); i++) {
            Double valueToBeUsed = divisionResult.getResult();
            if (i == lossIndex) {
                valueToBeUsed = NumberUtils.round(valueToBeUsed + divisionResult.getLoss());

            }
            Participant p = participantsWithBlanks.get(i);
            EditText editText =
                    amountDebitorParticipantToWidget.get(p);
            UiAmountViewUtils.writeAmountToEditText(getAmountFac().createAmount(valueToBeUsed), editText, getLocale(),
                    getDecimalNumberInputUtil());
            payment.getParticipantToSpending().get(p).setValue(valueToBeUsed);

        }

    }

    private String getDivisionCheckboxOnParticipantSelectionText(Amount amount) {
        String checkboxText;
        checkboxText = getResources().getString(R.string.participant_selection_popup_traveler_divide_amount)
                + " "
                + AmountViewUtils.getAmountString(getLocale(), amount, false, false, false, true, true);
        return checkboxText;
    }

    private boolean isAmountBiggerZero(Amount amount) {
        return amount != null && amount.getValue() > 0;
    }

    private Bundle createBundleForParticipantSelection(ArrayList<Participant> participantsInUse,
            Amount currentTotalAmount, boolean isPayerSelection) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(DIALOG_PARAM_PARTICIPANTS_IN_USE, participantsInUse);
        bundle.putSerializable(DIALOG_PARAM_TOTAL_PAYMENT_AMOUNT, currentTotalAmount);
        bundle.putBoolean(DIALOG_PARAM_IS_PAYMENT, isPayerSelection);
        return bundle;
    }

    private Amount getParamFromBundleAmount(Bundle args) {
        if (args == null) {
            return null;
        }
        return (Amount) args.get(DIALOG_PARAM_TOTAL_PAYMENT_AMOUNT);
    }

    private boolean getParamFromBundleIsPayment(Bundle args) {
        if (args == null) {
            return true;
        }
        return args.getBoolean(DIALOG_PARAM_IS_PAYMENT);
    }

    @SuppressWarnings({ "unchecked" })
    private List<Participant> getParamFromBundleParticipantsInUse(Bundle args) {
        if (args == null) {
            return null;
        }
        return (List<Participant>) args.get(DIALOG_PARAM_PARTICIPANTS_IN_USE);
    }

    private Dialog createDialogDebitorSelection() {
        int idParticipantSelectorTitle = R.string.participant_selection_popup_traveler_selection_title;
        int idParticipantSelectorMessage = R.string.participant_selection_popup_traveler_to_debit_msg;
        return createParticipantSelectionPopup(allRelevantParticipants, idParticipantSelectorTitle,
                idParticipantSelectorMessage,
                false);
    }

    private Dialog createDialogPayerSelection() {
        int idParticipantSelectorTitle = R.string.participant_selection_popup_traveler_selection_title;
        int idParticipantSelectorMessage = R.string.participant_selection_popup_payer_selection_msg;
        return createParticipantSelectionPopup(allRelevantParticipants, idParticipantSelectorTitle,
                idParticipantSelectorMessage,
                true);
    }

    private Dialog createParticipantSelectionPopup(List<Participant> participants, int idParticipantSelectorTitle,
            int idParticipantSelectorMessage, final boolean isPayment) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // sss

        final ListView participantSelectionListView = new ListView(PaymentEditActivity.this);
        final LinearLayout layout = new LinearLayout(PaymentEditActivity.this);
        final CheckBox checkbox = new CheckBox(PaymentEditActivity.this);

        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));

        Resources res = getResources();

        TextView textView = new TextView(this);
        textView.setText(res.getString(idParticipantSelectorMessage));

        LayoutParams lpHeader = new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        lpHeader.bottomMargin = 10;

        textView.setTextColor(res.getColor(android.R.color.white));
        layout.addView(textView, lpHeader);

        LinearLayout.LayoutParams lpListShit = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT); // Verbose!
        lpListShit.weight = 1.0f; // This is critical. Doesn't work without it.
        layout.addView(participantSelectionListView, lpListShit);

        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        checkbox.setId(R.id.payment_edit_selection_dialog_checkbox);
        layout.addView(checkbox, lp);

        RowObjectCallback<Participant> callback = new RowObjectCallback<Participant>() {
            public String getStringToDisplay(Participant t) {
                return t.getName()
                        + ((t.isActive()) ? "" : " " + getResources().getString(R.string.common_label_inactive_addon));
            }
        };
        List<RowObject<Participant>> participantsWrapped = new ArrayList<RowObject<Participant>>();
        for (Participant p : participants) {
            participantsWrapped.add(new RowObject<Participant>(callback, p));
        }

        final ArrayAdapter<RowObject<Participant>> arrayAdapterParticipantSelection = new ArrayAdapter<RowObject<Participant>>(
                PaymentEditActivity.this,
                // android.R.layout.simple_list_item_multiple_choice,
                R.layout.general_checked_text_view,
                participantsWrapped);

        participantSelectionListView.setAdapter(arrayAdapterParticipantSelection);
        participantSelectionListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        participantSelectionListView.setId(R.id.payment_edit_selection_dialog_list_view);

        builder.setTitle(res.getString(idParticipantSelectorTitle)).setCancelable(true)

                .setView(layout)/**/
                .setPositiveButton(R.string.common_button_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        boolean divideAmountResult = true;
                        final List<Participant> selectionResult = new ArrayList<Participant>();

                        SparseBooleanArray selection = participantSelectionListView.getCheckedItemPositions();
                        for (int i = 0; i < participantSelectionListView.getCount(); i++)
                            if (selection.get(i)) {
                                Participant selectedParticipant = arrayAdapterParticipantSelection.getItem(i)
                                        .getRowObject();
                                selectionResult.add(selectedParticipant);
                                divideAmountResult = checkbox.isChecked();
                            }
                        updateParticipantsAfterSelection(selectionResult, divideAmountResult, isPayment);

                    }
                })/**/
                .setNegativeButton(R.string.common_button_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert = builder.create();

        /**
         * Workaround for bug: Button cannot be retrieved unless shown.
         */
        alert.setOnShowListener(new OnShowListener() {

            public void onShow(DialogInterface dialog) {

                final Button positiveButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                long[] selectedIds = participantSelectionListView.getCheckItemIds();
                positiveButton.setEnabled(selectedIds.length > 0);
                participantSelectionListView.setOnItemClickListener(new OnItemClickListener() {

                    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                        long[] selectedIds = participantSelectionListView.getCheckItemIds();
                        positiveButton.setEnabled(selectedIds.length > 0);
                    }

                });
            }
        });

        return alert;
    }

    protected void updateParticipantsAfterSelection(List<Participant> selectionResult, boolean divideAmountResult,
            boolean isPayment) {

        Map<Participant, Amount> target = (isPayment) ?
                payment.getParticipantToPayment() :
                payment.getParticipantToSpending();

        Map<Participant, Amount> oldValues = new HashMap<Participant, Amount>();
        oldValues.putAll(target);
        target.clear();
        List<Amount> newValues = new ArrayList<Amount>();

        if (divideAmountResult) {
            List<Participant> participants = selectionResult;
            Map<Participant, Amount> targetMap = target;
            Amount amountTotal = (isPayment) ? calculateTotalSum(oldValues) : calculateTotalSumPayer();
            MathUtils.divideAndSetOnMap(amountTotal, participants, targetMap, !isPayment, getAmountFac());
        }
        else {
            for (Participant pSelected : selectionResult) {
                Amount amount = getAmountFac().createAmount();
                target.put(pSelected, amount);
                newValues.add(amount);
            }
            for (Entry<Participant, Amount> entry : target.entrySet()) {
                if (oldValues.get(entry.getKey()) != null && oldValues.get(entry.getKey()).getValue() != 0) {
                    entry.getValue().setValue(oldValues.get(entry.getKey()).getValue());
                }
            }

        }
        if (isPayment) {
            buildPaymentInput();
            updatePayerSum();
        }
        else {
            buildDebitorInput();
            updateSpentSum();
        }
    }

    private void bindPlainTextInput() {

        final Payment paymentFinal = this.payment;

        EditText editText = (EditText) findViewById(R.id.paymentView_editTextPaymentDescription);
        editText.setText(payment.getDescription());

        editText.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
                String input = StringUtils.clearInput(s);
                paymentFinal.setDescription(input);
            }
        });
    }

    private void bindAmountInput(final EditText widget, final Amount amount, final boolean isPayment) {

        widget.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
                String widgetInput = getDecimalNumberInputUtil().fixInputStringWidgetToParser(s.toString());
                Double valueInput = NumberUtils.getStringToDoubleRounded(getLocale(), widgetInput);
                if (!isPayment) {
                    valueInput = NumberUtils.neg(valueInput);
                }
                amount.setValue(valueInput);
                if (isPayment) {
                    PaymentEditActivity.this.updatePayerSum();
                }
                else {
                    PaymentEditActivity.this.updateSpentSum();
                }
            }
        });
    }

    protected void updatePayerSum() {
        amountTotalPayments = calculateTotalSumPayer();
        Amount amountHere = amountTotalPayments;
        int viewId = R.id.paymentView_createPaymentPayerTableLayout_total_sum_value;
        updateSumText(amountHere, viewId);
        updateSaveButtonState();
        updateDivideRestButtonState();
    }

    protected void updateSpentSum() {
        amountTotalDebits = calculateTotalSumSpending();
        Amount amountHere = amountTotalDebits;
        int viewId = R.id.paymentView_payee_createPaymentPayerTableLayout_total_sum_value;
        updateSumText(amountHere, viewId);
        updateSaveButtonState();
        updateDivideRestButtonState();
    }

    private void updateSumText(Amount amountHere, int viewId) {
        TextView textView = (TextView) findViewById(viewId);
        textView.setText(AmountViewUtils.getAmountString(getLocale(), amountHere, false, false, false, true, true));
    }

    private void updateSaveButtonState() {
        int buttonId = (ViewMode.CREATE.equals(viewMode)) ? R.id.paymentView_buttonCreate : R.id.paymentView_buttonSave;
        Button saveButton = (Button) findViewById(buttonId);
        saveButton.setEnabled(isPaymentSaveable());
    }

    private void updateDivideRestButtonState() {
        Button button = (Button) findViewById(R.id.paymentView_button_divide_remaining_spending);
        button.setEnabled(areBlankDebitors());
    }

    private boolean isPaymentSaveable() {
        return isAmountBiggerZero(amountTotalPayments)
                && (divideEqually || (amountTotalDebits != null && amountTotalPayments.getValue().doubleValue() == Math
                        .abs(amountTotalDebits
                                .getValue().doubleValue())));
    }

    private boolean areBlankDebitors() {
        if (amountTotalPayments == null
                || amountTotalPayments.getValue() <= 0) {
            return false;
        }
        Double totalAmountSpendingAbs = Math.abs(amountTotalDebits.getValue());
        Double totalAmountPaid = amountTotalPayments.getValue();

        if (totalAmountPaid.equals(totalAmountSpendingAbs) || totalAmountSpendingAbs > totalAmountPaid) {
            return false;
        }
        for (Entry<Participant, Amount> entry : payment.getParticipantToSpending().entrySet()) {
            if (entry.getValue().getValue() != null && entry.getValue().getValue() == 0) {
                return true;
            }
        }
        return false;

    }

    private Amount calculateTotalSumPayer() {
        return calculateTotalSum(payment.getParticipantToPayment());
    }

    private Amount calculateTotalSumSpending() {
        return calculateTotalSum(payment.getParticipantToSpending());
    }

    private Amount calculateTotalSum(Map<Participant, Amount> map) {
        Amount amountTotal = getAmountFac().createAmount();
        for (Entry<Participant, Amount> pair : map.entrySet()) {
            amountTotal.addAmount(pair.getValue());
        }
        return amountTotal;
    }

    @SuppressWarnings("rawtypes")
    private void initAndBindSpinner(PaymentCategory paymentCategory) {
        final Spinner spinner = (Spinner) findViewById(R.id.paymentView_spinnerPaymentCategory);
        List<RowObject> spinnerObjects = SpinnerViewSupport.createSpinnerObjects(PaymentCategory.BEVERAGES, false,
                Arrays.asList(new Object[] { PaymentCategory.MONEY_TRANSFER }), getResources(), getApp()
                        .getMiscController().getDefaultStringCollator());
        ArrayAdapter<RowObject> adapter = new ArrayAdapter<RowObject>(this, android.R.layout.simple_spinner_item,
                spinnerObjects);
        adapter.setDropDownViewResource(R.layout.selection_list_medium);
        spinner.setPromptId(R.string.payment_view_spinner_prompt);
        spinner.setAdapter(adapter);
        SpinnerViewSupport.setSelection(spinner, paymentCategory, adapter);

        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

            @SuppressWarnings("unchecked")
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (position >= 0) {
                    Object o = spinner.getSelectedItem();
                    PaymentCategory paymentCategorySelected = ((RowObject<PaymentCategory>) o).getRowObject();
                    PaymentEditActivity.this.payment.setCategory(paymentCategorySelected);
                }
            }

            public void onNothingSelected(AdapterView<?> parentView) {
                // intentionally blank
            }

        });
    }

    private TrickyTripperApp getApp() {
        TrickyTripperApp app = ((TrickyTripperApp) getApplication());
        return app;
    }

    private Locale getLocale() {
        Locale locale = getResources().getConfiguration().locale;
        return locale;
    }

    private DecimalNumberInputUtil getDecimalNumberInputUtil() {
        return getApp().getMiscController().getDecimalNumberInputUtil();
    }

    private AmountFactory getAmountFac() {
        return getFktnController().getAmountFactory();
    }

    private TripController getFktnController() {
        return getApp().getTripController();
    }

}
