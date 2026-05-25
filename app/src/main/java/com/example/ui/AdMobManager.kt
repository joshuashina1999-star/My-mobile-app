package com.example.ui

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object AdMobManager {
    private const val TAG = "AdMobManager"

    // Real Ad unit IDs specified by user
    const val APP_ID = "ca-app-pub-8472522654274740~6941371156"
    const val BANNER_ID = "ca-app-pub-8472522654274740/8935875252"
    const val INTERSTITIAL_ID = "ca-app-pub-8472522654274740/7912278253"
    const val REWARDED_ID = "ca-app-pub-8472522654274740/2139366766"
    const val YLOVERLAY_ID = "ca-app-pub-8472522654274740/6882077464"

    // Ad statuses for the UI indicators
    private val _isInitialized = MutableStateFlow(false)
    val isInitialized: StateFlow<Boolean> = _isInitialized

    private val _interstitialState = MutableStateFlow<AdState>(AdState.Unloaded)
    val interstitialState: StateFlow<AdState> = _interstitialState

    private val _rewardedState = MutableStateFlow<AdState>(AdState.Unloaded)
    val rewardedState: StateFlow<AdState> = _rewardedState

    private val _yloverlayState = MutableStateFlow<AdState>(AdState.Unloaded)
    val yloverlayState: StateFlow<AdState> = _yloverlayState

    private val _adLogs = MutableStateFlow<List<String>>(emptyList())
    val adLogs: StateFlow<List<String>> = _adLogs

    private var interstitialAd: InterstitialAd? = null
    private var rewardedAd: RewardedAd? = null
    private var yloverlayAd: InterstitialAd? = null

    sealed interface AdState {
        object Unloaded : AdState
        object Loading : AdState
        object Loaded : AdState
        data class Error(val message: String) : AdState
    }

    fun init(context: Context) {
        log("Initializing MobileAds SDK...")
        MobileAds.initialize(context) { status ->
            _isInitialized.value = true
            log("MobileAds initialized successfully.")
            loadAllAds(context)
        }
    }

    private fun log(message: String) {
        Log.d(TAG, message)
        val currentLogs = _adLogs.value.toMutableList()
        currentLogs.add(0, "[${System.currentTimeMillis() % 100000}] $message")
        _adLogs.value = currentLogs.take(50) // keep latest 50 logs
    }

    fun loadAllAds(context: Context) {
        loadInterstitial(context)
        loadRewarded(context)
        loadYloverlay(context)
    }

    fun loadInterstitial(context: Context) {
        _interstitialState.value = AdState.Loading
        log("Loading Interstitial: $INTERSTITIAL_ID")
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            context,
            INTERSTITIAL_ID,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                    _interstitialState.value = AdState.Loaded
                    log("Interstitial ad loaded successfully.")
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    interstitialAd = null
                    val errMsg = "Code ${error.code}: ${error.message}"
                    _interstitialState.value = AdState.Error(errMsg)
                    log("Interstitial load failed: $errMsg")
                }
            }
        )
    }

    fun loadRewarded(context: Context) {
        _rewardedState.value = AdState.Loading
        log("Loading Rewarded Ad: $REWARDED_ID")
        val adRequest = AdRequest.Builder().build()
        RewardedAd.load(
            context,
            REWARDED_ID,
            adRequest,
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedAd) {
                    rewardedAd = ad
                    _rewardedState.value = AdState.Loaded
                    log("Rewarded ad loaded successfully.")
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    rewardedAd = null
                    val errMsg = "Code ${error.code}: ${error.message}"
                    _rewardedState.value = AdState.Error(errMsg)
                    log("Rewarded load failed: $errMsg")
                }
            }
        )
    }

    fun loadYloverlay(context: Context) {
        _yloverlayState.value = AdState.Loading
        log("Loading Yloverlay (Interstitial): $YLOVERLAY_ID")
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            context,
            YLOVERLAY_ID,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    yloverlayAd = ad
                    _yloverlayState.value = AdState.Loaded
                    log("Yloverlay ad loaded successfully.")
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    yloverlayAd = null
                    val errMsg = "Code ${error.code}: ${error.message}"
                    _yloverlayState.value = AdState.Error(errMsg)
                    log("Yloverlay load failed: $errMsg")
                }
            }
        )
    }

    fun showInterstitial(activity: Activity, onDismiss: () -> Unit = {}) {
        val ad = interstitialAd
        if (ad != null) {
            log("Showing Interstitial Ad...")
            ad.show(activity)
            interstitialAd = null
            _interstitialState.value = AdState.Unloaded
            onDismiss()
            loadInterstitial(activity)
        } else {
            log("Triggered Interstitial show, but Ad is unloaded. Simulating overlay fallback.")
            Toast.makeText(activity, "Simulated Interstitial Ad Triggered!", Toast.LENGTH_SHORT).show()
            onDismiss()
        }
    }

    fun showRewarded(activity: Activity, onRewardEarned: (amount: Int) -> Unit) {
        val ad = rewardedAd
        if (ad != null) {
            log("Showing Rewarded Ad...")
            ad.show(activity) { rewardItem ->
                log("User earned reward: ${rewardItem.amount} ${rewardItem.type}")
                onRewardEarned(rewardItem.amount)
            }
            rewardedAd = null
            _rewardedState.value = AdState.Unloaded
            loadRewarded(activity)
        } else {
            log("Triggered Rewarded Ad show, but Ad is unloaded. Rewarding user via simulated fallback.")
            Toast.makeText(activity, "Simulated Ad View +100 Active Points Reward Added!", Toast.LENGTH_SHORT).show()
            onRewardEarned(100)
        }
    }

    fun showYloverlay(activity: Activity, onDismiss: () -> Unit = {}) {
        val ad = yloverlayAd
        if (ad != null) {
            log("Showing Yloverlay Ad...")
            ad.show(activity)
            yloverlayAd = null
            _yloverlayState.value = AdState.Unloaded
            onDismiss()
            loadYloverlay(activity)
        } else {
            log("Triggered Yloverlay show, but Ad is unloaded. Simulating full screen overlay transition.")
            Toast.makeText(activity, "Simulated Yloverlay Open!", Toast.LENGTH_SHORT).show()
            onDismiss()
        }
    }
}
