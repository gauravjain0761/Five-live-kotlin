package com.fivelive.app.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.InputStream

object PhotoUtil {
    private const val FOLDER = "/5Live/"
    fun getImageStringToUri(con: Context, selectImage: Uri?): String {
        var bitmap_name = ""
        var selectedImage: Bitmap? = null
        if (selectImage != null && selectImage.toString() != "") {
            var imageStream: InputStream? = null
            try {
                imageStream = con.contentResolver.openInputStream(selectImage)
                selectedImage = BitmapFactory.decodeStream(imageStream)
                selectedImage = getResizedBitmap(selectedImage, 450)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
            val fileName = System.currentTimeMillis().toString() + ".png"
            createDirectoryAndSaveFile(con, selectedImage, fileName)
            bitmap_name = con.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                .toString() + FOLDER + fileName
        }
        return bitmap_name
    }

    fun getResizedBitmap(image: Bitmap?, maxSize: Int): Bitmap {
        var width = image!!.width
        var height = image.height
        val bitmapRatio = width.toFloat() / height.toFloat()
        if (bitmapRatio > 1) {
            width = maxSize
            height = (width / bitmapRatio).toInt()
        } else {
            height = maxSize
            width = (height * bitmapRatio).toInt()
        }
        return Bitmap.createScaledBitmap(image, width, height, true)
    }

    fun createDirectoryAndSaveFile(context: Context, imageToSave: Bitmap?, fileName: String?) {
        val direct =
            File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString() + FOLDER)
        if (!direct.exists()) {
            val wallpaperDirectory = File(
                context.getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString() + FOLDER
            )
            wallpaperDirectory.mkdirs()
        }
        val file = File(
            File(
                context.getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString() + FOLDER
            ), fileName
        )
        if (file.exists()) {
            file.delete()
        }
        try {
            val out = FileOutputStream(file)
            imageToSave!!.compress(Bitmap.CompressFormat.JPEG, 100, out)
            out.flush()
            out.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}