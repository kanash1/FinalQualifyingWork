package ru.etu.graduatework.ui.map

import android.content.pm.PackageManager
import android.graphics.Color
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.BoundingBox
import com.yandex.mapkit.geometry.Geometry
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.layers.ObjectEvent
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.Cluster
import com.yandex.mapkit.map.ClusterListener
import com.yandex.mapkit.map.InputListener
import com.yandex.mapkit.map.MapObjectCollection
import com.yandex.mapkit.map.MapObjectTapListener
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.mapkit.user_location.UserLocationLayer
import com.yandex.mapkit.user_location.UserLocationObjectListener
import com.yandex.mapkit.user_location.UserLocationView
import com.yandex.runtime.image.ImageProvider
import ru.etu.graduatework.R
import ru.etu.graduatework.core.extension.setVisibility
import ru.etu.graduatework.core.ui.BaseFragment
import ru.etu.graduatework.core.utils.observeEvent
import ru.etu.graduatework.databinding.FragmentMapNewBinding
import ru.etu.graduatework.ui.MainViewModel
import ru.etu.graduatework.ui.location.GPSDialogFragment
import ru.etu.graduatework.ui.location.PermissionDialogFragment

// фрагмент, содержащий карту
class MapFragment :
    BaseFragment<FragmentMapNewBinding, MapViewModel>(),
    ClusterListener,
    UserLocationObjectListener {
    // координаты центра Спб
    companion object {
        private val TARGET_LOCATION =
            Point(59.945933, 30.320045)
    }
    override val bindingInflater: (
        LayoutInflater, ViewGroup?, Boolean
    ) -> FragmentMapNewBinding
        get() = { layoutInflater, viewGroup, attachToParent ->
            FragmentMapNewBinding.inflate(
                layoutInflater,
                viewGroup,
                attachToParent
            )
        }

    override val viewModel: MapViewModel by viewModels()

    private val activityViewModel: MainViewModel by activityViewModels()

    private val clusterizedPoints by lazy {
        binding.mapView.map
            .mapObjects.addClusterizedPlacemarkCollection(this)
    }

    private val placemarks = mutableSetOf<PlacemarkMapObject>()

    private var _routes: MapObjectCollection? = null
    val routes get() = _routes!!

    private var _userLocationLayer: UserLocationLayer? = null
    private val userLocationLayer get() = _userLocationLayer!!

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
        binding.mapView.onStart()
    }

    override fun onStop() {
        binding.mapView.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        initMap()
        observePointStatusChangedEvent()
        if (_userLocationLayer == null &&
            requireActivity().checkSelfPermission(
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            initUserLocation()
        }
    }
    // заполнени карты метками
    private fun initMap() {
        moveToTarget(TARGET_LOCATION, 12.0f)

        with(activityViewModel) {
            for (attraction in attractionById.value!!.values) {
                val resId = getPlacemarkResourseId(
                    attraction.id in currentRoute.value!!.pointIds
                )

                val mapObject = clusterizedPoints.addPlacemark(
                    attraction.point.toMapPoint(),
                    ImageProvider.fromResource(context, resId)
                )
                mapObject.userData = attraction.id
                placemarks.add(mapObject)
            }
        }
        clusterizedPoints.clusterPlacemarks(60.0, 10)

        _routes = binding.mapView.map.mapObjects.addCollection()

        clusterizedPoints.zIndex = 2f
        routes.zIndex = 1f
    }
    // изменение цвета метки в зависимоти от добавления или
    // удаление метки из маршрута
    fun updatePlacemarks() {
        with(activityViewModel) {
            for (attraction in attractionById.value!!.values) {
                try {
                    val resId = getPlacemarkResourseId(
                        attraction.id in currentRoute.value!!.pointIds
                    )
                    placemarks.first { mapObject ->
                        (mapObject.userData as Int) == attraction.id
                    }.setIcon(
                        ImageProvider.fromResource(context, resId)
                    )
                } catch (_ : Throwable) {}
            }
        }
    }
    // функция обратного вызова при добавлении или
    // удаление метки из маршрута
    private fun observePointStatusChangedEvent() =
        activityViewModel.pointStatusChangedEvent
            .observeEvent(viewLifecycleOwner) {
                try {
                    val resId = getPlacemarkResourseId(it.second)
                    placemarks.first { mapObject ->
                        (mapObject.userData as Int) == it.first
                    }.setIcon(
                        ImageProvider.fromResource(context, resId)
                    )
                } catch (_ : Throwable) {}
            }
    // получение изображения метки
    private fun getPlacemarkResourseId(inRoute : Boolean): Int {
        return if (inRoute) R.drawable.place_in_route
        else R.drawable.place
    }
    // инициализация слоя местоположения пользователя
    private fun initUserLocation() {
        val mapKit = MapKitFactory.getInstance()
        mapKit.resetLocationManagerToDefault()
        _userLocationLayer = mapKit
            .createUserLocationLayer(binding.mapView.mapWindow)
        userLocationLayer.isVisible = true
        userLocationLayer.isHeadingEnabled = true

        userLocationLayer.setObjectListener(this)
    }
    fun addMapInputListener(listener: InputListener) {
        binding.mapView.map.addInputListener(listener)
    }

    fun removeMapInputListener(listener: InputListener) {
        binding.mapView.map.removeInputListener(listener)
    }

    fun addMapObjectTapListener(listener: MapObjectTapListener) {
        clusterizedPoints.addTapListener(listener)
    }

    fun removeMapObjectTapListener(listener: MapObjectTapListener) {
        clusterizedPoints.removeTapListener(listener)
    }

    fun setMapVisibility(visible: Boolean) {
        binding.mapView.setVisibility(visible)
    }

    fun moveToTarget(target: Point, zoom: Float = 16f) {
        binding.mapView.map.move(
            CameraPosition(target, zoom, 0.0f, 0.0f),
            Animation(Animation.Type.SMOOTH, 0.0f),
            null
        )
    }

    fun fitBounds(points: Pair<Point, Point>) {
        val boundingBox = BoundingBox(points.first, points.second)
        val geometry = Geometry.fromBoundingBox(boundingBox)
        val position = binding.mapView.map
            .cameraPosition(geometry, null, null, null)
        with(position) {
            val newPosition = CameraPosition(
                target, zoom - 0.8f, azimuth, tilt
            )
            binding.mapView.map.move(
                newPosition,
                Animation(Animation.Type.SMOOTH, 0f),
                null
            )
        }
    }

    fun zoomIn() {
        val cameraPosition = binding.mapView.map.cameraPosition
        binding.mapView.map.move(
            CameraPosition(
                cameraPosition.target, cameraPosition.zoom + 1, 0.0f, 0.0f
            ),
            Animation(Animation.Type.SMOOTH, 0.0f),
            null
        )
    }

    fun zoomOut() {
        val cameraPosition = binding.mapView.map.cameraPosition
        binding.mapView.map.move(
            CameraPosition(
                cameraPosition.target, cameraPosition.zoom - 1, 0.0f, 0.0f
            ),
            Animation(Animation.Type.SMOOTH, 0.0f),
            null
        )
    }

    fun getUserLocation(): Point? {
        requestLocationPermission()
        if (_userLocationLayer != null && userLocationLayer.isValid) {
            return userLocationLayer.cameraPosition()?.target
        }
        return null
    }

    override fun onClusterAdded(cluster: Cluster) {
        cluster.appearance.setIcon(
            TextImageProvider(
                cluster.size.toString(),
                requireContext()
            )
        )
    }

    override fun onObjectAdded(
        userLocationView: UserLocationView
    ) {
        userLocationView.accuracyCircle.fillColor =
            Color.BLUE and 0x99ffffff.toInt()
    }

    override fun onObjectRemoved(
        userLocationView: UserLocationView
    ) {}

    override fun onObjectUpdated(
        userLocationView: UserLocationView,
        objectEvent: ObjectEvent
    ) {}

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
                statusCheck()
        }

    fun launchLocationPermission() {
        requestPermissionLauncher.launch(
            android.Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    private fun requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                val isPermanentlyDeclined =
                    ActivityCompat.shouldShowRequestPermissionRationale(
                        requireActivity(),
                        android.Manifest.permission.ACCESS_FINE_LOCATION
                    )
                PermissionDialogFragment.newInstance(isPermanentlyDeclined)
                    .show(childFragmentManager, null)
            } else {
                statusCheck()
            }
        } else {
            val isPermanentlyDeclined =
                ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(),
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                )
            if (isPermanentlyDeclined)
                PermissionDialogFragment.newInstance(true)
                    .show(childFragmentManager, null)
            else
                launchLocationPermission()
        }

    }

    private fun statusCheck() {
        val manager: LocationManager = requireActivity()
            .getSystemService(
                AppCompatActivity.LOCATION_SERVICE
            ) as LocationManager
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            GPSDialogFragment().show(childFragmentManager, null)
            if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                && _userLocationLayer == null)
                initUserLocation()
        } else if (_userLocationLayer == null) {
            initUserLocation()
        }
    }
}