package com.partiful.invitemaker.core.canvas

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import com.partiful.invitemaker.core.designsystem.AppThemeColors
import com.partiful.invitemaker.domain.model.CanvasLayer
import com.partiful.invitemaker.domain.model.CardDesign
import com.partiful.invitemaker.domain.model.LayerType
import com.partiful.invitemaker.domain.model.TextAlignmentType
import java.io.File
import kotlin.math.min

@Composable
fun CanvasRendererView(
    design: CardDesign,
    selectedLayerId: String? = null,
    isInteractive: Boolean = false,
    onSelectLayer: ((String) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .aspectRatio(design.aspectRatio)
            .pointerInput(isInteractive) {
                if (isInteractive) {
                    detectTapGestures { offset ->
                        // Hit test layer based on coordinates
                        val hit = design.layers
                            .sortedByDescending { it.zIndex }
                            .firstOrNull { !it.isLocked }
                        if (hit != null) {
                            onSelectLayer?.invoke(hit.id)
                        }
                    }
                }
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val scaleX = size.width / design.canvasWidth
            val scaleY = size.height / design.canvasHeight
            val canvasScale = min(scaleX, scaleY)

            // 1. Draw Background
            drawBackground(design)

            // 2. Draw Layers in Z-Index Order
            val sortedLayers = design.layers.sortedBy { it.zIndex }
            for (layer in sortedLayers) {
                if (!layer.isVisible) continue

                val layerCenterX = size.width / 2f + (layer.x * canvasScale)
                val layerCenterY = size.height / 2f + (layer.y * canvasScale)

                rotate(degrees = layer.rotation, pivot = Offset(layerCenterX, layerCenterY)) {
                    scale(scale = layer.scale, pivot = Offset(layerCenterX, layerCenterY)) {
                        when (layer.type) {
                            LayerType.TEXT -> drawTextLayer(layer, canvasScale, layerCenterX, layerCenterY)
                            LayerType.STICKER -> drawStickerLayer(layer, canvasScale, layerCenterX, layerCenterY)
                            LayerType.IMAGE -> drawPlaceholderImageLayer(layer, canvasScale, layerCenterX, layerCenterY)
                            LayerType.BACKGROUND -> {}
                        }

                        // Draw selection box if interactive
                        if (isInteractive && layer.id == selectedLayerId) {
                            val halfW = (layer.width * canvasScale) / 2f + 8f
                            val halfH = (layer.height * canvasScale) / 2f + 8f
                            drawRoundRect(
                                color = Color.Cyan,
                                topLeft = Offset(layerCenterX - halfW, layerCenterY - halfH),
                                size = Size(halfW * 2f, halfH * 2f),
                                style = Stroke(
                                    width = 2f,
                                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f)
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun DrawScope.drawBackground(design: CardDesign) {
    if (design.backgroundGradientHexes.size > 1) {
        val colors = design.backgroundGradientHexes.map { AppThemeColors.parseHex(it) }
        drawRect(
            brush = Brush.linearGradient(
                colors = colors,
                start = Offset.Zero,
                end = Offset(size.width, size.height)
            )
        )
    } else {
        drawRect(color = AppThemeColors.parseHex(design.backgroundColorHex, AppThemeColors.BackgroundDark))
    }
}

private fun DrawScope.drawTextLayer(
    layer: CanvasLayer,
    canvasScale: Float,
    centerX: Float,
    centerY: Float
) {
    val data = layer.textData ?: return
    val nativeCanvas = drawContext.canvas.nativeCanvas

    val paint = Paint().apply {
        isAntiAlias = true
        color = android.graphics.Color.parseColor(data.fontColorHex)
        textSize = data.fontSize * canvasScale
        typeface = if (data.isBold && data.isItalic) {
            Typeface.create(Typeface.DEFAULT, Typeface.BOLD_ITALIC)
        } else if (data.isBold) {
            Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        } else if (data.isItalic) {
            Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)
        } else {
            Typeface.DEFAULT
        }
        textAlign = when (data.alignment) {
            TextAlignmentType.LEFT -> Paint.Align.LEFT
            TextAlignmentType.CENTER -> Paint.Align.CENTER
            TextAlignmentType.RIGHT -> Paint.Align.RIGHT
        }
        if (data.shadowRadius > 0) {
            setShadowLayer(
                data.shadowRadius * canvasScale,
                data.shadowOffsetX * canvasScale,
                data.shadowOffsetY * canvasScale,
                android.graphics.Color.parseColor(data.shadowColorHex)
            )
        }
        letterSpacing = data.letterSpacing / 10f
    }

    val lines = data.text.split("\n")
    val lineHeight = paint.fontSpacing
    val startY = centerY - ((lines.size - 1) * lineHeight) / 2f + (paint.textSize / 3f)

    lines.forEachIndexed { index, line ->
        nativeCanvas.drawText(line, centerX, startY + (index * lineHeight), paint)
    }
}

private fun DrawScope.drawStickerLayer(
    layer: CanvasLayer,
    canvasScale: Float,
    centerX: Float,
    centerY: Float
) {
    val data = layer.stickerData ?: return
    val nativeCanvas = drawContext.canvas.nativeCanvas

    val radius = (layer.width * canvasScale) / 2f

    // Draw glowing circle / decorative badge
    val glowPaint = Paint().apply {
        isAntiAlias = true
        color = android.graphics.Color.parseColor(data.tintColorHex ?: "#EC4899")
        if (data.glowRadius > 0) {
            setShadowLayer(
                data.glowRadius * canvasScale * 2f,
                0f, 0f,
                android.graphics.Color.parseColor(data.glowColorHex)
            )
        }
    }
    nativeCanvas.drawCircle(centerX, centerY, radius * 0.7f, glowPaint)

    // Draw inner party icon text (symbol representation)
    val textPaint = Paint().apply {
        isAntiAlias = true
        color = android.graphics.Color.WHITE
        textSize = radius
        textAlign = Paint.Align.CENTER
        typeface = Typeface.DEFAULT_BOLD
    }
    val symbolChar = when {
        data.symbolOrName.contains("cake", true) -> "🎂"
        data.symbolOrName.contains("balloon", true) -> "🎈"
        data.symbolOrName.contains("sparkle", true) -> "✨"
        data.symbolOrName.contains("wine", true) || data.symbolOrName.contains("glass", true) -> "🍸"
        data.symbolOrName.contains("music", true) -> "🎵"
        data.symbolOrName.contains("heart", true) -> "💖"
        data.symbolOrName.contains("star", true) -> "⭐"
        data.symbolOrName.contains("cap", true) || data.symbolOrName.contains("grad", true) -> "🎓"
        data.symbolOrName.contains("sun", true) -> "☀️"
        data.symbolOrName.contains("moon", true) -> "🌙"
        else -> "🎉"
    }
    nativeCanvas.drawText(symbolChar, centerX, centerY + (radius / 3f), textPaint)
}

private fun DrawScope.drawPlaceholderImageLayer(
    layer: CanvasLayer,
    canvasScale: Float,
    centerX: Float,
    centerY: Float
) {
    val halfW = (layer.width * canvasScale) / 2f
    val halfH = (layer.height * canvasScale) / 2f

    drawRoundRect(
        color = AppThemeColors.SurfaceElevated,
        topLeft = Offset(centerX - halfW, centerY - halfH),
        size = Size(halfW * 2f, halfH * 2f)
    )
}

/**
 * High quality clean off-screen renderer for PNG / JPG / PDF Export
 */
object OffscreenCardRenderer {
    fun renderToBitmap(
        context: Context,
        design: CardDesign,
        scaleMultiplier: Float = 1.0f
    ): Bitmap {
        val targetWidth = (design.canvasWidth * scaleMultiplier).toInt()
        val targetHeight = (design.canvasHeight * scaleMultiplier).toInt()

        val bitmap = Bitmap.createBitmap(targetWidth, targetHeight, Bitmap.Config.ARGB_8888)
        val canvas = android.graphics.Canvas(bitmap)

        // 1. Draw Background
        val bgPaint = Paint().apply { isAntiAlias = true }
        if (design.backgroundImagePath != null) {
            val file = File(context.filesDir, design.backgroundImagePath)
            if (file.exists()) {
                val bgBitmap = BitmapFactory.decodeFile(file.absolutePath)
                if (bgBitmap != null) {
                    canvas.drawBitmap(bgBitmap, null, RectF(0f, 0f, targetWidth.toFloat(), targetHeight.toFloat()), bgPaint)
                }
            }
        } else if (design.backgroundGradientHexes.size > 1) {
            val colors = design.backgroundGradientHexes.map { android.graphics.Color.parseColor(it) }.toIntArray()
            val shader = android.graphics.LinearGradient(
                0f, 0f, targetWidth.toFloat(), targetHeight.toFloat(),
                colors, null, android.graphics.Shader.TileMode.CLAMP
            )
            bgPaint.shader = shader
            canvas.drawRect(0f, 0f, targetWidth.toFloat(), targetHeight.toFloat(), bgPaint)
        } else {
            bgPaint.color = android.graphics.Color.parseColor(design.backgroundColorHex)
            canvas.drawRect(0f, 0f, targetWidth.toFloat(), targetHeight.toFloat(), bgPaint)
        }

        // 2. Draw sorted layers
        val sortedLayers = design.layers.sortedBy { it.zIndex }
        for (layer in sortedLayers) {
            if (!layer.isVisible) continue

            canvas.save()
            val layerCenterX = (targetWidth / 2f) + (layer.x * scaleMultiplier)
            val layerCenterY = (targetHeight / 2f) + (layer.y * scaleMultiplier)

            canvas.translate(layerCenterX, layerCenterY)
            canvas.rotate(layer.rotation)
            canvas.scale(layer.scale, layer.scale)

            when (layer.type) {
                LayerType.TEXT -> {
                    val data = layer.textData
                    if (data != null) {
                        val paint = Paint().apply {
                            isAntiAlias = true
                            color = android.graphics.Color.parseColor(data.fontColorHex)
                            textSize = data.fontSize * scaleMultiplier
                            typeface = if (data.isBold && data.isItalic) Typeface.DEFAULT_BOLD else if (data.isBold) Typeface.DEFAULT_BOLD else Typeface.DEFAULT
                            textAlign = when (data.alignment) {
                                TextAlignmentType.LEFT -> Paint.Align.LEFT
                                TextAlignmentType.CENTER -> Paint.Align.CENTER
                                TextAlignmentType.RIGHT -> Paint.Align.RIGHT
                            }
                            if (data.shadowRadius > 0) {
                                setShadowLayer(
                                    data.shadowRadius * scaleMultiplier,
                                    data.shadowOffsetX * scaleMultiplier,
                                    data.shadowOffsetY * scaleMultiplier,
                                    android.graphics.Color.parseColor(data.shadowColorHex)
                                )
                            }
                        }
                        val lines = data.text.split("\n")
                        val lineHeight = paint.fontSpacing
                        val startY = -((lines.size - 1) * lineHeight) / 2f + (paint.textSize / 3f)
                        lines.forEachIndexed { idx, line ->
                            canvas.drawText(line, 0f, startY + (idx * lineHeight), paint)
                        }
                    }
                }
                LayerType.STICKER -> {
                    val data = layer.stickerData
                    if (data != null) {
                        val radius = (layer.width * scaleMultiplier) / 2f
                        val glowPaint = Paint().apply {
                            isAntiAlias = true
                            color = android.graphics.Color.parseColor(data.tintColorHex ?: "#EC4899")
                            if (data.glowRadius > 0) {
                                setShadowLayer(
                                    data.glowRadius * scaleMultiplier * 2f,
                                    0f, 0f,
                                    android.graphics.Color.parseColor(data.glowColorHex)
                                )
                            }
                        }
                        canvas.drawCircle(0f, 0f, radius * 0.7f, glowPaint)

                        val textPaint = Paint().apply {
                            isAntiAlias = true
                            color = android.graphics.Color.WHITE
                            textSize = radius
                            textAlign = Paint.Align.CENTER
                            typeface = Typeface.DEFAULT_BOLD
                        }
                        val symbolChar = when {
                            data.symbolOrName.contains("cake", true) -> "🎂"
                            data.symbolOrName.contains("balloon", true) -> "🎈"
                            data.symbolOrName.contains("wine", true) -> "🍸"
                            data.symbolOrName.contains("music", true) -> "🎵"
                            data.symbolOrName.contains("heart", true) -> "💖"
                            data.symbolOrName.contains("star", true) -> "⭐"
                            data.symbolOrName.contains("sun", true) -> "☀️"
                            else -> "🎉"
                        }
                        canvas.drawText(symbolChar, 0f, radius / 3f, textPaint)
                    }
                }
                LayerType.IMAGE -> {
                    val data = layer.imageData
                    if (data?.localImagePath != null) {
                        val file = File(context.filesDir, data.localImagePath)
                        if (file.exists()) {
                            val imgBitmap = BitmapFactory.decodeFile(file.absolutePath)
                            if (imgBitmap != null) {
                                val imgPaint = Paint().apply {
                                    isAntiAlias = true
                                    // Apply ColorMatrix filter
                                    val cm = ColorMatrix().apply {
                                        setSaturation(data.filter.saturation)
                                    }
                                    colorFilter = ColorMatrixColorFilter(cm)
                                }
                                val halfW = (layer.width * scaleMultiplier) / 2f
                                val halfH = (layer.height * scaleMultiplier) / 2f
                                canvas.drawBitmap(imgBitmap, null, RectF(-halfW, -halfH, halfW, halfH), imgPaint)
                            }
                        }
                    }
                }
                LayerType.BACKGROUND -> {}
            }

            canvas.restore()
        }

        return bitmap
    }
}
