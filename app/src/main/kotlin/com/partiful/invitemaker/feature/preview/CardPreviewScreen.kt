package com.partiful.invitemaker.feature.preview

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.IosShare
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.partiful.invitemaker.core.canvas.CanvasRendererView
import com.partiful.invitemaker.core.designsystem.AppThemeColors
import com.partiful.invitemaker.core.designsystem.NeonButton
import com.partiful.invitemaker.domain.model.CardDesign

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardPreviewScreen(
    cardDesign: CardDesign,
    onBack: () -> Unit,
    onNavigateToExport: () -> Unit,
    onNavigateToCalendar: () -> Unit
) {
    Scaffold(
        containerColor = AppThemeColors.BackgroundDark,
        topBar = {
            TopAppBar(
                title = { Text("Xem trước thiệp", color = Color.White) },
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
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Main Clean Card Preview
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                CanvasRendererView(
                    design = cardDesign,
                    isInteractive = false,
                    modifier = Modifier
                        .fillMaxHeight(0.95f)
                        .clip(RoundedCornerShape(24.dp))
                        .shadow(24.dp, RoundedCornerShape(24.dp), spotColor = AppThemeColors.BrightViolet)
                )
            }

            // Bottom Actions
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    NeonButton(
                        title = "Xuất & Chia sẻ",
                        icon = Icons.Default.IosShare,
                        gradient = AppThemeColors.PartyGradient,
                        onButtonClick = onNavigateToExport,
                        modifier = Modifier.weight(1f)
                    )

                    NeonButton(
                        title = "Lưu vào Lịch",
                        icon = Icons.Default.CalendarMonth,
                        gradient = AppThemeColors.MintGradient,
                        onButtonClick = onNavigateToCalendar,
                        modifier = Modifier.weight(1f)
                    )
                }

                TextButton(
                    onClick = onBack,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Tiếp tục chỉnh sửa", color = AppThemeColors.TextSecondary)
                }
            }
        }
    }
}
