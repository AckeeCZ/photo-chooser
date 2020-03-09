package cz.ackee.choosephoto

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.provider.MediaStore
import android.widget.AdapterView.OnItemClickListener
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import cz.ackee.choosephoto.utils.FileUtils.getUriForFilename

/**
 * Dialog with choices of selecting picture
 */
class ChoosePhotoDialogFragment : DialogFragment() {

    companion object {

        const val CAMERA_REQUEST = 12
        const val GALLERY_REQUEST = 13
        private const val REQUEST_PERMISSION = 123

        private const val URI_KEY = "uri"
        private const val STRING_PICK = "STRING_PICK"
        private const val STRING_TAKE = "STRING_TAKE"

        private val permissionsList = arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE)

        private fun newInstance(uri: Uri, pickPhoto: String, takePhoto: String): ChoosePhotoDialogFragment {
            val args = Bundle()
            args.putParcelable(URI_KEY, uri)
            args.putString(STRING_PICK, pickPhoto)
            args.putString(STRING_TAKE, takePhoto)
            val fragment = ChoosePhotoDialogFragment()
            fragment.arguments = args
            return fragment
        }

        private fun newInstance(uri: Uri): ChoosePhotoDialogFragment {
            val args = Bundle()
            args.putParcelable(URI_KEY, uri)
            val fragment = ChoosePhotoDialogFragment()
            fragment.arguments = args
            return fragment
        }
    }

    interface DialogBuiltCallback {
        fun dialogBuilt(uri: Uri?)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        val items = arrayOf(arguments!!.getString(STRING_PICK), arguments!!.getString(STRING_TAKE))
        builder.setItems(items, null)
        val alertDialog = builder.create()
        alertDialog.listView.onItemClickListener = OnItemClickListener { _, _, position, _ ->
            if (position != 0) {
                Builder.showCamera(activity, requireArguments().getParcelable<Parcelable>(URI_KEY) as Uri)
                alertDialog.dismiss()
            } else {
                if (
                    permissionsList.all { permission ->
                        ContextCompat.checkSelfPermission(requireContext(), permission) == PackageManager.PERMISSION_GRANTED
                    }
                ) {
                    Builder.showGalleryInner(activity)
                    alertDialog.dismiss()
                } else {
                    requestPermissions(permissionsList, REQUEST_PERMISSION)
                }
            }
        }
        return alertDialog
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED && requestCode == REQUEST_PERMISSION) {
            Builder.showGalleryInner(activity)
            dismissAllowingStateLoss()
        }
    }

    class Builder(private val ctx: Context, private val callback: DialogBuiltCallback) {

        companion object {

            fun showCamera(ac: Activity?, uri: Uri?) {
                val takePicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                takePicture.putExtra(MediaStore.EXTRA_OUTPUT, uri)
                val resolvedIntentActivities =
                    ac!!.packageManager.queryIntentActivities(takePicture, PackageManager.MATCH_DEFAULT_ONLY)
                for (resolvedIntentInfo in resolvedIntentActivities) {
                    val packageName = resolvedIntentInfo.activityInfo.packageName
                    ac.grantUriPermission(packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                ac.startActivityForResult(takePicture, CAMERA_REQUEST) //zero can be replaced with any action code
            }

            fun showGalleryInner(activity: Activity?) {
                val pickPhoto = Intent(Intent.ACTION_GET_CONTENT)
                pickPhoto.type = "image/*"
                activity!!.startActivityForResult(
                    pickPhoto,
                    GALLERY_REQUEST
                )
            }
        }

        private var pickPhotoString = "Pick photo"
        private var takePhotoString = "Take photo"
        private var fileName = "temp.jpg"

        fun setTakePhotoString(@StringRes takePhoto: Int): Builder {
            takePhotoString = ctx.resources.getString(takePhoto)
            return this
        }

        fun setTakePhotoString(takePhoto: String): Builder {
            takePhotoString = takePhoto
            return this
        }

        fun setPickPhotoString(@StringRes pickPhoto: Int): Builder {
            pickPhotoString = ctx.resources.getString(pickPhoto)
            return this
        }

        fun setPickPhotoString(pickPhoto: String): Builder {
            pickPhotoString = pickPhoto
            return this
        }

        fun show(manager: FragmentManager) {
            callback.dialogBuilt(buildUri())
            newInstance(buildUri(), pickPhotoString, takePhotoString)
                .show(manager, ChoosePhotoDialogFragment::class.java.name)
        }

        fun showCamera(activity: Activity?) {
            callback.dialogBuilt(buildUri())
            showCamera(activity, buildUri())
        }

        fun showGallery(activity: Activity?) {
            callback.dialogBuilt(buildUri())
            showGalleryInner(activity)
        }

        private fun buildUri(): Uri {
            return getUriForFilename(ctx, ctx.packageName + ".choose_photo", fileName)
        }

        fun setFileName(fileName: String): Builder {
            this.fileName = fileName
            return this
        }
    }
}