package com.maeen.mahfilhub

import android.app.Application
import com.maeen.mahfilhub.data.remote.RetrofitClient

class MahfilHubApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        // Initialize the authenticated Retrofit client with app context
        RetrofitClient.init(this)
    }
}
