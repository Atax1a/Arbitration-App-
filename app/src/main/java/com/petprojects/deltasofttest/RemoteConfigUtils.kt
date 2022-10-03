package com.petprojects.deltasofttest

import android.util.Log
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import java.net.URL

object RemoteConfigUtils {

    private const val TAG = "RemoteConfigUtils"

    private const val URL_TAG = "new_url"

    private val DEFAULT: HashMap<String, Any> =
        hashMapOf(
            URL_TAG to "www.google.com",
        )

    private lateinit var remoteConfig: FirebaseRemoteConfig

    fun init() {
        remoteConfig = getFirebaseRemoteConfig()
    }

    private fun getFirebaseRemoteConfig(): FirebaseRemoteConfig {
        val remoteConfig: FirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()

        val configSettings: FirebaseRemoteConfigSettings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(0)
            .build()
        remoteConfig.apply {
            setConfigSettingsAsync(configSettings)
            setDefaultsAsync(DEFAULT)
            fetchAndActivate().addOnCompleteListener {
                Log.d(TAG, "Config Fetched: fetched url - ${getNewUrl()}")
            }
            return remoteConfig
        }
    }
    fun getNewUrl(): String = remoteConfig.getString(URL_TAG)
}
