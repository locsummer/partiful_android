package com.partiful.invitemaker.feature.editor

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.partiful.invitemaker.core.canvas.CanvasRendererView
import com.partiful.invitemaker.core.designsystem.AppThemeColors
import com.partiful.invitemaker.domain.model.CanvasLayer
import com.partiful.invitemaker.domain.model.CardDesign
import com.partiful.invitemaker.domain.model.LayerType

@Composable
fun CardEditorScreen(
    initialDesign: CardDesign? = null,
    onBack: () -> Unit,
    onNavigateToPreview: (CardDesign) -> Unit
) {
    val context = LocalContext.current
    val viewModel = remember { CardEditorViewModel(context, initialDesign) }

    val design by viewModel.cardDesign.collectAsState()
    val selectedId by viewModel.selectedLayerId.collectAsState()
    val canUndo by viewModel.canUndo.collectAsState()
    val canRedo by viewModel.canRedo.collectAsState()
    val activeSheet by viewModel.activeSheet.collectAsState()

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            viewModel.addImageLayer(uri)
        }
    }

    Scaffold(
        containerColor = AppThemeColors.BackgroundDark,
        topBar = {
            EditorTopBar(
                canUndo = canUndo,
                canRedo = canRedo,
                onBack = onBack,
                onUndo = { viewModel.undo() },
                onRedo = { viewModel.redo() },
                onSave = {
                    viewModel.saveProject()
                    Toast.makeText(context, "Đã lưu bản thiết kế!", Toast.LENGTH_SHORT).show()
                },
                onPreview = {
                    onNavigateToPreview(design)
                }
            )
        },
        bottomBar = {
            EditorBottomBar(
                selectedLayer = viewModel.selectedLayer,
                onDuplicate = { viewModel.duplicateSelectedLayer() },
                onBringForward = { viewModel.bringForward() },
                onSendBackward = { viewModel.sendBackward() },
                onToggleLock = { viewModel.toggleLockSelectedLayer() },
                onDelete = { viewModel.deleteSelectedLayer() },
                onOpenSheet = { sheet ->
                    if (sheet == ActiveToolSheet.PHOTO_PICKER) {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    } else if (sheet == ActiveToolSheet.TEXT && viewModel.selectedLayer?.type != LayerType.TEXT) {
                        viewModel.addTextLayer("HAPPY BIRTHDAY")
                    } else {
                        viewModel.openSheet(sheet)
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .pointerInput(selectedId) {
                    detectDragGestures(
                        onDrag = { change, dragAmount ->
                            change.consume()
                            if (selectedId != null) {
                                viewModel.updatePosition(selectedId!!, dragAmount.x * 2.5f, dragAmount.y * 2.5f)
                            }
                        },
                        onDragEnd = {
                            viewModel.saveCheckpoint()
                        }
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            CanvasRendererView(
                design = design,
                selectedLayerId = selectedId,
                isInteractive = true,
                onSelectLayer = { id -> viewModel.selectLayer(id) },
                modifier = Modifier
                    .fillMaxHeight(0.92f)
                    .clip(RoundedCornerShape(20.dp))
                    .shadow(16.dp, RoundedCornerShape(20.dp), spotColor = AppThemeColors.BrightViolet)
            )
        }

        // Active Bottom Sheet
        when (activeSheet) {
            ActiveToolSheet.TEXT -> TextEditorBottomSheet(viewModel = viewModel, onDismiss = { viewModel.closeSheet() })
            ActiveToolSheet.STICKERS -> StickerPickerBottomSheet(viewModel = viewModel, onDismiss = { viewModel.closeSheet() })
            ActiveToolSheet.BACKGROUND -> BackgroundPickerBottomSheet(viewModel = viewModel, onDismiss = { viewModel.closeSheet() })
            ActiveToolSheet.LAYERS -> LayerManagementBottomSheet(viewModel = viewModel, onDismiss = { viewModel.closeSheet() })
            ActiveToolSheet.FILTERS -> {
                // Quick filter adjustment
                Toast.makeText(context, "Bộ lọc màu được áp dụng trên ảnh!", Toast.LENGTH_SHORT).show()
                viewModel.closeSheet()
            }
            else -> {}
        }
    }
}

@Composable
private fun EditorTopBar(
    canUndo: Boolean,
    canRedo: Boolean,
    onBack: () -> Unit,
    onUndo: () -> Unit,
    onRedo: () -> Unit,
    onSave: () -> Unit,
    onPreview: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onBack,
            modifier = Modifier.background(AppThemeColors.SurfaceElevated, CircleShape)
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            IconButton(
                onClick = onUndo,
                enabled = canUndo,
                modifier = Modifier.background(AppThemeColors.SurfaceElevated, CircleShape)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.Undo,
                    contentDescription = "Undo",
                    tint = if (canUndo) Color.White else AppThemeColors.TextMuted
                )
            }
            IconButton(
                onClick = onRedo,
                enabled = canRedo,
                modifier = Modifier.background(AppThemeColors.SurfaceElevated, CircleShape)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.Redo,
                    contentDescription = "Redo",
                    tint = if (canRedo) Color.White else AppThemeColors.TextMuted
                )
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            IconButton(
                onClick = onSave,
                modifier = Modifier.background(AppThemeColors.SurfaceElevated, CircleShape)
            ) {
                Icon(Icons.Default.Save, contentDescription = "Save", tint = Color.White)
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(AppThemeColors.PartyGradient)
                    .clickable { onPreview() }
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("Xem trước", color = Color.White, fontSize = 13.sp)
                    Icon(Icons.Default.Visibility, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

@Composable
private fun EditorBottomBar(
    selectedLayer: CanvasLayer?,
    onDuplicate: () -> Unit,
    onBringForward: () -> Unit,
    onSendBackward: () -> Unit,
    onToggleLock: () -> Unit,
    onDelete: () -> Unit,
    onOpenSheet: (ActiveToolSheet) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Quick Action Bar if layer is selected
        if (selectedLayer != null) {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(AppThemeColors.SurfaceElevated)
                    .border(1.dp, AppThemeColors.SurfaceBorder, RoundedCornerShape(16.dp))
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onDuplicate) {
                    Icon(Icons.Default.ContentCopy, contentDescription = "Duplicate", tint = Color.White)
                }
                IconButton(onClick = onBringForward) {
                    Icon(Icons.Default.ArrowUpward, contentDescription = "Bring Forward", tint = Color.White)
                }
                IconButton(onClick = onSendBackward) {
                    Icon(Icons.Default.ArrowDownward, contentDescription = "Send Backward", tint = Color.White)
                }
                IconButton(onClick = onToggleLock) {
                    Icon(
                        if (selectedLayer.isLocked) Icons.Default.Lock else Icons.Default.LockOpen,
                        contentDescription = "Lock",
                        tint = if (selectedLayer.isLocked) Color.Yellow else Color.White
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                }
            }
        }

        // Primary creation tools
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(AppThemeColors.SurfaceDark.copy(alpha = 0.95f))
                .border(1.dp, AppThemeColors.SurfaceBorder, RoundedCornerShape(24.dp))
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ToolItem(title = "Chữ", icon = Icons.Default.TextFields) { onOpenSheet(ActiveToolSheet.TEXT) }
            ToolItem(title = "Ảnh", icon = Icons.Default.AddPhotoAlternate) { onOpenSheet(ActiveToolSheet.PHOTO_PICKER) }
            ToolItem(title = "Sticker", icon = Icons.Default.AutoAwesome) { onOpenSheet(ActiveToolSheet.STICKERS) }
            ToolItem(title = "Nền", icon = Icons.Default.Palette) { onOpenSheet(ActiveToolSheet.BACKGROUND) }
            ToolItem(title = "Lớp", icon = Icons.Default.Layers) { onOpenSheet(ActiveToolSheet.LAYERS) }
        }
    }
}

@Composable
private fun ToolItem(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.clickable { onClick() }
    ) {
        Icon(icon, contentDescription = title, tint = Color.White, modifier = Modifier.size(22.dp))
        Text(title, color = AppThemeColors.TextSecondary, fontSize = 11.sp)
    }
}
