package com.partiful.invitemaker.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class LayerType {
    BACKGROUND,
    IMAGE,
    TEXT,
    STICKER
}

@Serializable
enum class TextAlignmentType {
    LEFT,
    CENTER,
    RIGHT
}

@Serializable
data class FilterEffect(
    val brightness: Float = 0f,      // -0.5f to 0.5f
    val contrast: Float = 1f,        // 0.5f to 2.0f
    val saturation: Float = 1f,      // 0.0f to 2.0f
    val blurRadius: Float = 0f,      // 0f to 10f
    val vignetteIntensity: Float = 0f, // 0f to 1f
    val presetName: String = "Normal"
)
