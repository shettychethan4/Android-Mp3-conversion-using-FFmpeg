package com.demo.samplemp3converter.application

import android.app.Application
import com.demo.samplemp3converter.wrapper.AudioConverter
import com.demo.samplemp3converter.wrapper.callback.LoadCallback

class Application : Application() {

    override fun onCreate() {
        super.onCreate()
        AudioConverter.load(this, object : LoadCallback {
            override fun onSuccess() {
                // Great!
            }

            override fun onFailure(error: Exception) {
                // FFmpeg is not supported by device
                error.printStackTrace()
            }
        })
    }
}