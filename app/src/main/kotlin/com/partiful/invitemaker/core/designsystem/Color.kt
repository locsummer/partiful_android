package com.partiful.invitemaker.core.designsystem

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

object AppThemeColors {
    // Primary Neon & Party Accents
    val NeonPurple = Color(0xFF8A2BE2)
    val BrightViolet = Color(0xFFA855F7)
    val HotPink = Color(0xFFEC4899)
    val RoseNeon = Color(0xFFF43F5E)
    val SunsetCoral = Color(0xFFFF6B6B)
    val AmberGlow = Color(0xFFFBBF24)
    val EmeraldMint = Color(0xFF10B981)
    val CyanVibe = Color(0xFF06B6D4)
    val ElectricBlue = Color(0xFF3B82F6)

    // Backgrounds & Surfaces (Velvet Dark)
    val BackgroundDark = Color(0xFF0F0E17)
    val SurfaceDark = Color(0xFF1A1926)
    val SurfaceElevated = Color(0xFF262338)
    val SurfaceBorder = Color.White.copy(alpha = 0.12f)

    // Text Colors
    val TextPrimary = Color(0xFFFFFFFE)
    val TextSecondary = Color(0xFFA7A9BE)
    val TextMuted = Color.White.copy(alpha = 0.4f)

    // Gradients
    val PartyGradient = Brush.linearGradient(
        listOf(NeonPurple, HotPink, SunsetCoral)
    )

    val DarkCardGradient = Brush.linearGradient(
        listOf(SurfaceElevated, SurfaceDark)
    )

    val MintGradient = Brush.linearGradient(
        listOf(EmeraldMint, CyanVibe)
    )

    fun parseHex(hex: String, default: Color = Color.White): Color {
        return try {
            val cleanHex = hex.removePrefix("#")
            when (cleanHex.length) {
                6 -> Color(android.graphics.Color.parseColor("#$cleanHex"))
                8 -> Color(android.graphics.Color.parseColor("#$cleanHex"))
                else -> default
            }
        } catch (e: Exception) {
            default
        }
    }
}
