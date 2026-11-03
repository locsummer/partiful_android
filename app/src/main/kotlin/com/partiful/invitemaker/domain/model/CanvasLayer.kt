package com.partiful.invitemaker.domain.model

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class ImageLayerData(
    val localImagePath: String? = null,
    val filter: FilterEffect = FilterEffect(),
    val cornerRadius: Float = 0f
)

@Serializable
data class TextLayerData(
    val text: String = "PARTY TIME",
    val fontFamily: String = "System",
    val fontSize: Float = 28f,
    val fontColorHex: String = "#FFFFFF",
    val alignment: TextAlignmentType = TextAlignmentType.CENTER,
    val isBold: Boolean = true,
    val isItalic: Boolean = false,
    val letterSpacing: Float = 1f,
    val strokeWidth: Float = 0f,
    val strokeColorHex: String = "#000000",
    val shadowRadius: Float = 4f,
    val shadowColorHex: String = "#80000000",
    val shadowOffsetX: Float = 2f,
    val shadowOffsetY: Float = 2f
)

@Serializable
data class StickerLayerData(
    val symbolOrName: String,
    val category: String = "Party",
    val tintColorHex: String? = null,
    val glowRadius: Float = 0f,
    val glowColorHex: String = "#FF007F"
)

@Serializable
data class CanvasLayer(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "Layer",
    val type: LayerType,
    val x: Float = 0f,
    val y: Float = 0f,
    val width: Float = 200f,
    val height: Float = 200f,
    val rotation: Float = 0f, // degrees
    val scale: Float = 1f,
    val opacity: Float = 1f,
    val zIndex: Int = 0,
    val isLocked: Boolean = false,
    val isVisible: Boolean = true,
    val imageData: ImageLayerData? = null,
    val textData: TextLayerData? = null,
    val stickerData: StickerLayerData? = null
)
