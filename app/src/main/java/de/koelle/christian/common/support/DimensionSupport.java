package de.koelle.christian.common.support;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.WindowManager;

import java.util.HashMap;
import java.util.Map;

public class DimensionSupport {

    private static Map<Float, Float> pxCache = new HashMap<>();
    private final Context context;

    public DimensionSupport(Context context) {
        this.context = context;
    }


    public int dp2Px(float dp) {
        Float f = pxCache.get(dp);
        if (f == null) {
            synchronized (pxCache) {
                f = calculateDpToPixel(dp);
                pxCache.put(dp, f);
            }
        }
        return f.intValue();
    }

    private float calculateDpToPixel(float dp) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return px;

    }
}
