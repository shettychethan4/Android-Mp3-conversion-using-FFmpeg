package com.demo.samplemp3converter.ui

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.demo.samplemp3converter.R
import com.demo.samplemp3converter.common.Constants.FILETYPE
import com.demo.samplemp3converter.common.Constants.REQUEST_CODE_LOCATION_PERMISSION
import com.demo.samplemp3converter.common.Constants.REQUEST_CODE_PICK_SOUNDFILE
import com.demo.samplemp3converter.common.Mp3Utility
import com.demo.samplemp3converter.databinding.ActivityMainBinding
import com.demo.samplemp3converter.ui.viewmodel.MainViewModel
import com.google.android.material.snackbar.Snackbar
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.io.File


class MainActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks, FileHandler {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this
        binding.choosefile = this
        viewModel =
            ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory(application)).get(
                MainViewModel::class.java
            )
        binding.viewmodel = viewModel
        requestPermission()

        viewModel.loaded.observe(this, {
            if (it) {
                Snackbar.make(binding.root, getString(R.string.converted), Snackbar.LENGTH_LONG)
                    .show()
            }
        })

    }


    private fun requestPermission() {
        if (Mp3Utility.hasStoragePermissions(this)) {
            return
        }
        EasyPermissions.requestPermissions(
            this,
            getString(R.string.mandatory_permission),
            REQUEST_CODE_LOCATION_PERMISSION,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {}

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        } else {
            requestPermission()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun chooseMp3() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = FILETYPE
        startActivityForResult(
            Intent.createChooser(
                intent,
                getString(R.string.select_audio_file_title)
            ), REQUEST_CODE_PICK_SOUNDFILE
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_PICK_SOUNDFILE && resultCode == RESULT_OK && data != null) {
            data.data?.let {
                val audioFileUri: Uri = it
                audioFileUri.let {
                    val myFile = File(audioFileUri.path)
                    val paths = myFile.absolutePath.split(":").toTypedArray()
                    val finalPath = paths.let {
                        paths[paths.size - 1]
                    }
                    finalPath.let { viewModel.filePath(finalPath) }
                }
            }
        }
    }


}