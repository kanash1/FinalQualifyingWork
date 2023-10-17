package ru.etu.graduatework.ui.interaction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.InputListener
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.map.MapObject
import com.yandex.mapkit.map.MapObjectTapListener
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.runtime.image.ImageProvider
import ru.etu.graduatework.R
import ru.etu.graduatework.core.extension.collapse
import ru.etu.graduatework.core.extension.dpToPx
import ru.etu.graduatework.core.extension.hide
import ru.etu.graduatework.core.ui.BaseFragment
import ru.etu.graduatework.databinding.FragmentInteractionWithMapBinding
import ru.etu.graduatework.ui.MainViewModel
import ru.etu.graduatework.ui.attraction.AttractionFragment
import ru.etu.graduatework.ui.map.MapFragment

class InteractionWithMapFragment :
    BaseFragment<FragmentInteractionWithMapBinding, InteractionWithMapViewModel>(), InputListener,
    MapObjectTapListener {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentInteractionWithMapBinding
        get() = { layoutInflater, viewGroup, attachToParent ->
            FragmentInteractionWithMapBinding.inflate(layoutInflater, viewGroup, attachToParent)
        }
    override val viewModel: InteractionWithMapViewModel by viewModels()
    private val activityViewModel: MainViewModel by activityViewModels()

    private var _behavior: BottomSheetBehavior<*>? = null
    private val behavior get() = _behavior!!

    private val args: InteractionWithMapFragmentArgs by navArgs()

    private val bottomSheetCallback = object : BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            if (newState >= BottomSheetBehavior.STATE_EXPANDED && newState <= BottomSheetBehavior.STATE_HALF_EXPANDED) {
                viewModel.setBottomSheetState(newState)
            }
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {}
    }

    private var onBackPressedCallback: OnBackPressedCallback? = null

    private val mapFragment: MapFragment by lazy {
        val fragmentManager = requireActivity().supportFragmentManager
        val navHostFragment = fragmentManager.findFragmentById(R.id.fragment_container)
        val fragments = navHostFragment!!.childFragmentManager.fragments
        fragments[fragments.lastIndex] as MapFragment
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        mapFragment.addMapInputListener(this)
        mapFragment.addMapObjectTapListener(this)
        mapFragment.setMapVisibility(true)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _behavior = BottomSheetBehavior.from(binding.bottomSheetContainer)

        binding.btnEditRoute.setOnClickListener { viewModel.goToRouteEditor() }
        binding.tvSearch.setOnClickListener { viewModel.goToSearch() }
        binding.btnZoomIn.setOnClickListener { mapFragment.zoomIn() }
        binding.btnZoomOut.setOnClickListener { mapFragment.zoomOut() }
        binding.btnLocation.setOnClickListener {
            val point = mapFragment.getUserLocation()
            if (point != null) mapFragment.moveToTarget(point)
        }

        behavior.state = viewModel.bottomSheetState.value!!
        behavior.addBottomSheetCallback(bottomSheetCallback)

        observeOnBackPressedCallback()

        moveMap()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        onBackPressedCallback?.isEnabled = false
        behavior.removeBottomSheetCallback(bottomSheetCallback)
        _behavior = null
        mapFragment.setMapVisibility(false)
        mapFragment.removeMapInputListener(this)
        mapFragment.removeMapObjectTapListener(this)
    }

    private fun moveMap() {
        val id = args.attractionId
        if (id >= 0) {
            createAttractionFragment(id)
        }
    }

    private fun observeOnBackPressedCallback() {
        onBackPressedCallback = requireActivity().onBackPressedDispatcher.addCallback {
            if (behavior.state != BottomSheetBehavior.STATE_HIDDEN) {
                behavior.hide()
            } else {
                isEnabled = false
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }
        onBackPressedCallback?.isEnabled = true
    }

    override fun onMapTap(map: Map, point: Point) {
        behavior.hide()
    }

    override fun onMapLongTap(map: Map, point: Point) {}

    override fun onMapObjectTap(mapObject: MapObject, point: Point): Boolean {
        val id = mapObject.userData as Int
        val currentFragment = childFragmentManager.findFragmentById(R.id.bottom_sheet_container)
        if (currentFragment !is AttractionFragment || id != currentFragment.getAttractionId()) {
            createAttractionFragment(id)
        } else if (id == currentFragment.getAttractionId()) {
            behavior.collapse()
        }
        return true
    }

    private fun createAttractionFragment(id: Int) {
        val newFragment = AttractionFragment.newInstance(id)
        transitBottomSheetToFragment(newFragment)
        behavior.peekHeight = 136f.dpToPx()
        behavior.collapse()
    }

    private fun transitBottomSheetToFragment(fragment: Fragment) {
        childFragmentManager.beginTransaction().replace(R.id.bottom_sheet_container, fragment)
            .commit()
    }
}