package ru.etu.graduatework.core.extension

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.Settings

fun Activity.openAppSettings() {
    Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", packageName, null)
    ).also(::startActivity)
}

fun Activity.openLocationSettings() {
    Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).also(::startActivity)
}