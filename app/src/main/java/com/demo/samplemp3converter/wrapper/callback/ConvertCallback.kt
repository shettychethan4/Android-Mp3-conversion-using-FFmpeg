package com.demo.samplemp3converter.wrapper.callback

import java.io.File

interface ConvertCallback {
    fun onSuccess(convertedFile: File)

    fun onFailure(error: Exception)
}