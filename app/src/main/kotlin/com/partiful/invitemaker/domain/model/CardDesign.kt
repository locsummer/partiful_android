package com.partiful.invitemaker.domain.model

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class CardDesign(
    val id: String = UUID.randomUUID().toString(),
    val title: String = "Thiệp mời mới",
    val canvasWidth: Float = 1080f,
    val canvasHeight: Float = 1920f,
    val backgroundColorHex: String = "#0F0E17",
    val backgroundGradientHexes: List<String> = listOf("#1A0B2E", "#0F0E17", "#2D0A4E"),
    val backgroundGradientAngle: Float = 45f,
    val backgroundImagePath: String? = null,
    val layers: List<CanvasLayer> = emptyList(),
    val eventInfo: PartyEvent = PartyEvent(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isDraft: Boolean = true,
    val thumbnailPath: String? = null
) {
    val aspectRatio: Float
        get() = if (canvasHeight > 0f) canvasWidth / canvasHeight else 9f / 16f
}

@Serializable
data class Template(
    val id: String,
    val name: String,
    val category: TemplateCategory,
    val cardDesign: CardDesign,
    val previewColorHex: String = "#8A2BE2",
    val tags: List<String> = emptyList()
)
