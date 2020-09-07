package cz.ackee.choosephoto.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.util.TypedValue;
import android.view.Display;
import android.view.WindowManager;


/**
 * Utilities for UI related things like device size or conversion from dp to px
 */
public class UiUtils {

    public static int[] getWindowSize(Context context) {
        int screenWidth, screenHeight;
        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE))
                .getDefaultDisplay();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Point point = new Point();
            display.getSize(point);
            screenWidth = point.x;
            screenHeight = point.y;
        } else {
            screenWidth = display.getWidth();
            screenHeight = display.getHeight();
        }

        return new int[]{screenWidth, screenHeight};
    }

    public static int getColorAttribute(Context ctx, int attribute) {
        TypedValue typedValue = new TypedValue();
        int[] colorAttr = new int[]{attribute};
        int indexOfAttr = 0;
        TypedArray a = ctx.obtainStyledAttributes(typedValue.data, colorAttr);
        int color = a.getColor(indexOfAttr, Color.WHITE);
        a.recycle();
        return color;
    }
}
