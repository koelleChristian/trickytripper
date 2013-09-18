package de.koelle.christian.trickytripper.ui.utils;

import de.koelle.christian.trickytripper.R;
import de.koelle.christian.trickytripper.TrickyTripperApp;

public class PrepareOptionsSupport {
    
    public static void prepareMajorTabOptions(com.actionbarsherlock.view.Menu menu, TrickyTripperApp app, boolean showCreateParticipant) {
        boolean enabled = app.getTripController().hasLoadedTripPayments();
        menu.findItem(R.id.option_export).setEnabled(enabled);
        menu.findItem(R.id.option_export).getIcon().setAlpha(enabled ? 255 : 64);
        
        menu.findItem(R.id.option_create_participant).setEnabled(showCreateParticipant);
        menu.findItem(R.id.option_create_participant).getIcon().setAlpha(showCreateParticipant ? 255 : 0);
        
    }
    public static void reset(com.actionbarsherlock.view.Menu menu){
        for(int i = 0 ; i < menu.size(); i++){
            if(menu.getItem(i) != null && menu.getItem(i).getIcon() !=  null){
                menu.getItem(i).getIcon().setAlpha(255);
            }
        }
    }

}
