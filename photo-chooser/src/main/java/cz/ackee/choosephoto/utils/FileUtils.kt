package cz.ackee.choosephoto.utils

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.io.IOException

/**
 * Utility methods for working with files
 */
internal object FileUtils {

    /**
     * Get directory in which photos will be stored
     *
     * @return the directori in which the photos are saved in
     */
    private fun Context.getPhotosDir(): File {
        val photos = File(cacheDir, "photos")
        if (!photos.exists()) {
            photos.mkdir()
        }
        return photos
    }

    /**
     * Get file in which picture will be stored
     *
     * @param filename Name of the future file
     * @return file to store the picture into
     */
    fun Context.getPictureFile(filename: String): File {
        val f = File(getPhotosDir(), filename)
        if (!f.exists()) {
            try {
                f.createNewFile()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return f
    }

    /**
     * Return uri of file to which photo will be stored
     *
     * @param ctx Context in which the photos directory is looked up in
     * @return uri of file to which photo will be stored
     */
    fun getUriForFilename(ctx: Context, authority: String, filename: String): Uri {
        return FileProvider.getUriForFile(ctx, authority, ctx.getPictureFile(filename))
    }

    /**
     * Clear and remove photos directory/
     *
     * @param ctx context
     * @return true if deleted successfully
     */
    fun clearPhotosDir(ctx: Context): Boolean {
        return deleteRecursive(ctx.getPhotosDir())
    }

    /**
     * Recursively delete directory or file
     */
    private fun deleteRecursive(fileOrDirectory: File): Boolean {
        if (fileOrDirectory.isDirectory) {
            for (child in fileOrDirectory.listFiles().orEmpty()) {
                deleteRecursive(child)
            }
        }
        return fileOrDirectory.delete()
    }
}