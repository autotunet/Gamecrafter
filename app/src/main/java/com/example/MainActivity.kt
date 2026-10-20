package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.model.GameMode
import com.example.ui.screens.AdventureScreen
import com.example.ui.screens.GameScreen
import com.example.ui.screens.MenuScreen
import com.example.ui.screens.StatsScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.GameViewModel

enum class AppScreen {
    MENU,
    GAME,
    ADVENTURE_MAP,
    STATS
}

class MainActivity : ComponentActivity() {
    private val gameViewModel: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Inicializar Google Mobile Ads (AdMob)
        com.example.ads.AdMobManager(this).initialize()

        setContent {
            var currentLanguage by remember { mutableStateOf(gameViewModel.prefs.appLanguage) }

            androidx.compose.runtime.CompositionLocalProvider(
                com.example.localization.LocalAppStrings provides com.example.localization.AppStrings.get(currentLanguage),
                com.example.localization.LocalCurrentLanguage provides currentLanguage
            ) {
                MyApplicationTheme {
                    Surface(modifier = Modifier.fillMaxSize()) {
                        var currentScreen by remember { mutableStateOf(AppScreen.MENU) }

                        AnimatedContent(
                            targetState = currentScreen,
                            label = "ScreenTransition",
                            transitionSpec = {
                                fadeIn() togetherWith fadeOut()
                            }
                        ) { target ->
                            when (target) {
                                AppScreen.MENU -> {
                                    MenuScreen(
                                        prefs = gameViewModel.prefs,
                                        currentLanguage = currentLanguage,
                                        onSelectLanguage = { newLang ->
                                            currentLanguage = newLang
                                            gameViewModel.prefs.appLanguage = newLang
                                        },
                                        onStartClassic = {
                                            gameViewModel.startClassicGame()
                                            currentScreen = AppScreen.GAME
                                        },
                                        onStartTimed = {
                                            gameViewModel.startTimedGame(60)
                                            currentScreen = AppScreen.GAME
                                        },
                                        onOpenAdventure = {
                                            currentScreen = AppScreen.ADVENTURE_MAP
                                        },
                                        onStartDaily = {
                                            gameViewModel.startDailyChallenge()
                                            currentScreen = AppScreen.GAME
                                        },
                                        onOpenStats = {
                                            currentScreen = AppScreen.STATS
                                        }
                                    )
                                }
                                AppScreen.GAME -> {
                                    GameScreen(
                                        viewModel = gameViewModel,
                                        currentLanguage = currentLanguage,
                                        onSelectLanguage = { newLang ->
                                            currentLanguage = newLang
                                            gameViewModel.prefs.appLanguage = newLang
                                        },
                                        onNavigateBack = {
                                            currentScreen = AppScreen.MENU
                                        }
                                    )
                                }
                                AppScreen.ADVENTURE_MAP -> {
                                    AdventureScreen(
                                        prefs = gameViewModel.prefs,
                                        onSelectLevel = { levelNum ->
                                            gameViewModel.startAdventureLevel(levelNum)
                                            currentScreen = AppScreen.GAME
                                        },
                                        onNavigateBack = {
                                            currentScreen = AppScreen.MENU
                                        }
                                    )
                                }
                                AppScreen.STATS -> {
                                    StatsScreen(
                                        prefs = gameViewModel.prefs,
                                        onNavigateBack = {
                                            currentScreen = AppScreen.MENU
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
