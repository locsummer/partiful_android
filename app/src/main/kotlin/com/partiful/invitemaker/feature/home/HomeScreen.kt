package com.partiful.invitemaker.feature.home

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.partiful.invitemaker.core.canvas.CanvasRendererView
import com.partiful.invitemaker.core.designsystem.AppThemeColors
import com.partiful.invitemaker.data.repository.TemplateRepository
import com.partiful.invitemaker.data.service.ImageProcessorService
import com.partiful.invitemaker.domain.model.CanvasLayer
import com.partiful.invitemaker.domain.model.CardDesign
import com.partiful.invitemaker.domain.model.LayerType
import com.partiful.invitemaker.domain.model.Template
import com.partiful.invitemaker.domain.model.TemplateCategory
import com.partiful.invitemaker.domain.model.TextLayerData

@Composable
fun HomeScreen(
    onCreateBlank: () -> Unit,
    onCreateFromPhoto: (CardDesign) -> Unit,
    onSelectTemplate: (Template) -> Unit,
    onOpenProjects: () -> Unit,
    onOpenSettings: () -> Unit
) {
    val context = LocalContext.current
    val imageProcessor = remember { ImageProcessorService(context) }
    var selectedCategory by remember { mutableStateOf(TemplateCategory.ALL) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            val savedName = imageProcessor.saveImageToInternal(uri)
            val newDesign = CardDesign(
                title = "Thiệp từ ảnh cá nhân",
                backgroundImagePath = savedName,
                layers = listOf(
                    CanvasLayer(
                        name = "Lời mời",
                        type = LayerType.TEXT,
                        x = 0f, y = -200f, width = 750f, height = 120f,
                        textData = TextLayerData(text = "LET'S PARTY TOGETHER", fontSize = 36f, fontColorHex = "#FFFFFF")
                    )
                )
            )
            onCreateFromPhoto(newDesign)
        }
    }

    Scaffold(
        containerColor = AppThemeColors.BackgroundDark
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "PARTIFUL",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        letterSpacing = 2.sp
                    )
                    Text(
                        text = "Thiết kế thiệp & poster sự kiện",
                        fontSize = 12.sp,
                        color = AppThemeColors.TextSecondary
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    IconButton(
                        onClick = onOpenProjects,
                        modifier = Modifier.background(AppThemeColors.SurfaceElevated, CircleShape)
                    ) {
                        Icon(Icons.Default.Folder, contentDescription = "Projects", tint = Color.White)
                    }
                    IconButton(
                        onClick = onOpenSettings,
                        modifier = Modifier.background(AppThemeColors.SurfaceElevated, CircleShape)
                    ) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings", tint = Color.White)
                    }
                }
            }

            // Quick Create Cards
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Blank Card
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(18.dp))
                        .background(AppThemeColors.SurfaceElevated)
                        .border(1.dp, AppThemeColors.SurfaceBorder, RoundedCornerShape(18.dp))
                        .clickable { onCreateBlank() }
                        .padding(14.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(AppThemeColors.NeonPurple.copy(alpha = 0.3f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = AppThemeColors.BrightViolet)
                        }
                        Column {
                            Text("Tạo mới", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("Trống", color = AppThemeColors.TextSecondary, fontSize = 12.sp)
                        }
                    }
                }

                // From Photo
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(18.dp))
                        .background(AppThemeColors.SurfaceElevated)
                        .border(1.dp, AppThemeColors.SurfaceBorder, RoundedCornerShape(18.dp))
                        .clickable {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        }
                        .padding(14.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(AppThemeColors.HotPink.copy(alpha = 0.3f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Image, contentDescription = null, tint = AppThemeColors.HotPink)
                        }
                        Column {
                            Text("Từ ảnh", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("Thư viện", color = AppThemeColors.TextSecondary, fontSize = 12.sp)
                        }
                    }
                }
            }

            // Categories
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(TemplateCategory.entries.toTypedArray()) { cat ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (selectedCategory == cat) AppThemeColors.BrightViolet else AppThemeColors.SurfaceElevated)
                            .clickable { selectedCategory = cat }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = cat.displayName,
                            color = if (selectedCategory == cat) Color.White else AppThemeColors.TextSecondary,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            // Featured Carousel
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Mẫu nổi bật",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(TemplateRepository.templates.take(4)) { tpl ->
                        TemplateCardItem(
                            template = tpl,
                            width = 200.dp,
                            height = 300.dp,
                            onClick = { onSelectTemplate(tpl) }
                        )
                    }
                }
            }

            // Category Grid
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (selectedCategory == TemplateCategory.ALL) "Tất cả mẫu thiết kế" else selectedCategory.displayName,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "${TemplateRepository.getTemplates(selectedCategory).size} mẫu",
                        color = AppThemeColors.TextSecondary,
                        fontSize = 12.sp
                    )
                }

                val currentTemplates = TemplateRepository.getTemplates(selectedCategory)
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.height(600.dp)
                ) {
                    items(currentTemplates) { tpl ->
                        TemplateCardItem(
                            template = tpl,
                            width = null,
                            height = 240.dp,
                            onClick = { onSelectTemplate(tpl) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TemplateCardItem(
    template: Template,
    width: androidx.compose.ui.unit.Dp?,
    height: androidx.compose.ui.unit.Dp,
    onClick: () -> Unit
) {
    val mod = if (width != null) Modifier.width(width) else Modifier.fillMaxWidth()

    Column(
        modifier = mod.clickable { onClick() },
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .height(height)
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .border(1.dp, AppThemeColors.SurfaceBorder, RoundedCornerShape(18.dp))
        ) {
            CanvasRendererView(
                design = template.cardDesign,
                isInteractive = false,
                modifier = Modifier.fillMaxSize()
            )
        }

        Text(
            text = template.name,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            maxLines = 1
        )
        Text(
            text = template.category.displayName,
            color = AppThemeColors.TextSecondary,
            fontSize = 12.sp
        )
    }
}
