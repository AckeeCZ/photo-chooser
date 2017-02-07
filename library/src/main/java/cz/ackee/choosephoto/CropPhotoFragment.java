package cz.ackee.choosephoto;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.isseiaoki.simplecropview.CropImageView;
import com.isseiaoki.simplecropview.callback.CropCallback;
import com.isseiaoki.simplecropview.callback.SaveCallback;

import java.io.File;

import cz.ackee.choosephoto.utils.UiUtils;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Fragment for photo cropping.
 * Created by Georgiy Shur (georgiy.shur@ackee.cz) on 4/18/2016.
 */
public class CropPhotoFragment extends Fragment {
    public static final String TAG = CropPhotoFragment.class.getName();
    public static final String KEY_FILE = "file";
    public static final int REQUEST_CROP = 456;
    public static final int MAX_SIZE = 1080;


    CropImageView cropImageView;
    ProgressBar progressBar;
    FrameLayout content;

    public static Bundle getArgs(String file) {
        Bundle args = new Bundle();
        args.putString(KEY_FILE, file);
        return args;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_crop_photo, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        cropImageView = (CropImageView) view.findViewById(R.id.img_crop);
        progressBar = (ProgressBar) view.findViewById(android.R.id.progress);
        content = (FrameLayout) view.findViewById(android.R.id.content);

        cropImageView.setOutputMaxSize(MAX_SIZE, MAX_SIZE);

        view.findViewById(R.id.btn_rotate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rotate();
            }
        });

        view.findViewById(R.id.btn_done).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                done();
            }
        });
        if (cropImageView.getImageBitmap() == null) {
            Observable.just(getImageFile())
                    .subscribeOn(Schedulers.newThread())
                    .map(new Func1<String, Bitmap>() {
                        @Override
                        public Bitmap call(String filename) {
                            return BitmapFactory.decodeFile(filename);
                        }
                    })
                    .map(new Func1<Bitmap, Bitmap>() {
                        @Override
                        public Bitmap call(Bitmap bitmap) {
                            int screenWidth = UiUtils.getWindowSize(getActivity())[0];
                            if (bitmap.getWidth() >= screenWidth) {
                                float ratio = (float) bitmap.getHeight() / bitmap.getWidth();
                                Bitmap resized = Bitmap.createScaledBitmap(bitmap, screenWidth, (int) (screenWidth * ratio), false);
                                if (bitmap != resized) {
                                    bitmap.recycle();
                                }
                                return resized;
                            }
                            return bitmap;
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<Bitmap>() {
                        @Override
                        public void call(Bitmap bitmap) {
                            if (cropImageView != null) {
                                cropImageView.setImageBitmap(bitmap);
                            }
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            throwable.printStackTrace();
                        }
                    });
        }

    }


    private String getImageFile() {
        return getArguments().getString(KEY_FILE);
    }

    public void rotate() {
        cropImageView.rotateImage(CropImageView.RotateDegrees.ROTATE_M90D);
    }


    public void done() {
        showProgress(true);
        cropImageView.startCrop(Uri.fromFile(new File(getImageFile())), new CropCallback() {
            @Override
            public void onSuccess(Bitmap cropped) {

            }

            @Override
            public void onError() {
//                showSnack(R.string.photo_crop_error);
            }
        }, new SaveCallback() {
            @Override
            public void onSuccess(Uri outputUri) {
                Intent intent = new Intent();
                intent.putExtra("file", outputUri.getPath());
                getActivity().setResult(Activity.RESULT_OK, intent);
                getActivity().finish();
            }

            @Override
            public void onError() {
//                showSnack(R.string.photo_crop_error);
            }
        });
    }

    public void showProgress(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        content.setVisibility(show ? View.GONE : View.VISIBLE);
    }

}
