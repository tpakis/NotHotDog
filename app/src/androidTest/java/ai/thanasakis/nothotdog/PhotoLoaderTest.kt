package ai.thanasakis.nothotdog

import android.graphics.Bitmap
import android.graphics.Color
import android.media.ExifInterface
import android.net.Uri
import androidx.core.graphics.createBitmap
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class PhotoLoaderTest {
    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private val file = File(context.cacheDir, "photo_loader_test.jpg")

    @After
    fun deleteFile() {
        file.delete()
    }

    private fun writeJpeg(width: Int, height: Int, orientation: Int? = null): Uri {
        val bitmap = createBitmap(width, height).apply { eraseColor(Color.RED) }
        file.outputStream().use { bitmap.compress(Bitmap.CompressFormat.JPEG, 90, it) }
        orientation?.let {
            ExifInterface(file.path).apply {
                setAttribute(ExifInterface.TAG_ORIENTATION, it.toString())
                saveAttributes()
            }
        }
        return Uri.fromFile(file)
    }

    private fun load(uri: Uri) = loadPhoto(context.contentResolver, uri)

    @Test
    fun keepsTheSizeOfAnUprightPhoto() {
        val photo = load(writeJpeg(400, 300))
        assertEquals(400, photo.width)
        assertEquals(300, photo.height)
    }

    @Test
    fun rotatesAPhotoTakenSideways() {
        val photo = load(writeJpeg(400, 300, ExifInterface.ORIENTATION_ROTATE_90))
        assertEquals(300, photo.width)
        assertEquals(400, photo.height)
    }

    @Test
    fun keepsTheSizeWhenRotatedByHalfATurn() {
        val photo = load(writeJpeg(400, 300, ExifInterface.ORIENTATION_ROTATE_180))
        assertEquals(400, photo.width)
        assertEquals(300, photo.height)
    }

    @Test
    fun downsamplesALargePhoto() {
        val photo = load(writeJpeg(4096, 3000))
        assertEquals(2048, photo.width)
        assertEquals(1500, photo.height)
    }

    @Test
    fun failsOnAFileThatIsNotAnImage() {
        file.writeText("not an image")
        assertThrows(IOException::class.java) { load(Uri.fromFile(file)) }
    }
}
