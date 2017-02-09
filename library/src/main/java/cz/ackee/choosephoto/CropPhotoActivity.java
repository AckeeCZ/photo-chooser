package cz.ackee.choosephoto;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Activity that holds crop photo fragment
 *
 * @author David Bilik [david.bilik@ackee.cz]
 * @since 07/02/2017
 **/
public class CropPhotoActivity extends AppCompatActivity {
    public static final String TAG = CropPhotoActivity.class.getName();
    private static final String FILENAME_KEY = "filename";
    private static final String COLOR_KEY = "color";


    public static void open(Context ctx, String filename, int tintColor, int requestCode) {
        ((Activity) ctx).startActivityForResult(new Intent(ctx, CropPhotoActivity.class).putExtra(FILENAME_KEY, filename).putExtra(COLOR_KEY, tintColor), requestCode);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);
        if (getSupportFragmentManager().findFragmentById(R.id.fragment_container) == null) {
            CropPhotoFragment cropPhotoFragment = new CropPhotoFragment();
            cropPhotoFragment.setArguments(getIntent().getIntExtra(COLOR_KEY, Integer.MIN_VALUE) == Integer.MIN_VALUE ? CropPhotoFragment.getArgs(getIntent().getStringExtra(FILENAME_KEY)) : CropPhotoFragment.getArgs(getIntent().getStringExtra(FILENAME_KEY), getIntent().getIntExtra(COLOR_KEY, 0)));
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, cropPhotoFragment).commit();
        }
    }
}
