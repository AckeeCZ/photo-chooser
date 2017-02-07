package cz.ackee.choosephoto.sample;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.ackee.photo_chooser.BuildConfig;
import com.ackee.photo_chooser.R;

import java.io.File;

import cz.ackee.choosephoto.ChoosePhotoHelper;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Activity with sample usage
 *
 * @author David Bilik [david.bilik@ackee.cz]
 * @since 07/02/2017
 **/
public class MainActivity extends AppCompatActivity {
    public static final String TAG = MainActivity.class.getName();
    private ChoosePhotoHelper choosePhotoHelper;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final ImageView imgPicture = (ImageView) findViewById(R.id.img_avatar);
        choosePhotoHelper = new ChoosePhotoHelper(this, new ChoosePhotoHelper.OnPhotoPickedListener() {
            @Override
            public void onPhotoPicked(Observable<File> fileObservable) {
                fileObservable
                        .map(new Func1<File, Bitmap>() {
                            @Override
                            public Bitmap call(File file) {
                                return BitmapFactory.decodeFile(file.getAbsolutePath());
                            }
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<Bitmap>() {
                            @Override
                            public void call(Bitmap file) {
                                imgPicture.setImageBitmap(file);
                            }
                        });
            }
        }, new ChoosePhotoHelper.OnPhotoCopyingListener() {
            @Override
            public void photoCopying(boolean isCopying) {

            }
        });
        findViewById(R.id.btn_choose_photo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosePhotoHelper.getChoosePhotoDialogBuilder(BuildConfig.APPLICATION_ID).show(getSupportFragmentManager());
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        choosePhotoHelper.onActivityResult(requestCode, resultCode, data);
    }
}
