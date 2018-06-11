package cz.ackee.choosephoto;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Activity that holds crop photo fragment
 */
public class CropPhotoActivity extends AppCompatActivity {

    public static final String TAG = CropPhotoActivity.class.getName();
    private static final String FILENAME_KEY = "filename";
    private static final String COLOR_KEY = "color";
    private static final String MAX_WIDTH_KEY = "max_width";
    private static final String MAX_HEIGHT_KEY = "max_height";
    protected static final int UNKNOWN_VALUE = -1;

    public static void open(Activity ctx, String filename, int tintColor, Integer maxWidth, Integer maxHeight, int requestCode) {
        Intent intent = new Intent(ctx, CropPhotoActivity.class).putExtra(FILENAME_KEY, filename).putExtra(COLOR_KEY, tintColor);
        if (maxWidth != null) {
            intent.putExtra(MAX_WIDTH_KEY, maxWidth);
        }
        if (maxHeight != null) {
            intent.putExtra(MAX_HEIGHT_KEY, maxHeight);
        }
        ctx.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);
        if (getSupportFragmentManager().findFragmentById(R.id.fragment_container) == null) {
            CropPhotoFragment cropPhotoFragment = new CropPhotoFragment();
            String fileName = getIntent().getStringExtra(FILENAME_KEY);
            int color = getIntent().getIntExtra(COLOR_KEY, UNKNOWN_VALUE);
            int maxWidth = getIntent().getIntExtra(MAX_WIDTH_KEY, UNKNOWN_VALUE);
            int maxHeight = getIntent().getIntExtra(MAX_HEIGHT_KEY, UNKNOWN_VALUE);
            cropPhotoFragment.setArguments(CropPhotoFragment.getArgs(fileName, color == UNKNOWN_VALUE ? null : color, maxWidth == UNKNOWN_VALUE ? null : maxWidth, maxHeight == UNKNOWN_VALUE ? null : maxHeight));
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, cropPhotoFragment).commit();
        }
    }
}
