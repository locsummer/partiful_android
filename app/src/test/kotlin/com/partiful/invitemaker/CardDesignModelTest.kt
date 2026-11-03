package com.partiful.invitemaker

import com.partiful.invitemaker.core.canvas.HistoryManager
import com.partiful.invitemaker.domain.model.CanvasLayer
import com.partiful.invitemaker.domain.model.CardDesign
import com.partiful.invitemaker.domain.model.LayerType
import com.partiful.invitemaker.domain.model.TextLayerData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CardDesignModelTest {

    @Test
    fun testCardDesignInitialization() {
        val design = CardDesign(
            title = "Sinh nhật 25 tuổi",
            canvasWidth = 1080f,
            canvasHeight = 1920f
        )
        assertEquals("Sinh nhật 25 tuổi", design.title)
        assertEquals(1080f / 1920f, design.aspectRatio, 0.001f)
        assertTrue(design.layers.isEmpty())
        assertTrue(design.isDraft)
    }

    @Test
    fun testLayerHierarchyAndZIndexSorting() {
        val layerPhoto = CanvasLayer(name = "Photo", type = LayerType.IMAGE, zIndex = 0)
        val layerTitle = CanvasLayer(name = "Title", type = LayerType.TEXT, zIndex = 1)
        val layerSticker = CanvasLayer(name = "Sticker", type = LayerType.STICKER, zIndex = 2)

        val design = CardDesign(layers = listOf(layerTitle, layerSticker, layerPhoto))
        val sorted = design.layers.sortedBy { it.zIndex }

        assertEquals("Photo", sorted[0].name)
        assertEquals("Title", sorted[1].name)
        assertEquals("Sticker", sorted[2].name)
    }

    @Test
    fun testTextLayerDataMutation() {
        val textLayer = CanvasLayer(
            name = "Title",
            type = LayerType.TEXT,
            textData = TextLayerData(text = "LET'S PARTY", fontSize = 36f, fontColorHex = "#FF007F")
        )
        assertNotNull(textLayer.textData)
        assertEquals("LET'S PARTY", textLayer.textData?.text)
        assertEquals(36f, textLayer.textData?.fontSize ?: 0f, 0.01f)
    }

    @Test
    fun testHistoryManagerUndoRedo() {
        val history = HistoryManager(maxHistory = 5)
        val state1 = CardDesign(title = "State 1")
        val state2 = CardDesign(title = "State 2")
        val state3 = CardDesign(title = "State 3")

        assertFalse(history.canUndo)
        assertFalse(history.canRedo)

        history.saveCheckpoint(state1)
        assertTrue(history.canUndo)

        history.saveCheckpoint(state2)

        val undone = history.undo(state3)
        assertNotNull(undone)
        assertEquals("State 2", undone?.title)
        assertTrue(history.canRedo)

        val redone = history.redo(undone!!)
        assertNotNull(redone)
        assertEquals("State 3", redone?.title)
    }
}
