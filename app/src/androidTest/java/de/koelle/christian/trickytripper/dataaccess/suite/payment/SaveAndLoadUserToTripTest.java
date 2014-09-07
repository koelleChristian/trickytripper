package de.koelle.christian.trickytripper.dataaccess.suite.payment;

import java.util.Currency;

import junit.framework.Assert;
import android.test.ApplicationTestCase;
import de.koelle.christian.trickytripper.TrickyTripperApp;
import de.koelle.christian.trickytripper.dataaccess.impl.DataConstants;
import de.koelle.christian.trickytripper.dataaccess.impl.DataManagerImpl;
import de.koelle.christian.trickytripper.dataaccess.suite.util.AssertionUtil;
import de.koelle.christian.trickytripper.factories.ModelFactory;
import de.koelle.christian.trickytripper.model.Participant;
import de.koelle.christian.trickytripper.model.Trip;

public class SaveAndLoadUserToTripTest extends ApplicationTestCase<TrickyTripperApp> {

    public SaveAndLoadUserToTripTest() {
        super(TrickyTripperApp.class);
    }

    @Override
    protected void setUp() {
        getContext().deleteDatabase(DataConstants.DATABASE_NAME);
    }

    public void testSaveAndLoadUserToTrip() {
        DataManagerImpl dataManager = new DataManagerImpl(getContext());

        dataManager.removeAll();

        String PARTICIPANT_1_NAME_TONY = "Tony";
        String PARTICIPANT_2_NAME_CHRISTIAN = "Christian";
        String PARTICIPANT_2_NAME_UPDATE = PARTICIPANT_2_NAME_CHRISTIAN + "Update";

        Participant participant1Out;
        Participant participant2Out;

        long id1Exp = 1L;
        long id2Exp = 2L;

        long tripId = dataManager.persistTrip(ModelFactory.createNewTrip("MyTrip",
                Currency.getInstance("USD"))).getId();

        /* Save Participant 1 */
        Participant participant01In = ModelFactory.createNewParticipant(PARTICIPANT_1_NAME_TONY, true);
        participant1Out = dataManager.persistParticipantInTrip(tripId, participant01In);
        AssertionUtil.assertParticipantEquality(participant1Out, id1Exp, participant01In);

        /* Save Participant 2 */
        Participant participant02In = ModelFactory.createNewParticipant(PARTICIPANT_2_NAME_CHRISTIAN, false);
        participant2Out = dataManager.persistParticipantInTrip(tripId, participant02In);
        AssertionUtil.assertParticipantEquality(participant2Out, id2Exp, participant02In);

        /* Existence Check */
        Assert.assertEquals(true, dataManager.doesParticipantAlreadyExist(PARTICIPANT_1_NAME_TONY, tripId, 0L));
        Assert.assertEquals(true, dataManager.doesParticipantAlreadyExist(PARTICIPANT_2_NAME_CHRISTIAN, tripId, 0L));
        Assert.assertEquals(false, dataManager.doesParticipantAlreadyExist(PARTICIPANT_1_NAME_TONY, tripId, id1Exp));
        Assert.assertEquals(false,
                dataManager.doesParticipantAlreadyExist(PARTICIPANT_2_NAME_CHRISTIAN, tripId, id2Exp));
        Assert.assertEquals(false, dataManager.doesParticipantAlreadyExist(PARTICIPANT_1_NAME_TONY, 42L, 0L));
        Assert.assertEquals(false, dataManager.doesParticipantAlreadyExist("Something not yet persisted", tripId, 0L));
        Assert.assertEquals(false, dataManager.doesParticipantAlreadyExist(null, tripId, 0L));

        /* Update Participant 2 */
        participant02In.setActive(true);

        participant02In.setName(PARTICIPANT_2_NAME_UPDATE);
        participant2Out = dataManager.persistParticipantInTrip(tripId, participant02In);
        AssertionUtil.assertParticipantEquality(participant2Out, id2Exp, participant02In);

        /* Test data load */
        Trip loadedTripFull = dataManager.loadTripById(tripId);
        Assert.assertEquals(2, loadedTripFull.getParticipant().size());
        for (Participant p : loadedTripFull.getParticipant()) {
            if (p.getId() == id1Exp) {
                AssertionUtil.assertParticipantEquality(p, id1Exp, participant01In);
            }
            else if (p.getId() == id2Exp) {
                AssertionUtil.assertParticipantEquality(p, id2Exp, participant02In);
            }
            else {
                Assert.fail();
            }
        }

    }
}
