package com.partiful.invitemaker.core.canvas

import com.partiful.invitemaker.domain.model.CardDesign

class HistoryManager(private val maxHistory: Int = 30) {
    private val undoStack = ArrayDeque<CardDesign>()
    private val redoStack = ArrayDeque<CardDesign>()

    val canUndo: Boolean
        get() = undoStack.isNotEmpty()

    val canRedo: Boolean
        get() = redoStack.isNotEmpty()

    fun saveCheckpoint(state: CardDesign) {
        undoStack.addLast(state)
        if (undoStack.size > maxHistory) {
            undoStack.removeFirst()
        }
        redoStack.clear()
    }

    fun undo(currentState: CardDesign): CardDesign? {
        val previousState = undoStack.removeLastOrNull() ?: return null
        redoStack.addLast(currentState)
        return previousState
    }

    fun redo(currentState: CardDesign): CardDesign? {
        val nextState = redoStack.removeLastOrNull() ?: return null
        undoStack.addLast(currentState)
        return nextState
    }

    fun clear() {
        undoStack.clear()
        redoStack.clear()
    }
}
