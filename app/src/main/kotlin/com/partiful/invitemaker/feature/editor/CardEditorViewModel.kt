package com.partiful.invitemaker.feature.editor

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import com.partiful.invitemaker.core.canvas.HistoryManager
import com.partiful.invitemaker.data.repository.TemplateRepository
import com.partiful.invitemaker.data.service.DraftStorageService
import com.partiful.invitemaker.data.service.ImageProcessorService
import com.partiful.invitemaker.domain.model.CanvasLayer
import com.partiful.invitemaker.domain.model.CardDesign
import com.partiful.invitemaker.domain.model.ImageLayerData
import com.partiful.invitemaker.domain.model.LayerType
import com.partiful.invitemaker.domain.model.StickerLayerData
import com.partiful.invitemaker.domain.model.TextLayerData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

enum class ActiveToolSheet {
    TEXT,
    PHOTO_PICKER,
    STICKERS,
    FILTERS,
    LAYERS,
    BACKGROUND
}

class CardEditorViewModel(private val context: Context, initialDesign: CardDesign? = null) : ViewModel() {

    private val draftStorage = DraftStorageService(context)
    private val imageProcessor = ImageProcessorService(context)
    private val historyManager = HistoryManager(maxHistory = 30)

    private val _cardDesign = MutableStateFlow(
        initialDesign ?: draftStorage.loadDraft() ?: (TemplateRepository.templates.firstOrNull()?.cardDesign ?: CardDesign())
    )
    val cardDesign: StateFlow<CardDesign> = _cardDesign.asStateFlow()

    private val _selectedLayerId = MutableStateFlow<String?>(null)
    val selectedLayerId: StateFlow<String?> = _selectedLayerId.asStateFlow()

    private val _activeSheet = MutableStateFlow<ActiveToolSheet?>(null)
    val activeSheet: StateFlow<ActiveToolSheet?> = _activeSheet.asStateFlow()

    private val _canUndo = MutableStateFlow(false)
    val canUndo: StateFlow<Boolean> = _canUndo.asStateFlow()

    private val _canRedo = MutableStateFlow(false)
    val canRedo: StateFlow<Boolean> = _canRedo.asStateFlow()

    val selectedLayer: CanvasLayer?
        get() = _cardDesign.value.layers.firstOrNull { it.id == _selectedLayerId.value }

    fun openSheet(sheet: ActiveToolSheet) {
        _activeSheet.value = sheet
    }

    fun closeSheet() {
        _activeSheet.value = null
    }

    fun saveCheckpoint() {
        historyManager.saveCheckpoint(_cardDesign.value)
        draftStorage.saveDraft(_cardDesign.value)
        updateHistoryStatus()
    }

    fun undo() {
        val prev = historyManager.undo(_cardDesign.value) ?: return
        _cardDesign.value = prev
        updateHistoryStatus()
    }

    fun redo() {
        val next = historyManager.redo(_cardDesign.value) ?: return
        _cardDesign.value = next
        updateHistoryStatus()
    }

    private fun updateHistoryStatus() {
        _canUndo.value = historyManager.canUndo
        _canRedo.value = historyManager.canRedo
    }

    fun selectLayer(id: String) {
        val layer = _cardDesign.value.layers.firstOrNull { it.id == id }
        if (layer != null && !layer.isLocked) {
            _selectedLayerId.value = id
        }
    }

    fun deselectLayer() {
        _selectedLayerId.value = null
    }

    fun addTextLayer(text: String = "HAPPY BIRTHDAY") {
        saveCheckpoint()
        val maxZ = (_cardDesign.value.layers.maxOfOrNull { it.zIndex } ?: 0) + 1
        val newLayer = CanvasLayer(
            name = "Text ${_cardDesign.value.layers.size + 1}",
            type = LayerType.TEXT,
            x = 0f,
            y = 0f,
            width = 700f,
            height = 120f,
            zIndex = maxZ,
            textData = TextLayerData(text = text)
        )
        _cardDesign.value = _cardDesign.value.copy(
            layers = _cardDesign.value.layers + newLayer
        )
        _selectedLayerId.value = newLayer.id
        openSheet(ActiveToolSheet.TEXT)
    }

    fun updateSelectedTextData(data: TextLayerData) {
        val currentId = _selectedLayerId.value ?: return
        saveCheckpoint()
        _cardDesign.value = _cardDesign.value.copy(
            layers = _cardDesign.value.layers.map {
                if (it.id == currentId) it.copy(textData = data) else it
            }
        )
    }

    fun addImageLayer(uri: Uri) {
        val savedName = imageProcessor.saveImageToInternal(uri) ?: return
        saveCheckpoint()
        val maxZ = (_cardDesign.value.layers.maxOfOrNull { it.zIndex } ?: 0) + 1
        val newLayer = CanvasLayer(
            name = "Image ${_cardDesign.value.layers.size + 1}",
            type = LayerType.IMAGE,
            x = 0f,
            y = 0f,
            width = 600f,
            height = 600f,
            zIndex = maxZ,
            imageData = ImageLayerData(localImagePath = savedName)
        )
        _cardDesign.value = _cardDesign.value.copy(
            layers = _cardDesign.value.layers + newLayer
        )
        _selectedLayerId.value = newLayer.id
    }

    fun addStickerLayer(symbol: String, category: String) {
        saveCheckpoint()
        val maxZ = (_cardDesign.value.layers.maxOfOrNull { it.zIndex } ?: 0) + 1
        val newLayer = CanvasLayer(
            name = "Sticker ${_cardDesign.value.layers.size + 1}",
            type = LayerType.STICKER,
            x = 0f,
            y = 0f,
            width = 150f,
            height = 150f,
            zIndex = maxZ,
            stickerData = StickerLayerData(symbolOrName = symbol, category = category)
        )
        _cardDesign.value = _cardDesign.value.copy(
            layers = _cardDesign.value.layers + newLayer
        )
        _selectedLayerId.value = newLayer.id
    }

    fun deleteSelectedLayer() {
        val currentId = _selectedLayerId.value ?: return
        saveCheckpoint()
        _cardDesign.value = _cardDesign.value.copy(
            layers = _cardDesign.value.layers.filter { it.id != currentId }
        )
        _selectedLayerId.value = null
    }

    fun duplicateSelectedLayer() {
        val layer = selectedLayer ?: return
        saveCheckpoint()
        val dup = layer.copy(
            id = UUID.randomUUID().toString(),
            name = "${layer.name} (Copy)",
            x = layer.x + 30f,
            y = layer.y + 30f,
            zIndex = (_cardDesign.value.layers.maxOfOrNull { it.zIndex } ?: 0) + 1
        )
        _cardDesign.value = _cardDesign.value.copy(
            layers = _cardDesign.value.layers + dup
        )
        _selectedLayerId.value = dup.id
    }

    fun bringForward() {
        val currentId = _selectedLayerId.value ?: return
        saveCheckpoint()
        _cardDesign.value = _cardDesign.value.copy(
            layers = _cardDesign.value.layers.map {
                if (it.id == currentId) it.copy(zIndex = it.zIndex + 1) else it
            }
        )
    }

    fun sendBackward() {
        val currentId = _selectedLayerId.value ?: return
        saveCheckpoint()
        _cardDesign.value = _cardDesign.value.copy(
            layers = _cardDesign.value.layers.map {
                if (it.id == currentId) it.copy(zIndex = (it.zIndex - 1).coerceAtLeast(0)) else it
            }
        )
    }

    fun toggleLockSelectedLayer() {
        val currentId = _selectedLayerId.value ?: return
        _cardDesign.value = _cardDesign.value.copy(
            layers = _cardDesign.value.layers.map {
                if (it.id == currentId) it.copy(isLocked = !it.isLocked) else it
            }
        )
        if (selectedLayer?.isLocked == true) {
            _selectedLayerId.value = null
        }
    }

    fun updatePosition(id: String, deltaX: Float, deltaY: Float) {
        _cardDesign.value = _cardDesign.value.copy(
            layers = _cardDesign.value.layers.map {
                if (it.id == id) it.copy(x = it.x + deltaX, y = it.y + deltaY) else it
            }
        )
    }

    fun setBackgroundGradients(hexes: List<String>) {
        saveCheckpoint()
        _cardDesign.value = _cardDesign.value.copy(
            backgroundGradientHexes = hexes
        )
    }

    fun saveProject() {
        draftStorage.saveProject(_cardDesign.value)
    }
}
