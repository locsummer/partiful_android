package com.partiful.invitemaker.data.repository

import com.partiful.invitemaker.domain.model.CanvasLayer
import com.partiful.invitemaker.domain.model.CardDesign
import com.partiful.invitemaker.domain.model.LayerType
import com.partiful.invitemaker.domain.model.PartyEvent
import com.partiful.invitemaker.domain.model.StickerLayerData
import com.partiful.invitemaker.domain.model.Template
import com.partiful.invitemaker.domain.model.TemplateCategory
import com.partiful.invitemaker.domain.model.TextLayerData

object TemplateRepository {

    val templates: List<Template> by lazy { generateTemplates() }

    fun getTemplates(category: TemplateCategory): List<Template> {
        return if (category == TemplateCategory.ALL) {
            templates
        } else {
            templates.filter { it.category == category }
        }
    }

    fun getTemplateById(id: String): Template? {
        return templates.firstOrNull { it.id == id }
    }

    private fun generateTemplates(): List<Template> {
        return listOf(
            // 1. Birthday
            Template(
                id = "tpl_birthday_neon",
                name = "Neon Glow Birthday",
                category = TemplateCategory.BIRTHDAY,
                cardDesign = CardDesign(
                    title = "Neon Birthday Bash",
                    canvasWidth = 1080f,
                    canvasHeight = 1920f,
                    backgroundColorHex = "#0D0221",
                    backgroundGradientHexes = listOf("#0F051D", "#2E0249", "#570A57"),
                    layers = listOf(
                        CanvasLayer(
                            name = "Badge",
                            type = LayerType.TEXT,
                            x = 0f, y = -450f, width = 600f, height = 60f,
                            textData = TextLayerData(text = "✨ YOU ARE INVITED ✨", fontSize = 22f, fontColorHex = "#FBBF24", letterSpacing = 3f)
                        ),
                        CanvasLayer(
                            name = "Main Title",
                            type = LayerType.TEXT,
                            x = 0f, y = -300f, width = 850f, height = 220f,
                            textData = TextLayerData(text = "HAPPY\nBIRTHDAY", fontSize = 76f, fontColorHex = "#FF007F", shadowRadius = 15f, shadowColorHex = "#80EC4899")
                        ),
                        CanvasLayer(
                            name = "Host Name",
                            type = LayerType.TEXT,
                            x = 0f, y = -80f, width = 700f, height = 70f,
                            textData = TextLayerData(text = "CELEBRATING ALEX'S 25TH", fontSize = 26f, fontColorHex = "#FFFFFF")
                        ),
                        CanvasLayer(
                            name = "Date & Time",
                            type = LayerType.TEXT,
                            x = 0f, y = 150f, width = 750f, height = 90f,
                            textData = TextLayerData(text = "SATURDAY, NOV 21 • 8:00 PM", fontSize = 28f, fontColorHex = "#00F0FF")
                        ),
                        CanvasLayer(
                            name = "Cake",
                            type = LayerType.STICKER,
                            x = 0f, y = 420f, width = 140f, height = 140f,
                            stickerData = StickerLayerData(symbolOrName = "cake", tintColorHex = "#FFD166")
                        )
                    ),
                    eventInfo = PartyEvent(
                        title = "Alex's 25th Birthday Bash",
                        eventDescription = "Rooftop neon dance party and cake!",
                        location = "Skyline Lounge"
                    )
                ),
                previewColorHex = "#FF007F",
                tags = listOf("birthday", "neon", "cake")
            ),

            // 2. Wedding
            Template(
                id = "tpl_wedding_rose",
                name = "Rose Gold Romance",
                category = TemplateCategory.WEDDING,
                cardDesign = CardDesign(
                    title = "Wedding Celebration",
                    canvasWidth = 1080f,
                    canvasHeight = 1920f,
                    backgroundColorHex = "#1A0914",
                    backgroundGradientHexes = listOf("#1F0E1C", "#381831", "#150713"),
                    layers = listOf(
                        CanvasLayer(
                            name = "Header",
                            type = LayerType.TEXT,
                            x = 0f, y = -420f, width = 700f, height = 60f,
                            textData = TextLayerData(text = "TOGETHER WITH THEIR FAMILIES", fontSize = 20f, fontColorHex = "#E5C3A6", isBold = false)
                        ),
                        CanvasLayer(
                            name = "Names",
                            type = LayerType.TEXT,
                            x = 0f, y = -260f, width = 850f, height = 180f,
                            textData = TextLayerData(text = "Sophia & James", fontSize = 66f, fontColorHex = "#FFFFFF", shadowRadius = 10f, shadowColorHex = "#80E5C3A6")
                        ),
                        CanvasLayer(
                            name = "Date",
                            type = LayerType.TEXT,
                            x = 0f, y = 140f, width = 750f, height = 80f,
                            textData = TextLayerData(text = "SUNDAY, DEC 14 • 5:00 PM", fontSize = 24f, fontColorHex = "#E5C3A6")
                        ),
                        CanvasLayer(
                            name = "Heart",
                            type = LayerType.STICKER,
                            x = 0f, y = 420f, width = 120f, height = 120f,
                            stickerData = StickerLayerData(symbolOrName = "heart", tintColorHex = "#E5C3A6")
                        )
                    )
                ),
                previewColorHex = "#E5C3A6",
                tags = listOf("wedding", "rose", "love")
            ),

            // 3. Graduation
            Template(
                id = "tpl_graduation_gold",
                name = "Class of 2026 Celebration",
                category = TemplateCategory.GRADUATION,
                cardDesign = CardDesign(
                    title = "Graduation Gala",
                    canvasWidth = 1080f,
                    canvasHeight = 1920f,
                    backgroundColorHex = "#0B132B",
                    backgroundGradientHexes = listOf("#0B132B", "#1C2541", "#3A506B"),
                    layers = listOf(
                        CanvasLayer(
                            name = "Cap",
                            type = LayerType.STICKER,
                            x = 0f, y = -450f, width = 140f, height = 140f,
                            stickerData = StickerLayerData(symbolOrName = "cap", tintColorHex = "#FBBF24")
                        ),
                        CanvasLayer(
                            name = "Title",
                            type = LayerType.TEXT,
                            x = 0f, y = -280f, width = 850f, height = 180f,
                            textData = TextLayerData(text = "CLASS OF 2026\nGRADUATION", fontSize = 70f, fontColorHex = "#FBBF24")
                        ),
                        CanvasLayer(
                            name = "Details",
                            type = LayerType.TEXT,
                            x = 0f, y = 140f, width = 750f, height = 80f,
                            textData = TextLayerData(text = "JUNE 18 • 6:30 PM\nDINNER & CELEBRATION", fontSize = 24f, fontColorHex = "#FFFFFF")
                        )
                    )
                ),
                previewColorHex = "#FBBF24",
                tags = listOf("graduation", "stars")
            ),

            // 4. Halloween
            Template(
                id = "tpl_halloween_haunt",
                name = "Spooky Costume Night",
                category = TemplateCategory.HALLOWEEN,
                cardDesign = CardDesign(
                    title = "Haunted Halloween",
                    canvasWidth = 1080f,
                    canvasHeight = 1920f,
                    backgroundColorHex = "#0A0101",
                    backgroundGradientHexes = listOf("#160000", "#370617", "#03071E"),
                    layers = listOf(
                        CanvasLayer(
                            name = "Title",
                            type = LayerType.TEXT,
                            x = 0f, y = -340f, width = 850f, height = 220f,
                            textData = TextLayerData(text = "HALLOWEEN\nCOSTUME PARTY", fontSize = 72f, fontColorHex = "#FF5400", shadowRadius = 15f)
                        ),
                        CanvasLayer(
                            name = "Time",
                            type = LayerType.TEXT,
                            x = 0f, y = 120f, width = 750f, height = 80f,
                            textData = TextLayerData(text = "OCTOBER 31ST • 9:00 PM TILL LATE", fontSize = 24f, fontColorHex = "#FFD60A")
                        )
                    )
                ),
                previewColorHex = "#FF5400",
                tags = listOf("halloween", "spooky")
            ),

            // 5. Christmas
            Template(
                id = "tpl_christmas_pine",
                name = "Holiday Cheer & Pine",
                category = TemplateCategory.CHRISTMAS,
                cardDesign = CardDesign(
                    title = "Christmas Cheer",
                    canvasWidth = 1080f,
                    canvasHeight = 1920f,
                    backgroundColorHex = "#061A14",
                    backgroundGradientHexes = listOf("#0A2E20", "#1E4D2B", "#05160E"),
                    layers = listOf(
                        CanvasLayer(
                            name = "Title",
                            type = LayerType.TEXT,
                            x = 0f, y = -280f, width = 850f, height = 180f,
                            textData = TextLayerData(text = "HOLIDAY\nCHEER & BEER", fontSize = 72f, fontColorHex = "#EF4444")
                        ),
                        CanvasLayer(
                            name = "Date",
                            type = LayerType.TEXT,
                            x = 0f, y = 140f, width = 750f, height = 80f,
                            textData = TextLayerData(text = "DECEMBER 24 • 7:00 PM", fontSize = 26f, fontColorHex = "#FEF3C7")
                        )
                    )
                ),
                previewColorHex = "#EF4444",
                tags = listOf("christmas", "holiday")
            ),

            // 6. New Year
            Template(
                id = "tpl_new_year_gala",
                name = "Midnight Countdown 2027",
                category = TemplateCategory.NEW_YEAR,
                cardDesign = CardDesign(
                    title = "NYE Countdown",
                    canvasWidth = 1080f,
                    canvasHeight = 1920f,
                    backgroundColorHex = "#080708",
                    backgroundGradientHexes = listOf("#1A181B", "#372549", "#000000"),
                    layers = listOf(
                        CanvasLayer(
                            name = "Title",
                            type = LayerType.TEXT,
                            x = 0f, y = -300f, width = 850f, height = 200f,
                            textData = TextLayerData(text = "NEW YEAR'S EVE\nCOUNTDOWN", fontSize = 70f, fontColorHex = "#FACC15", shadowRadius = 15f)
                        ),
                        CanvasLayer(
                            name = "Time",
                            type = LayerType.TEXT,
                            x = 0f, y = 160f, width = 750f, height = 90f,
                            textData = TextLayerData(text = "DEC 31 • DOORS OPEN 9 PM\nMIDNIGHT CHAMPAGNE TOAST", fontSize = 24f, fontColorHex = "#FFFFFF")
                        )
                    )
                ),
                previewColorHex = "#FACC15",
                tags = listOf("newyear", "fireworks")
            ),

            // 7. Music
            Template(
                id = "tpl_music_cyber",
                name = "Cyberpunk EDM Rave",
                category = TemplateCategory.MUSIC,
                cardDesign = CardDesign(
                    title = "Cyber Rave",
                    canvasWidth = 1080f,
                    canvasHeight = 1920f,
                    backgroundColorHex = "#050517",
                    backgroundGradientHexes = listOf("#0B032D", "#1C0F4F", "#030219"),
                    layers = listOf(
                        CanvasLayer(
                            name = "Title",
                            type = LayerType.TEXT,
                            x = 0f, y = -300f, width = 850f, height = 200f,
                            textData = TextLayerData(text = "ELECTRIC\nPULSE", fontSize = 84f, fontColorHex = "#00F5D4", shadowRadius = 20f, shadowColorHex = "#8000F5D4")
                        ),
                        CanvasLayer(
                            name = "Music Icon",
                            type = LayerType.STICKER,
                            x = 0f, y = 100f, width = 130f, height = 130f,
                            stickerData = StickerLayerData(symbolOrName = "music", tintColorHex = "#00F5D4")
                        )
                    )
                ),
                previewColorHex = "#00F5D4",
                tags = listOf("music", "rave", "edm")
            ),

            // 8. Outdoor
            Template(
                id = "tpl_outdoor_sunset",
                name = "Sunset Pool & BBQ",
                category = TemplateCategory.OUTDOOR,
                cardDesign = CardDesign(
                    title = "Sunset Pool Party",
                    canvasWidth = 1080f,
                    canvasHeight = 1920f,
                    backgroundColorHex = "#1C0A00",
                    backgroundGradientHexes = listOf("#3D1300", "#7C2D12", "#EA580C"),
                    layers = listOf(
                        CanvasLayer(
                            name = "Title",
                            type = LayerType.TEXT,
                            x = 0f, y = -270f, width = 850f, height = 180f,
                            textData = TextLayerData(text = "SUNSET BBQ &\nPOOL PARTY", fontSize = 70f, fontColorHex = "#FFFFFF")
                        ),
                        CanvasLayer(
                            name = "Details",
                            type = LayerType.TEXT,
                            x = 0f, y = 150f, width = 750f, height = 90f,
                            textData = TextLayerData(text = "THIS SATURDAY • 3:00 PM\nBRING SWIMSUIT & GOOD ENERGY", fontSize = 24f, fontColorHex = "#FEF08A")
                        )
                    )
                ),
                previewColorHex = "#EA580C",
                tags = listOf("outdoor", "bbq", "pool")
            ),

            // 9. Friends
            Template(
                id = "tpl_friends_pizza",
                name = "Pizza & Chill Gathering",
                category = TemplateCategory.FRIENDS,
                cardDesign = CardDesign(
                    title = "Weekend Meetup",
                    canvasWidth = 1080f,
                    canvasHeight = 1920f,
                    backgroundColorHex = "#131515",
                    backgroundGradientHexes = listOf("#1E2222", "#2B2D42", "#131515"),
                    layers = listOf(
                        CanvasLayer(
                            name = "Title",
                            type = LayerType.TEXT,
                            x = 0f, y = -260f, width = 850f, height = 180f,
                            textData = TextLayerData(text = "PIZZA, DRINKS\n& BOARD GAMES", fontSize = 66f, fontColorHex = "#FFD166")
                        ),
                        CanvasLayer(
                            name = "Subtext",
                            type = LayerType.TEXT,
                            x = 0f, y = 100f, width = 750f, height = 90f,
                            textData = TextLayerData(text = "SUNDAY 5:00 PM • MY PLACE\nCHILLING & CATCHING UP", fontSize = 24f, fontColorHex = "#E2E8F0")
                        )
                    )
                ),
                previewColorHex = "#FFD166",
                tags = listOf("friends", "pizza", "chill")
            ),

            // 10. Grand Opening
            Template(
                id = "tpl_grand_opening",
                name = "Grand Ribbon Cutting Gala",
                category = TemplateCategory.GRAND_OPENING,
                cardDesign = CardDesign(
                    title = "Grand Opening VIP",
                    canvasWidth = 1080f,
                    canvasHeight = 1920f,
                    backgroundColorHex = "#140005",
                    backgroundGradientHexes = listOf("#2B000B", "#5C0018", "#140005"),
                    layers = listOf(
                        CanvasLayer(
                            name = "Title",
                            type = LayerType.TEXT,
                            x = 0f, y = -300f, width = 850f, height = 200f,
                            textData = TextLayerData(text = "GRAND\nOPENING", fontSize = 80f, fontColorHex = "#FFD700", shadowRadius = 15f)
                        ),
                        CanvasLayer(
                            name = "Info",
                            type = LayerType.TEXT,
                            x = 0f, y = 120f, width = 750f, height = 90f,
                            textData = TextLayerData(text = "RIBBON CUTTING & GALA\nNOVEMBER 28 • 6:00 PM", fontSize = 24f, fontColorHex = "#FFFFFF")
                        )
                    )
                ),
                previewColorHex = "#FFD700",
                tags = listOf("opening", "gala", "luxury")
            ),

            // 11. Private Party
            Template(
                id = "tpl_private_speakeasy",
                name = "Secret Speakeasy Lounge",
                category = TemplateCategory.PRIVATE_PARTY,
                cardDesign = CardDesign(
                    title = "Secret Speakeasy",
                    canvasWidth = 1080f,
                    canvasHeight = 1920f,
                    backgroundColorHex = "#09090B",
                    backgroundGradientHexes = listOf("#18181B", "#27272A", "#09090B"),
                    layers = listOf(
                        CanvasLayer(
                            name = "Badge",
                            type = LayerType.TEXT,
                            x = 0f, y = -450f, width = 700f, height = 60f,
                            textData = TextLayerData(text = "🔒 STRICTLY CONFIDENTIAL", fontSize = 20f, fontColorHex = "#A1A1AA", letterSpacing = 3f)
                        ),
                        CanvasLayer(
                            name = "Title",
                            type = LayerType.TEXT,
                            x = 0f, y = -280f, width = 850f, height = 180f,
                            textData = TextLayerData(text = "THE SECRET\nSOIRÉE", fontSize = 72f, fontColorHex = "#FAFAFA")
                        ),
                        CanvasLayer(
                            name = "Note",
                            type = LayerType.TEXT,
                            x = 0f, y = 140f, width = 750f, height = 90f,
                            textData = TextLayerData(text = "PASSWORD REQUIRED AT DOOR\nLOCATION SENT 2H BEFORE", fontSize = 22f, fontColorHex = "#E4E4E7")
                        )
                    )
                ),
                previewColorHex = "#A1A1AA",
                tags = listOf("private", "vip", "secret")
            )
        )
    }
}
