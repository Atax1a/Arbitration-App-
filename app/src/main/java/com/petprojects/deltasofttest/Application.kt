package com.petprojects.deltasofttest

import android.app.Application

class Application: Application() {
    override fun onCreate() {
        super.onCreate()
        RemoteConfigUtils.init()
    }
}