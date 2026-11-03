package com.partiful.invitemaker.feature.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.partiful.invitemaker.core.designsystem.AppThemeColors
import com.partiful.invitemaker.core.designsystem.NeonButton
import kotlinx.coroutines.launch

data class AndroidOnboardingSlide(
    val title: String,
    val description: String,
    val icon: ImageVector
)

@Composable
fun OnboardingScreen(onComplete: () -> Unit) {
    val slides = listOf(
        AndroidOnboardingSlide(
            title = "Thiết kế thiệp tiệc\nđẳng cấp & độc đáo",
            description = "Hơn 11 danh mục mẫu thiệp sinh nhật, đám cưới, lễ hội, âm nhạc và ngoài trời được thiết kế sẵn theo chuẩn visual Partiful hiện đại.",
            icon = Icons.Default.AutoAwesome
        ),
        AndroidOnboardingSlide(
            title = "Trình chỉnh sửa\nđa lớp chuyên nghiệp",
            description = "Thêm ảnh cá nhân, chỉnh sửa typography với hiệu ứng neon glow, sticker sôi động và bộ lọc màu nghệ thuật trực tiếp trên điện thoại.",
            icon = Icons.Default.Layers
        ),
        AndroidOnboardingSlide(
            title = "Xuất file sắc nét &\nĐồng bộ Lịch 1 chạm",
            description = "Xuất ảnh PNG, JPG chất lượng cao hoặc file PDF in ấn. Chia sẻ nhanh qua Zalo, Messenger và lưu trực tiếp sự kiện vào Lịch Android.",
            icon = Icons.Default.EventNote
        )
    )

    val pagerState = rememberPagerState { slides.size }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        containerColor = AppThemeColors.BackgroundDark,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onComplete) {
                    Text("Bỏ qua", color = AppThemeColors.TextSecondary)
                }
            }
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
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { index ->
                val slide = slides[index]
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Box(
                            modifier = Modifier
                                .size(140.dp)
                                .blur(24.dp)
                                .background(AppThemeColors.PartyGradient, CircleShape)
                        )
                        Icon(
                            imageVector = slide.icon,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(72.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(36.dp))

                    Text(
                        text = slide.title,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        lineHeight = 34.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = slide.description,
                        fontSize = 15.sp,
                        color = AppThemeColors.TextSecondary,
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }

            // Bottom Indicators and Button
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    repeat(slides.size) { idx ->
                        Box(
                            modifier = Modifier
                                .size(if (pagerState.currentPage == idx) 24.dp else 8.dp, 8.dp)
                                .background(
                                    if (pagerState.currentPage == idx) AppThemeColors.BrightViolet else AppThemeColors.SurfaceElevated,
                                    CircleShape
                                )
                        )
                    }
                }

                NeonButton(
                    title = if (pagerState.currentPage == slides.size - 1) "Bắt đầu sáng tạo" else "Tiếp theo",
                    icon = if (pagerState.currentPage == slides.size - 1) Icons.Default.AutoAwesome else Icons.Default.ArrowForward,
                    gradient = AppThemeColors.PartyGradient,
                    onButtonClick = {
                        if (pagerState.currentPage < slides.size - 1) {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        } else {
                            onComplete()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
