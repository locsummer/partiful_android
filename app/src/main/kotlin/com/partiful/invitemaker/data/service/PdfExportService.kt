package com.partiful.invitemaker.data.service

import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfDocument
import java.io.File
import java.io.FileOutputStream

class PdfExportService(private val context: Context) {

    fun exportPdf(bitmap: Bitmap, title: String): File? {
        val document = PdfDocument()
        return try {
            // Standard A4 aspect ratio in points: 595 x 842 or exact matching ratio
            val pageWidth = 595
            val pageHeight = (pageWidth * (bitmap.height.toFloat() / bitmap.width.coerceAtLeast(1))).toInt()

            val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
            val page = document.startPage(pageInfo)

            val canvas = page.canvas
            val dstRect = android.graphics.Rect(0, 0, pageWidth, pageHeight)
            val paint = android.graphics.Paint().apply { isFilterBitmap = true }
            canvas.drawBitmap(bitmap, null, dstRect, paint)

            document.finishPage(page)

            val safeTitle = title.replace(" ", "_").replace(Regex("[^a-zA-Z0-9_]"), "")
            val outputFile = File(context.cacheDir, "${safeTitle}_${System.currentTimeMillis()}.pdf")
            val outputStream = FileOutputStream(outputFile)
            document.writeTo(outputStream)
            outputStream.flush()
            outputStream.close()

            outputFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        } finally {
            document.close()
        }
    }
}
