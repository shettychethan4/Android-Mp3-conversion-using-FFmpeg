package com.demo.samplemp3converter.ui.viewmodel

import android.app.Application
import android.os.Environment
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.demo.samplemp3converter.common.Constants.FILELOCATION
import com.demo.samplemp3converter.wrapper.AudioConverter
import com.demo.samplemp3converter.wrapper.AudioFormat
import com.demo.samplemp3converter.wrapper.callback.ConvertCallback
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import java.io.File
import java.util.*

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private lateinit var callback: ConvertCallback

    private var _loaded: SingleLiveEvent<Boolean> = SingleLiveEvent()

    val loaded: SingleLiveEvent<Boolean>
        get() = _loaded

    fun filePath(path: String) {

        val inFile = File(path)
        val outFileName =
            FILELOCATION +
                    "${Date().time}" +
                    ".${
                        AudioFormat.MP3.toString()
                            .toLowerCase(Locale.ROOT)
                    }"
        val outFile = File(Environment.getExternalStorageDirectory(), outFileName)
        callback = object : ConvertCallback {
            override fun onSuccess(convertedFile: File) {
                _loaded.value = true
            }

            override fun onFailure(error: Exception) {
                println("Exception:    $error")
            }
        }

        viewModelScope.async(Dispatchers.IO) {
            convertMp3(inFile, outFile, callback)
        }
    }


    private fun convertMp3(inFile: File, outFile: File, callback: ConvertCallback) {
        AudioConverter.with(getApplication())
            .setFile(inFile)
            .setFormat(AudioFormat.MP3)
            .setCallback(callback)
            .setOutFile(outFile)
            .convert()
    }


}