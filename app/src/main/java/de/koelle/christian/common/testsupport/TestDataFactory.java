package de.koelle.christian.common.testsupport;

import java.util.ArrayList;
import java.util.Currency;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.koelle.christian.trickytripper.factories.AmountFactory;
import de.koelle.christian.trickytripper.model.Amount;
import de.koelle.christian.trickytripper.model.Participant;
import de.koelle.christian.trickytripper.model.Payment;
import de.koelle.christian.trickytripper.model.PaymentCategory;
import de.koelle.christian.trickytripper.model.Trip;

public class TestDataFactory {

    public Participant chris;
    public Participant niko;
    public Participant wolfram;

    public AmountFactory amountFactory;

    public TestDataFactory() {
        chris = new Participant();
        chris.setName("Christian");
        chris.setId(1);

        niko = new Participant();
        niko.setName("Nikolas");
        niko.setId(2);

        wolfram = new Participant();
        wolfram.setName("Wolfram");
        wolfram.setId(3);

        amountFactory = new AmountFactory();
        amountFactory.setCurrency(Currency.getInstance("EUR"));
    }

    /**
     * Creates a trip for testing purposes.
     * 
     * @param testDataSet
     *            The set to be created.
     * @return The created test data trip.
     */
    public Trip createTestData(TestDataSet testDataSet) {

        Trip trip = new Trip();
        trip.setName("Greece 2010 - Summer");

        List<Participant> participants = new ArrayList<Participant>();
        List<Payment> payments = new ArrayList<Payment>();

        /* For reuse. */
        Payment payment;
        Amount amount;
        Map<Participant, Amount> payerToPayment;
        Map<Participant, Amount> debitorToAmount;

        if (TestDataSet.DEFAULT.equals(testDataSet)) {

            participants.add(chris);
            participants.add(niko);
            participants.add(wolfram);

            /**/

            debitorToAmount = new HashMap<Participant, Amount>();
            payerToPayment = new HashMap<Participant, Amount>();

            amount = amountFactory.createAmount(30.00d);
            payerToPayment.put(chris, amount);

            amount = amountFactory.createAmount(-10.00d);
            debitorToAmount.put(chris, amount);
            amount = amountFactory.createAmount(-10.00d);
            debitorToAmount.put(niko, amount);
            amount = amountFactory.createAmount(-10.00d);
            debitorToAmount.put(wolfram, amount);

            payment = new Payment();
            payment.setCategory(PaymentCategory.FOOD);
            payment.setDescription("Essen in Mykonos");
            payment.setPayerToPayment(payerToPayment);
            payment.setDebitorToAmount(debitorToAmount);
            payment.setPaymentDateTime(new GregorianCalendar(1996, 5, 22, 18, 40, 50).getTime());

            payments.add(payment);

            /**/

            debitorToAmount = new HashMap<Participant, Amount>();
            payerToPayment = new HashMap<Participant, Amount>();

            amount = amountFactory.createAmount(10.00d);
            payerToPayment.put(chris, amount);

            amount = amountFactory.createAmount(-5.00d);
            debitorToAmount.put(chris, amount);
            amount = amountFactory.createAmount(-5.00d);
            debitorToAmount.put(niko, amount);

            payment = new Payment();
            payment.setCategory(PaymentCategory.OTHER);
            payment.setDescription("Kippen");
            payment.setPayerToPayment(payerToPayment);
            payment.setDebitorToAmount(debitorToAmount);
            payment.setPaymentDateTime(new GregorianCalendar(1997, 6, 23, 19, 41, 51).getTime());

            payments.add(payment);

            /**/

        }
        else if (TestDataSet.PAYMENTS_ONE_WITHOUT_CHRISTIAN.equals(testDataSet)) {

            participants.add(chris);
            participants.add(niko);
            participants.add(wolfram);

            /**/

            debitorToAmount = new HashMap<Participant, Amount>();
            payerToPayment = new HashMap<Participant, Amount>();

            amount = amountFactory.createAmount(10.00d);
            payerToPayment.put(wolfram, amount);

            amount = amountFactory.createAmount(-5.00d);
            debitorToAmount.put(wolfram, amount);
            amount = amountFactory.createAmount(-5.00d);
            debitorToAmount.put(niko, amount);

            payment = new Payment();
            payment.setCategory(PaymentCategory.OTHER);
            payment.setDescription("Kippen");
            payment.setPayerToPayment(payerToPayment);
            payment.setDebitorToAmount(debitorToAmount);

            payments.add(payment);

            /**/
            /**/

            debitorToAmount = new HashMap<Participant, Amount>();
            payerToPayment = new HashMap<Participant, Amount>();

            amount = amountFactory.createAmount(30.00d);
            payerToPayment.put(chris, amount);

            amount = amountFactory.createAmount(-10.00d);
            debitorToAmount.put(chris, amount);
            amount = amountFactory.createAmount(-10.00d);
            debitorToAmount.put(niko, amount);
            amount = amountFactory.createAmount(-10.00d);
            debitorToAmount.put(wolfram, amount);

            payment = new Payment();
            payment.setCategory(PaymentCategory.FOOD);
            payment.setDescription("Essen in Mykonos");
            payment.setPayerToPayment(payerToPayment);
            payment.setDebitorToAmount(debitorToAmount);

            payments.add(payment);

            /**/

            debitorToAmount = new HashMap<Participant, Amount>();
            payerToPayment = new HashMap<Participant, Amount>();

            amount = amountFactory.createAmount(30.00d);
            payerToPayment.put(chris, amount);

            amount = amountFactory.createAmount(-15.00d);
            debitorToAmount.put(niko, amount);
            amount = amountFactory.createAmount(-15.00d);
            debitorToAmount.put(wolfram, amount);

            payment = new Payment();
            payment.setCategory(PaymentCategory.FOOD);
            payment.setDescription("Essen in Mykonos2");
            payment.setPayerToPayment(payerToPayment);
            payment.setDebitorToAmount(debitorToAmount);

            payments.add(payment);

            /**/

            debitorToAmount = new HashMap<Participant, Amount>();
            payerToPayment = new HashMap<Participant, Amount>();

            amount = amountFactory.createAmount(30.00d);
            payerToPayment.put(niko, amount);

            amount = amountFactory.createAmount(-15.00d);
            debitorToAmount.put(chris, amount);
            amount = amountFactory.createAmount(-15.00d);
            debitorToAmount.put(wolfram, amount);

            payment = new Payment();
            payment.setCategory(PaymentCategory.FOOD);
            payment.setDescription("Essen in Mykonos3");
            payment.setPayerToPayment(payerToPayment);
            payment.setDebitorToAmount(debitorToAmount);

            payments.add(payment);

        }
        else if (TestDataSet.BLANK.equals(testDataSet)) {

            participants.add(chris);
            participants.add(niko);
            participants.add(wolfram);

            /**/

            debitorToAmount = new HashMap<Participant, Amount>();
            payerToPayment = new HashMap<Participant, Amount>();

            amount = amountFactory.createAmount(00.00d);
            payerToPayment.put(chris, amount);

            amount = amountFactory.createAmount(00.00d);
            debitorToAmount.put(chris, amount);
            amount = amountFactory.createAmount(00.00d);
            debitorToAmount.put(niko, amount);

            payment = new Payment();
            payment.setCategory(PaymentCategory.FOOD);
            payment.setDescription("Null entry");
            payment.setPayerToPayment(payerToPayment);
            payment.setDebitorToAmount(debitorToAmount);

            payments.add(payment);

            /**/
            debitorToAmount = new HashMap<Participant, Amount>();
            payerToPayment = new HashMap<Participant, Amount>();

            amount = amountFactory.createAmount(00.00d);
            payerToPayment.put(chris, amount);

            amount = amountFactory.createAmount(00.00d);
            debitorToAmount.put(chris, amount);
            amount = amountFactory.createAmount(00.00d);
            debitorToAmount.put(niko, amount);
            amount = amountFactory.createAmount(00.00d);
            debitorToAmount.put(wolfram, amount);

            payment = new Payment();
            payment.setCategory(PaymentCategory.GAS);
            payment.setDescription("Null entry 2");
            payment.setPayerToPayment(payerToPayment);
            payment.setDebitorToAmount(debitorToAmount);

            payments.add(payment);

        }
        else if (TestDataSet.FIRST_PAYMENT_HIGHER_THAN_FIRST_DEBITOR.equals(testDataSet)) {

            participants.add(chris);
            participants.add(niko);
            participants.add(wolfram);

            /**/

            debitorToAmount = new HashMap<Participant, Amount>();
            payerToPayment = new HashMap<Participant, Amount>();

            amount = amountFactory.createAmount(80.00d);
            payerToPayment.put(chris, amount);
            amount = amountFactory.createAmount(20.00d);
            payerToPayment.put(niko, amount);

            amount = amountFactory.createAmount(-5.00d);
            debitorToAmount.put(chris, amount);
            amount = amountFactory.createAmount(-10.00d);
            debitorToAmount.put(niko, amount);
            amount = amountFactory.createAmount(-85.00d);
            debitorToAmount.put(wolfram, amount);

            payment = new Payment();
            payment.setCategory(PaymentCategory.FOOD);
            payment.setDescription("FIRST_PAYMENT_HIGHER_THAN_FIRST_DEBITOR");
            payment.setPayerToPayment(payerToPayment);
            payment.setDebitorToAmount(debitorToAmount);

            payments.add(payment);

        }
        else if (TestDataSet.FIRST_PAYMENT_LOWER_THAN_FIRST_DEBITOR.equals(testDataSet)) {

            participants.add(chris);
            participants.add(niko);
            participants.add(wolfram);

            /**/

            debitorToAmount = new HashMap<Participant, Amount>();
            payerToPayment = new HashMap<Participant, Amount>();

            amount = amountFactory.createAmount(5.00d);
            payerToPayment.put(chris, amount);
            amount = amountFactory.createAmount(75.00d);
            payerToPayment.put(niko, amount);
            amount = amountFactory.createAmount(20.00d);
            payerToPayment.put(wolfram, amount);

            amount = amountFactory.createAmount(-85.00d);
            debitorToAmount.put(chris, amount);
            amount = amountFactory.createAmount(-15.00d);
            debitorToAmount.put(niko, amount);

            payment = new Payment();
            payment.setCategory(PaymentCategory.FOOD);
            payment.setDescription("FIRST_PAYMENT_LOWER_THAN_FIRST_DEBITOR");
            payment.setPayerToPayment(payerToPayment);
            payment.setDebitorToAmount(debitorToAmount);

            payments.add(payment);

        }

        trip.setParticipant(participants);
        trip.setPayments(payments);
        trip.setBaseCurrency(Currency.getInstance("EUR"));

        return trip;

    }
}
