package com.partiful.invitemaker

import androidx.compose.animation.Crossfade
import androidx.compose.runtime.*
import com.partiful.invitemaker.domain.model.CardDesign
import com.partiful.invitemaker.domain.model.Template
import com.partiful.invitemaker.feature.calendar.EventFormScreen
import com.partiful.invitemaker.feature.editor.CardEditorScreen
import com.partiful.invitemaker.feature.export.ExportOptionsScreen
import com.partiful.invitemaker.feature.home.HomeScreen
import com.partiful.invitemaker.feature.onboarding.OnboardingScreen
import com.partiful.invitemaker.feature.preview.CardPreviewScreen
import com.partiful.invitemaker.feature.projects.SavedProjectsScreen
import com.partiful.invitemaker.feature.settings.SettingsScreen
import com.partiful.invitemaker.feature.splash.SplashScreen
import com.partiful.invitemaker.feature.templates.TemplateDetailScreen

sealed interface ScreenState {
    data object Splash : ScreenState
    data object Onboarding : ScreenState
    data object Home : ScreenState
    data class TemplateDetail(val template: Template) : ScreenState
    data class Editor(val initialDesign: CardDesign? = null) : ScreenState
    data class Preview(val cardDesign: CardDesign) : ScreenState
    data class Export(val cardDesign: CardDesign) : ScreenState
    data class Calendar(val cardDesign: CardDesign) : ScreenState
    data object SavedProjects : ScreenState
    data object Settings : ScreenState
}

@Composable
fun MainNavigation() {
    var currentScreen by remember { mutableStateOf<ScreenState>(ScreenState.Splash) }
    val screenStack = remember { mutableStateListOf<ScreenState>(ScreenState.Splash) }

    fun navigateTo(screen: ScreenState) {
        screenStack.add(screen)
        currentScreen = screen
    }

    fun navigateBack() {
        if (screenStack.size > 1) {
            screenStack.removeLastOrNull()
            currentScreen = screenStack.last()
        }
    }

    Crossfade(targetState = currentScreen, label = "screen_transition") { screen ->
        when (screen) {
            is ScreenState.Splash -> {
                SplashScreen(onFinish = {
                    screenStack.clear()
                    navigateTo(ScreenState.Onboarding)
                })
            }
            is ScreenState.Onboarding -> {
                OnboardingScreen(onComplete = {
                    screenStack.clear()
                    navigateTo(ScreenState.Home)
                })
            }
            is ScreenState.Home -> {
                HomeScreen(
                    onCreateBlank = {
                        navigateTo(ScreenState.Editor(CardDesign(title = "Thiết kế mới")))
                    },
                    onCreateFromPhoto = { design ->
                        navigateTo(ScreenState.Editor(design))
                    },
                    onSelectTemplate = { tpl ->
                        navigateTo(ScreenState.TemplateDetail(tpl))
                    },
                    onOpenProjects = {
                        navigateTo(ScreenState.SavedProjects)
                    },
                    onOpenSettings = {
                        navigateTo(ScreenState.Settings)
                    }
                )
            }
            is ScreenState.TemplateDetail -> {
                TemplateDetailScreen(
                    template = screen.template,
                    onBack = { navigateBack() },
                    onUseTemplate = {
                        navigateTo(ScreenState.Editor(screen.template.cardDesign))
                    }
                )
            }
            is ScreenState.Editor -> {
                CardEditorScreen(
                    initialDesign = screen.initialDesign,
                    onBack = { navigateBack() },
                    onNavigateToPreview = { design ->
                        navigateTo(ScreenState.Preview(design))
                    }
                )
            }
            is ScreenState.Preview -> {
                CardPreviewScreen(
                    cardDesign = screen.cardDesign,
                    onBack = { navigateBack() },
                    onNavigateToExport = {
                        navigateTo(ScreenState.Export(screen.cardDesign))
                    },
                    onNavigateToCalendar = {
                        navigateTo(ScreenState.Calendar(screen.cardDesign))
                    }
                )
            }
            is ScreenState.Export -> {
                ExportOptionsScreen(
                    cardDesign = screen.cardDesign,
                    onBack = { navigateBack() }
                )
            }
            is ScreenState.Calendar -> {
                EventFormScreen(
                    initialEvent = screen.cardDesign.eventInfo,
                    onBack = { navigateBack() }
                )
            }
            is ScreenState.SavedProjects -> {
                SavedProjectsScreen(
                    onBack = { navigateBack() },
                    onOpenProject = { project ->
                        navigateTo(ScreenState.Editor(project))
                    }
                )
            }
            is ScreenState.Settings -> {
                SettingsScreen(onBack = { navigateBack() })
            }
        }
    }
}
