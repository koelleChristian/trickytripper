package de.koelle.christian.trickytripper.activitysupport;

import android.os.Bundle;
import de.koelle.christian.trickytripper.constants.Rd;
import de.koelle.christian.trickytripper.model.Participant;
import de.koelle.christian.trickytripper.model.Payment;

public class TabDialogSupport {

    public static Bundle createBundleWithParticipantSelected(Participant selectedParticipant) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Rd.DIALOG_PARAM_PARTICIPANT, selectedParticipant);
        return bundle;
    }

    public static Bundle createBundleWithPaymentSelected(Payment selectedPayment) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Rd.DIALOG_PARAM_PAYMENT, selectedPayment);
        return bundle;
    }

    public static Participant getParticipantFromBundle(Bundle args) {
        if (args == null) {
            return null; // Create
        }
        // Edit
        Participant participant = (Participant) args.get(Rd.DIALOG_PARAM_PARTICIPANT);
        return participant;
    }

    public static Payment getPaymentFromBundle(Bundle args) {
        if (args == null) {
            return null;
        }
        Payment payment = (Payment) args.get(Rd.DIALOG_PARAM_PAYMENT);
        return payment;
    }
}
