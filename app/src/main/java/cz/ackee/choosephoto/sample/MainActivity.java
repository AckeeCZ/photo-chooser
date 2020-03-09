package cz.ackee.choosephoto.sample;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.ackee.photo_chooser.R;

import java.io.File;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import cz.ackee.choosephoto.ChoosePhotoHelper;
import cz.ackee.choosephoto.OnPhotoCopyingListener;
import cz.ackee.choosephoto.OnPhotoPickedListener;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * Activity with sample usage
 */
public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getName();
    private ChoosePhotoHelper choosePhotoHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final ImageView imgPicture = findViewById(R.id.img_avatar);
        choosePhotoHelper = new ChoosePhotoHelper(this, new OnPhotoCopyingListener() {
            @Override
            public void photoCopying(boolean isCopying) {

            }
        }, new OnPhotoPickedListener() {
            @Override
            public void onPhotoPicked(Observable<File> fileObservable) {
                fileObservable
                    .map(new Function<File, Bitmap>() {
                        @Override
                        public Bitmap apply(File file) {
                            return BitmapFactory.decodeFile(file.getAbsolutePath());
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Bitmap>() {
                        @Override
                        public void accept(Bitmap file) {
                            imgPicture.setImageBitmap(file);
                        }
                    });
            }
        });
        choosePhotoHelper.restoreInstanceState(savedInstanceState);
        findViewById(R.id.btn_choose_photo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosePhotoHelper.getChoosePhotoDialogBuilder().show(getSupportFragmentManager());
            }
        });
        findViewById(R.id.btn_choose_photo_with_crop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosePhotoHelper.getChoosePhotoDialogBuilder(true).show(getSupportFragmentManager());
            }
        });
        findViewById(R.id.btn_choose_photo_with_crop_wide).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosePhotoHelper.getChoosePhotoDialogBuilder(true, getResources().getColor(R.color.colorPrimary), 1920, 1080).show(getSupportFragmentManager());
            }
        });
        findViewById(R.id.btn_choose_photo_custom_dialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosePhotoHelper.getChoosePhotoDialogBuilder(true)
                    .setPickPhotoString("Gallery selection")
                    .setTakePhotoString("Selfie Time!")
                    .setFileName("myselfie.jpg")
                    .show(getSupportFragmentManager());
            }
        });
        findViewById(R.id.btn_take_picture_directly).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosePhotoHelper.getChoosePhotoDialogBuilder(true).showCamera(MainActivity.this);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        choosePhotoHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        choosePhotoHelper.saveInstanceState(outState);
    }
}
