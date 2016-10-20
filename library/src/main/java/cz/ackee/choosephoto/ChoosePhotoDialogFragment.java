package cz.ackee.choosephoto;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.util.List;

/**
 * Dialog with choices of selecting picture
 * Created by David Bilik[david.bilik@ackee.cz] on {8. 4. 2015}
 */
public class ChoosePhotoDialogFragment extends DialogFragment {
    public static final String TAG = ChoosePhotoDialogFragment.class.getName();
    public static final int CAMERA_REQUEST = 12;
    public static final int GALLERY_REQUEST = 13;

    private static final String URI_KEY = "uri";
    private static final String STRING_PICK = "STRING_PICK";
    private static final String STRING_TAKE = "STRING_TAKE";
    private static final int REQUEST_PERMISSION = 123;

    private static ChoosePhotoDialogFragment newInstance(Uri uri, String pickPhoto, String
            takePhoto) {
        Bundle args = new Bundle();
        args.putParcelable(URI_KEY, uri);
        args.putString(STRING_PICK, pickPhoto);
        args.putString(STRING_TAKE, takePhoto);
        ChoosePhotoDialogFragment fragment = new ChoosePhotoDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private static ChoosePhotoDialogFragment newInstance(Uri uri) {
        Bundle args = new Bundle();
        args.putParcelable(URI_KEY, uri);
        ChoosePhotoDialogFragment fragment = new ChoosePhotoDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        String[] items = {getArguments().getString(STRING_PICK), getArguments().getString(STRING_TAKE)};
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which != 0) {
                    Builder.showCamera(getActivity(), (Uri) getArguments().getParcelable(URI_KEY));
                } else {
                    if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        Builder.showGallery(getActivity());
                    } else {
                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION);
                    }
                }
            }
        });
        return builder.create();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult() called with: " + "requestCode = [" + requestCode + "], permissions = [" + permissions + "], grantResults = [" + grantResults + "]");
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && requestCode == REQUEST_PERMISSION) {
            Builder.showGallery(getActivity());
            dismissAllowingStateLoss();
        }
    }

    public static class Builder {

        private final Context mCtx;
        private String mPickPhotoString = "Pick photo";
        private String mTakePhotoString = "Take photo";
        private Uri mCustomUri;

        public Builder(Context ctx) {
            mCtx = ctx;

            mCustomUri = GalleryUtils.getPicturePhoto(mCtx);

        }

        public Builder setTakePhotoString(@StringRes int takePhoto) {
            mTakePhotoString = mCtx.getResources().getString(takePhoto);
            return this;
        }

        public Builder setTakePhotoString(@NonNull String takePhoto) {
            mTakePhotoString = takePhoto;
            return this;
        }

        public Builder setPickPhotoString(@StringRes int pickPhoto) {
            mPickPhotoString = mCtx.getResources().getString(pickPhoto);
            return this;
        }

        public Builder setPickPhotoString(@NonNull String pickPhoto) {
            mPickPhotoString = pickPhoto;
            return this;
        }

        public Builder setCustomUri(@NonNull Uri uri) {
            mCustomUri = uri;
            return this;
        }

        public void show(@NonNull FragmentManager manager) {
            ChoosePhotoDialogFragment.newInstance(mCustomUri, mPickPhotoString, mTakePhotoString)
                    .show(manager, ChoosePhotoDialogFragment.TAG);
        }

        public void showCamera(Activity activity) {
            showCamera(activity, mCustomUri);
        }

        private static void showCamera(Activity ac, Uri uri) {
            Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            takePicture.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            List<ResolveInfo> resolvedIntentActivities = ac.getPackageManager().queryIntentActivities(takePicture, PackageManager.MATCH_DEFAULT_ONLY);
//            ac.revokeUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);

            for (ResolveInfo resolvedIntentInfo : resolvedIntentActivities) {
                String packageName = resolvedIntentInfo.activityInfo.packageName;
                ac.grantUriPermission(packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }
            ac.startActivityForResult(takePicture, CAMERA_REQUEST);//zero can be replaced with any action code
        }

        public static void showGallery(Activity activity) {
            Intent pickPhoto = new Intent(Intent.ACTION_GET_CONTENT);
            pickPhoto.setType("image/*");
            activity.startActivityForResult(pickPhoto, GALLERY_REQUEST);//one can be replaced with any action code
        }
    }
}
