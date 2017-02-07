package cz.ackee.choosephoto.utils;

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

    /**
     * Get directory in which photos will be stored
     * @param ctx
     * @return
     */
    public static File getPhotosDir(Context ctx) {
        File photos = new File((ctx.getCacheDir()), "photos");
        if (!photos.exists()) {
            photos.mkdir();
        }
        return photos;
    }

    /**
     * Get file in which picture will be stored
     * @param context
     * @param filename
     * @return
     */
    public static File getPictureFile(Context context, String filename) {
        File f = new File(getPhotosDir(context), filename);
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
     * Return uri of file to which photo will be stored
     *
     * @param ctx
     * @return
     */
    public static Uri getUriForFilename(Context ctx, String authority, String filename) {
        Uri imageUri = FileProvider.getUriForFile(ctx, authority, getPictureFile(ctx, filename));
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

    /**
     * Recursively delete directory
     * @param fileOrDirectory
     * @return
     */
    private static boolean deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory()) {
            for (File child : fileOrDirectory.listFiles()) {
                deleteRecursive(child);
            }
        }
        return fileOrDirectory.delete();
    }
}
