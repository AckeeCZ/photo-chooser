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

import cz.ackee.choosephoto.utils.FileUtils;

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

    public interface DialogBuiltCallback {
        public void dialogBuilt(Uri uri);
    }


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
                        Builder.showGalleryInner(getActivity());
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
            Builder.showGalleryInner(getActivity());
            dismissAllowingStateLoss();
        }
    }

    public static class Builder {

        private final Context ctx;
        private final DialogBuiltCallback callback;
        private final String authority;
        private String pickPhotoString = "Pick photo";
        private String takePhotoString = "Take photo";
        private String fileName = "temp.jpg";


        public Builder(Context ctx, String authority, DialogBuiltCallback callback) {
            this.ctx = ctx;
            this.callback = callback;
            this.authority = authority;
        }

        public Builder setTakePhotoString(@StringRes int takePhoto) {
            takePhotoString = ctx.getResources().getString(takePhoto);
            return this;
        }

        public Builder setTakePhotoString(@NonNull String takePhoto) {
            takePhotoString = takePhoto;
            return this;
        }

        public Builder setPickPhotoString(@StringRes int pickPhoto) {
            pickPhotoString = ctx.getResources().getString(pickPhoto);
            return this;
        }

        public Builder setPickPhotoString(@NonNull String pickPhoto) {
            pickPhotoString = pickPhoto;
            return this;
        }

        public void show(@NonNull FragmentManager manager) {
            callback.dialogBuilt(buildUri());
            ChoosePhotoDialogFragment.newInstance(buildUri(), pickPhotoString, takePhotoString)
                    .show(manager, ChoosePhotoDialogFragment.TAG);
        }

        public void showCamera(Activity activity) {
            callback.dialogBuilt(buildUri());
            showCamera(activity, buildUri());
        }

        public void showGallery(Activity activity) {
            callback.dialogBuilt(buildUri());
            showGalleryInner(activity);
        }

        private Uri buildUri() {
            return FileUtils.getUriForFilename(ctx, authority, fileName);
        }


        private static void showCamera(Activity ac, Uri uri) {
            Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            takePicture.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            List<ResolveInfo> resolvedIntentActivities = ac.getPackageManager().queryIntentActivities(takePicture, PackageManager.MATCH_DEFAULT_ONLY);
            for (ResolveInfo resolvedIntentInfo : resolvedIntentActivities) {
                String packageName = resolvedIntentInfo.activityInfo.packageName;
                ac.grantUriPermission(packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }
            ac.startActivityForResult(takePicture, CAMERA_REQUEST);//zero can be replaced with any action code
        }

        private static void showGalleryInner(Activity activity) {
            Intent pickPhoto = new Intent(Intent.ACTION_GET_CONTENT);
            pickPhoto.setType("image/*");
            activity.startActivityForResult(pickPhoto, GALLERY_REQUEST);//one can be replaced with any action code
        }

        public Builder setFileName(String fileName) {
            this.fileName = fileName;
            return this;
        }
    }
}
