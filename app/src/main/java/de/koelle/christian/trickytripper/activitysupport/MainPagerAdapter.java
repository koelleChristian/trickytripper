package de.koelle.christian.trickytripper.activitysupport;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import de.koelle.christian.trickytripper.R;
import de.koelle.christian.trickytripper.activities.ParticipantTabActivity;
import de.koelle.christian.trickytripper.activities.PaymentTabActivity;
import de.koelle.christian.trickytripper.activities.ReportTabActivity;
import de.koelle.christian.trickytripper.constants.Rc;

public class MainPagerAdapter extends FragmentStatePagerAdapter {

    private final Context mContext;

    public MainPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.mContext = context;
    }

    @Override
    public Fragment getItem(int i) {
        switch (i) {
            case Rc.TAB_ID_PARTICIPANTS:
                return new ParticipantTabActivity();
            case Rc.TAB_ID_PAYMENTS:
                return new PaymentTabActivity();
            case Rc.TAB_ID_REPORT:
                return new ReportTabActivity();
            default:
                throw new UnsupportedOperationException("There is no tab with id " + i + " supported.");
        }
    }
//
//    @Override
//    public int getItemPosition(Object object) {
//        if(reset){
//            return POSITION_NONE;
//        } else{
//
//        }
//        return super.getItemPosition(object);
//    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        switch (position) {
            case Rc.TAB_ID_PARTICIPANTS:
                container.setTag(Rc.TAB_ID_PARTICIPANTS);
                break;
            case Rc.TAB_ID_PAYMENTS:
                container.setTag(Rc.TAB_ID_PAYMENTS);
                break;
            case Rc.TAB_ID_REPORT:
                container.setTag(Rc.TAB_ID_REPORT);
                break;
            default:
                throw new UnsupportedOperationException("There is no tab with position " + position + " supported.");
        }

        return super.instantiateItem(container, position);
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int i) {
        switch (i) {
            case Rc.TAB_ID_PARTICIPANTS:
                return mContext.getResources().getString(R.string.activity_label_participants);
            case Rc.TAB_ID_PAYMENTS:
                return mContext.getResources().getString(R.string.activity_label_payments);
            case Rc.TAB_ID_REPORT:
                return mContext.getResources().getString(R.string.activity_label_report);
            default:
                throw new UnsupportedOperationException("There is no tab with position " + i + " supported.");
        }
    }
}
