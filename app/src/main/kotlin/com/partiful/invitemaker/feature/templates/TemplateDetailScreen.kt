package com.partiful.invitemaker.feature.templates

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.partiful.invitemaker.core.canvas.CanvasRendererView
import com.partiful.invitemaker.core.designsystem.AppThemeColors
import com.partiful.invitemaker.core.designsystem.NeonButton
import com.partiful.invitemaker.domain.model.Template

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TemplateDetailScreen(
    template: Template,
    onBack: () -> Unit,
    onUseTemplate: () -> Unit
) {
    Scaffold(
        containerColor = AppThemeColors.BackgroundDark,
        topBar = {
            TopAppBar(
                title = { Text(template.name, color = Color.White) },
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
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Large Preview
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                CanvasRendererView(
                    design = template.cardDesign,
                    isInteractive = false,
                    modifier = Modifier
                        .fillMaxHeight(0.95f)
                        .clip(RoundedCornerShape(24.dp))
                        .shadow(24.dp, RoundedCornerShape(24.dp), spotColor = AppThemeColors.BrightViolet)
                )
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = template.name,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = template.category.displayName,
                            color = AppThemeColors.BrightViolet,
                            fontSize = 14.sp
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        template.tags.forEach { tag ->
                            Text("#$tag", color = AppThemeColors.TextSecondary, fontSize = 12.sp)
                        }
                    }
                }

                NeonButton(
                    title = "Dùng mẫu này để thiết kế",
                    icon = Icons.Default.Brush,
                    gradient = AppThemeColors.PartyGradient,
                    onButtonClick = onUseTemplate,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
