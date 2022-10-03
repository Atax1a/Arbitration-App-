package com.petprojects.deltasofttest

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.telephony.TelephonyManager
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.petprojects.deltasofttest.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private val TAG = "RemoteConfigUtils"

    private val SHARED_REMOTE_KEY = "REMOTE_NEW_URL"

    private lateinit var binding: ActivityMainBinding

    private var url: String? = null

    private val PREFERENCES_NAME = "remote_config_shared_preferences"

    private var sharedPrefs: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPrefs = this.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

        loadSharedPreferences(SHARED_REMOTE_KEY)

        //clearSharedPreference()

        //loadPlug()


        if (url != null) {
            loadWebView(url!!)
            remoteUrlLoaded()
        } else {
            if (remoteUrlLoaded()) {
                Log.v(TAG, "Remote Url Loaded, new url: $url")
                saveRemoteUrlToShared()
                loadWebView(url!!)
            } else {
                loadPlug()
            }
        }

    }

    private fun loadPlug() {
        binding.apply {
            plugContainer.visibility = View.VISIBLE
            webView.visibility = View.GONE
        }
    }

    private fun saveRemoteUrlToShared() {
        val editor: SharedPreferences.Editor? = sharedPrefs?.edit()
        editor?.putString(SHARED_REMOTE_KEY, RemoteConfigUtils.getNewUrl())
        editor?.commit()
        Log.v(
            TAG,
            "Remote url saved to $PREFERENCES_NAME + $SHARED_REMOTE_KEY with ${RemoteConfigUtils.getNewUrl()}"
        )
    }

    private fun remoteUrlLoaded(): Boolean {
        url = RemoteConfigUtils.getNewUrl()
        val mark = Build.MANUFACTURER
        val model = Build.MODEL
        Log.v(TAG,"     TF mark: $mark ; TF model: $mark; Contains Google: ${mark.contains("google") && model.contains("google")}")
        Log.v(TAG, "     Has sim: ${isSIMInserted(this)}")
        return isSIMInserted(this) && !(mark.contains("google")) && !(model.contains("google")) && url != null
    }

    private fun loadWebView(url: String) {
        val webSettings = binding.webView.settings
        webSettings.javaScriptEnabled = true

        binding.webView.apply {
            loadUrl(url)
            webViewClient = WebViewClient()
            canGoBack()
            setOnKeyListener(View.OnKeyListener() { v, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_BACK
                    && event.action == MotionEvent.ACTION_UP
                    && binding.webView.canGoBack()
                ) {
                    binding.webView.goBack()
                    return@OnKeyListener true
                }
                false
            })

        }
    }

    private fun loadSharedPreferences(key: String) {
        url = sharedPrefs?.getString(key, null)
    }

    private fun isSIMInserted(context: Context): Boolean {
        return TelephonyManager.SIM_STATE_ABSENT != (context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager).simState
    }


    private fun clearSharedPreference() {
        val editor: SharedPreferences.Editor? = sharedPrefs?.edit()
        //sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        editor?.clear()
        editor?.commit()
        Log.v(TAG, "SharedPrefs - cleared")
    }

}