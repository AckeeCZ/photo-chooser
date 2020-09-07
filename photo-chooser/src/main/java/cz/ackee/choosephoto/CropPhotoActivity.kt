package cz.ackee.choosephoto

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

/**
 * Activity that holds crop photo fragment
 */
class CropPhotoActivity : AppCompatActivity() {

    companion object {
        private const val FILENAME_KEY = "filename"
        private const val COLOR_KEY = "color"
        private const val MAX_WIDTH_KEY = "max_width"
        private const val MAX_HEIGHT_KEY = "max_height"
        private const val UNKNOWN_VALUE = -1

        fun open(ctx: Activity, filename: String?, tintColor: Int, maxWidth: Int?, maxHeight: Int?, requestCode: Int) {
            val intent = Intent(ctx, CropPhotoActivity::class.java)
                .putExtra(FILENAME_KEY, filename)
                .putExtra(COLOR_KEY, tintColor)
            if (maxWidth != null) {
                intent.putExtra(MAX_WIDTH_KEY, maxWidth)
            }
            if (maxHeight != null) {
                intent.putExtra(MAX_HEIGHT_KEY, maxHeight)
            }
            ctx.startActivityForResult(intent, requestCode)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crop)
        if (supportFragmentManager.findFragmentById(R.id.fragment_container) == null) {
            val cropPhotoFragment = CropPhotoFragment()
            val fileName = intent.getStringExtra(FILENAME_KEY)
            val color = intent.getIntExtra(COLOR_KEY, UNKNOWN_VALUE)
            val maxWidth = intent.getIntExtra(MAX_WIDTH_KEY, UNKNOWN_VALUE)
            val maxHeight = intent.getIntExtra(MAX_HEIGHT_KEY, UNKNOWN_VALUE)
            cropPhotoFragment.arguments = CropPhotoFragment.arguments(fileName, if (color == UNKNOWN_VALUE) null else color,
                if (maxWidth == UNKNOWN_VALUE) null else maxWidth,
                if (maxHeight == UNKNOWN_VALUE) null else maxHeight
            )
            supportFragmentManager.beginTransaction().add(R.id.fragment_container, cropPhotoFragment).commit()
        }
    }
}