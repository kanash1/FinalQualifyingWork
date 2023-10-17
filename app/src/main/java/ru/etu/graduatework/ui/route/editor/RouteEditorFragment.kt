package ru.etu.graduatework.ui.route.editor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.button.MaterialButtonToggleGroup.OnButtonCheckedListener
import com.google.android.material.checkbox.MaterialCheckBox
import ru.etu.graduatework.R
import ru.etu.graduatework.core.extension.makeLinks
import ru.etu.graduatework.core.extension.setVisibility
import ru.etu.graduatework.core.extension.toInt
import ru.etu.graduatework.core.extension.visible
import ru.etu.graduatework.core.ui.BaseFragment
import ru.etu.graduatework.core.utils.observeEvent
import ru.etu.graduatework.databinding.FragmentRouteEditorBinding
import ru.etu.graduatework.domain.model.Route
import ru.etu.graduatework.ui.MainViewModel


class RouteEditorFragment : BaseFragment<FragmentRouteEditorBinding, RouteEditorViewModel>() {
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentRouteEditorBinding
        get() = { layoutInflater, viewGroup, attachToParent ->
            FragmentRouteEditorBinding.inflate(layoutInflater, viewGroup, attachToParent)
        }

    override val viewModel: RouteEditorViewModel by viewModels { RouteEditorViewModel.Factory() }
    private val activityViewModel: MainViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecyclerView()
//        initToggleButtons()
        initCheckBox()

        observeIsSignedIn()
        observeRoute()
        observeOptimizeEvent()
        observeFailure()
        observeSaveRouteEvent()

        binding.tvSignIn.makeLinks(
            Pair(resources.getString(R.string.offer_to_sign_in_link_part), View.OnClickListener {
                viewModel.goToSignIn()
            })
        )
        binding.btnCreateRoute.setOnClickListener { viewModel.goToRouteNavigation() }
        binding.btnOptimizeRoute.setOnClickListener { onOptimizeBtnClick() }
        binding.btnUploadRoute.setOnClickListener {
            binding.btnUploadRoute.isEnabled = false
            viewModel.goToSaveDialog(activityViewModel.currentRoute.value!!.name)
        }
        binding.btnUser.setOnClickListener { viewModel.goToAccountFragment() }
        binding.btnDownloadRoute.setOnClickListener {
            viewModel.goToLoadSavedRoutesFragment()
        }
        binding.toolbar.setNavigationOnClickListener { viewModel.navigateBack() }

        activityViewModel.isAuth()
    }

    override fun onResume() {
        super.onResume()
        initToggleButtons()
    }

    override fun onDestroyView() {
        binding.tgRouteType.clearOnButtonCheckedListeners()
        super.onDestroyView()
    }

    private fun onOptimizeBtnClick() {
        viewModel.goToOptimizationDialog()
    }

    private fun initCheckBox() {
        binding.cbUserLocation.checkedState =
            if (activityViewModel.currentRoute.value!!.useLocation)
                MaterialCheckBox.STATE_CHECKED
            else
                MaterialCheckBox.STATE_UNCHECKED

        binding.cbUserLocation.setOnCheckedChangeListener { _, isChecked ->
            activityViewModel.setUseLocation(isChecked)
        }
    }

    private fun initRecyclerView() {
        val adapter = RouteAdapter(activityViewModel)
        val touchHelper = ItemTouchHelper(ItemMoveCallback(adapter))
        touchHelper.attachToRecyclerView(binding.rvRoute)
        adapter.setOnRemoveClickListener(object : RouteAdapter.OnRemoveClickListener {
            override fun onRemoveClick(position: Int) {
                adapter.removePointAt(position)
            }
        })
        binding.rvRoute.layoutManager = LinearLayoutManager(requireContext())
        binding.rvRoute.adapter = adapter
    }

    private fun initToggleButtons() {
        binding.tgRouteType.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                when (checkedId) {
                    R.id.btn_pedestrian -> activityViewModel.setRouteType(Route.Type.PEDESTRIAN)
                    R.id.btn_bicycle -> activityViewModel.setRouteType(Route.Type.BICYCLE)
                    R.id.btn_masstransit -> activityViewModel.setRouteType(Route.Type.MASSTRANSIT)
                    R.id.btn_driving -> activityViewModel.setRouteType(Route.Type.DRIVING)
                }
            }
        }
        setToggleButtonCheck(activityViewModel.currentRoute.value?.type)
    }

    private fun setToggleButtonCheck(type: Route.Type?) {
        when (type) {
            Route.Type.BICYCLE -> binding.tgRouteType.check(R.id.btn_bicycle)
            Route.Type.MASSTRANSIT -> binding.tgRouteType.check(R.id.btn_masstransit)
            Route.Type.DRIVING -> binding.tgRouteType.check(R.id.btn_driving)
            else -> binding.tgRouteType.check(R.id.btn_pedestrian)
        }
    }

    private fun observeOptimizeEvent() =
        activityViewModel.optimizeEvent.observeEvent(viewLifecycleOwner) {
            if (it) (binding.rvRoute.adapter as RouteAdapter).updateAll()
            binding.rvRoute.setVisibility(it)
            binding.btnCreateRoute.isEnabled = it
            binding.btnOptimizeRoute.isEnabled = it
            binding.tgRouteType.isEnabled = it
            binding.btnUploadRoute.isEnabled = it
            binding.btnDownloadRoute.isEnabled = it
            binding.btnUser.isEnabled = it
        }

    private fun observeFailure() = activityViewModel.failureEvent.observeEvent(viewLifecycleOwner) {
        Toast.makeText(context, R.string.network_error_message, Toast.LENGTH_SHORT).show()
        binding.rvRoute.visible()
        binding.btnCreateRoute.isEnabled = true
        binding.btnOptimizeRoute.isEnabled = true
        binding.btnUploadRoute.isEnabled = true
        binding.tgRouteType.isEnabled = true
        binding.btnDownloadRoute.isEnabled = true
        binding.btnUser.isEnabled = true
    }

    private fun observeSaveRouteEvent() =
        activityViewModel.saveRouteEvent.observeEvent(viewLifecycleOwner) {
            binding.toolbar.title = activityViewModel.currentRoute.value?.name
            binding.btnUploadRoute.isEnabled = true
        }

    private fun observeIsSignedIn() = activityViewModel.isSignedIn.observe(viewLifecycleOwner) {
        binding.llPossibilities.setVisibility(it)
        binding.tvSignIn.setVisibility(!it)
    }

    private fun observeRoute() = activityViewModel.currentRoute.observe(viewLifecycleOwner) {
        binding.rvRoute.setVisibility(it.pointIds.isNotEmpty())
        binding.tvOrderOfPoints.setVisibility(it.pointIds.isEmpty())
        val count = it.pointIds.size + it.useLocation.toInt()
        binding.btnCreateRoute.isEnabled = count > 1
        binding.btnOptimizeRoute.isEnabled = count > 2
        binding.toolbar.title = activityViewModel.currentRoute.value?.name
    }
}