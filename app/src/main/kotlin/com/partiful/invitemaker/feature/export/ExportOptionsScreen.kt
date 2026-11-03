package com.partiful.invitemaker.feature.export

import android.graphics.Bitmap
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Share
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
import com.partiful.invitemaker.core.canvas.OffscreenCardRenderer
import com.partiful.invitemaker.core.designsystem.AppThemeColors
import com.partiful.invitemaker.core.designsystem.NeonButton
import com.partiful.invitemaker.data.service.ImageProcessorService
import com.partiful.invitemaker.data.service.PdfExportService
import com.partiful.invitemaker.data.service.ShareService
import com.partiful.invitemaker.domain.model.CardDesign
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExportOptionsScreen(
    cardDesign: CardDesign,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val imageProcessor = remember { ImageProcessorService(context) }
    val pdfExportService = remember { PdfExportService(context) }
    val shareService = remember { ShareService(context) }

    var exportFormat by remember { mutableStateOf("PNG") }
    var resolutionScale by remember { mutableFloatStateOf(2.0f) }
    var jpegQuality by remember { mutableFloatStateOf(0.95f) }
    var isProcessing by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = AppThemeColors.BackgroundDark,
        topBar = {
            TopAppBar(
                title = { Text("Xuất & Chia sẻ thiệp", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AppThemeColors.BackgroundDark)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Card Thumbnail Preview
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp),
                contentAlignment = Alignment.Center
            ) {
                CanvasRendererView(
                    design = cardDesign,
                    isInteractive = false,
                    modifier = Modifier
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(18.dp))
                )
            }

            // Format Selection
            Text("Định dạng file xuất", color = AppThemeColors.TextSecondary, fontSize = 13.sp)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                listOf("PNG", "JPG", "PDF").forEach { fmt ->
                    FilterChip(
                        selected = exportFormat == fmt,
                        onClick = { exportFormat = fmt },
                        label = { Text(fmt, fontWeight = FontWeight.Bold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AppThemeColors.BrightViolet,
                            selectedLabelColor = Color.White,
                            containerColor = AppThemeColors.SurfaceElevated,
                            labelColor = Color.White
                        )
                    )
                }
            }

            // Resolution Scale
            Text("Độ phân giải", color = AppThemeColors.TextSecondary, fontSize = 13.sp)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                listOf(
                    1.0f to "1x (Chuẩn)",
                    2.0f to "2x (HD Sắc nét)",
                    3.0f to "3x (In ấn)"
                ).forEach { (scale, label) ->
                    FilterChip(
                        selected = resolutionScale == scale,
                        onClick = { resolutionScale = scale },
                        label = { Text(label) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AppThemeColors.BrightViolet,
                            selectedLabelColor = Color.White,
                            containerColor = AppThemeColors.SurfaceElevated,
                            labelColor = Color.White
                        )
                    )
                }
            }

            // Action Buttons
            NeonButton(
                title = "Lưu file vào máy",
                icon = Icons.Default.FileDownload,
                gradient = AppThemeColors.PartyGradient,
                isLoading = isProcessing,
                onButtonClick = {
                    isProcessing = true
                    coroutineScope.launch {
                        val bitmap = withContext(Dispatchers.Default) {
                            OffscreenCardRenderer.renderToBitmap(context, cardDesign, resolutionScale)
                        }
                        if (exportFormat == "PDF") {
                            val pdfFile = withContext(Dispatchers.IO) {
                                pdfExportService.exportPdf(bitmap, cardDesign.title)
                            }
                            isProcessing = false
                            if (pdfFile != null) {
                                Toast.makeText(context, "Đã xuất PDF thành công: ${pdfFile.name}", Toast.LENGTH_LONG).show()
                            }
                        } else {
                            val compressFormat = if (exportFormat == "PNG") Bitmap.CompressFormat.PNG else Bitmap.CompressFormat.JPEG
                            val quality = if (exportFormat == "PNG") 100 else (jpegQuality * 100).toInt()
                            val savedFile = withContext(Dispatchers.IO) {
                                imageProcessor.saveBitmap(bitmap, compressFormat, quality)
                            }
                            isProcessing = false
                            if (savedFile != null) {
                                Toast.makeText(context, "Đã lưu thiệp thành công vào bộ nhớ!", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            NeonButton(
                title = "Mở bảng chia sẻ (Sharesheet)",
                icon = Icons.Default.Share,
                gradient = AppThemeColors.PartyGradient,
                isOutlined = true,
                isLoading = isProcessing,
                onButtonClick = {
                    isProcessing = true
                    coroutineScope.launch {
                        val bitmap = withContext(Dispatchers.Default) {
                            OffscreenCardRenderer.renderToBitmap(context, cardDesign, resolutionScale)
                        }
                        if (exportFormat == "PDF") {
                            val pdfFile = withContext(Dispatchers.IO) {
                                pdfExportService.exportPdf(bitmap, cardDesign.title)
                            }
                            isProcessing = false
                            if (pdfFile != null) {
                                shareService.shareFile(pdfFile, "application/pdf", cardDesign.title)
                            }
                        } else {
                            val compressFormat = if (exportFormat == "PNG") Bitmap.CompressFormat.PNG else Bitmap.CompressFormat.JPEG
                            val mimeType = if (exportFormat == "PNG") "image/png" else "image/jpeg"
                            val savedFile = withContext(Dispatchers.IO) {
                                imageProcessor.saveBitmap(bitmap, compressFormat, 95)
                            }
                            isProcessing = false
                            if (savedFile != null) {
                                shareService.shareFile(savedFile, mimeType, cardDesign.title)
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
