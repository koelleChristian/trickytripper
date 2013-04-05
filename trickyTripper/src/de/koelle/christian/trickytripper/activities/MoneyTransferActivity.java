package de.koelle.christian.trickytripper.activities;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import de.koelle.christian.common.abs.ActionBarSupport;
import de.koelle.christian.common.options.OptionContraintsAbs;
import de.koelle.christian.common.text.BlankTextWatcher;
import de.koelle.christian.common.ui.filter.DecimalNumberInputUtil;
import de.koelle.christian.common.utils.NumberUtils;
import de.koelle.christian.common.utils.UiUtils;
import de.koelle.christian.trickytripper.R;
import de.koelle.christian.trickytripper.TrickyTripperApp;
import de.koelle.christian.trickytripper.activitysupport.CurrencyCalculatorResultSupport;
import de.koelle.christian.trickytripper.activitysupport.PopupFactory;
import de.koelle.christian.trickytripper.constants.Rc;
import de.koelle.christian.trickytripper.constants.Rd;
import de.koelle.christian.trickytripper.constants.Rx;
import de.koelle.christian.trickytripper.controller.MiscController;
import de.koelle.christian.trickytripper.controller.TripController;
import de.koelle.christian.trickytripper.factories.AmountFactory;
import de.koelle.christian.trickytripper.factories.ModelFactory;
import de.koelle.christian.trickytripper.model.Amount;
import de.koelle.christian.trickytripper.model.Debts;
import de.koelle.christian.trickytripper.model.Participant;
import de.koelle.christian.trickytripper.model.Payment;
import de.koelle.christian.trickytripper.model.PaymentCategory;
import de.koelle.christian.trickytripper.modelutils.AmountViewUtils;

public class MoneyTransferActivity extends SherlockActivity {

    private final Map<Participant, Amount> amountByParticipant = new HashMap<Participant, Amount>();
    private Participant transferer;
    private Amount amountTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.money_transfer_view);

        transferer = (Participant) getIntent().getExtras().getSerializable(
                Rc.ACTIVITY_PARAM_KEY_PARTICIPANT);
        List<Participant> allParticipants = getFktnController().getAllParticipants(false);
        Debts debtsOfTransferer = getFktnController().getDebts().get(transferer);

        initView(transferer, allParticipants, debtsOfTransferer);
        
        ActionBarSupport.addBackButton(this);

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

    private void initView(Participant transferer, List<Participant> allParticipants, Debts debtsOfTransferer) {
        createDynamicTable(transferer, allParticipants, debtsOfTransferer);
    }

    private void createDynamicTable(Participant transferer, List<Participant> allParticipants, Debts debtsOfTransferer) {
        TableLayout listView = (TableLayout) findViewById(R.id.money_transfer_view_table_layout);
        addDynamicRows(transferer, allParticipants, debtsOfTransferer, listView);
    }

    /**
     * View method.
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

    /**
     * View method.
     * 
     * @param view
     *            Required parameter.
     */
    public void transfer(View view) {
        createNewPayments();
        finish();
    }

    private void createNewPayments() {
        for (Entry<Participant, Amount> entry : amountByParticipant.entrySet()) {
            Amount amountInput = entry.getValue();
            if (amountInput.getValue() > 0) {
                Amount amountInputNeg = amountInput.clone();
                amountInputNeg.setValue(NumberUtils.neg(amountInputNeg.getValue()));
                Payment newPayment = ModelFactory.createNewPayment(
                        getResources().getString(PaymentCategory.MONEY_TRANSFER.getResourceStringId()),
                        PaymentCategory.MONEY_TRANSFER);
                newPayment.getParticipantToPayment().put(entry.getKey(), amountInputNeg);
                newPayment.getParticipantToSpending().put(transferer, amountInput);
                getFktnController().persistPayment(newPayment);
            }
        }
    }

    private void addDynamicRows(Participant transferer, List<Participant> allParticipants,
            Debts debtsOfTransferer, TableLayout tableLayout) {

        TextView textView = ((TextView) findViewById(R.id.money_transfer_view_output_participant_from));
        textView.setText(transferer.getName());

        List<Participant> allToBe;
        allToBe = getAllToBeListed(transferer, allParticipants, debtsOfTransferer, true);
        addTransferRow(debtsOfTransferer, tableLayout, allToBe, true);
        allToBe = getAllToBeListed(transferer, allParticipants, debtsOfTransferer, false);
        addTransferRow(debtsOfTransferer, tableLayout, allToBe, false);

    }

    private void addTransferRow(Debts debtsOfTransferer, TableLayout tableLayout, List<Participant> allToBe,
            boolean firstAddRows) {
        TableRow newRow;
        Button buttonDueAmount;
        Button buttonCurrency;
        TextView nameTextView;
        int offset = tableLayout.getChildCount();

        int dynViewId = (firstAddRows) ? Rx.DYN_ID_PAYMENT_MONEY_TRANSFER_FIRST
                : Rx.DYN_ID_PAYMENT_MONEY_TRANSFER_SECOND;

        for (int i = 0; i < allToBe.size(); i++) {
            Participant p = allToBe.get(i);
            final Amount amountDue = getNullSafeDebts(debtsOfTransferer, p);
            final Amount inputValueModel = getAmountFac().createAmount();
            newRow = (TableRow) inflate(R.layout.money_transfer_list_view);

            final EditText editText = (EditText) newRow.findViewById(R.id.money_transfer_list_view_input_amount);
            nameTextView = (TextView) newRow.findViewById(R.id.money_transfer_list_view_output_name);
            buttonDueAmount = (Button) newRow.findViewById(R.id.money_transfer_list_view_button_due_amount);
            buttonCurrency = (Button) newRow.findViewById(R.id.money_transfer_list_view_button_currency);

            // TODO(ckoelle) Post merge
            UiUtils.makeProperNumberInput(editText, getDecimalNumberInputUtil().getInputPatternMatcher());
            editText.setId(dynViewId);
            tableLayout.addView(newRow, i + offset);
            nameTextView.setText(p.getName());
            UiUtils.setFontAndStyle(this, nameTextView, !p.isActive(), android.R.style.TextAppearance_Small);

            buttonCurrency.setText(getApp().getTripController().getLodadedTripCurrencySymbol(false));
            bindCurrencyCalculatorAction(buttonCurrency, inputValueModel, dynViewId);

            if (amountDue == null) {
                buttonDueAmount.setEnabled(false);
                buttonDueAmount.setText("0");
            }
            else {
                buttonDueAmount.setText(AmountViewUtils.getAmountString(getLocale(), amountDue, true, true));
                buttonDueAmount.setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                        editText.setText(getDecimalNumberInputUtil().fixInputStringModelToWidget(
                                AmountViewUtils.getAmountString(getLocale(), amountDue, true, true)));
                    }
                });
            }
            bindPaymentInputAndUpdate(editText, inputValueModel);
            amountByParticipant.put(p, inputValueModel);
            dynViewId++;
        }
        updateSum();
        updateSaveButtonState();
    }

    private void bindCurrencyCalculatorAction(final Button buttonCurrency, final Amount sourceAndTargetAmountReference,
            final int viewIdForResult) {
        buttonCurrency.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getApp().getViewController().openMoneyCalculatorView(sourceAndTargetAmountReference, viewIdForResult,
                        MoneyTransferActivity.this);
            }
        });
    }

    private void bindPaymentInputAndUpdate(final EditText widget, final Amount amount) {

        widget.addTextChangedListener(new BlankTextWatcher() {
            public void afterTextChanged(Editable s) {
                String widgetInput = getDecimalNumberInputUtil().fixInputStringWidgetToParser(s.toString());
                amount.setValue(NumberUtils.getStringToDoubleRounded(getLocale(), widgetInput));
                MoneyTransferActivity.this.updateSum();
                MoneyTransferActivity.this.updateSaveButtonState();
            }
        });
    }

    protected void updateSum() {
        Locale locale = getLocale();
        amountTotal = calculateTotalSum();
        TextView textView = (TextView) findViewById(R.id.money_transfer_view_output_total_transfer_amount);
        textView.setText(AmountViewUtils.getAmountString(locale, amountTotal, false, false, false, true, true));
    }

    private void updateSaveButtonState() {
        Button saveButton = (Button) findViewById(R.id.money_transfer_view_button_transfer);
        saveButton.setEnabled(amountTotal.getValue() > 0);
    }

    private Amount calculateTotalSum() {
        Amount amountTotal = getAmountFac().createAmount();
        for (Entry<Participant, Amount> pair : amountByParticipant.entrySet()) {
            amountTotal.addAmount(pair.getValue());
        }
        return amountTotal;
    }

    private Amount getNullSafeDebts(Debts debtsOfTransferer, Participant p) {
        final Amount a = (
                debtsOfTransferer != null
                        && debtsOfTransferer.getLoanerToDepts() != null
                        && debtsOfTransferer.getLoanerToDepts().get(p) != null
                        && debtsOfTransferer.getLoanerToDepts().get(p).getValue() > 0) ?

                        debtsOfTransferer.getLoanerToDepts().get(p) :
                        null;
        return a;
    }

    private List<Participant> getAllToBeListed(Participant transferer, List<Participant> allParticipants,
            Debts debtsOfTransferer, boolean onlyOwing) {
        List<Participant> result = new ArrayList<Participant>();
        for (Participant p : allParticipants) {
            if (!p.equals(transferer)) {
                Amount owingAmount = getNullSafeDebts(debtsOfTransferer, p);
                if (onlyOwing && owingAmount != null) {
                    result.add(p);
                }
                else if (!onlyOwing && owingAmount == null) {
                    result.add(p);
                }
            }
        }
        final Collator collator = getMiscController().getDefaultStringCollator();
        Collections.sort(result, new Comparator<Participant>() {
            public int compare(Participant object1, Participant object2) {
                return collator.compare(object1.getName(), object2.getName());
            }
        });
        return result;
    }

    private View inflate(int layoutId) {
        LayoutInflater inflater = getLayoutInflater();
        final View viewInf = inflater.inflate(layoutId, null);
        return viewInf;
    }

    private TrickyTripperApp getApp() {
        TrickyTripperApp app = ((TrickyTripperApp) getApplication());
        return app;
    }

    private DecimalNumberInputUtil getDecimalNumberInputUtil() {
        return getMiscController().getDecimalNumberInputUtil();
    }

    private Locale getLocale() {
        Locale locale = getResources().getConfiguration().locale;
        return locale;
    }

    private AmountFactory getAmountFac() {
        return getApp().getTripController().getAmountFactory();
    }

    private TripController getFktnController() {
        return getApp().getTripController();
    }

    private MiscController getMiscController() {
        return getApp().getMiscController();
    }

}
