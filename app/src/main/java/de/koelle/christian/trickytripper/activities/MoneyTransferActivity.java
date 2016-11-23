package de.koelle.christian.trickytripper.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import de.koelle.christian.common.abs.ActionBarSupport;
import de.koelle.christian.common.options.OptionConstraintsInflater;
import de.koelle.christian.common.text.BlankTextWatcher;
import de.koelle.christian.common.ui.filter.DecimalNumberInputUtil;
import de.koelle.christian.common.utils.NumberUtils;
import de.koelle.christian.common.utils.UiUtils;
import de.koelle.christian.trickytripper.R;
import de.koelle.christian.trickytripper.TrickyTripperApp;
import de.koelle.christian.trickytripper.activitysupport.CurrencyCalculatorResultSupport;
import de.koelle.christian.trickytripper.constants.Rc;
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

public class MoneyTransferActivity extends AppCompatActivity {

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
                new OptionConstraintsInflater().activity(getMenuInflater()).menu(menu)
                        .options(new int[]{
                                R.id.option_save_edit,
                                R.id.option_help
                        }));
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean saveEnabled = amountTotal.getValue() > 0;
        MenuItem item = menu.findItem(R.id.option_save_edit);
        item.setEnabled(saveEnabled);
        item.getIcon().setAlpha((saveEnabled) ? 255 : 64);
        item.setTitle(R.string.option_money_transfer_execute);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.option_save_edit:
                transfer();
                return true;
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

    public void transfer() {
        createNewPayments();
        finish();
    }

    private void createNewPayments() {
        for (Entry<Participant, Amount> entry : amountByParticipant.entrySet()) {
            Amount amountInput = entry.getValue();
            if (amountInput.getValue() > 0) {
                Amount amountInputNeg = amountInput.doClone();
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

            UiUtils.makeProperNumberInput(editText, getDecimalNumberInputUtil().getInputPatternMatcher());
            //noinspection ResourceType
            editText.setId(dynViewId);
            tableLayout.addView(newRow, i + offset);
            nameTextView.setText(p.getName());
            UiUtils.setFontAndStyle(this, nameTextView, !p.isActive(), android.R.style.TextAppearance_Small);

            buttonCurrency.setText(getApp().getTripController().getLoadedTripCurrencySymbol(false));
            bindCurrencyCalculatorAction(buttonCurrency, inputValueModel, dynViewId);

            if (amountDue == null) {
                buttonDueAmount.setEnabled(false);
                buttonDueAmount.setText("0");
            } else {
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
        supportInvalidateOptionsMenu();
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
                MoneyTransferActivity.this.supportInvalidateOptionsMenu();
            }
        });
    }

    protected void updateSum() {
        Locale locale = getLocale();
        amountTotal = calculateTotalSum();
        TextView textView = (TextView) findViewById(R.id.money_transfer_view_output_total_transfer_amount);
        textView.setText(AmountViewUtils.getAmountString(locale, amountTotal, false, false, false, true, true));
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
                        && debtsOfTransferer.getLoanerToDebts() != null
                        && debtsOfTransferer.getLoanerToDebts().get(p) != null
                        && debtsOfTransferer.getLoanerToDebts().get(p).getValue() > 0) ?

                debtsOfTransferer.getLoanerToDebts().get(p) :
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
                } else if (!onlyOwing && owingAmount == null) {
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
