package com.example.ads

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.ui.theme.FredokaFontFamily
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

object AdMobConfig {
    // 1. Banner debajo en pantalla de juego
    const val BANNER_GAME_BOTTOM = "ca-app-pub-1495262574338316/2803163788"
    // 2. Banner debajo en menú principal
    const val BANNER_MENU_BOTTOM = "ca-app-pub-1495262574338316/2903409205"
    // 3. Anuncio emergente (Intersticial) de Game Over
    const val INTERSTITIAL_GAME_OVER = "ca-app-pub-1495262574338316/2452733430"
    // 4. Anuncio emergente (Intersticial) de Nivel Superado
    const val INTERSTITIAL_LEVEL_WIN = "ca-app-pub-1495262574338316/8970040322"
    // 5. Anuncio Bonificado (Rewarded) para obtener monedas gratis
    const val REWARDED_FREE_COINS = "ca-app-pub-1495262574338316/1182179137"
    // 6. Anuncio emergente secundario
    const val INTERSTITIAL_POPUP_SECONDARY = "ca-app-pub-1495262574338316/4962596335"
    // 7. Banner debajo en mapa de aventura
    const val BANNER_ADVENTURE_BOTTOM = "ca-app-pub-1495262574338316/2714752655"
    // 8. Anuncio emergente alternativo
    const val INTERSTITIAL_ALT = "ca-app-pub-1495262574338316/3503687777"
}

class AdMobManager(private val context: Context) {
    private var isInitialized = false

    private var gameOverInterstitialAd: InterstitialAd? = null
    private var levelWinInterstitialAd: InterstitialAd? = null
    private var secondaryInterstitialAd: InterstitialAd? = null
    private var rewardedAd: RewardedAd? = null

    private var isLoadingGameOverAd = false
    private var isLoadingLevelWinAd = false
    private var isLoadingSecondaryAd = false
    private var isLoadingRewardedAd = false

    fun initialize(onComplete: () -> Unit = {}) {
        if (isInitialized) return
        try {
            MobileAds.initialize(context) { status ->
                Log.d("AdMobManager", "MobileAds initialized: $status")
                isInitialized = true
                preloadAds()
                onComplete()
            }
        } catch (e: Exception) {
            Log.e("AdMobManager", "Error initializing MobileAds", e)
        }
    }

    fun preloadAds() {
        loadGameOverInterstitial()
        loadLevelWinInterstitial()
        loadSecondaryInterstitial()
        loadRewardedAd()
    }

    fun loadGameOverInterstitial() {
        if (isLoadingGameOverAd || gameOverInterstitialAd != null) return
        isLoadingGameOverAd = true

        val request = AdRequest.Builder().build()
        InterstitialAd.load(
            context,
            AdMobConfig.INTERSTITIAL_GAME_OVER,
            request,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    gameOverInterstitialAd = interstitialAd
                    isLoadingGameOverAd = false
                    Log.d("AdMobManager", "Game Over Interstitial loaded successfully")
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    gameOverInterstitialAd = null
                    isLoadingGameOverAd = false
                    Log.e("AdMobManager", "Game Over Interstitial failed: ${loadAdError.message}")
                }
            }
        )
    }

    fun loadLevelWinInterstitial() {
        if (isLoadingLevelWinAd || levelWinInterstitialAd != null) return
        isLoadingLevelWinAd = true

        val request = AdRequest.Builder().build()
        InterstitialAd.load(
            context,
            AdMobConfig.INTERSTITIAL_LEVEL_WIN,
            request,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    levelWinInterstitialAd = interstitialAd
                    isLoadingLevelWinAd = false
                    Log.d("AdMobManager", "Level Win Interstitial loaded successfully")
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    levelWinInterstitialAd = null
                    isLoadingLevelWinAd = false
                    Log.e("AdMobManager", "Level Win Interstitial failed: ${loadAdError.message}")
                }
            }
        )
    }

    fun loadSecondaryInterstitial() {
        if (isLoadingSecondaryAd || secondaryInterstitialAd != null) return
        isLoadingSecondaryAd = true

        val request = AdRequest.Builder().build()
        InterstitialAd.load(
            context,
            AdMobConfig.INTERSTITIAL_POPUP_SECONDARY,
            request,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    secondaryInterstitialAd = interstitialAd
                    isLoadingSecondaryAd = false
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    secondaryInterstitialAd = null
                    isLoadingSecondaryAd = false
                }
            }
        )
    }

    fun loadRewardedAd() {
        if (isLoadingRewardedAd || rewardedAd != null) return
        isLoadingRewardedAd = true

        val request = AdRequest.Builder().build()
        RewardedAd.load(
            context,
            AdMobConfig.REWARDED_FREE_COINS,
            request,
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedAd) {
                    rewardedAd = ad
                    isLoadingRewardedAd = false
                    Log.d("AdMobManager", "Rewarded Ad loaded successfully")
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    rewardedAd = null
                    isLoadingRewardedAd = false
                    Log.e("AdMobManager", "Rewarded Ad failed: ${error.message}")
                }
            }
        )
    }

    /**
     * Muestra anuncio emergente al perder / Game Over
     */
    fun showGameOverInterstitial(activity: Activity, onDismissed: () -> Unit) {
        val ad = gameOverInterstitialAd
        if (ad != null) {
            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    gameOverInterstitialAd = null
                    loadGameOverInterstitial()
                    onDismissed()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    gameOverInterstitialAd = null
                    loadGameOverInterstitial()
                    onDismissed()
                }
            }
            ad.show(activity)
        } else {
            // Si aún no ha cargado, continuar inmediatamente
            loadGameOverInterstitial()
            onDismissed()
        }
    }

    /**
     * Muestra anuncio emergente al ganar un nivel
     */
    fun showLevelWinInterstitial(activity: Activity, onDismissed: () -> Unit) {
        val ad = levelWinInterstitialAd
        if (ad != null) {
            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    levelWinInterstitialAd = null
                    loadLevelWinInterstitial()
                    onDismissed()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    levelWinInterstitialAd = null
                    loadLevelWinInterstitial()
                    onDismissed()
                }
            }
            ad.show(activity)
        } else {
            loadLevelWinInterstitial()
            onDismissed()
        }
    }

    /**
     * Muestra anuncio bonificado para ganar monedas
     */
    fun showRewardedAd(activity: Activity, onRewardEarned: (Int) -> Unit, onClosed: () -> Unit) {
        val ad = rewardedAd
        if (ad != null) {
            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    rewardedAd = null
                    loadRewardedAd()
                    onClosed()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    rewardedAd = null
                    loadRewardedAd()
                    onClosed()
                }
            }
            ad.show(activity) { rewardItem ->
                val amount = rewardItem.amount.takeIf { it > 0 } ?: 50
                onRewardEarned(amount)
            }
        } else {
            loadRewardedAd()
            onClosed()
        }
    }
}

/**
 * Componente Jetpack Compose para mostrar Banners automáticos debajo.
 */
@Composable
fun AdMobBanner(
    adUnitId: String,
    modifier: Modifier = Modifier
) {
    if (LocalInspectionMode.current) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(50.dp)
                .background(Color(0xFF1E1838)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "AdMob Banner Preview",
                fontFamily = FredokaFontFamily,
                fontSize = 12.sp,
                color = Color.LightGray
            )
        }
        return
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .background(Color(0xFF0F0C20))
            .testTag("admob_banner_${adUnitId.takeLast(4)}"),
        contentAlignment = Alignment.Center
    ) {
        AndroidView(
            modifier = Modifier.fillMaxWidth(),
            factory = { ctx ->
                AdView(ctx).apply {
                    setAdSize(AdSize.BANNER)
                    this.adUnitId = adUnitId
                    loadAd(AdRequest.Builder().build())
                }
            }
        )
    }
}
