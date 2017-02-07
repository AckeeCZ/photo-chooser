package cz.ackee.choosephoto;
//
//import android.app.Activity;
//import android.content.Intent;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.Color;
//import android.net.Uri;
//import android.os.Build;
//import android.os.Bundle;
//import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
//import android.support.v7.app.ActionBar;
//import android.support.v7.widget.Toolbar;
//import android.view.View;
//import android.widget.FrameLayout;
//import android.widget.ProgressBar;
//
//import com.isseiaoki.simplecropview.CropImageView;
//import com.isseiaoki.simplecropview.callback.CropCallback;
//import com.isseiaoki.simplecropview.callback.SaveCallback;
//
//import java.io.File;
//
//import butterknife.BindView;
//import butterknife.OnClick;
//import cz.ackee.babysitting.mvp.view.base.IProgressView;
//import cz.ackee.babysitting.ui.activity.base.BaseActivity;
//import cz.ackee.babysitting.ui.fragment.base.BaseFragment;
//import cz.ackee.babysitting.utils.UiUtils;
//import rx.Observable;
//import rx.android.schedulers.AndroidSchedulers;
//import rx.schedulers.Schedulers;

/**
 * Fragment for photo cropping.
 * Created by Georgiy Shur (georgiy.shur@ackee.cz) on 4/18/2016.
 */
public class CropPhotoFragment extends Fragment {
    public static final String TAG = CropPhotoFragment.class.getName();
    public static final String KEY_FILE = "file";
    public static final int REQUEST_CROP = 456;
    public static final int MAX_SIZE = 1080;

//    @BindView(R.id.toolbar)
//    Toolbar toolbar;
//    @BindView(R.id.img_crop)
//    CropImageView cropImageView;
//    @BindView(android.R.id.progress)
//    ProgressBar progressBar;
//    @BindView(android.R.id.content)
//    FrameLayout content;
//
//    public static Bundle getArgs(String file) {
//        Bundle args = new Bundle();
//        args.putString(KEY_FILE, file);
//        return args;
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setRetainInstance(true);
//    }
//
//    @Override
//    public void onViewCreated(View view, Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//
//        cropImageView.setOutputMaxSize(MAX_SIZE, MAX_SIZE);
//
//        if (cropImageView.getImageBitmap() == null) {
//            Observable.just(getImageFile())
//                    .subscribeOn(Schedulers.newThread())
//                    .map(BitmapFactory::decodeFile)
//                    .map(bitmap -> {
//                        int screenWidth = UiUtils.getWindowSize(getActivity())[0];
//                        if (bitmap.getWidth() >= screenWidth) {
//                            float ratio = (float) bitmap.getHeight() / bitmap.getWidth();
//                            Bitmap resized = Bitmap.createScaledBitmap(bitmap, screenWidth, (int) (screenWidth * ratio), false);
//                            if (bitmap != resized) {
//                                bitmap.recycle();
//                            }
//                            return resized;
//                        }
//                        return bitmap;
//                    })
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe(resized -> {
//                        if (cropImageView != null) {
//                            cropImageView.setImageBitmap(resized);
//                        }
//                    }, Throwable::printStackTrace);
//        }
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            cropImageView.setPadding(0, cropImageView.getPaddingTop() + UiUtils.getStatusBarHeight(getActivity()), 0, cropImageView.getPaddingBottom());
//        }
//    }
//
//
//    @Override
//    public String getTitle() {
//        return getString(R.string.photo_crop_title);
//    }
//
//    @Override
//    protected void initAB(@Nullable ActionBar ab) {
//        ((BaseActivity) getActivity()).setSupportActionBar(toolbar);
//        baseSettingsAB(ab);
//        if (ab != null) {
//            ab.setDisplayHomeAsUpEnabled(true);
//        }
//        toolbar.setNavigationIcon(R.drawable.ic_ab_back);
//        toolbar.setTitleTextColor(Color.WHITE);
//    }
//
//
//    @Override
//    protected int getLayoutResId() {
//        return R.layout.fragment_crop_photo;
//    }
//
//    private String getImageFile() {
//        return getArguments().getString(KEY_FILE);
//    }
//
//    @OnClick(R.id.btn_rotate)
//    public void rotate() {
//        cropImageView.rotateImage(CropImageView.RotateDegrees.ROTATE_M90D);
//    }
//
//    @OnClick(R.id.btn_done)
//    public void done() {
//        showProgress(true);
//        cropImageView.startCrop(Uri.fromFile(new File(getImageFile())), new CropCallback() {
//            @Override
//            public void onSuccess(Bitmap cropped) {
//
//            }
//
//            @Override
//            public void onError() {
//                showSnack(R.string.photo_crop_error);
//            }
//        }, new SaveCallback() {
//            @Override
//            public void onSuccess(Uri outputUri) {
//                Intent intent = new Intent();
//                intent.putExtra("file", outputUri.getPath());
//                getActivity().setResult(Activity.RESULT_OK, intent);
//                getActivity().finish();
//            }
//
//            @Override
//            public void onError() {
//                showSnack(R.string.photo_crop_error);
//            }
//        });
//    }
//
//    @Override
//    public void showProgress(boolean show) {
//        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
//        content.setVisibility(show ? View.GONE : View.VISIBLE);
//    }

}
