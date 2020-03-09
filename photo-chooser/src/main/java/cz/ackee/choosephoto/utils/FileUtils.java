package cz.ackee.choosephoto.utils;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.io.IOException;

import androidx.core.content.FileProvider;

/**
 * Utility methods for working with files
 */
public class FileUtils {
    public static final String TAG = FileUtils.class.getName();

    /**
     * Get directory in which photos will be stored
     * @param ctx Context in which the cache folder is looked up in
     * @return the directori in which the photos are saved in
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
     * @param context Context in which the photos directory is looked up in
     * @param filename Name of the future file
     * @return file to store the picture into
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
     * @param ctx Context in which the photos directory is looked up in
     * @return uri of file to which photo will be stored
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
