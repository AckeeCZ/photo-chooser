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
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

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
                            .subscribe(new Action1<File>() {
                                @Override
                                public void call(File file) {
                                    CropPhotoActivity.open(ctx, file.getAbsolutePath(), REQUEST_CROP);
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
                .map(new Func1<Object, Boolean>() {
                         @Override
                         public Boolean call(Object o) {
                             if (fromGallery) {
                                 return GalleryUtils.copyUriToMyUri(ctx, uri, Uri.fromFile(FileUtils.getPictureFile(ctx, lastUri.getLastPathSegment())));
                             }
                             return true;
                         }
                     }
                )
                .map(new Func1<Boolean, File>() {
                    @Override
                    public File call(Boolean success) {
                        return FileUtils.getPictureFile(ctx, lastUri.getLastPathSegment());
                    }
                })
                .doOnNext(new Action1<File>() {
                    @Override
                    public void call(File file) {
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
