package cz.ackee.choosephoto;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.File;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static cz.ackee.choosephoto.ChoosePhotoDialogFragment.CAMERA_REQUEST;
import static cz.ackee.choosephoto.ChoosePhotoDialogFragment.GALLERY_REQUEST;

/**
 * Helper class for choosing an image from the device
 * <p>
 * Class using photo helper has to call pass onActivityResult callback to this class to
 * obtain picked photo.
 * <p>
 * Created by Jan Stanek[jan.stanek@ackee.cz] on {30.11.15}
 **/
public class ChoosePhotoHelper {
    public static final String TAG = ChoosePhotoHelper.class.getName();

    private final Context mCtx;
    private final OnPhotoPickedListener mOnPhotoPickedListener;

    /**
     * Constructor
     *
     * @param ctx                   Context
     * @param onPhotoPickedListener Interface for callback
     */
    public ChoosePhotoHelper(@NonNull Context ctx, @NonNull OnPhotoPickedListener onPhotoPickedListener) {
        mCtx = ctx;
        mOnPhotoPickedListener = onPhotoPickedListener;
    }

    /**
     * Returns builder for creating ChoosePhotoDialogFragment
     */
    public ChoosePhotoDialogFragment.Builder getChoosePhotoDialogBuilder() {
        return new ChoosePhotoDialogFragment.Builder(mCtx);
    }

    /**
     * This method has to be called in onActivityResult method
     *
     * @param requestCode RequestCode
     * @param resultCode  ResultCode
     * @param data        Intent
     */
    public void onActivityResult(int requestCode, int resultCode, @NonNull Intent data) {

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GALLERY_REQUEST || requestCode == CAMERA_REQUEST) {
                mOnPhotoPickedListener.photoCopying(true);
                final boolean fromGallery = requestCode == GALLERY_REQUEST;
                Uri uri;
                if (!fromGallery) {
                    uri = Uri.fromFile(FileUtils.getTempPictureFile(mCtx));
                } else {
                    uri = data.getData();
                }
                final Uri finalUri = uri;
                mOnPhotoPickedListener.onPhotoPicked(onPhotoPicked(finalUri, fromGallery));
            }
        }
    }

    public void clear() {
        if (!FileUtils.clearPhotosDir(mCtx)) {
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
                                 File tempPictureFile = FileUtils.getTempPictureFile(mCtx);
                                 return GalleryUtils.copyUriToMyUri(mCtx, uri, Uri.fromFile(tempPictureFile));
                             }
                             return true;
                         }
                     }
                )
                .map(new Func1<Boolean, File>() {
                    @Override
                    public File call(Boolean success) {
                        return FileUtils.getTempPictureFile(mCtx);
                    }
                })
                .doOnNext(new Action1<File>() {
                    @Override
                    public void call(File file) {
                        mOnPhotoPickedListener.photoCopying(false);
                    }
                })

           /*     .observeOn(AndroidSchedulers.mainThread())*/;
    }

    /**
     * Interface to obtained picked image in a file
     */
    public interface OnPhotoPickedListener {
        void onPhotoPicked(Observable<File> fileObservable);

        /**
         * Photo has begin copying to our location
         *
         * @param isCopying boolean indicator if copying is proceeding
         */
        void photoCopying(boolean isCopying);
    }
}
