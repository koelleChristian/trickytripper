package de.koelle.christian.common.support;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.WindowManager;

public class DimensionSupport {

    private final Context context;

    public DimensionSupport(Context context) {
        this.context = context;
    }

   
    private float getDimension(int attribute) {
        TypedValue value = new TypedValue();        
        DisplayMetrics metrics = new DisplayMetrics();
        context.getTheme().resolveAttribute(attribute, value, true);       
        ((WindowManager) (context.getSystemService(Context.WINDOW_SERVICE))).getDefaultDisplay().getMetrics(metrics);
        return TypedValue.complexToDimension(value.data, metrics);
    }

}
