package com.demo.samplemp3converter.wrapper

import android.content.Context
import android.util.Log
import com.demo.samplemp3converter.wrapper.callback.ConvertCallback
import com.demo.samplemp3converter.wrapper.callback.LoadCallback
import com.github.hiteshsondhi88.libffmpeg.FFmpeg
import com.github.hiteshsondhi88.libffmpeg.FFmpegExecuteResponseHandler
import com.github.hiteshsondhi88.libffmpeg.FFmpegLoadBinaryResponseHandler
import java.io.File
import java.io.IOException

class AudioConverter() {
    private var context: Context? = null
    private var audioFile: File? = null
    private var outFile: File? = null
    private var format: AudioFormat? = null
    private var callback: ConvertCallback? = null

    constructor(context: Context) : this() {
        this.context = context
    }

    companion object {
        private var loaded = false

        fun with(context: Context): AudioConverter {
            return AudioConverter(context)
        }

        fun load(context: Context, callback: LoadCallback) {
            try {
                FFmpeg.getInstance(context).loadBinary(object : FFmpegLoadBinaryResponseHandler {
                    override fun onStart() {}
                    override fun onSuccess() {
                        loaded = true
                        callback.onSuccess()
                    }

                    override fun onFailure() {
                        loaded = false
                        callback.onFailure(Exception("Failed to loaded FFmpeg lib"))
                    }

                    override fun onFinish() {}
                })
            } catch (e: Exception) {
                loaded = false
                callback.onFailure(e)
            }
        }
    }

    fun isLoaded(): Boolean {
        return loaded
    }


    fun setFile(originalFile: File): AudioConverter {
        audioFile = originalFile
        return this
    }

    fun setOutFile(inFile: File): AudioConverter {
        outFile = inFile
        return this
    }

    fun setFormat(format: AudioFormat): AudioConverter {
        this.format = format
        return this
    }

    fun setCallback(callback: ConvertCallback): AudioConverter {
        this.callback = callback
        return this
    }

    fun convert() {


        if (!isLoaded()) {
            callback!!.onFailure(Exception("FFmpeg not loaded"))
            return
        }
        if (audioFile == null || !audioFile!!.exists()) {
            callback!!.onFailure(IOException("File not exists"))
            return
        }
        if (!audioFile!!.canRead()) {
            callback!!.onFailure(IOException("Can't read the file. Missing permission?"))
            return
        }


        val convertedFile: File = outFile!!

        val command = arrayOf(
            "-i",
            audioFile!!.path,
            "-ar",
            "44100",
            "-ac",
            "1",
            "-ab",
            "16k",
            convertedFile.path,
        )

        val command2 = arrayOf(
            "-i",
            audioFile!!.path,
            "-hide_banner"
        )

        val command3 = arrayOf(
            "-i",
            convertedFile.path,
            "-hide_banner"
        )



        try {

            FFmpeg.getInstance(context).execute(command, object : FFmpegExecuteResponseHandler {
                override fun onStart() {}
                override fun onProgress(message: String) {}
                override fun onSuccess(message: String) {
                    convertedFile.let { callback!!.onSuccess(it) }
                }

                override fun onFailure(message: String) {
                    System.out.println("Message " + message)
                    callback!!.onFailure(IOException(message))
                }

                override fun onFinish() {}
            })
        } catch (e: Exception) {
            callback!!.onFailure(e)
        }

        try {

            FFmpeg.getInstance(context).execute(command2, object : FFmpegExecuteResponseHandler {
                override fun onStart() {}
                override fun onProgress(message: String) {}
                override fun onSuccess(message: String) {}

                override fun onFailure(message: String) {

                    writeToFile(message, audioFile!!)
                }

                override fun onFinish() {}
            })
        } catch (e: Exception) {
            callback!!.onFailure(e)
        }
        try {

            FFmpeg.getInstance(context).execute(command3, object : FFmpegExecuteResponseHandler {
                override fun onStart() {}
                override fun onProgress(message: String) {}
                override fun onSuccess(message: String) {}

                override fun onFailure(message: String) {
                    writeToFile(message, convertedFile)
                }

                override fun onFinish() {}
            })
        } catch (e: Exception) {
            callback!!.onFailure(e)
        }

    }

    private fun writeToFile(data: String, fileName: File) {

        try {
            val file = File("$fileName.info.txt")
            file.writeText(data)
        } catch (e: Exception) {
            Log.e("Exception", "writeToFile: $e")
        }

    }

}