package com.partiful.invitemaker.feature.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.partiful.invitemaker.core.designsystem.AppThemeColors
import com.partiful.invitemaker.core.designsystem.GlassCard
import com.partiful.invitemaker.core.designsystem.NeonButton
import com.partiful.invitemaker.domain.model.CanvasLayer
import com.partiful.invitemaker.domain.model.LayerType
import com.partiful.invitemaker.domain.model.TextAlignmentType
import com.partiful.invitemaker.domain.model.TextLayerData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextEditorBottomSheet(
    viewModel: CardEditorViewModel,
    onDismiss: () -> Unit
) {
    val currentLayer = viewModel.selectedLayer
    var textContent by remember { mutableStateOf(currentLayer?.textData?.text ?: "PARTY TIME") }
    var fontSize by remember { mutableFloatStateOf(currentLayer?.textData?.fontSize ?: 32f) }
    var fontColorHex by remember { mutableStateOf(currentLayer?.textData?.fontColorHex ?: "#FFFFFF") }
    var alignment by remember { mutableStateOf(currentLayer?.textData?.alignment ?: TextAlignmentType.CENTER) }
    var isBold by remember { mutableStateOf(currentLayer?.textData?.isBold ?: true) }
    var isItalic by remember { mutableStateOf(currentLayer?.textData?.isItalic ?: false) }
    var letterSpacing by remember { mutableFloatStateOf(currentLayer?.textData?.letterSpacing ?: 1f) }

    val presets = listOf(
        "YOU ARE INVITED",
        "HAPPY BIRTHDAY",
        "LET'S PARTY",
        "PARTY TONIGHT",
        "SAVE THE DATE",
        "CELEBRATE WITH US",
        "VIP ACCESS ONLY",
        "DRINKS & GOOD VIBES"
    )

    val colorPalette = listOf(
        "#FFFFFF", "#FF007F", "#8A2BE2", "#00F0FF",
        "#FBBF24", "#10B981", "#FF5400", "#E5C3A6",
        "#000000", "#71717A"
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = AppThemeColors.BackgroundDark
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Text(
                text = "Chỉnh sửa văn bản",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            // Text Input
            OutlinedTextField(
                value = textContent,
                onValueChange = { textContent = it },
                label = { Text("Nội dung thiệp") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = AppThemeColors.BrightViolet,
                    unfocusedBorderColor = AppThemeColors.SurfaceBorder
                )
            )

            // Presets
            Text("Mẫu câu có sẵn", color = AppThemeColors.TextSecondary, fontSize = 13.sp)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(presets) { preset ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(AppThemeColors.SurfaceElevated)
                            .clickable { textContent = preset }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(preset, color = Color.White, fontSize = 12.sp)
                    }
                }
            }

            // Colors
            Text("Màu chữ", color = AppThemeColors.TextSecondary, fontSize = 13.sp)
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                colorPalette.forEach { hex ->
                    val color = AppThemeColors.parseHex(hex)
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(color)
                            .border(
                                width = if (fontColorHex == hex) 3.dp else 1.dp,
                                color = if (fontColorHex == hex) Color.White else Color.Transparent,
                                shape = CircleShape
                            )
                            .clickable { fontColorHex = hex }
                    )
                }
            }

            // Font Size
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Cỡ chữ", color = AppThemeColors.TextSecondary, fontSize = 13.sp)
                Text("${fontSize.toInt()} pt", color = Color.White, fontSize = 13.sp)
            }
            Slider(
                value = fontSize,
                onValueChange = { fontSize = it },
                valueRange = 14f..100f,
                colors = SliderDefaults.colors(thumbColor = AppThemeColors.BrightViolet, activeTrackColor = AppThemeColors.BrightViolet)
            )

            // Alignment & Style
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(
                        onClick = { isBold = !isBold },
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = if (isBold) AppThemeColors.BrightViolet else AppThemeColors.SurfaceElevated
                        )
                    ) {
                        Icon(Icons.Default.FormatBold, contentDescription = "Bold", tint = Color.White)
                    }
                    IconButton(
                        onClick = { isItalic = !isItalic },
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = if (isItalic) AppThemeColors.BrightViolet else AppThemeColors.SurfaceElevated
                        )
                    ) {
                        Icon(Icons.Default.FormatItalic, contentDescription = "Italic", tint = Color.White)
                    }
                }

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(AppThemeColors.SurfaceElevated)
                        .padding(4.dp)
                ) {
                    IconButton(onClick = { alignment = TextAlignmentType.LEFT }) {
                        Icon(
                            Icons.Default.FormatAlignLeft,
                            contentDescription = "Left",
                            tint = if (alignment == TextAlignmentType.LEFT) AppThemeColors.BrightViolet else Color.White
                        )
                    }
                    IconButton(onClick = { alignment = TextAlignmentType.CENTER }) {
                        Icon(
                            Icons.Default.FormatAlignCenter,
                            contentDescription = "Center",
                            tint = if (alignment == TextAlignmentType.CENTER) AppThemeColors.BrightViolet else Color.White
                        )
                    }
                    IconButton(onClick = { alignment = TextAlignmentType.RIGHT }) {
                        Icon(
                            Icons.Default.FormatAlignRight,
                            contentDescription = "Right",
                            tint = if (alignment == TextAlignmentType.RIGHT) AppThemeColors.BrightViolet else Color.White
                        )
                    }
                }
            }

            // Apply Button
            NeonButton(
                title = "Áp dụng",
                icon = Icons.Default.Check,
                onButtonClick = {
                    val updated = (currentLayer?.textData ?: TextLayerData()).copy(
                        text = textContent.ifEmpty { "PARTY TIME" },
                        fontSize = fontSize,
                        fontColorHex = fontColorHex,
                        alignment = alignment,
                        isBold = isBold,
                        isItalic = isItalic,
                        letterSpacing = letterSpacing
                    )
                    viewModel.updateSelectedTextData(updated)
                    onDismiss()
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

data class AndroidStickerItem(val name: String, val symbol: String, val category: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StickerPickerBottomSheet(
    viewModel: CardEditorViewModel,
    onDismiss: () -> Unit
) {
    var selectedCategory by remember { mutableStateOf("Tất cả") }
    val categories = listOf("Tất cả", "Sinh nhật", "Tiệc & Rượu", "Âm nhạc", "Tình yêu", "Lễ hội")

    val stickers = listOf(
        AndroidStickerItem("Bánh kem", "cake", "Sinh nhật"),
        AndroidStickerItem("Bóng bay", "balloon", "Sinh nhật"),
        AndroidStickerItem("Hộp quà", "gift", "Sinh nhật"),
        AndroidStickerItem("Vương miện", "crown", "Sinh nhật"),
        AndroidStickerItem("Ly rượu", "wine", "Tiệc & Rượu"),
        AndroidStickerItem("Pháo hoa", "sparkle", "Tiệc & Rượu"),
        AndroidStickerItem("Lửa trại", "flame", "Tiệc & Rượu"),
        AndroidStickerItem("Nốt nhạc", "music", "Âm nhạc"),
        AndroidStickerItem("Tai nghe", "headphones", "Âm nhạc"),
        AndroidStickerItem("Trái tim", "heart", "Tình yêu"),
        AndroidStickerItem("Ngôi sao", "star", "Tình yêu"),
        AndroidStickerItem("Mặt trời", "sun", "Tình yêu"),
        AndroidStickerItem("Mặt trăng", "moon", "Tình yêu"),
        AndroidStickerItem("Mũ tốt nghiệp", "cap", "Lễ hội")
    )

    val filtered = if (selectedCategory == "Tất cả") stickers else stickers.filter { it.category == selectedCategory }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = AppThemeColors.BackgroundDark
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Thư viện Sticker",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            // Category Row
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(categories) { cat ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (selectedCategory == cat) AppThemeColors.BrightViolet else AppThemeColors.SurfaceElevated)
                            .clickable { selectedCategory = cat }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(cat, color = Color.White, fontSize = 13.sp)
                    }
                }
            }

            // Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.height(280.dp)
            ) {
                items(filtered) { sticker ->
                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(AppThemeColors.SurfaceElevated)
                            .clickable {
                                viewModel.addStickerLayer(sticker.symbol, sticker.category)
                                onDismiss()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = when (sticker.symbol) {
                                "cake" -> "🎂"
                                "balloon" -> "🎈"
                                "gift" -> "🎁"
                                "crown" -> "👑"
                                "wine" -> "🍸"
                                "sparkle" -> "✨"
                                "flame" -> "🔥"
                                "music" -> "🎵"
                                "headphones" -> "🎧"
                                "heart" -> "💖"
                                "star" -> "⭐"
                                "sun" -> "☀️"
                                "moon" -> "🌙"
                                "cap" -> "🎓"
                                else -> "🎉"
                            },
                            fontSize = 32.sp
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackgroundPickerBottomSheet(
    viewModel: CardEditorViewModel,
    onDismiss: () -> Unit
) {
    val gradients = listOf(
        listOf("#0F051D", "#2E0249", "#570A57"),
        listOf("#0B132B", "#1C2541", "#3A506B"),
        listOf("#160000", "#370617", "#03071E"),
        listOf("#0A2E20", "#1E4D2B", "#05160E"),
        listOf("#1A181B", "#372549", "#000000"),
        listOf("#0B032D", "#1C0F4F", "#030219"),
        listOf("#3D1300", "#7C2D12", "#EA580C"),
        listOf("#18181B", "#27272A", "#09090B")
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = AppThemeColors.BackgroundDark
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "Chọn nền Gradient",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            gradients.forEach { grad ->
                val colors = grad.map { AppThemeColors.parseHex(it) }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Brush.linearGradient(colors))
                        .clickable {
                            viewModel.setBackgroundGradients(grad)
                            onDismiss()
                        }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LayerManagementBottomSheet(
    viewModel: CardEditorViewModel,
    onDismiss: () -> Unit
) {
    val design by viewModel.cardDesign.collectAsState()
    val selectedId by viewModel.selectedLayerId.collectAsState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = AppThemeColors.BackgroundDark
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Quản lý các lớp (Layers)",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            design.layers.sortedByDescending { it.zIndex }.forEach { layer ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (layer.id == selectedId) AppThemeColors.SurfaceElevated else AppThemeColors.SurfaceDark)
                        .clickable { viewModel.selectLayer(layer.id) }
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = when (layer.type) {
                                LayerType.TEXT -> Icons.Default.TextFields
                                LayerType.STICKER -> Icons.Default.AutoAwesome
                                LayerType.IMAGE -> Icons.Default.Image
                                LayerType.BACKGROUND -> Icons.Default.Palette
                            },
                            contentDescription = null,
                            tint = AppThemeColors.BrightViolet
                        )
                        Text(layer.name, color = Color.White, fontWeight = FontWeight.SemiBold)
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        IconButton(onClick = { viewModel.toggleLockSelectedLayer() }) {
                            Icon(
                                imageVector = if (layer.isLocked) Icons.Default.Lock else Icons.Default.LockOpen,
                                contentDescription = null,
                                tint = if (layer.isLocked) Color.Yellow else Color.Gray
                            )
                        }
                    }
                }
            }
        }
    }
}
