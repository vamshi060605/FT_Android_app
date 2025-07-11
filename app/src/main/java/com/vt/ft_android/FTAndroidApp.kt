package com.vt.ft_android

import android.app.Application
import com.google.firebase.FirebaseApp

class FTAndroidApp : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
} 