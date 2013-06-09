package de.koelle.christian.trickytripper.constants;

public enum Rt {
    /**
     * 
     */
    PARTICIPANTS(0, Rc.TAB_SPEC_ID_PARTICIPANTS),
    /**
 * 
 */
    PAYMENT(1, Rc.TAB_SPEC_ID_PAYMENT),

    /**
     * 
     */
    REPORT(2, Rc.TAB_SPEC_ID_REPORT),
    /**/;

    private final int position;
    private final String id;
    
    private Rt(int position, String id) {
        this.position = position;
        this.id = id;
    }

    public int getPosition() {
        return position;
    }

    public String getId() {
        return id;
    }
    
    
    
}
