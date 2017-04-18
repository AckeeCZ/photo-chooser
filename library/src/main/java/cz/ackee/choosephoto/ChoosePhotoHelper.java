package cz.ackee.choosephoto;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.File;

import cz.ackee.choosephoto.utils.FileUtils;
import cz.ackee.choosephoto.utils.GalleryUtils;
import cz.ackee.choosephoto.utils.UiUtils;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static cz.ackee.choosephoto.ChoosePhotoDialogFragment.CAMERA_REQUEST;
import static cz.ackee.choosephoto.ChoosePhotoDialogFragment.GALLERY_REQUEST;
import static cz.ackee.choosephoto.CropPhotoFragment.KEY_FILE;
import static cz.ackee.choosephoto.CropPhotoFragment.REQUEST_CROP;

/**
 * Helper class for choosing an image from the device
 * <p>
 * Class using photo helper has to call pass onActivityResult callback to this class to
 * obtain picked photo.
 * <p>
 * Created by Jan Stanek[jan.stanek@ackee.cz] on {30.11.15}
 **/
public class ChoosePhotoHelper implements ChoosePhotoDialogFragment.DialogBuiltCallback {
    public static final String TAG = ChoosePhotoHelper.class.getName();

    private final Context ctx;
    private final OnPhotoPickedListener onPhotoPickedListener;
    private final OnPhotoCopyingListener onPhotoCopyingListener;
    private boolean withCrop;
    // last used uri
    private Uri lastUri;
    private int tintColor = -1;

    /**
     * Constructor
     *
     * @param ctx                   Context
     * @param onPhotoPickedListener Interface for callback
     */
    public ChoosePhotoHelper(@NonNull Context ctx, @NonNull OnPhotoPickedListener onPhotoPickedListener, @Nullable OnPhotoCopyingListener onPhotoCopyingListener) {
        this.ctx = ctx;
        this.onPhotoPickedListener = onPhotoPickedListener;
        this.onPhotoCopyingListener = onPhotoCopyingListener;
        tintColor = UiUtils.getColorAttribute(ctx, R.attr.colorPrimary);
    }

    /**
     * Returns builder for creating ChoosePhotoDialogFragment
     *
     * @param applicationId application id (package name)
     */
    public ChoosePhotoDialogFragment.Builder getChoosePhotoDialogBuilder(String applicationId, boolean withCrop) {
        this.withCrop = withCrop;
        return new ChoosePhotoDialogFragment.Builder(ctx, applicationId + ".choose_photo", this);
    }
    /**
     * Returns builder for creating ChoosePhotoDialogFragment
     *
     * @param applicationId application id (package name)
     */
    public ChoosePhotoDialogFragment.Builder getChoosePhotoDialogBuilder(String applicationId, boolean withCrop, int tintColor) {
        this.withCrop = withCrop;
        this.tintColor = tintColor;
        return new ChoosePhotoDialogFragment.Builder(ctx, applicationId + ".choose_photo", this);
    }
    /**
     * Returns builder for creating ChoosePhotoDialogFragment
     *
     * @param applicationId application id (package name)
     */
    public ChoosePhotoDialogFragment.Builder getChoosePhotoDialogBuilder(String applicationId) {
        return getChoosePhotoDialogBuilder(applicationId, false);
    }

    /**
     * This method has to be called in onActivityResult method
     *
     * @param requestCode RequestCode
     * @param resultCode  ResultCode
     * @param data        Intent
     */
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GALLERY_REQUEST || requestCode == CAMERA_REQUEST) {
                if (onPhotoCopyingListener != null) {
                    onPhotoCopyingListener.photoCopying(true);
                }
                final boolean fromGallery = requestCode == GALLERY_REQUEST;
                Uri uri;
                if (!fromGallery) {
                    uri = lastUri;
                } else {
                    uri = data.getData();
                }
                final Uri finalUri = uri;
                Observable<File> fileObservable = onPhotoPicked(finalUri, fromGallery);
                if (!withCrop) {
                    onPhotoPickedListener.onPhotoPicked(fileObservable);
                } else {
                    fileObservable
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Consumer<File>() {
                                @Override
                                public void accept(File file) {
                                    CropPhotoActivity.open(ctx, file.getAbsolutePath(), tintColor, REQUEST_CROP);
                                }
                            });

                }
            }
            if (requestCode == REQUEST_CROP) {
                onPhotoPickedListener.onPhotoPicked(Observable.just(new File(data.getStringExtra(KEY_FILE))));
            }
        }
    }

    public void clear() {
        if (!FileUtils.clearPhotosDir(ctx)) {
            Log.e(TAG, "The error occurred while trying to clear temporary photos folder.");
        }
    }

    @NonNull
    private Observable<File> onPhotoPicked(final Uri uri, final boolean fromGallery) {
        return Observable.just(new Object())
                .subscribeOn(Schedulers.newThread())
                .map(new Function<Object, Boolean>() {
                         @Override
                         public Boolean apply(Object o) {
                             if (fromGallery) {
                                 return GalleryUtils.copyUriToMyUri(ctx, uri, Uri.fromFile(FileUtils.getPictureFile(ctx, lastUri.getLastPathSegment())));
                             }
                             return true;
                         }
                     }
                )
                .map(new Function<Boolean, File>() {
                    @Override
                    public File apply(Boolean success) {
                        return FileUtils.getPictureFile(ctx, lastUri.getLastPathSegment());
                    }
                })
                .doOnNext(new Consumer<File>() {
                    @Override
                    public void accept(File file) {
                        if (onPhotoCopyingListener != null) {
                            onPhotoCopyingListener.photoCopying(false);
                        }
                    }
                });
    }

    @Override
    public void dialogBuilt(Uri uri) {
        this.lastUri = uri;
    }

    /**
     * Interface to obtained picked image in a file
     */
    public interface OnPhotoPickedListener {
        /**
         * Photo was picked
         *
         * @param fileObservable
         */
        void onPhotoPicked(Observable<File> fileObservable);
    }

    /**
     * Interface indicating that copying is in progress
     */
    public interface OnPhotoCopyingListener {
        /**
         * Photo has begin copying
         *
         * @param isCopying boolean indicator if copying is proceeding
         */
        void photoCopying(boolean isCopying);
    }
}
