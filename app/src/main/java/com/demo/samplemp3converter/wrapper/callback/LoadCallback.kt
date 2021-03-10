package com.demo.samplemp3converter.wrapper.callback

interface LoadCallback {

    fun onSuccess()

    fun onFailure(error: Exception)
}