package com.partiful.invitemaker.data.service

import android.content.Context
import com.partiful.invitemaker.domain.model.CanvasLayer
import com.partiful.invitemaker.domain.model.CardDesign
import com.partiful.invitemaker.domain.model.FilterEffect
import com.partiful.invitemaker.domain.model.ImageLayerData
import com.partiful.invitemaker.domain.model.LayerType
import com.partiful.invitemaker.domain.model.PartyEvent
import com.partiful.invitemaker.domain.model.StickerLayerData
import com.partiful.invitemaker.domain.model.TextAlignmentType
import com.partiful.invitemaker.domain.model.TextLayerData
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

class DraftStorageService(private val context: Context) {

    private val draftFile: File
        get() = File(context.filesDir, "active_draft.json")

    private val projectsDir: File
        get() = File(context.filesDir, "projects").apply { if (!exists()) mkdirs() }

    fun saveDraft(design: CardDesign) {
        try {
            val json = designToJson(design)
            draftFile.writeText(json.toString())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun loadDraft(): CardDesign? {
        return try {
            if (!draftFile.exists()) return null
            val json = JSONObject(draftFile.readText())
            jsonToDesign(json)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun clearDraft() {
        if (draftFile.exists()) {
            draftFile.delete()
        }
    }

    fun saveProject(design: CardDesign) {
        try {
            val file = File(projectsDir, "${design.id}.json")
            val json = designToJson(design.copy(updatedAt = System.currentTimeMillis()))
            file.writeText(json.toString())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getAllProjects(): List<CardDesign> {
        val list = mutableListOf<CardDesign>()
        try {
            val files = projectsDir.listFiles() ?: return emptyList()
            for (file in files) {
                if (file.extension == "json") {
                    try {
                        val json = JSONObject(file.readText())
                        val design = jsonToDesign(json)
                        if (design != null) {
                            list.add(design)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list.sortedByDescending { it.updatedAt }
    }

    fun deleteProject(id: String) {
        val file = File(projectsDir, "$id.json")
        if (file.exists()) {
            file.delete()
        }
    }

    // MARK: - JSON Serialization
    private fun designToJson(design: CardDesign): JSONObject {
        return JSONObject().apply {
            put("id", design.id)
            put("title", design.title)
            put("canvasWidth", design.canvasWidth.toDouble())
            put("canvasHeight", design.canvasHeight.toDouble())
            put("backgroundColorHex", design.backgroundColorHex)
            val gradArr = JSONArray()
            design.backgroundGradientHexes.forEach { gradArr.put(it) }
            put("backgroundGradientHexes", gradArr)
            put("backgroundGradientAngle", design.backgroundGradientAngle.toDouble())
            put("backgroundImagePath", design.backgroundImagePath ?: JSONObject.NULL)
            put("createdAt", design.createdAt)
            put("updatedAt", design.updatedAt)
            put("isDraft", design.isDraft)

            // Event info
            val eventObj = JSONObject().apply {
                put("id", design.eventInfo.id)
                put("title", design.eventInfo.title)
                put("eventDescription", design.eventInfo.eventDescription)
                put("startTimestamp", design.eventInfo.startTimestamp)
                put("endTimestamp", design.eventInfo.endTimestamp)
                put("location", design.eventInfo.location)
                put("notes", design.eventInfo.notes)
                put("hasReminder", design.eventInfo.hasReminder)
                put("reminderMinutesBefore", design.eventInfo.reminderMinutesBefore)
            }
            put("eventInfo", eventObj)

            // Layers
            val layerArr = JSONArray()
            for (layer in design.layers) {
                val lObj = JSONObject().apply {
                    put("id", layer.id)
                    put("name", layer.name)
                    put("type", layer.type.name)
                    put("x", layer.x.toDouble())
                    put("y", layer.y.toDouble())
                    put("width", layer.width.toDouble())
                    put("height", layer.height.toDouble())
                    put("rotation", layer.rotation.toDouble())
                    put("scale", layer.scale.toDouble())
                    put("opacity", layer.opacity.toDouble())
                    put("zIndex", layer.zIndex)
                    put("isLocked", layer.isLocked)
                    put("isVisible", layer.isVisible)

                    if (layer.textData != null) {
                        val t = JSONObject().apply {
                            put("text", layer.textData.text)
                            put("fontFamily", layer.textData.fontFamily)
                            put("fontSize", layer.textData.fontSize.toDouble())
                            put("fontColorHex", layer.textData.fontColorHex)
                            put("alignment", layer.textData.alignment.name)
                            put("isBold", layer.textData.isBold)
                            put("isItalic", layer.textData.isItalic)
                            put("letterSpacing", layer.textData.letterSpacing.toDouble())
                            put("shadowRadius", layer.textData.shadowRadius.toDouble())
                            put("shadowColorHex", layer.textData.shadowColorHex)
                        }
                        put("textData", t)
                    }

                    if (layer.stickerData != null) {
                        val s = JSONObject().apply {
                            put("symbolOrName", layer.stickerData.symbolOrName)
                            put("category", layer.stickerData.category)
                            put("tintColorHex", layer.stickerData.tintColorHex ?: JSONObject.NULL)
                            put("glowRadius", layer.stickerData.glowRadius.toDouble())
                            put("glowColorHex", layer.stickerData.glowColorHex)
                        }
                        put("stickerData", s)
                    }

                    if (layer.imageData != null) {
                        val img = JSONObject().apply {
                            put("localImagePath", layer.imageData.localImagePath ?: JSONObject.NULL)
                            put("filter", JSONObject().apply {
                                put("brightness", layer.imageData.filter.brightness.toDouble())
                                put("contrast", layer.imageData.filter.contrast.toDouble())
                                put("saturation", layer.imageData.filter.saturation.toDouble())
                                put("blurRadius", layer.imageData.filter.blurRadius.toDouble())
                                put("vignetteIntensity", layer.imageData.filter.vignetteIntensity.toDouble())
                                put("presetName", layer.imageData.filter.presetName)
                            })
                            put("cornerRadius", layer.imageData.cornerRadius.toDouble())
                        }
                        put("imageData", img)
                    }
                }
                layerArr.put(lObj)
            }
            put("layers", layerArr)
        }
    }

    private fun jsonToDesign(json: JSONObject): CardDesign? {
        return try {
            val gradArr = json.optJSONArray("backgroundGradientHexes")
            val gradients = mutableListOf<String>()
            if (gradArr != null) {
                for (i in 0 until gradArr.length()) {
                    gradients.add(gradArr.getString(i))
                }
            }

            // Event Info
            val evObj = json.optJSONObject("eventInfo")
            val event = if (evObj != null) {
                PartyEvent(
                    id = evObj.optString("id"),
                    title = evObj.optString("title"),
                    eventDescription = evObj.optString("eventDescription"),
                    startTimestamp = evObj.optLong("startTimestamp", System.currentTimeMillis()),
                    endTimestamp = evObj.optLong("endTimestamp", System.currentTimeMillis() + 14400000L),
                    location = evObj.optString("location"),
                    notes = evObj.optString("notes"),
                    hasReminder = evObj.optBoolean("hasReminder", true),
                    reminderMinutesBefore = evObj.optInt("reminderMinutesBefore", 120)
                )
            } else {
                PartyEvent()
            }

            // Layers
            val layerArr = json.optJSONArray("layers")
            val layers = mutableListOf<CanvasLayer>()
            if (layerArr != null) {
                for (i in 0 until layerArr.length()) {
                    val lObj = layerArr.getJSONObject(i)
                    val type = LayerType.valueOf(lObj.getString("type"))

                    var textData: TextLayerData? = null
                    val tObj = lObj.optJSONObject("textData")
                    if (tObj != null) {
                        textData = TextLayerData(
                            text = tObj.optString("text", ""),
                            fontFamily = tObj.optString("fontFamily", "System"),
                            fontSize = tObj.optDouble("fontSize", 28.0).toFloat(),
                            fontColorHex = tObj.optString("fontColorHex", "#FFFFFF"),
                            alignment = TextAlignmentType.valueOf(tObj.optString("alignment", "CENTER")),
                            isBold = tObj.optBoolean("isBold", true),
                            isItalic = tObj.optBoolean("isItalic", false),
                            letterSpacing = tObj.optDouble("letterSpacing", 1.0).toFloat(),
                            shadowRadius = tObj.optDouble("shadowRadius", 4.0).toFloat(),
                            shadowColorHex = tObj.optString("shadowColorHex", "#80000000")
                        )
                    }

                    var stickerData: StickerLayerData? = null
                    val sObj = lObj.optJSONObject("stickerData")
                    if (sObj != null) {
                        stickerData = StickerLayerData(
                            symbolOrName = sObj.optString("symbolOrName", "sparkles"),
                            category = sObj.optString("category", "Party"),
                            tintColorHex = if (sObj.isNull("tintColorHex")) null else sObj.optString("tintColorHex"),
                            glowRadius = sObj.optDouble("glowRadius", 0.0).toFloat(),
                            glowColorHex = sObj.optString("glowColorHex", "#FF007F")
                        )
                    }

                    var imageData: ImageLayerData? = null
                    val iObj = lObj.optJSONObject("imageData")
                    if (iObj != null) {
                        val fObj = iObj.optJSONObject("filter")
                        val filter = if (fObj != null) {
                            FilterEffect(
                                brightness = fObj.optDouble("brightness", 0.0).toFloat(),
                                contrast = fObj.optDouble("contrast", 1.0).toFloat(),
                                saturation = fObj.optDouble("saturation", 1.0).toFloat(),
                                blurRadius = fObj.optDouble("blurRadius", 0.0).toFloat(),
                                vignetteIntensity = fObj.optDouble("vignetteIntensity", 0.0).toFloat(),
                                presetName = fObj.optString("presetName", "Normal")
                            )
                        } else FilterEffect()

                        imageData = ImageLayerData(
                            localImagePath = if (iObj.isNull("localImagePath")) null else iObj.optString("localImagePath"),
                            filter = filter,
                            cornerRadius = iObj.optDouble("cornerRadius", 0.0).toFloat()
                        )
                    }

                    layers.add(
                        CanvasLayer(
                            id = lObj.optString("id"),
                            name = lObj.optString("name", "Layer"),
                            type = type,
                            x = lObj.optDouble("x", 0.0).toFloat(),
                            y = lObj.optDouble("y", 0.0).toFloat(),
                            width = lObj.optDouble("width", 200.0).toFloat(),
                            height = lObj.optDouble("height", 200.0).toFloat(),
                            rotation = lObj.optDouble("rotation", 0.0).toFloat(),
                            scale = lObj.optDouble("scale", 1.0).toFloat(),
                            opacity = lObj.optDouble("opacity", 1.0).toFloat(),
                            zIndex = lObj.optInt("zIndex", 0),
                            isLocked = lObj.optBoolean("isLocked", false),
                            isVisible = lObj.optBoolean("isVisible", true),
                            imageData = imageData,
                            textData = textData,
                            stickerData = stickerData
                        )
                    )
                }
            }

            CardDesign(
                id = json.getString("id"),
                title = json.optString("title", "Thiệp mời mới"),
                canvasWidth = json.optDouble("canvasWidth", 1080.0).toFloat(),
                canvasHeight = json.optDouble("canvasHeight", 1920.0).toFloat(),
                backgroundColorHex = json.optString("backgroundColorHex", "#0F0E17"),
                backgroundGradientHexes = if (gradients.isEmpty()) listOf("#1A0B2E", "#0F0E17") else gradients,
                backgroundGradientAngle = json.optDouble("backgroundGradientAngle", 45.0).toFloat(),
                backgroundImagePath = if (json.isNull("backgroundImagePath")) null else json.optString("backgroundImagePath"),
                layers = layers,
                eventInfo = event,
                createdAt = json.optLong("createdAt", System.currentTimeMillis()),
                updatedAt = json.optLong("updatedAt", System.currentTimeMillis()),
                isDraft = json.optBoolean("isDraft", true)
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
