package de.koelle.christian.strategies;

import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import de.koelle.christian.common.utils.NumberUtils;
import de.koelle.christian.trickytripper.factories.AmountFactory;
import de.koelle.christian.trickytripper.factories.ModelFactory;
import de.koelle.christian.trickytripper.model.Amount;
import de.koelle.christian.trickytripper.model.Participant;
import de.koelle.christian.trickytripper.model.Payment;
import de.koelle.christian.trickytripper.model.PaymentCategory;
import de.koelle.christian.trickytripper.model.Trip;
import de.koelle.christian.trickytripper.strategies.TripReportLogic;
import de.koelle.christian.common.testsupport.TestDataFactory;
import de.koelle.christian.common.testsupport.TestDataSet;
import de.koelle.christian.common.testsupport.TransientDataHelper;

public class TripReportLogicTest {

    private Trip tripToBeEdited;

    private Participant chris;
    private Participant niko;
    private Participant wolfram;

    private final AmountFactory amountFactory = new AmountFactory();
    private TestDataFactory testDatafactory;
    private TripReportLogic tripReportLogic;

    @Before
    public void init() {
        testDatafactory = new TestDataFactory();
        tripReportLogic = new TripReportLogic();

        chris = testDatafactory.chris;
        niko = testDatafactory.niko;
        wolfram = testDatafactory.wolfram;
    }

    @Test
    public void testBalanceLogicDefault() {
        tripToBeEdited = testDatafactory.createTestData(TestDataSet.DEFAULT);
        TransientDataHelper.updateAllTransientData(tripToBeEdited, tripReportLogic, amountFactory);

        Assert.assertEquals(25.00d, tripToBeEdited.getSumReport().getBalanceByUser().get(chris).getValue());
        Assert.assertEquals(-15.00d, tripToBeEdited.getSumReport().getBalanceByUser().get(niko).getValue());
        Assert.assertEquals(-10.00d, tripToBeEdited.getSumReport().getBalanceByUser().get(wolfram).getValue());

        Assert.assertEquals(40.00d, tripToBeEdited.getSumReport().getPaymentByUser().get(chris).getValue());
        Assert.assertEquals(0.00d, tripToBeEdited.getSumReport().getPaymentByUser().get(niko).getValue());
        Assert.assertEquals(0.00d, tripToBeEdited.getSumReport().getPaymentByUser().get(wolfram).getValue());

        Assert.assertEquals(-15.00d, tripToBeEdited.getSumReport().getSpendingByUser().get(chris).getValue());
        Assert.assertEquals(-15.00d, tripToBeEdited.getSumReport().getSpendingByUser().get(niko).getValue());
        Assert.assertEquals(-10.00d, tripToBeEdited.getSumReport().getSpendingByUser().get(wolfram).getValue());

        Assert.assertEquals(true, tripToBeEdited.getDebts().get(chris).getLoanerToDebts().isEmpty());

        Assert.assertEquals(1, tripToBeEdited.getDebts().get(niko).getLoanerToDebts().size());
        Assert.assertEquals(15.00d, tripToBeEdited.getDebts().get(niko).getLoanerToDebts().get(chris).getValue());

        Assert.assertEquals(1, tripToBeEdited.getDebts().get(wolfram).getLoanerToDebts().size());
        Assert.assertEquals(10.00d, tripToBeEdited.getDebts().get(wolfram).getLoanerToDebts().get(chris).getValue());

    }

    @Test
    public void testBalanceLogicMoneyTransfer() {
        tripToBeEdited = testDatafactory.createTestData(TestDataSet.DEFAULT);

        TransientDataHelper.updateAllTransientData(tripToBeEdited, tripReportLogic, amountFactory);

        Payment moneyTransfer;

        // Already tested

        moneyTransfer = ModelFactory.createNewPayment("Transfer 01", PaymentCategory.MONEY_TRANSFER);
        moneyTransfer.getParticipantToPayment().put(chris, amountFactory.createAmount(-15d));
        moneyTransfer.getParticipantToSpending().put(niko, amountFactory.createAmount(15d));
        tripToBeEdited.getPayments().add(moneyTransfer);

        TransientDataHelper.updateAllTransientData(tripToBeEdited, tripReportLogic, amountFactory);

        Assert.assertEquals(10.00d, tripToBeEdited.getSumReport().getBalanceByUser().get(chris).getValue());
        Assert.assertEquals(0.00d, tripToBeEdited.getSumReport().getBalanceByUser().get(niko).getValue());
        Assert.assertEquals(-10.00d, tripToBeEdited.getSumReport().getBalanceByUser().get(wolfram).getValue());

        Assert.assertEquals(25.00d, tripToBeEdited.getSumReport().getPaymentByUser().get(chris).getValue());
        Assert.assertEquals(15.00d, tripToBeEdited.getSumReport().getPaymentByUser().get(niko).getValue());
        Assert.assertEquals(0.00d, tripToBeEdited.getSumReport().getPaymentByUser().get(wolfram).getValue());

        Assert.assertEquals(-15.00d, tripToBeEdited.getSumReport().getSpendingByUser().get(chris).getValue());
        Assert.assertEquals(-15.00d, tripToBeEdited.getSumReport().getSpendingByUser().get(niko).getValue());
        Assert.assertEquals(-10.00d, tripToBeEdited.getSumReport().getSpendingByUser().get(wolfram).getValue());

        Assert.assertEquals(true, tripToBeEdited.getDebts().get(chris).getLoanerToDebts().isEmpty());
        Assert.assertEquals(true, tripToBeEdited.getDebts().get(niko).getLoanerToDebts().isEmpty());

        Assert.assertEquals(1, tripToBeEdited.getDebts().get(wolfram).getLoanerToDebts().size());
        Assert.assertEquals(10.00d, tripToBeEdited.getDebts().get(wolfram).getLoanerToDebts().get(chris).getValue());

        /**/

        moneyTransfer = ModelFactory.createNewPayment("Transfer 02", PaymentCategory.MONEY_TRANSFER);
        moneyTransfer.getParticipantToPayment().put(chris, amountFactory.createAmount(-10d));
        moneyTransfer.getParticipantToSpending().put(wolfram, amountFactory.createAmount(10d));
        tripToBeEdited.getPayments().add(moneyTransfer);

        TransientDataHelper.updateAllTransientData(tripToBeEdited, tripReportLogic, amountFactory);

        Assert.assertEquals(0.00d, tripToBeEdited.getSumReport().getBalanceByUser().get(chris).getValue());
        Assert.assertEquals(0.00d, tripToBeEdited.getSumReport().getBalanceByUser().get(niko).getValue());
        Assert.assertEquals(0.00d, tripToBeEdited.getSumReport().getBalanceByUser().get(wolfram).getValue());

        Assert.assertEquals(15.00d, tripToBeEdited.getSumReport().getPaymentByUser().get(chris).getValue());
        Assert.assertEquals(15.00d, tripToBeEdited.getSumReport().getPaymentByUser().get(niko).getValue());
        Assert.assertEquals(10.00d, tripToBeEdited.getSumReport().getPaymentByUser().get(wolfram).getValue());

        Assert.assertEquals(-15.00d, tripToBeEdited.getSumReport().getSpendingByUser().get(chris).getValue());
        Assert.assertEquals(-15.00d, tripToBeEdited.getSumReport().getSpendingByUser().get(niko).getValue());
        Assert.assertEquals(-10.00d, tripToBeEdited.getSumReport().getSpendingByUser().get(wolfram).getValue());

        Assert.assertEquals(true, tripToBeEdited.getDebts().get(chris).getLoanerToDebts().isEmpty());
        Assert.assertEquals(true, tripToBeEdited.getDebts().get(niko).getLoanerToDebts().isEmpty());
        Assert.assertEquals(true, tripToBeEdited.getDebts().get(niko).getLoanerToDebts().isEmpty());

    }

    @Test
    public void testBalanceLogicMoneyTransferInFragments() {
        tripToBeEdited = testDatafactory.createTestData(TestDataSet.DEFAULT);
        TransientDataHelper.updateAllTransientData(tripToBeEdited, tripReportLogic, amountFactory);

        Payment moneyTransfer;

        // Already tested

        moneyTransfer = ModelFactory.createNewPayment("Transfer 01", PaymentCategory.MONEY_TRANSFER);
        moneyTransfer.getParticipantToPayment().put(chris, amountFactory.createAmount(-2d));
        moneyTransfer.getParticipantToSpending().put(niko, amountFactory.createAmount(2d));
        tripToBeEdited.getPayments().add(moneyTransfer);

        TransientDataHelper.updateAllTransientData(tripToBeEdited, tripReportLogic, amountFactory);

        Assert.assertEquals(23.00d, tripToBeEdited.getSumReport().getBalanceByUser().get(chris).getValue());
        Assert.assertEquals(-13.00d, tripToBeEdited.getSumReport().getBalanceByUser().get(niko).getValue());
        Assert.assertEquals(-10.00d, tripToBeEdited.getSumReport().getBalanceByUser().get(wolfram).getValue());

        Assert.assertEquals(38.00d, tripToBeEdited.getSumReport().getPaymentByUser().get(chris).getValue());
        Assert.assertEquals(2.00d, tripToBeEdited.getSumReport().getPaymentByUser().get(niko).getValue());
        Assert.assertEquals(0.00d, tripToBeEdited.getSumReport().getPaymentByUser().get(wolfram).getValue());

        Assert.assertEquals(-15.00d, tripToBeEdited.getSumReport().getSpendingByUser().get(chris).getValue());
        Assert.assertEquals(-15.00d, tripToBeEdited.getSumReport().getSpendingByUser().get(niko).getValue());
        Assert.assertEquals(-10.00d, tripToBeEdited.getSumReport().getSpendingByUser().get(wolfram).getValue());

        Assert.assertEquals(true, tripToBeEdited.getDebts().get(chris).getLoanerToDebts().isEmpty());
        Assert.assertEquals(1, tripToBeEdited.getDebts().get(niko).getLoanerToDebts().size());
        Assert.assertEquals(13.00d, tripToBeEdited.getDebts().get(niko).getLoanerToDebts().get(chris).getValue());

        Assert.assertEquals(1, tripToBeEdited.getDebts().get(wolfram).getLoanerToDebts().size());
        Assert.assertEquals(10.00d, tripToBeEdited.getDebts().get(wolfram).getLoanerToDebts().get(chris).getValue());

        /**/

        moneyTransfer = ModelFactory.createNewPayment("Transfer 02", PaymentCategory.MONEY_TRANSFER);
        moneyTransfer.getParticipantToPayment().put(chris, amountFactory.createAmount(-3d));
        moneyTransfer.getParticipantToSpending().put(niko, amountFactory.createAmount(3d));
        tripToBeEdited.getPayments().add(moneyTransfer);

        TransientDataHelper.updateAllTransientData(tripToBeEdited, tripReportLogic, amountFactory);

        Assert.assertEquals(20.00d, tripToBeEdited.getSumReport().getBalanceByUser().get(chris).getValue());
        Assert.assertEquals(-10.00d, tripToBeEdited.getSumReport().getBalanceByUser().get(niko).getValue());
        Assert.assertEquals(-10.00d, tripToBeEdited.getSumReport().getBalanceByUser().get(wolfram).getValue());

        Assert.assertEquals(35.00d, tripToBeEdited.getSumReport().getPaymentByUser().get(chris).getValue());
        Assert.assertEquals(5.00d, tripToBeEdited.getSumReport().getPaymentByUser().get(niko).getValue());
        Assert.assertEquals(0.00d, tripToBeEdited.getSumReport().getPaymentByUser().get(wolfram).getValue());

        Assert.assertEquals(-15.00d, tripToBeEdited.getSumReport().getSpendingByUser().get(chris).getValue());
        Assert.assertEquals(-15.00d, tripToBeEdited.getSumReport().getSpendingByUser().get(niko).getValue());
        Assert.assertEquals(-10.00d, tripToBeEdited.getSumReport().getSpendingByUser().get(wolfram).getValue());

        Assert.assertEquals(true, tripToBeEdited.getDebts().get(chris).getLoanerToDebts().isEmpty());
        Assert.assertEquals(1, tripToBeEdited.getDebts().get(niko).getLoanerToDebts().size());
        Assert.assertEquals(10.00d, tripToBeEdited.getDebts().get(niko).getLoanerToDebts().get(chris).getValue());

        Assert.assertEquals(1, tripToBeEdited.getDebts().get(wolfram).getLoanerToDebts().size());
        Assert.assertEquals(10.00d, tripToBeEdited.getDebts().get(wolfram).getLoanerToDebts().get(chris).getValue());

        moneyTransfer = ModelFactory.createNewPayment("Transfer 03", PaymentCategory.MONEY_TRANSFER);
        moneyTransfer.getParticipantToPayment().put(chris, amountFactory.createAmount(-0.01d));
        moneyTransfer.getParticipantToSpending().put(niko, amountFactory.createAmount(0.01d));
        tripToBeEdited.getPayments().add(moneyTransfer);

        TransientDataHelper.updateAllTransientData(tripToBeEdited, tripReportLogic, amountFactory);

        Assert.assertEquals(19.99d, tripToBeEdited.getSumReport().getBalanceByUser().get(chris).getValue());
        Assert.assertEquals(-9.99d, tripToBeEdited.getSumReport().getBalanceByUser().get(niko).getValue());
        Assert.assertEquals(-10.00d, tripToBeEdited.getSumReport().getBalanceByUser().get(wolfram).getValue());

        Assert.assertEquals(34.99d, tripToBeEdited.getSumReport().getPaymentByUser().get(chris).getValue());
        Assert.assertEquals(5.01d, tripToBeEdited.getSumReport().getPaymentByUser().get(niko).getValue());
        Assert.assertEquals(0.00d, tripToBeEdited.getSumReport().getPaymentByUser().get(wolfram).getValue());

        Assert.assertEquals(-15.00d, tripToBeEdited.getSumReport().getSpendingByUser().get(chris).getValue());
        Assert.assertEquals(-15.00d, tripToBeEdited.getSumReport().getSpendingByUser().get(niko).getValue());
        Assert.assertEquals(-10.00d, tripToBeEdited.getSumReport().getSpendingByUser().get(wolfram).getValue());

        Assert.assertEquals(true, tripToBeEdited.getDebts().get(chris).getLoanerToDebts().isEmpty());
        Assert.assertEquals(1, tripToBeEdited.getDebts().get(niko).getLoanerToDebts().size());
        Assert.assertEquals(9.99d, tripToBeEdited.getDebts().get(niko).getLoanerToDebts().get(chris).getValue());

        Assert.assertEquals(1, tripToBeEdited.getDebts().get(wolfram).getLoanerToDebts().size());
        Assert.assertEquals(10.00d, tripToBeEdited.getDebts().get(wolfram).getLoanerToDebts().get(chris).getValue());

    }

    @Test
    public void testBalanceLogicBlank() {
        tripToBeEdited = testDatafactory.createTestData(TestDataSet.BLANK);
        TransientDataHelper.updateAllTransientData(tripToBeEdited, tripReportLogic, amountFactory);

        Assert.assertEquals(0.00d, tripToBeEdited.getSumReport().getBalanceByUser().get(chris).getValue());
        Assert.assertEquals(0.00d, tripToBeEdited.getSumReport().getBalanceByUser().get(niko).getValue());
        Assert.assertEquals(0.00d, tripToBeEdited.getSumReport().getBalanceByUser().get(wolfram).getValue());

        Assert.assertEquals(0.00d, tripToBeEdited.getSumReport().getPaymentByUser().get(chris).getValue());
        Assert.assertEquals(0.00d, tripToBeEdited.getSumReport().getPaymentByUser().get(niko).getValue());
        Assert.assertEquals(0.00d, tripToBeEdited.getSumReport().getPaymentByUser().get(wolfram).getValue());

        Assert.assertEquals(0.00d, tripToBeEdited.getSumReport().getSpendingByUser().get(chris).getValue());
        Assert.assertEquals(0.00d, tripToBeEdited.getSumReport().getSpendingByUser().get(niko).getValue());
        Assert.assertEquals(0.00d, tripToBeEdited.getSumReport().getSpendingByUser().get(wolfram).getValue());

        Assert.assertEquals(true, tripToBeEdited.getDebts().get(chris).getLoanerToDebts().isEmpty());
        Assert.assertEquals(true, tripToBeEdited.getDebts().get(chris).getLoanerToDebts().isEmpty());
        Assert.assertEquals(true, tripToBeEdited.getDebts().get(chris).getLoanerToDebts().isEmpty());

    }

    @Test
    public void testBalanceLogicFirstPaymentHigherThanFirstDebitor() {
        tripToBeEdited = testDatafactory.createTestData(TestDataSet.FIRST_PAYMENT_HIGHER_THAN_FIRST_DEBITOR);
        TransientDataHelper.updateAllTransientData(tripToBeEdited, tripReportLogic, amountFactory);

        Assert.assertEquals(75.00d, tripToBeEdited.getSumReport().getBalanceByUser().get(chris).getValue());
        Assert.assertEquals(10.00d, tripToBeEdited.getSumReport().getBalanceByUser().get(niko).getValue());
        Assert.assertEquals(-85.00d, tripToBeEdited.getSumReport().getBalanceByUser().get(wolfram).getValue());

        Assert.assertEquals(80.00d, tripToBeEdited.getSumReport().getPaymentByUser().get(chris).getValue());
        Assert.assertEquals(20.00d, tripToBeEdited.getSumReport().getPaymentByUser().get(niko).getValue());
        Assert.assertEquals(0.00d, tripToBeEdited.getSumReport().getPaymentByUser().get(wolfram).getValue());

        Assert.assertEquals(-5.00d, tripToBeEdited.getSumReport().getSpendingByUser().get(chris).getValue());
        Assert.assertEquals(-10.00d, tripToBeEdited.getSumReport().getSpendingByUser().get(niko).getValue());
        Assert.assertEquals(-85.00d, tripToBeEdited.getSumReport().getSpendingByUser().get(wolfram).getValue());

        Assert.assertEquals(true, tripToBeEdited.getDebts().get(chris).getLoanerToDebts().isEmpty());
        Assert.assertEquals(true, tripToBeEdited.getDebts().get(niko).getLoanerToDebts().isEmpty());

        Assert.assertEquals(2, tripToBeEdited.getDebts().get(wolfram).getLoanerToDebts().size());
        Assert.assertEquals(75.00d, tripToBeEdited.getDebts().get(wolfram).getLoanerToDebts().get(chris).getValue());
        Assert.assertEquals(10.00d, tripToBeEdited.getDebts().get(wolfram).getLoanerToDebts().get(niko).getValue());

    }

    @Test
    public void testBalanceLogicFirstPaymentLowerThanFirstDebitor() {
        tripToBeEdited = testDatafactory.createTestData(TestDataSet.FIRST_PAYMENT_LOWER_THAN_FIRST_DEBITOR);
        TransientDataHelper.updateAllTransientData(tripToBeEdited, tripReportLogic, amountFactory);

        Assert.assertEquals(-80.00d, tripToBeEdited.getSumReport().getBalanceByUser().get(chris).getValue());
        Assert.assertEquals(60.00d, tripToBeEdited.getSumReport().getBalanceByUser().get(niko).getValue());
        Assert.assertEquals(20.00d, tripToBeEdited.getSumReport().getBalanceByUser().get(wolfram).getValue());

        Assert.assertEquals(5.00d, tripToBeEdited.getSumReport().getPaymentByUser().get(chris).getValue());
        Assert.assertEquals(75.00d, tripToBeEdited.getSumReport().getPaymentByUser().get(niko).getValue());
        Assert.assertEquals(20.00d, tripToBeEdited.getSumReport().getPaymentByUser().get(wolfram).getValue());

        Assert.assertEquals(-85.00d, tripToBeEdited.getSumReport().getSpendingByUser().get(chris).getValue());
        Assert.assertEquals(-15.00d, tripToBeEdited.getSumReport().getSpendingByUser().get(niko).getValue());
        Assert.assertEquals(0.00d, tripToBeEdited.getSumReport().getSpendingByUser().get(wolfram).getValue());

        Assert.assertEquals(2, tripToBeEdited.getDebts().get(chris).getLoanerToDebts().size());
        Assert.assertEquals(60.00d, tripToBeEdited.getDebts().get(chris).getLoanerToDebts().get(niko).getValue());
        Assert.assertEquals(20.00d, tripToBeEdited.getDebts().get(chris).getLoanerToDebts().get(wolfram).getValue());

        Assert.assertEquals(true, tripToBeEdited.getDebts().get(niko).getLoanerToDebts().isEmpty());
        Assert.assertEquals(true, tripToBeEdited.getDebts().get(wolfram).getLoanerToDebts().isEmpty());

    }

    @Test
    public void testBalanceDebtsFlipping() {
        tripToBeEdited = testDatafactory.createTestData(TestDataSet.DEFAULT);
        TransientDataHelper.updateAllTransientData(tripToBeEdited, tripReportLogic, amountFactory);

        Assert.assertEquals(true, tripToBeEdited.getDebts().get(chris).getLoanerToDebts().isEmpty());

        Assert.assertEquals(1, tripToBeEdited.getDebts().get(niko).getLoanerToDebts().size());
        Assert.assertEquals(15.00d, tripToBeEdited.getDebts().get(niko).getLoanerToDebts().get(chris).getValue());

        Assert.assertEquals(1, tripToBeEdited.getDebts().get(niko).getLoanerToDebts().size());
        Assert.assertEquals(10.00d, tripToBeEdited.getDebts().get(wolfram).getLoanerToDebts().get(chris).getValue());

        Assert.assertEquals(25.00d, tripToBeEdited.getSumReport().getBalanceByUser().get(chris).getValue());
        Assert.assertEquals(-15.00d, tripToBeEdited.getSumReport().getBalanceByUser().get(niko).getValue());
        Assert.assertEquals(-10.00d, tripToBeEdited.getSumReport().getBalanceByUser().get(wolfram).getValue());

        /* ========== */
        addNewSplitPayment(niko, 60.00d);
        TransientDataHelper.updateAllTransientData(tripToBeEdited, tripReportLogic, amountFactory);
        /* ========== */

        Assert.assertEquals(5.00d, tripToBeEdited.getSumReport().getBalanceByUser().get(chris).getValue());
        Assert.assertEquals(25.00d, tripToBeEdited.getSumReport().getBalanceByUser().get(niko).getValue());
        Assert.assertEquals(-30.00d, tripToBeEdited.getSumReport().getBalanceByUser().get(wolfram).getValue());

        Assert.assertEquals(0, tripToBeEdited.getDebts().get(chris).getLoanerToDebts().size());

        Assert.assertEquals(0, tripToBeEdited.getDebts().get(niko).getLoanerToDebts().size());

        Assert.assertEquals(2, tripToBeEdited.getDebts().get(wolfram).getLoanerToDebts().size());
        Assert.assertEquals(5.00d, tripToBeEdited.getDebts().get(wolfram).getLoanerToDebts().get(chris).getValue());
        Assert.assertEquals(25.00d, tripToBeEdited.getDebts().get(wolfram).getLoanerToDebts().get(niko).getValue());

        /* ========== */
        addNewSplitPayment(wolfram, 90.00d);
        TransientDataHelper.updateAllTransientData(tripToBeEdited, tripReportLogic, amountFactory);
        /* ========== */

        Assert.assertEquals(-25.00d, tripToBeEdited.getSumReport().getBalanceByUser().get(chris).getValue());
        Assert.assertEquals(-5.00d, tripToBeEdited.getSumReport().getBalanceByUser().get(niko).getValue());
        Assert.assertEquals(30.00d, tripToBeEdited.getSumReport().getBalanceByUser().get(wolfram).getValue());

        Assert.assertEquals(1, tripToBeEdited.getDebts().get(chris).getLoanerToDebts().size());
        Assert.assertEquals(25.00d, tripToBeEdited.getDebts().get(chris).getLoanerToDebts().get(wolfram).getValue());

        Assert.assertEquals(1, tripToBeEdited.getDebts().get(niko).getLoanerToDebts().size());
        Assert.assertEquals(5.00d, tripToBeEdited.getDebts().get(niko).getLoanerToDebts().get(wolfram).getValue());

        Assert.assertEquals(0, tripToBeEdited.getDebts().get(wolfram).getLoanerToDebts().size());
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
        tripToBeEdited = testDatafactory.createTestData(TestDataSet.PAYMENTS_ONE_WITHOUT_CHRISTIAN);
        TransientDataHelper.updateAllTransientData(tripToBeEdited, tripReportLogic, amountFactory);

        Assert.assertEquals(true, tripToBeEdited.partOfPayments(chris));
        tripToBeEdited.getPayments().remove(3);
        Assert.assertEquals(true, tripToBeEdited.partOfPayments(chris));
        tripToBeEdited.getPayments().remove(2);
        Assert.assertEquals(true, tripToBeEdited.partOfPayments(chris));
        tripToBeEdited.getPayments().remove(1);
        Assert.assertEquals(false, tripToBeEdited.partOfPayments(chris));

    }

}
