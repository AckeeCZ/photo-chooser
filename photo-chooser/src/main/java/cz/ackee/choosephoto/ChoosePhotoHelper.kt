package cz.ackee.choosephoto

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import cz.ackee.choosephoto.ChoosePhotoDialogFragment.DialogBuiltCallback
import cz.ackee.choosephoto.utils.FileUtils.clearPhotosDir
import cz.ackee.choosephoto.utils.FileUtils.getPictureFile
import cz.ackee.choosephoto.utils.GalleryUtils.copyUriToMyUri
import cz.ackee.choosephoto.utils.getColorAttribute
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.io.File

/**
 * Helper class for choosing an image from the device
 *
 * Class using photo helper has to call pass onActivityResult callback to this class to
 * obtain picked photo.
 */
class ChoosePhotoHelper(
    private val activity: Activity,
    private val onPhotoCopyingListener: OnPhotoCopyingListener? = null,
    private val onPhotoPickedListener: OnPhotoPickedListener
) : DialogBuiltCallback {

    companion object {

        const val LAST_URI_KEY = "cz.ackee.choosephoto.last_uri"
        const val WITH_CROP_KEY = "cz.ackee.choosephoto.with_crop"
        const val TINT_COLOR_KEY = "cz.ackee.choosephoto.tint_color"
    }

    private var withCrop = false

    // last used uri
    private var lastUri: Uri? = null
    private var tintColor: Int = activity.getColorAttribute(R.attr.colorPrimary)
    private var maxWidth: Int? = null
    private var maxHeight: Int? = null

    /**
     * Returns builder for creating ChoosePhotoDialogFragment
     */
    fun getChoosePhotoDialogBuilder(withCrop: Boolean): ChoosePhotoDialogFragment.Builder {
        this.withCrop = withCrop
        return ChoosePhotoDialogFragment.Builder(activity, this)
    }

    /**
     * Returns builder for creating ChoosePhotoDialogFragment
     */
    fun getChoosePhotoDialogBuilder(withCrop: Boolean, tintColor: Int): ChoosePhotoDialogFragment.Builder {
        this.withCrop = withCrop
        this.tintColor = tintColor
        return ChoosePhotoDialogFragment.Builder(activity, this)
    }

    /**
     * Returns builder for creating ChoosePhotoDialogFragment
     */
    fun getChoosePhotoDialogBuilder(withCrop: Boolean, tintColor: Int, maxWidth: Int?,
        maxHeight: Int?): ChoosePhotoDialogFragment.Builder {
        this.withCrop = withCrop
        this.tintColor = tintColor
        this.maxWidth = maxWidth
        this.maxHeight = maxHeight
        return ChoosePhotoDialogFragment.Builder(activity, this)
    }

    /**
     * Returns builder for creating ChoosePhotoDialogFragment
     */
    val choosePhotoDialogBuilder: ChoosePhotoDialogFragment.Builder
        get() = getChoosePhotoDialogBuilder(false)

    /**
     * This method has to be called in onActivityResult method
     *
     * @param requestCode RequestCode
     * @param resultCode  ResultCode
     * @param data        Intent
     */
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == ChoosePhotoDialogFragment.GALLERY_REQUEST || requestCode == ChoosePhotoDialogFragment.CAMERA_REQUEST) {
                onPhotoCopyingListener?.photoCopying(true)
                val fromGallery = requestCode == ChoosePhotoDialogFragment.GALLERY_REQUEST

                val uri: Uri?
                uri = if (!fromGallery) {
                    lastUri!!
                } else {
                    data!!.data!!
                }
                val fileObservable = onPhotoPicked(uri, fromGallery)
                if (!withCrop) {
                    onPhotoPickedListener.onPhotoPicked(fileObservable)
                } else {
                    fileObservable
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe { file ->
                            CropPhotoActivity.open(activity, file.absolutePath, tintColor, maxWidth, maxHeight,
                                CropPhotoFragment.REQUEST_CROP)
                        }
                }
            }
            if (requestCode == CropPhotoFragment.REQUEST_CROP) {
                onPhotoPickedListener.onPhotoPicked(
                    Observable.just(File(data!!.getStringExtra(CropPhotoFragment.KEY_FILE)))
                )
            }
        }
    }

    fun clear() {
        clearPhotosDir(activity)
    }

    private fun onPhotoPicked(uri: Uri, fromGallery: Boolean): Observable<File> {
        return Observable.just(Any())
            .subscribeOn(Schedulers.newThread())
            .map {
                if (fromGallery) {
                    copyUriToMyUri(activity, uri, Uri.fromFile(activity.getPictureFile(lastUri!!.lastPathSegment!!)))
                } else true
            }
            .map { activity.getPictureFile(lastUri!!.lastPathSegment!!) }
            .doOnNext { onPhotoCopyingListener?.photoCopying(false) }
    }

    override fun dialogBuilt(uri: Uri?) {
        lastUri = uri
    }

    fun saveInstanceState(outState: Bundle) {
        if (lastUri != null) {
            outState.putParcelable(LAST_URI_KEY, lastUri)
        }
        outState.putBoolean(WITH_CROP_KEY, withCrop)
        outState.putInt(TINT_COLOR_KEY, tintColor)
    }

    fun restoreInstanceState(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            lastUri = savedInstanceState.getParcelable(LAST_URI_KEY)
            withCrop = savedInstanceState.getBoolean(WITH_CROP_KEY, false)
            tintColor = savedInstanceState.getInt(TINT_COLOR_KEY, -1)
        }
    }
}

/**
 * Interface to obtained picked image in a file
 */
interface OnPhotoPickedListener {

    /**
     * Photo was picked
     *
     * @param fileObservable observable that emits the picked file
     */
    fun onPhotoPicked(fileObservable: Observable<File>?)
}

/**
 * Interface indicating that copying is in progress
 */
interface OnPhotoCopyingListener {

    /**
     * Photo has begin copying
     *
     * @param isCopying boolean indicator if copying is proceeding
     */
    fun photoCopying(isCopying: Boolean)
}