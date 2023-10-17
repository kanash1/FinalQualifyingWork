package ru.etu.graduatework.ui.route.navigation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import com.yandex.mapkit.RequestPoint
import com.yandex.mapkit.RequestPointType
import com.yandex.mapkit.geometry.Point
import ru.etu.graduatework.R
import ru.etu.graduatework.core.exception.Failure
import ru.etu.graduatework.core.extension.gone
import ru.etu.graduatework.core.extension.visible
import ru.etu.graduatework.core.ui.BaseFragment
import ru.etu.graduatework.databinding.FragmentRouteNavigationBinding
import ru.etu.graduatework.ui.MainViewModel
import ru.etu.graduatework.ui.map.MapFragment
import ru.etu.graduatework.ui.route.router.MasstransitRouter
import ru.etu.graduatework.ui.route.router.Router
import ru.etu.graduatework.ui.route.router.stop.Stop
import kotlin.math.abs

class RouteNavigationFragment :
    BaseFragment<FragmentRouteNavigationBinding, RouteNavigationViewModel>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentRouteNavigationBinding
        get() = { layoutInflater, viewGroup, attachToParent ->
            FragmentRouteNavigationBinding.inflate(layoutInflater, viewGroup, attachToParent)
        }
    override val viewModel: RouteNavigationViewModel by viewModels()
    private val activityViewModel: MainViewModel by activityViewModels()

    private val mapFragment: MapFragment by lazy {
        val fragmentManager = requireActivity().supportFragmentManager
        val navHostFragment = fragmentManager.findFragmentById(R.id.fragment_container)
        val fragments = navHostFragment!!.childFragmentManager.fragments
        fragments[fragments.lastIndex] as MapFragment
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.flLoad.visible()
        binding.rvDescription.gone()
        binding.btnZoomIn.gone()
        binding.btnZoomOut.gone()
        binding.btnLocation.gone()

        binding.toolbar.setNavigationOnClickListener { viewModel.navigateBack() }
        binding.btnZoomIn.setOnClickListener { mapFragment.zoomIn() }
        binding.btnZoomOut.setOnClickListener { mapFragment.zoomOut() }
        binding.btnLocation.setOnClickListener {
            val point = mapFragment.getUserLocation()
            if (point != null) mapFragment.moveToTarget(point)
        }
        createRoute()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mapFragment.setMapVisibility(false)
        mapFragment.routes.clear()
    }

    private fun createRoute() {
        val router = Router.create(
            activityViewModel.currentRoute.value!!.type,
            mapFragment.routes,
            requireContext()
        )

        val onSuccess = { timeAndDistance: String ->
            if (router is MasstransitRouter) {
                initRecyclerView(router.getStops())
                binding.rvDescription.visible()
                binding.rvDescription.visible()
            }
            binding.btnZoomIn.visible()
            binding.btnZoomOut.visible()
            binding.btnLocation.visible()
            binding.toolbar.title = timeAndDistance
            binding.flLoad.gone()
            mapFragment.setMapVisibility(true)
        }

        val onError = { viewModel.navigateBack() }

        try {
            val requestPoint = createListOfRequestPoints()
            router.request(requestPoint, onSuccess, onError)
        } catch (e : Failure) {
            Toast.makeText(context, R.string.cannot_get_location, Toast.LENGTH_LONG).show()
            viewModel.navigateBack()
        }
    }

    private fun initRecyclerView(stops: List<Stop>) {
        val adapter = DescriptionAdapter(stops)
        binding.rvDescription.attachSnapHelperWithListener(PagerSnapHelper(), {
            mapFragment.fitBounds(adapter.pointPairs[it])
        })
        binding.rvDescription.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvDescription.adapter = adapter
    }

    private fun createListOfRequestPoints(): List<RequestPoint> {
        val points = mutableListOf<RequestPoint>()
        val attractionById = activityViewModel.attractionById.value!!
        val pointIds = activityViewModel.currentRoute.value!!.pointIds

        if (activityViewModel.currentRoute.value!!.useLocation) {
            val point = mapFragment.getUserLocation()
            if (point == null) {
                throw Failure.Location
            } else {
                points.add(RequestPoint(point, RequestPointType.WAYPOINT, null))
            }
        }

        for ((index, id) in pointIds.withIndex()) {
            val point = attractionById[id]!!.point.toMapPoint()
            if (index == 0 || index == pointIds.lastIndex)
                points.add(RequestPoint(point, RequestPointType.WAYPOINT, null))
            else
                points.add(RequestPoint(point, RequestPointType.VIAPOINT, null))
        }
        return points
    }
}