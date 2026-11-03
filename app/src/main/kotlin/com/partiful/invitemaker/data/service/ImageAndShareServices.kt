package com.partiful.invitemaker.data.service

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

class ImageProcessorService(private val context: Context) {

    fun saveBitmap(bitmap: Bitmap, format: Bitmap.CompressFormat = Bitmap.CompressFormat.PNG, quality: Int = 100): File? {
        return try {
            val extension = if (format == Bitmap.CompressFormat.PNG) "png" else "jpg"
            val file = File(context.cacheDir, "partiful_${UUID.randomUUID()}.$extension")
            val out = FileOutputStream(file)
            bitmap.compress(format, quality, out)
            out.flush()
            out.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun saveImageToInternal(uri: Uri): String? {
        return try {
            val fileName = "img_${UUID.randomUUID()}.jpg"
            val destFile = File(context.filesDir, fileName)
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            val outputStream = FileOutputStream(destFile)
            inputStream.copyTo(outputStream)
            inputStream.close()
            outputStream.flush()
            outputStream.close()
            fileName
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

class ShareService(private val context: Context) {

    fun shareFile(file: File, mimeType: String, title: String = "Chia sẻ thiệp mời Partiful") {
        try {
            val contentUri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = mimeType
                putExtra(Intent.EXTRA_STREAM, contentUri)
                putExtra(Intent.EXTRA_SUBJECT, title)
                putExtra(Intent.EXTRA_TEXT, "Tham gia bữa tiệc cùng tôi nhé! 🎉\n(Thiết kế bằng Partiful)")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            val chooser = Intent.createChooser(shareIntent, title).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(chooser)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
