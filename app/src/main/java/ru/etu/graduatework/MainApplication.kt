package ru.etu.graduatework

import android.app.Application
import com.yandex.mapkit.MapKitFactory
import com.yandex.runtime.i18n.I18nManagerFactory
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MainApplication : Application() {
    companion object {
        private const val MAPKIT_API_KEY = "" // API key should be here
    }
    override fun onCreate() {
        super.onCreate()
        MapKitFactory.setApiKey(MAPKIT_API_KEY)
        MapKitFactory.setLocale("ru_RU")
    }
}