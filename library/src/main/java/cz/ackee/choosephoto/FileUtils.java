package cz.ackee.choosephoto;

import android.content.Context;
import android.net.Uri;
import android.support.v4.content.FileProvider;
import android.util.Log;

import java.io.File;
import java.io.IOException;

/**
 * utility methods for working with files
 * Created by David Bilik[david.bilik@ackee.cz] on {06.08.2015}
 **/
public class FileUtils {
    public static final String TAG = FileUtils.class.getName();

    public static File getPhotosDir(Context ctx) {
        return new File((ctx.getCacheDir()), "photos");
    }

    public static File getTempPictureFile(Context context) {
        File f = new File(getPhotosDir(context), String.format("picture%s", Long.toString(System.currentTimeMillis())));
        if (!f.exists()) {
            try {
                if (!f.createNewFile()) {
                    Log.e(TAG, "Creating new temp file failed");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return f;
    }

    /**
     * Return uri for camera
     *
     * @param ctx
     * @return
     */
    public static Uri getUriForCamera(Context ctx, String authority) {
        Uri imageUri = FileProvider.getUriForFile(ctx, authority, getTempPictureFile(ctx));
        return imageUri;
    }

    /**
     * Clear and remove photos directory/
     *
     * @param ctx context
     * @return true if deleted successfully
     */
    public static boolean clearPhotosDir(Context ctx) {
        return deleteRecursive(getPhotosDir(ctx));
    }

    private static boolean deleteRecursive(File fileOrDirectory) {

        if (fileOrDirectory.isDirectory()) {
            for (File child : fileOrDirectory.listFiles()) {
                deleteRecursive(child);
            }
        }

        return fileOrDirectory.delete();
    }
}
