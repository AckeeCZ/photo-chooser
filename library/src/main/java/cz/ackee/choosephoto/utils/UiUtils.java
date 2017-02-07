package cz.ackee.choosephoto.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.isseiaoki.simplecropview.util.Utils;
import com.yqritc.recyclerviewflexibledivider.FlexibleDividerDecoration;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import cz.ackee.babysitting.R;
import uk.co.chrisjenx.calligraphy.TypefaceUtils;

/**
 * Utilities for UI related things like device size or conversion from dp to px
 * Created by David Bilik[david.bilik@ackee.cz] on {18/12/15}
 **/
public class UiUtils {
    public static final String TAG = UiUtils.class.getName();

    public static int dpToPx(Context ctx, int dp) {
        Resources resources = ctx.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return (int) px;
    }


    public static int getScreenHeight(Context ctx) {
        WindowManager wm = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        return display.getHeight();
    }

    public static int getScreenWidth(Context ctx) {
        WindowManager wm = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        return display.getWidth();
    }


    public static int getStatusBarHeight(Context ctx) {
        int result = 0;
        int resourceId = ctx.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            result = ctx.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }


    public static void hideIme(View target) {
        if (target != null) {
            InputMethodManager imm = (InputMethodManager) target.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(target.getWindowToken(), 0);
        }
    }


    /**
     * Load bitmap representation of view
     *
     * @param v - view to take screenshot
     * @return bitmap of view
     */
    public static Bitmap loadBitmapFromView(View v) {
        v.setDrawingCacheEnabled(true);
        Bitmap b = v.getDrawingCache();
        return b;
    }



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

}
