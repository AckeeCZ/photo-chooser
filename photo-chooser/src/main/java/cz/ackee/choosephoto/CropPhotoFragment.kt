package cz.ackee.choosephoto

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ProgressBar
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.Fragment
import com.isseiaoki.simplecropview.CropImageView
import com.isseiaoki.simplecropview.callback.CropCallback
import com.isseiaoki.simplecropview.callback.SaveCallback
import cz.ackee.choosephoto.utils.GalleryUtils.getRotationDegrees
import cz.ackee.choosephoto.utils.getColorAttribute
import cz.ackee.choosephoto.utils.getWindowSize
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.io.File
import java.io.IOException

/**
 * Fragment for photo cropping.
 */
class CropPhotoFragment : Fragment() {

    companion object {
        const val KEY_FILE = "file"
        private const val COLOR_KEY = "color"
        private const val MAX_WIDTH_KEY = "max_width"
        private const val MAX_HEIGHT_KEY = "max_height"

        const val REQUEST_CROP = 456
        private const val DEFAULT_MAX_SIZE = 1080

        fun arguments(file: String, color: Int?, maxWidth: Int?, maxHeight: Int?): Bundle {
            val args = Bundle()
            args.putString(KEY_FILE, file)
            if (color != null) {
                args.putInt(COLOR_KEY, color)
            }
            if (maxWidth != null) {
                args.putInt(MAX_WIDTH_KEY, maxWidth)
            }
            if (maxHeight != null) {
                args.putInt(MAX_HEIGHT_KEY, maxHeight)
            }
            return args
        }
    }

    private lateinit var cropImageView: CropImageView
    private lateinit var progressBar: ProgressBar
    private lateinit var content: FrameLayout
    private lateinit var rotateButton: ImageButton
    private lateinit var doneButton: ImageButton

    private val imageFile: String
        get() = requireArguments().getString(KEY_FILE, "")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_crop_photo, container, false)
    }

    @SuppressLint("CheckResult")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cropImageView = view.findViewById(R.id.img_crop)
        progressBar = view.findViewById(android.R.id.progress)
        content = view.findViewById(android.R.id.content)
        rotateButton = view.findViewById(R.id.btn_rotate)
        doneButton = view.findViewById(R.id.btn_done)

        cropImageView.setOutputMaxSize(
            requireArguments().getInt(MAX_WIDTH_KEY, DEFAULT_MAX_SIZE),
            requireArguments().getInt(MAX_HEIGHT_KEY, DEFAULT_MAX_SIZE)
        )

        cropImageView.setCustomRatio(
            requireArguments().getInt(MAX_WIDTH_KEY, DEFAULT_MAX_SIZE),
            requireArguments().getInt(MAX_HEIGHT_KEY, DEFAULT_MAX_SIZE)
        )
        rotateButton.isEnabled = false // to prevent clicks when cropImageView is not yet populated with bitmap
        rotateButton.setOnClickListener { rotate() }
        doneButton.isEnabled = false // to prevent clicks when cropImageView is not yet populated with bitmap
        doneButton.setOnClickListener { done() }

        if (cropImageView.imageBitmap == null) {
            Observable.just(imageFile)
                .subscribeOn(Schedulers.newThread())
                .map { filename ->
                    var bitmap = BitmapFactory.decodeFile(filename)
                    val screenWidth: Int = requireActivity().getWindowSize()[0]
                    if (bitmap.width >= screenWidth) {
                        val ratio = bitmap.height.toFloat() / bitmap.width
                        val resized = Bitmap.createScaledBitmap(bitmap, screenWidth, (screenWidth * ratio).toInt(), false)
                        if (bitmap != resized) {
                            bitmap.recycle()
                            bitmap = resized
                        }
                    }
                    try {
                        val exif = ExifInterface(filename)
                        val rotationDegrees = getRotationDegrees(exif)
                        if (rotationDegrees != 0) {
                            val m = Matrix()
                            m.preRotate(rotationDegrees.toFloat())
                            val rotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, m, false)
                            if (rotated != bitmap) {
                                bitmap.recycle()
                            }
                            bitmap = rotated
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                    bitmap
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ bitmap ->
                    if (cropImageView != null) {
                        cropImageView.imageBitmap = bitmap
                        rotateButton.isEnabled = true
                        doneButton.isEnabled = true
                    }
                }) { throwable -> throwable.printStackTrace() }
        }
        val wrapper = DrawableCompat.wrap(doneButton.drawable.mutate())
        DrawableCompat.setTint(
            wrapper,
            if (arguments!!.containsKey(COLOR_KEY)) arguments!!.getInt(COLOR_KEY) else requireContext().getColorAttribute(R.attr.colorAccent)
        )
        doneButton.setImageDrawable(wrapper)
    }

    private fun rotate() {
        cropImageView.rotateImage(CropImageView.RotateDegrees.ROTATE_M90D)
    }

    private fun done() {
        showProgress(true)
        cropImageView.startCrop(Uri.fromFile(File(imageFile)), object : CropCallback {
            override fun onSuccess(cropped: Bitmap) {}
            override fun onError(t: Throwable) {}
        }, object : SaveCallback {
            override fun onSuccess(outputUri: Uri) {
                val intent = Intent()
                intent.putExtra(KEY_FILE, outputUri.path)
                requireActivity().setResult(Activity.RESULT_OK, intent)
                requireActivity().finish()
            }

            override fun onError(t: Throwable) {}
        })
    }

    private fun showProgress(show: Boolean) {
        progressBar.visibility = if (show) View.VISIBLE else View.GONE
        content.visibility = if (show) View.GONE else View.VISIBLE
    }
}