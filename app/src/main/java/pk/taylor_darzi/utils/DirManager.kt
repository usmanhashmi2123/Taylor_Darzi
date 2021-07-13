package pk.taylor_darzi.utils

import android.content.ContentValues
import android.content.Context.MODE_PRIVATE
import android.graphics.Bitmap
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import java.io.File
import java.io.IOException


/**
 * Created by Devlomi on 12/08/2017.
 */
//this class will manage all create file name
//and it contains all folder paths for all types (image,video etc..)
object DirManager {
    private const val EXTENSION_JPG = ".jpg"
    private const val APP_FOLDER_NAME = "Tailor"
    lateinit var currentPhotoPath: String
    //Main App Folder: /sdcard/FireApp/

     fun createImageFile(name: String): File? {
        try {
            val storageDir: File = Utils.mContext!!.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
          return File.createTempFile(
                  "JPEG_${name}_",
                  ".jpg",
                  storageDir
          ).apply {
                // Save a file: path for use with ACTION_VIEW intents
                currentPhotoPath = absolutePath
            }
        }
        catch (e: IOException) {
            e.printStackTrace()
            return null
        }

    }
    
    fun savePhotoToInternalStorage(filename: String, bmp: Bitmap): Boolean {
        var saved= false
        try {
            Utils.curentActivity!!.openFileOutput("$filename.jpg", MODE_PRIVATE).use { stream ->
                bmp.compress(Bitmap.CompressFormat.JPEG, 80, stream)
            }
            saved = true
        } catch (e: IOException) {
            e.printStackTrace()
            saved =false
        }
        return saved
    }
    fun saveImage(imageName: String, bitmap: Bitmap)
    {
        val location = Environment.DIRECTORY_PICTURES + File.pathSeparator + "Tailor"
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, imageName)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.MediaColumns.RELATIVE_PATH, location)
                put(MediaStore.MediaColumns.IS_PENDING, 1)
            }
            else
            {
                @Suppress("DEPRECATION")
                with(File("$location/$imageName")) {
                    @Suppress("DEPRECATION")
                    put(MediaStore.MediaColumns.DATA, path)

                    parentFile!!.mkdir()
                    createNewFile()
                }
            }
        }

        val resolver = Utils.curentActivity!!.contentResolver

        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        try {

            uri?.let { uri ->
                val stream = resolver.openOutputStream(uri)

                stream?.let { stream ->
                    if (!bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream)) {
                        throw IOException("Failed to save bitmap.")
                    }
                } ?: throw IOException("Failed to get output stream.")

            } ?: throw IOException("Failed to create new MediaStore record")

        } catch (e: IOException) {
            if (uri != null) {
                resolver.delete(uri, null, null)
            }
            throw IOException(e)
        } finally {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
        }
    }
}