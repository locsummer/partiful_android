package com.partiful.invitemaker.feature.settings

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
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
import com.partiful.invitemaker.core.designsystem.AppThemeColors
import com.partiful.invitemaker.data.service.DraftStorageService

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val draftStorage = remember { DraftStorageService(context) }

    var defaultFormat by remember { mutableStateOf("PNG") }
    var defaultResolution by remember { mutableStateOf("2x") }

    Scaffold(
        containerColor = AppThemeColors.BackgroundDark,
        topBar = {
            TopAppBar(
                title = { Text("Cài đặt", color = Color.White) },
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
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Export preferences
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Tùy chọn xuất file",
                    color = AppThemeColors.BrightViolet,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(AppThemeColors.SurfaceElevated)
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Định dạng mặc định", color = Color.White)
                    Text(defaultFormat, color = AppThemeColors.TextSecondary, fontWeight = FontWeight.Bold)
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(AppThemeColors.SurfaceElevated)
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Độ phân giải mặc định", color = Color.White)
                    Text(defaultResolution, color = AppThemeColors.TextSecondary, fontWeight = FontWeight.Bold)
                }
            }

            // Storage actions
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Dữ liệu & Bộ nhớ",
                    color = AppThemeColors.BrightViolet,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(AppThemeColors.SurfaceElevated)
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Xóa bản nháp đang chỉnh sửa", color = Color.Red)
                    IconButton(onClick = {
                        draftStorage.clearDraft()
                        Toast.makeText(context, "Đã xóa bản nháp!", Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(Icons.Default.Delete, contentDescription = "Clear", tint = Color.Red)
                    }
                }
            }

            // App Info
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Thông tin ứng dụng",
                    color = AppThemeColors.BrightViolet,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(AppThemeColors.SurfaceElevated)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Phiên bản", color = Color.White)
                        Text("1.0.0 (Native Kotlin)", color = AppThemeColors.TextSecondary)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Giao diện", color = Color.White)
                        Text("Jetpack Compose Material 3", color = AppThemeColors.TextSecondary)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Bảo mật", color = Color.White)
                        Text("Offline-first 100%", color = AppThemeColors.EmeraldMint)
                    }
                }
            }
        }
    }
}
