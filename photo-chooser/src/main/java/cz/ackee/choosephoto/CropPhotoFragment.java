package cz.ackee.choosephoto;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.isseiaoki.simplecropview.CropImageView;
import com.isseiaoki.simplecropview.callback.CropCallback;
import com.isseiaoki.simplecropview.callback.SaveCallback;

import java.io.File;

import cz.ackee.choosephoto.utils.UiUtils;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Fragment for photo cropping.
 */
public class CropPhotoFragment extends Fragment {

    public static final String TAG = CropPhotoFragment.class.getName();
    public static final String KEY_FILE = "file";
    private static final String COLOR_KEY = "color";
    public static final int REQUEST_CROP = 456;
    public static final int MAX_SIZE = 1080;

    CropImageView cropImageView;
    ProgressBar progressBar;
    FrameLayout content;
    ImageButton rotateButton;
    ImageButton doneButton;

    public static Bundle getArgs(String file) {
        Bundle args = new Bundle();
        args.putString(KEY_FILE, file);
        return args;
    }

    public static Bundle getArgs(String file, int color) {
        Bundle args = new Bundle();
        args.putString(KEY_FILE, file);
        args.putInt(COLOR_KEY, color);
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
        cropImageView = view.findViewById(R.id.img_crop);
        progressBar = view.findViewById(android.R.id.progress);
        content = view.findViewById(android.R.id.content);
        rotateButton = view.findViewById(R.id.btn_rotate);
        doneButton = view.findViewById(R.id.btn_done);

        cropImageView.setOutputMaxSize(MAX_SIZE, MAX_SIZE);

        rotateButton.setEnabled(false); // to prevent clicks when cropImageView is not yet populated with bitmap
        rotateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rotate();
            }
        });

        doneButton.setEnabled(false); // to prevent clicks when cropImageView is not yet populated with bitmap
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                done();
            }
        });
        if (cropImageView.getImageBitmap() == null) {
            Observable.just(getImageFile())
                    .subscribeOn(Schedulers.newThread())
                    .map(new Function<String, Bitmap>() {
                        @Override
                        public Bitmap apply(String filename) {
                            return BitmapFactory.decodeFile(filename);
                        }
                    })
                    .map(new Function<Bitmap, Bitmap>() {
                        @Override
                        public Bitmap apply(Bitmap bitmap) {
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
                    .subscribe(new Consumer<Bitmap>() {
                        @Override
                        public void accept(Bitmap bitmap) {
                            if (cropImageView != null) {
                                cropImageView.setImageBitmap(bitmap);
                                rotateButton.setEnabled(true);
                                doneButton.setEnabled(true);
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) {
                            throwable.printStackTrace();
                        }
                    });
        }

        Drawable wrapper = DrawableCompat.wrap(doneButton.getDrawable().mutate());
        DrawableCompat.setTint(wrapper, getArguments().containsKey(COLOR_KEY) ? getArguments().getInt(COLOR_KEY) : UiUtils.getColorAttribute(getContext(), R.attr.colorAccent));
        doneButton.setImageDrawable(wrapper);
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
            public void onError(Throwable t) {
//                showSnack(R.string.photo_crop_error);
            }
        }, new SaveCallback() {
            @Override
            public void onSuccess(Uri outputUri) {
                Intent intent = new Intent();
                intent.putExtra(KEY_FILE, outputUri.getPath());
                getActivity().setResult(Activity.RESULT_OK, intent);
                getActivity().finish();
            }

            @Override
            public void onError(Throwable t) {
//                showSnack(R.string.photo_crop_error);
            }
        });
    }

    public void showProgress(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        content.setVisibility(show ? View.GONE : View.VISIBLE);
    }
}