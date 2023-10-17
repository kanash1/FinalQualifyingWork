package ru.etu.graduatework.ui

import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.yandex.mapkit.MapKitFactory
import dagger.hilt.android.AndroidEntryPoint
import ru.etu.graduatework.core.ui.BaseActivity
import ru.etu.graduatework.databinding.ActivityMainBinding
import ru.etu.graduatework.ui.location.GPSDialogFragment
import ru.etu.graduatework.ui.location.PermissionDialogFragment


@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {
    override val bindingInflater: (LayoutInflater) -> ActivityMainBinding
        get() = { layoutInflater -> ActivityMainBinding.inflate(layoutInflater) }

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapKitFactory.initialize(this)
        viewModel.loadLocalRoute()
    }

    override fun onPause() {
        viewModel.saveLocalRoute()
        super.onPause()
    }
}