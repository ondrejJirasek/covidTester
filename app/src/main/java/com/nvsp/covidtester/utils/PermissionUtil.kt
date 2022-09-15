package com.kotlin.education.android.easytodo.utils

import android.Manifest
import android.Manifest.permission
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import androidx.core.app.ActivityCompat
import android.app.Activity
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat


/**
 * Utilita pro získávání a kontrolu oprávnění. Pro různá oprávnění jsem si udělal různé metody.
 * Jen pomocník, ať to není v aktivitě.
 * Od verze Androidu 6.0 nestačí mít oprávnění jen v manifestu. Je nutné si ho i vyžádat při běhu aplikace.
 * https://developer.android.com/training/permissions/requesting
 * https://developer.android.com/training/permissions/usage-notes
 */
class PermissionUtil {

    companion object {

        fun requestWriteStoragePermission(context: AppCompatActivity, requestCode: Int)
                = requestPermissions(context, requestCode, Manifest.permission.WRITE_EXTERNAL_STORAGE)

        fun checkWriteStoragePermission(context: AppCompatActivity): Boolean
                = checkPermissions(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)

        fun requestCameraStoragePermission(context: AppCompatActivity, requestCode: Int)
                = requestPermissions(context, requestCode, Manifest.permission.CAMERA)

        fun checkCameraStoragePermission(context: AppCompatActivity): Boolean
                = checkPermissions(context, Manifest.permission.CAMERA)

        fun wasPermissionGranted(grantResults: IntArray): Boolean
                = grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED

        private fun checkPermissions(context: AppCompatActivity, permission: String): Boolean {
            return ContextCompat.checkSelfPermission(
                context,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        }


        private fun requestPermissions(context: Activity, requestCode: Int, permission: String) {
            ActivityCompat.requestPermissions(
                context,
                arrayOf(permission),
                requestCode
            )
        }


    }

}