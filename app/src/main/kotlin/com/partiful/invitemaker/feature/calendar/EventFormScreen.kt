package com.partiful.invitemaker.feature.calendar

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EventAvailable
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
import com.partiful.invitemaker.core.designsystem.GlassCard
import com.partiful.invitemaker.core.designsystem.NeonButton
import com.partiful.invitemaker.data.service.CalendarService
import com.partiful.invitemaker.domain.model.PartyEvent
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventFormScreen(
    initialEvent: PartyEvent,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val calendarService = remember { CalendarService(context) }

    var title by remember { mutableStateOf(initialEvent.title) }
    var location by remember { mutableStateOf(initialEvent.location) }
    var description by remember { mutableStateOf(initialEvent.eventDescription) }
    var notes by remember { mutableStateOf(initialEvent.notes) }
    var hasReminder by remember { mutableStateOf(initialEvent.hasReminder) }
    var reminderMinutes by remember { mutableIntStateOf(initialEvent.reminderMinutesBefore) }
    var isConfirmed by remember { mutableStateOf(false) }

    val dateFormat = remember { SimpleDateFormat("EEE, dd/MM/yyyy HH:mm", Locale.getDefault()) }

    Scaffold(
        containerColor = AppThemeColors.BackgroundDark,
        topBar = {
            TopAppBar(
                title = { Text("Lưu sự kiện vào Lịch", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AppThemeColors.BackgroundDark)
            )
        }
    ) { innerPadding ->
        if (isConfirmed) {
            // Confirmation Screen
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = AppThemeColors.EmeraldMint,
                    modifier = Modifier.size(80.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Đã lên lịch thành công!",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Sự kiện \"$title\" đã được đồng bộ vào ứng dụng Lịch trên điện thoại.",
                    fontSize = 15.sp,
                    color = AppThemeColors.TextSecondary,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                NeonButton(
                    title = "Hoàn tất",
                    icon = Icons.Default.EventAvailable,
                    gradient = AppThemeColors.PartyGradient,
                    onButtonClick = onBack,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        } else {
            // Form Screen
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Tên sự kiện / Bữa tiệc") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = AppThemeColors.BrightViolet,
                        unfocusedBorderColor = AppThemeColors.SurfaceBorder
                    )
                )

                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Địa điểm tổ chức") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = AppThemeColors.BrightViolet,
                        unfocusedBorderColor = AppThemeColors.SurfaceBorder
                    )
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Mô tả bữa tiệc") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = AppThemeColors.BrightViolet,
                        unfocusedBorderColor = AppThemeColors.SurfaceBorder
                    )
                )

                // Date/Time display
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Thời gian bắt đầu:", color = AppThemeColors.TextSecondary, fontSize = 12.sp)
                        Text(
                            dateFormat.format(Date(initialEvent.startTimestamp)),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Thời gian kết thúc:", color = AppThemeColors.TextSecondary, fontSize = 12.sp)
                        Text(
                            dateFormat.format(Date(initialEvent.endTimestamp)),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }

                // Reminder Toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(AppThemeColors.SurfaceElevated)
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Thông báo nhắc nhở trước giờ tiệc", color = Color.White, fontSize = 14.sp)
                    Switch(
                        checked = hasReminder,
                        onCheckedChange = { hasReminder = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = AppThemeColors.BrightViolet
                        )
                    )
                }

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Ghi chú bổ sung (Dress code, mang đồ uống...)") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = AppThemeColors.BrightViolet,
                        unfocusedBorderColor = AppThemeColors.SurfaceBorder
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                NeonButton(
                    title = "Lưu vào Lịch Android",
                    icon = Icons.Default.EventAvailable,
                    gradient = AppThemeColors.PartyGradient,
                    onButtonClick = {
                        val updatedEvent = initialEvent.copy(
                            title = title,
                            location = location,
                            eventDescription = description,
                            notes = notes,
                            hasReminder = hasReminder,
                            reminderMinutesBefore = reminderMinutes
                        )
                        val success = calendarService.addEventViaIntent(updatedEvent)
                        if (success) {
                            isConfirmed = true
                        } else {
                            Toast.makeText(context, "Không thể mở ứng dụng Lịch trên thiết bị.", Toast.LENGTH_LONG).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
