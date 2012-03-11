package de.christian.koelle.strategies;

import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;

import de.koelle.christian.common.utils.NumberUtils;
import de.koelle.christian.trickytripper.factories.AmountFactory;
import de.koelle.christian.trickytripper.factories.ModelFactory;
import de.koelle.christian.trickytripper.model.Amount;
import de.koelle.christian.trickytripper.model.Participant;
import de.koelle.christian.trickytripper.model.Payment;
import de.koelle.christian.trickytripper.model.PaymentCategory;
import de.koelle.christian.trickytripper.model.Trip;
import de.koelle.christian.trickytripper.strategies.SumReport;
import de.koelle.christian.trickytripper.strategies.TripReportLogic;

public class TripReportLogicTest {

    private Trip tripToBeEdited;
    private Participant chris;
    private Participant niko;
    private Participant wolfram;

    private final AmountFactory amountFactory = new AmountFactory();

    private enum TestDataSet {
        /** Default test data set. */
        DEFAULT,
        /** Blank data pool. */
        BLANK,
        /** Blank data pool. */
        PAYMENTS_ONE_WITHOUT_CHRISTIAN,
        /** */
        FIRST_PAYMENT_HIGHER_THAN_FIRST_DEBITOR,
        /** */
        FIRST_PAYMENT_LOWER_THAN_FIRST_DEBITOR,

        /**/;
    }

    @Test
    public void testBalanceLogicDefault() {
        createTestData(TestDataSet.DEFAULT);
        TripReportLogic tripReportLogic = new TripReportLogic();
        updateAllTransientData(tripToBeEdited, tripReportLogic);

        Assert.assertEquals(25.00d, tripToBeEdited.getSumReport().getBalanceByUser().get(chris).getValue());
        Assert.assertEquals(-15.00d, tripToBeEdited.getSumReport().getBalanceByUser().get(niko).getValue());
        Assert.assertEquals(-10.00d, tripToBeEdited.getSumReport().getBalanceByUser().get(wolfram).getValue());

        Assert.assertEquals(40.00d, tripToBeEdited.getSumReport().getPaymentByUser().get(chris).getValue());
        Assert.assertEquals(0.00d, tripToBeEdited.getSumReport().getPaymentByUser().get(niko).getValue());
        Assert.assertEquals(0.00d, tripToBeEdited.getSumReport().getPaymentByUser().get(wolfram).getValue());

        Assert.assertEquals(-15.00d, tripToBeEdited.getSumReport().getSpendingByUser().get(chris).getValue());
        Assert.assertEquals(-15.00d, tripToBeEdited.getSumReport().getSpendingByUser().get(niko).getValue());
        Assert.assertEquals(-10.00d, tripToBeEdited.getSumReport().getSpendingByUser().get(wolfram).getValue());

        Assert.assertEquals(true, tripToBeEdited.getDebts().get(chris).getLoanerToDepts().isEmpty());

        Assert.assertEquals(1, tripToBeEdited.getDebts().get(niko).getLoanerToDepts().size());
        Assert.assertEquals(15.00d, tripToBeEdited.getDebts().get(niko).getLoanerToDepts().get(chris).getValue());

        Assert.assertEquals(1, tripToBeEdited.getDebts().get(wolfram).getLoanerToDepts().size());
        Assert.assertEquals(10.00d, tripToBeEdited.getDebts().get(wolfram).getLoanerToDepts().get(chris).getValue());

    }

    @Test
    public void testBalanceLogicMoneyTransfer() {
        createTestData(TestDataSet.DEFAULT);
        TripReportLogic tripReportLogic = new TripReportLogic();
        updateAllTransientData(tripToBeEdited, tripReportLogic);

        Payment moneyTransfer;

        // Already tested

        moneyTransfer = ModelFactory.createNewPayment("Transfer 01", PaymentCategory.MONEY_TRANSFER);
        moneyTransfer.getParticipantToPayment().put(chris, amountFactory.createAmount(-15d));
        moneyTransfer.getParticipantToSpending().put(niko, amountFactory.createAmount(15d));
        tripToBeEdited.getPayments().add(moneyTransfer);

        updateAllTransientData(tripToBeEdited, tripReportLogic);

        Assert.assertEquals(10.00d, tripToBeEdited.getSumReport().getBalanceByUser().get(chris).getValue());
        Assert.assertEquals(0.00d, tripToBeEdited.getSumReport().getBalanceByUser().get(niko).getValue());
        Assert.assertEquals(-10.00d, tripToBeEdited.getSumReport().getBalanceByUser().get(wolfram).getValue());

        Assert.assertEquals(25.00d, tripToBeEdited.getSumReport().getPaymentByUser().get(chris).getValue());
        Assert.assertEquals(15.00d, tripToBeEdited.getSumReport().getPaymentByUser().get(niko).getValue());
        Assert.assertEquals(0.00d, tripToBeEdited.getSumReport().getPaymentByUser().get(wolfram).getValue());

        Assert.assertEquals(-15.00d, tripToBeEdited.getSumReport().getSpendingByUser().get(chris).getValue());
        Assert.assertEquals(-15.00d, tripToBeEdited.getSumReport().getSpendingByUser().get(niko).getValue());
        Assert.assertEquals(-10.00d, tripToBeEdited.getSumReport().getSpendingByUser().get(wolfram).getValue());

        Assert.assertEquals(true, tripToBeEdited.getDebts().get(chris).getLoanerToDepts().isEmpty());
        Assert.assertEquals(true, tripToBeEdited.getDebts().get(niko).getLoanerToDepts().isEmpty());

        Assert.assertEquals(1, tripToBeEdited.getDebts().get(wolfram).getLoanerToDepts().size());
        Assert.assertEquals(10.00d, tripToBeEdited.getDebts().get(wolfram).getLoanerToDepts().get(chris).getValue());

        /**/

        moneyTransfer = ModelFactory.createNewPayment("Transfer 02", PaymentCategory.MONEY_TRANSFER);
        moneyTransfer.getParticipantToPayment().put(chris, amountFactory.createAmount(-10d));
        moneyTransfer.getParticipantToSpending().put(wolfram, amountFactory.createAmount(10d));
        tripToBeEdited.getPayments().add(moneyTransfer);

        updateAllTransientData(tripToBeEdited, tripReportLogic);

        Assert.assertEquals(0.00d, tripToBeEdited.getSumReport().getBalanceByUser().get(chris).getValue());
        Assert.assertEquals(0.00d, tripToBeEdited.getSumReport().getBalanceByUser().get(niko).getValue());
        Assert.assertEquals(0.00d, tripToBeEdited.getSumReport().getBalanceByUser().get(wolfram).getValue());

        Assert.assertEquals(15.00d, tripToBeEdited.getSumReport().getPaymentByUser().get(chris).getValue());
        Assert.assertEquals(15.00d, tripToBeEdited.getSumReport().getPaymentByUser().get(niko).getValue());
        Assert.assertEquals(10.00d, tripToBeEdited.getSumReport().getPaymentByUser().get(wolfram).getValue());

        Assert.assertEquals(-15.00d, tripToBeEdited.getSumReport().getSpendingByUser().get(chris).getValue());
        Assert.assertEquals(-15.00d, tripToBeEdited.getSumReport().getSpendingByUser().get(niko).getValue());
        Assert.assertEquals(-10.00d, tripToBeEdited.getSumReport().getSpendingByUser().get(wolfram).getValue());

        Assert.assertEquals(true, tripToBeEdited.getDebts().get(chris).getLoanerToDepts().isEmpty());
        Assert.assertEquals(true, tripToBeEdited.getDebts().get(niko).getLoanerToDepts().isEmpty());
        Assert.assertEquals(true, tripToBeEdited.getDebts().get(niko).getLoanerToDepts().isEmpty());

    }

    @Test
    public void testBalanceLogicMoneyTransferInFragments() {
        createTestData(TestDataSet.DEFAULT);
        TripReportLogic tripReportLogic = new TripReportLogic();
        updateAllTransientData(tripToBeEdited, tripReportLogic);

        Payment moneyTransfer;

        // Already tested

        moneyTransfer = ModelFactory.createNewPayment("Transfer 01", PaymentCategory.MONEY_TRANSFER);
        moneyTransfer.getParticipantToPayment().put(chris, amountFactory.createAmount(-2d));
        moneyTransfer.getParticipantToSpending().put(niko, amountFactory.createAmount(2d));
        tripToBeEdited.getPayments().add(moneyTransfer);

        updateAllTransientData(tripToBeEdited, tripReportLogic);

        Assert.assertEquals(23.00d, tripToBeEdited.getSumReport().getBalanceByUser().get(chris).getValue());
        Assert.assertEquals(-13.00d, tripToBeEdited.getSumReport().getBalanceByUser().get(niko).getValue());
        Assert.assertEquals(-10.00d, tripToBeEdited.getSumReport().getBalanceByUser().get(wolfram).getValue());

        Assert.assertEquals(38.00d, tripToBeEdited.getSumReport().getPaymentByUser().get(chris).getValue());
        Assert.assertEquals(2.00d, tripToBeEdited.getSumReport().getPaymentByUser().get(niko).getValue());
        Assert.assertEquals(0.00d, tripToBeEdited.getSumReport().getPaymentByUser().get(wolfram).getValue());

        Assert.assertEquals(-15.00d, tripToBeEdited.getSumReport().getSpendingByUser().get(chris).getValue());
        Assert.assertEquals(-15.00d, tripToBeEdited.getSumReport().getSpendingByUser().get(niko).getValue());
        Assert.assertEquals(-10.00d, tripToBeEdited.getSumReport().getSpendingByUser().get(wolfram).getValue());

        Assert.assertEquals(true, tripToBeEdited.getDebts().get(chris).getLoanerToDepts().isEmpty());
        Assert.assertEquals(1, tripToBeEdited.getDebts().get(niko).getLoanerToDepts().size());
        Assert.assertEquals(13.00d, tripToBeEdited.getDebts().get(niko).getLoanerToDepts().get(chris).getValue());

        Assert.assertEquals(1, tripToBeEdited.getDebts().get(wolfram).getLoanerToDepts().size());
        Assert.assertEquals(10.00d, tripToBeEdited.getDebts().get(wolfram).getLoanerToDepts().get(chris).getValue());

        /**/

        moneyTransfer = ModelFactory.createNewPayment("Transfer 02", PaymentCategory.MONEY_TRANSFER);
        moneyTransfer.getParticipantToPayment().put(chris, amountFactory.createAmount(-3d));
        moneyTransfer.getParticipantToSpending().put(niko, amountFactory.createAmount(3d));
        tripToBeEdited.getPayments().add(moneyTransfer);

        updateAllTransientData(tripToBeEdited, tripReportLogic);

        Assert.assertEquals(20.00d, tripToBeEdited.getSumReport().getBalanceByUser().get(chris).getValue());
        Assert.assertEquals(-10.00d, tripToBeEdited.getSumReport().getBalanceByUser().get(niko).getValue());
        Assert.assertEquals(-10.00d, tripToBeEdited.getSumReport().getBalanceByUser().get(wolfram).getValue());

        Assert.assertEquals(35.00d, tripToBeEdited.getSumReport().getPaymentByUser().get(chris).getValue());
        Assert.assertEquals(5.00d, tripToBeEdited.getSumReport().getPaymentByUser().get(niko).getValue());
        Assert.assertEquals(0.00d, tripToBeEdited.getSumReport().getPaymentByUser().get(wolfram).getValue());

        Assert.assertEquals(-15.00d, tripToBeEdited.getSumReport().getSpendingByUser().get(chris).getValue());
        Assert.assertEquals(-15.00d, tripToBeEdited.getSumReport().getSpendingByUser().get(niko).getValue());
        Assert.assertEquals(-10.00d, tripToBeEdited.getSumReport().getSpendingByUser().get(wolfram).getValue());

        Assert.assertEquals(true, tripToBeEdited.getDebts().get(chris).getLoanerToDepts().isEmpty());
        Assert.assertEquals(1, tripToBeEdited.getDebts().get(niko).getLoanerToDepts().size());
        Assert.assertEquals(10.00d, tripToBeEdited.getDebts().get(niko).getLoanerToDepts().get(chris).getValue());

        Assert.assertEquals(1, tripToBeEdited.getDebts().get(wolfram).getLoanerToDepts().size());
        Assert.assertEquals(10.00d, tripToBeEdited.getDebts().get(wolfram).getLoanerToDepts().get(chris).getValue());

        moneyTransfer = ModelFactory.createNewPayment("Transfer 03", PaymentCategory.MONEY_TRANSFER);
        moneyTransfer.getParticipantToPayment().put(chris, amountFactory.createAmount(-0.01d));
        moneyTransfer.getParticipantToSpending().put(niko, amountFactory.createAmount(0.01d));
        tripToBeEdited.getPayments().add(moneyTransfer);

        updateAllTransientData(tripToBeEdited, tripReportLogic);

        Assert.assertEquals(19.99d, tripToBeEdited.getSumReport().getBalanceByUser().get(chris).getValue());
        Assert.assertEquals(-9.99d, tripToBeEdited.getSumReport().getBalanceByUser().get(niko).getValue());
        Assert.assertEquals(-10.00d, tripToBeEdited.getSumReport().getBalanceByUser().get(wolfram).getValue());

        Assert.assertEquals(34.99d, tripToBeEdited.getSumReport().getPaymentByUser().get(chris).getValue());
        Assert.assertEquals(5.01d, tripToBeEdited.getSumReport().getPaymentByUser().get(niko).getValue());
        Assert.assertEquals(0.00d, tripToBeEdited.getSumReport().getPaymentByUser().get(wolfram).getValue());

        Assert.assertEquals(-15.00d, tripToBeEdited.getSumReport().getSpendingByUser().get(chris).getValue());
        Assert.assertEquals(-15.00d, tripToBeEdited.getSumReport().getSpendingByUser().get(niko).getValue());
        Assert.assertEquals(-10.00d, tripToBeEdited.getSumReport().getSpendingByUser().get(wolfram).getValue());

        Assert.assertEquals(true, tripToBeEdited.getDebts().get(chris).getLoanerToDepts().isEmpty());
        Assert.assertEquals(1, tripToBeEdited.getDebts().get(niko).getLoanerToDepts().size());
        Assert.assertEquals(9.99d, tripToBeEdited.getDebts().get(niko).getLoanerToDepts().get(chris).getValue());

        Assert.assertEquals(1, tripToBeEdited.getDebts().get(wolfram).getLoanerToDepts().size());
        Assert.assertEquals(10.00d, tripToBeEdited.getDebts().get(wolfram).getLoanerToDepts().get(chris).getValue());

    }

    @Test
    public void testBalanceLogicBlank() {
        createTestData(TestDataSet.BLANK);
        TripReportLogic tripReportLogic = new TripReportLogic();
        updateAllTransientData(tripToBeEdited, tripReportLogic);

        Assert.assertEquals(0.00d, tripToBeEdited.getSumReport().getBalanceByUser().get(chris).getValue());
        Assert.assertEquals(0.00d, tripToBeEdited.getSumReport().getBalanceByUser().get(niko).getValue());
        Assert.assertEquals(0.00d, tripToBeEdited.getSumReport().getBalanceByUser().get(wolfram).getValue());

        Assert.assertEquals(0.00d, tripToBeEdited.getSumReport().getPaymentByUser().get(chris).getValue());
        Assert.assertEquals(0.00d, tripToBeEdited.getSumReport().getPaymentByUser().get(niko).getValue());
        Assert.assertEquals(0.00d, tripToBeEdited.getSumReport().getPaymentByUser().get(wolfram).getValue());

        Assert.assertEquals(0.00d, tripToBeEdited.getSumReport().getSpendingByUser().get(chris).getValue());
        Assert.assertEquals(0.00d, tripToBeEdited.getSumReport().getSpendingByUser().get(niko).getValue());
        Assert.assertEquals(0.00d, tripToBeEdited.getSumReport().getSpendingByUser().get(wolfram).getValue());

        Assert.assertEquals(true, tripToBeEdited.getDebts().get(chris).getLoanerToDepts().isEmpty());
        Assert.assertEquals(true, tripToBeEdited.getDebts().get(chris).getLoanerToDepts().isEmpty());
        Assert.assertEquals(true, tripToBeEdited.getDebts().get(chris).getLoanerToDepts().isEmpty());

    }

    @Test
    public void testBalanceLogicFirstPaymentHigherThanFirstDebitor() {
        createTestData(TestDataSet.FIRST_PAYMENT_HIGHER_THAN_FIRST_DEBITOR);
        TripReportLogic tripReportLogic = new TripReportLogic();
        updateAllTransientData(tripToBeEdited, tripReportLogic);

        Assert.assertEquals(75.00d, tripToBeEdited.getSumReport().getBalanceByUser().get(chris).getValue());
        Assert.assertEquals(10.00d, tripToBeEdited.getSumReport().getBalanceByUser().get(niko).getValue());
        Assert.assertEquals(-85.00d, tripToBeEdited.getSumReport().getBalanceByUser().get(wolfram).getValue());

        Assert.assertEquals(80.00d, tripToBeEdited.getSumReport().getPaymentByUser().get(chris).getValue());
        Assert.assertEquals(20.00d, tripToBeEdited.getSumReport().getPaymentByUser().get(niko).getValue());
        Assert.assertEquals(0.00d, tripToBeEdited.getSumReport().getPaymentByUser().get(wolfram).getValue());

        Assert.assertEquals(-5.00d, tripToBeEdited.getSumReport().getSpendingByUser().get(chris).getValue());
        Assert.assertEquals(-10.00d, tripToBeEdited.getSumReport().getSpendingByUser().get(niko).getValue());
        Assert.assertEquals(-85.00d, tripToBeEdited.getSumReport().getSpendingByUser().get(wolfram).getValue());

        Assert.assertEquals(true, tripToBeEdited.getDebts().get(chris).getLoanerToDepts().isEmpty());
        Assert.assertEquals(true, tripToBeEdited.getDebts().get(niko).getLoanerToDepts().isEmpty());

        Assert.assertEquals(2, tripToBeEdited.getDebts().get(wolfram).getLoanerToDepts().size());
        Assert.assertEquals(75.00d, tripToBeEdited.getDebts().get(wolfram).getLoanerToDepts().get(chris).getValue());
        Assert.assertEquals(10.00d, tripToBeEdited.getDebts().get(wolfram).getLoanerToDepts().get(niko).getValue());

    }

    @Test
    public void testBalanceLogicFirstPaymentLowerThanFirstDebitor() {
        createTestData(TestDataSet.FIRST_PAYMENT_LOWER_THAN_FIRST_DEBITOR);
        TripReportLogic tripReportLogic = new TripReportLogic();
        updateAllTransientData(tripToBeEdited, tripReportLogic);

        Assert.assertEquals(-80.00d, tripToBeEdited.getSumReport().getBalanceByUser().get(chris).getValue());
        Assert.assertEquals(60.00d, tripToBeEdited.getSumReport().getBalanceByUser().get(niko).getValue());
        Assert.assertEquals(20.00d, tripToBeEdited.getSumReport().getBalanceByUser().get(wolfram).getValue());

        Assert.assertEquals(5.00d, tripToBeEdited.getSumReport().getPaymentByUser().get(chris).getValue());
        Assert.assertEquals(75.00d, tripToBeEdited.getSumReport().getPaymentByUser().get(niko).getValue());
        Assert.assertEquals(20.00d, tripToBeEdited.getSumReport().getPaymentByUser().get(wolfram).getValue());

        Assert.assertEquals(-85.00d, tripToBeEdited.getSumReport().getSpendingByUser().get(chris).getValue());
        Assert.assertEquals(-15.00d, tripToBeEdited.getSumReport().getSpendingByUser().get(niko).getValue());
        Assert.assertEquals(0.00d, tripToBeEdited.getSumReport().getSpendingByUser().get(wolfram).getValue());

        Assert.assertEquals(2, tripToBeEdited.getDebts().get(chris).getLoanerToDepts().size());
        Assert.assertEquals(60.00d, tripToBeEdited.getDebts().get(chris).getLoanerToDepts().get(niko).getValue());
        Assert.assertEquals(20.00d, tripToBeEdited.getDebts().get(chris).getLoanerToDepts().get(wolfram).getValue());

        Assert.assertEquals(true, tripToBeEdited.getDebts().get(niko).getLoanerToDepts().isEmpty());
        Assert.assertEquals(true, tripToBeEdited.getDebts().get(wolfram).getLoanerToDepts().isEmpty());

    }

    @Test
    public void testBalanceDebtsFlipping() {
        createTestData(TestDataSet.DEFAULT);
        TripReportLogic tripReportLogic = new TripReportLogic();
        updateAllTransientData(tripToBeEdited, tripReportLogic);

        Assert.assertEquals(true, tripToBeEdited.getDebts().get(chris).getLoanerToDepts().isEmpty());

        Assert.assertEquals(1, tripToBeEdited.getDebts().get(niko).getLoanerToDepts().size());
        Assert.assertEquals(15.00d, tripToBeEdited.getDebts().get(niko).getLoanerToDepts().get(chris).getValue());

        Assert.assertEquals(1, tripToBeEdited.getDebts().get(niko).getLoanerToDepts().size());
        Assert.assertEquals(10.00d, tripToBeEdited.getDebts().get(wolfram).getLoanerToDepts().get(chris).getValue());

        Assert.assertEquals(25.00d, tripToBeEdited.getSumReport().getBalanceByUser().get(chris).getValue());
        Assert.assertEquals(-15.00d, tripToBeEdited.getSumReport().getBalanceByUser().get(niko).getValue());
        Assert.assertEquals(-10.00d, tripToBeEdited.getSumReport().getBalanceByUser().get(wolfram).getValue());

        /* ========== */
        addNewSplitPayment(niko, 60.00d);
        updateAllTransientData(tripToBeEdited, tripReportLogic);
        /* ========== */

        Assert.assertEquals(5.00d, tripToBeEdited.getSumReport().getBalanceByUser().get(chris).getValue());
        Assert.assertEquals(25.00d, tripToBeEdited.getSumReport().getBalanceByUser().get(niko).getValue());
        Assert.assertEquals(-30.00d, tripToBeEdited.getSumReport().getBalanceByUser().get(wolfram).getValue());

        Assert.assertEquals(0, tripToBeEdited.getDebts().get(chris).getLoanerToDepts().size());

        Assert.assertEquals(0, tripToBeEdited.getDebts().get(niko).getLoanerToDepts().size());

        Assert.assertEquals(2, tripToBeEdited.getDebts().get(wolfram).getLoanerToDepts().size());
        Assert.assertEquals(5.00d, tripToBeEdited.getDebts().get(wolfram).getLoanerToDepts().get(chris).getValue());
        Assert.assertEquals(25.00d, tripToBeEdited.getDebts().get(wolfram).getLoanerToDepts().get(niko).getValue());

        /* ========== */
        addNewSplitPayment(wolfram, 90.00d);
        updateAllTransientData(tripToBeEdited, tripReportLogic);
        /* ========== */

        Assert.assertEquals(-25.00d, tripToBeEdited.getSumReport().getBalanceByUser().get(chris).getValue());
        Assert.assertEquals(-5.00d, tripToBeEdited.getSumReport().getBalanceByUser().get(niko).getValue());
        Assert.assertEquals(30.00d, tripToBeEdited.getSumReport().getBalanceByUser().get(wolfram).getValue());

        Assert.assertEquals(1, tripToBeEdited.getDebts().get(chris).getLoanerToDepts().size());
        Assert.assertEquals(25.00d, tripToBeEdited.getDebts().get(chris).getLoanerToDepts().get(wolfram).getValue());

        Assert.assertEquals(1, tripToBeEdited.getDebts().get(niko).getLoanerToDepts().size());
        Assert.assertEquals(5.00d, tripToBeEdited.getDebts().get(niko).getLoanerToDepts().get(wolfram).getValue());

        Assert.assertEquals(0, tripToBeEdited.getDebts().get(wolfram).getLoanerToDepts().size());
    }

    private void addNewSplitPayment(Participant payer, double newPaymentValue) {
        Payment payment;
        Amount amount;
        Map<Participant, Amount> payerToPayment;
        Map<Participant, Amount> deptorToAmount;

        deptorToAmount = new HashMap<Participant, Amount>();
        payerToPayment = new HashMap<Participant, Amount>();

        amount = amountFactory.createAmount(newPaymentValue);
        payerToPayment.put(payer, amount);

        Double splitValue = NumberUtils.divide(Double.valueOf(newPaymentValue), Integer.valueOf(3));
        splitValue = splitValue * -1;

        amount = amountFactory.createAmount(splitValue);
        deptorToAmount.put(chris, amount);
        amount = amountFactory.createAmount(splitValue);
        deptorToAmount.put(niko, amount);
        amount = amountFactory.createAmount(splitValue);
        deptorToAmount.put(wolfram, amount);

        payment = new Payment();
        payment.setCategory(PaymentCategory.RENTALS);
        payment.setDescription("Whatever");
        payment.setPayerToPayment(payerToPayment);
        payment.setDebitorToAmount(deptorToAmount);

        tripToBeEdited.getPayments().add(payment);
    }

    @Test
    public void testParticipantDeleteCondition() {
        createTestData(TestDataSet.PAYMENTS_ONE_WITHOUT_CHRISTIAN);
        TripReportLogic tripReportLogic = new TripReportLogic();
        updateAllTransientData(tripToBeEdited, tripReportLogic);

        Assert.assertEquals(true, tripToBeEdited.partOfPayments(chris));
        tripToBeEdited.getPayments().remove(3);
        Assert.assertEquals(true, tripToBeEdited.partOfPayments(chris));
        tripToBeEdited.getPayments().remove(2);
        Assert.assertEquals(true, tripToBeEdited.partOfPayments(chris));
        tripToBeEdited.getPayments().remove(1);
        Assert.assertEquals(false, tripToBeEdited.partOfPayments(chris));

    }

    /* ======================== Testdata ============================ */
    private void createTestData(TestDataSet testDataSet) {
        AmountFactory amountFactory = new AmountFactory();
        amountFactory.setCurrency(Currency.getInstance("EUR"));

        Trip trip = new Trip();
        trip.setName("Greece 2010 - Summer");

        List<Participant> participants = new ArrayList<Participant>();
        List<Payment> payments = new ArrayList<Payment>();

        /* For reuse. */
        Payment payment;
        Amount amount;
        Map<Participant, Amount> payerToPayment;
        Map<Participant, Amount> deptorToAmount;

        chris = new Participant();
        chris.setName("Christian");
        chris.setId(1);

        niko = new Participant();
        niko.setName("Nikolas");
        niko.setId(2);

        wolfram = new Participant();
        wolfram.setName("Wolfram");
        wolfram.setId(3);

        if (TestDataSet.DEFAULT.equals(testDataSet)) {

            participants.add(chris);
            participants.add(niko);
            participants.add(wolfram);

            /**/

            deptorToAmount = new HashMap<Participant, Amount>();
            payerToPayment = new HashMap<Participant, Amount>();

            amount = amountFactory.createAmount(30.00d);
            payerToPayment.put(chris, amount);

            amount = amountFactory.createAmount(-10.00d);
            deptorToAmount.put(chris, amount);
            amount = amountFactory.createAmount(-10.00d);
            deptorToAmount.put(niko, amount);
            amount = amountFactory.createAmount(-10.00d);
            deptorToAmount.put(wolfram, amount);

            payment = new Payment();
            payment.setCategory(PaymentCategory.FOOD);
            payment.setDescription("Essen in Mykonos");
            payment.setPayerToPayment(payerToPayment);
            payment.setDebitorToAmount(deptorToAmount);

            payments.add(payment);

            /**/

            deptorToAmount = new HashMap<Participant, Amount>();
            payerToPayment = new HashMap<Participant, Amount>();

            amount = amountFactory.createAmount(10.00d);
            payerToPayment.put(chris, amount);

            amount = amountFactory.createAmount(-5.00d);
            deptorToAmount.put(chris, amount);
            amount = amountFactory.createAmount(-5.00d);
            deptorToAmount.put(niko, amount);

            payment = new Payment();
            payment.setCategory(PaymentCategory.OTHER);
            payment.setDescription("Kippen");
            payment.setPayerToPayment(payerToPayment);
            payment.setDebitorToAmount(deptorToAmount);

            payments.add(payment);

            /**/

        }
        else if (TestDataSet.PAYMENTS_ONE_WITHOUT_CHRISTIAN.equals(testDataSet)) {

            participants.add(chris);
            participants.add(niko);
            participants.add(wolfram);

            /**/

            deptorToAmount = new HashMap<Participant, Amount>();
            payerToPayment = new HashMap<Participant, Amount>();

            amount = amountFactory.createAmount(10.00d);
            payerToPayment.put(wolfram, amount);

            amount = amountFactory.createAmount(-5.00d);
            deptorToAmount.put(wolfram, amount);
            amount = amountFactory.createAmount(-5.00d);
            deptorToAmount.put(niko, amount);

            payment = new Payment();
            payment.setCategory(PaymentCategory.OTHER);
            payment.setDescription("Kippen");
            payment.setPayerToPayment(payerToPayment);
            payment.setDebitorToAmount(deptorToAmount);

            payments.add(payment);

            /**/
            /**/

            deptorToAmount = new HashMap<Participant, Amount>();
            payerToPayment = new HashMap<Participant, Amount>();

            amount = amountFactory.createAmount(30.00d);
            payerToPayment.put(chris, amount);

            amount = amountFactory.createAmount(-10.00d);
            deptorToAmount.put(chris, amount);
            amount = amountFactory.createAmount(-10.00d);
            deptorToAmount.put(niko, amount);
            amount = amountFactory.createAmount(-10.00d);
            deptorToAmount.put(wolfram, amount);

            payment = new Payment();
            payment.setCategory(PaymentCategory.FOOD);
            payment.setDescription("Essen in Mykonos");
            payment.setPayerToPayment(payerToPayment);
            payment.setDebitorToAmount(deptorToAmount);

            payments.add(payment);

            /**/

            deptorToAmount = new HashMap<Participant, Amount>();
            payerToPayment = new HashMap<Participant, Amount>();

            amount = amountFactory.createAmount(30.00d);
            payerToPayment.put(chris, amount);

            amount = amountFactory.createAmount(-15.00d);
            deptorToAmount.put(niko, amount);
            amount = amountFactory.createAmount(-15.00d);
            deptorToAmount.put(wolfram, amount);

            payment = new Payment();
            payment.setCategory(PaymentCategory.FOOD);
            payment.setDescription("Essen in Mykonos2");
            payment.setPayerToPayment(payerToPayment);
            payment.setDebitorToAmount(deptorToAmount);

            payments.add(payment);

            /**/

            deptorToAmount = new HashMap<Participant, Amount>();
            payerToPayment = new HashMap<Participant, Amount>();

            amount = amountFactory.createAmount(30.00d);
            payerToPayment.put(niko, amount);

            amount = amountFactory.createAmount(-15.00d);
            deptorToAmount.put(chris, amount);
            amount = amountFactory.createAmount(-15.00d);
            deptorToAmount.put(wolfram, amount);

            payment = new Payment();
            payment.setCategory(PaymentCategory.FOOD);
            payment.setDescription("Essen in Mykonos3");
            payment.setPayerToPayment(payerToPayment);
            payment.setDebitorToAmount(deptorToAmount);

            payments.add(payment);

        }
        else if (TestDataSet.BLANK.equals(testDataSet)) {

            participants.add(chris);
            participants.add(niko);
            participants.add(wolfram);

            /**/

            deptorToAmount = new HashMap<Participant, Amount>();
            payerToPayment = new HashMap<Participant, Amount>();

            amount = amountFactory.createAmount(00.00d);
            payerToPayment.put(chris, amount);

            amount = amountFactory.createAmount(00.00d);
            deptorToAmount.put(chris, amount);
            amount = amountFactory.createAmount(00.00d);
            deptorToAmount.put(niko, amount);

            payment = new Payment();
            payment.setCategory(PaymentCategory.FOOD);
            payment.setDescription("Null entry");
            payment.setPayerToPayment(payerToPayment);
            payment.setDebitorToAmount(deptorToAmount);

            payments.add(payment);

            /**/
            deptorToAmount = new HashMap<Participant, Amount>();
            payerToPayment = new HashMap<Participant, Amount>();

            amount = amountFactory.createAmount(00.00d);
            payerToPayment.put(chris, amount);

            amount = amountFactory.createAmount(00.00d);
            deptorToAmount.put(chris, amount);
            amount = amountFactory.createAmount(00.00d);
            deptorToAmount.put(niko, amount);
            amount = amountFactory.createAmount(00.00d);
            deptorToAmount.put(wolfram, amount);

            payment = new Payment();
            payment.setCategory(PaymentCategory.GAS);
            payment.setDescription("Null entry 2");
            payment.setPayerToPayment(payerToPayment);
            payment.setDebitorToAmount(deptorToAmount);

            payments.add(payment);

        }
        else if (TestDataSet.FIRST_PAYMENT_HIGHER_THAN_FIRST_DEBITOR.equals(testDataSet)) {

            participants.add(chris);
            participants.add(niko);
            participants.add(wolfram);

            /**/

            deptorToAmount = new HashMap<Participant, Amount>();
            payerToPayment = new HashMap<Participant, Amount>();

            amount = amountFactory.createAmount(80.00d);
            payerToPayment.put(chris, amount);
            amount = amountFactory.createAmount(20.00d);
            payerToPayment.put(niko, amount);

            amount = amountFactory.createAmount(-5.00d);
            deptorToAmount.put(chris, amount);
            amount = amountFactory.createAmount(-10.00d);
            deptorToAmount.put(niko, amount);
            amount = amountFactory.createAmount(-85.00d);
            deptorToAmount.put(wolfram, amount);

            payment = new Payment();
            payment.setCategory(PaymentCategory.FOOD);
            payment.setDescription("FIRST_PAYMENT_HIGHER_THAN_FIRST_DEBITOR");
            payment.setPayerToPayment(payerToPayment);
            payment.setDebitorToAmount(deptorToAmount);

            payments.add(payment);

        }
        else if (TestDataSet.FIRST_PAYMENT_LOWER_THAN_FIRST_DEBITOR.equals(testDataSet)) {

            participants.add(chris);
            participants.add(niko);
            participants.add(wolfram);

            /**/

            deptorToAmount = new HashMap<Participant, Amount>();
            payerToPayment = new HashMap<Participant, Amount>();

            amount = amountFactory.createAmount(5.00d);
            payerToPayment.put(chris, amount);
            amount = amountFactory.createAmount(75.00d);
            payerToPayment.put(niko, amount);
            amount = amountFactory.createAmount(20.00d);
            payerToPayment.put(wolfram, amount);

            amount = amountFactory.createAmount(-85.00d);
            deptorToAmount.put(chris, amount);
            amount = amountFactory.createAmount(-15.00d);
            deptorToAmount.put(niko, amount);

            payment = new Payment();
            payment.setCategory(PaymentCategory.FOOD);
            payment.setDescription("FIRST_PAYMENT_LOWER_THAN_FIRST_DEBITOR");
            payment.setPayerToPayment(payerToPayment);
            payment.setDebitorToAmount(deptorToAmount);

            payments.add(payment);

        }

        trip.setParticipant(participants);
        trip.setPayments(payments);
        trip.setBaseCurrency(Currency.getInstance("EUR"));

        tripToBeEdited = trip;

    }

    private void updateAllTransientData(Trip tripToBeEdited2, TripReportLogic tripReportLogic) {
        amountFactory.setCurrency(tripToBeEdited.getBaseCurrency());
        tripReportLogic.setAmountFactory(amountFactory);

        List<Participant> participants = tripToBeEdited2.getParticipant();
        List<Payment> payments = tripToBeEdited2.getPayments();

        SumReport sumReport = tripReportLogic.createSumReport(participants, payments);
        tripToBeEdited2.setSumReport(sumReport);
        tripToBeEdited2.setDebts(tripReportLogic.createDebts2(participants, sumReport.getBalanceByUser()));

    }

}
