package ru.etu.graduatework.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import ru.etu.graduatework.R
import ru.etu.graduatework.core.extension.gone
import ru.etu.graduatework.core.extension.visible
import ru.etu.graduatework.core.ui.BaseFragment
import ru.etu.graduatework.core.utils.observeEvent
import ru.etu.graduatework.databinding.FragmentSearchBinding
import ru.etu.graduatework.ui.MainViewModel
import ru.etu.graduatework.ui.map.MapFragment

@AndroidEntryPoint
class SearchFragment : BaseFragment<FragmentSearchBinding, SearchViewModel>() {
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentSearchBinding
        get() = { layoutInflater, viewGroup, attachToParent ->
            FragmentSearchBinding.inflate(
                layoutInflater,
                viewGroup,
                attachToParent
            )
        }

    override val viewModel: SearchViewModel by viewModels()
    private val activityViewModel: MainViewModel by activityViewModels()

    val adapter = SearchAdapter()

    private val mapFragment: MapFragment by lazy {
        val fragmentManager = requireActivity().supportFragmentManager
        val navHostFragment = fragmentManager.findFragmentById(R.id.fragment_container)
        val fragments = navHostFragment!!.childFragmentManager.fragments
        fragments[fragments.lastIndex] as MapFragment
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initSearchView()
        initRecyclerView()

        observeSearchEvent()
        observeFailureEvent()

        binding.btnBack.setOnClickListener { viewModel.navigateBack() }

        binding.rvSearchResults.gone()
        binding.tvNoResults.visible()
    }

    private fun initRecyclerView() {
        binding.rvSearchResults.layoutManager = LinearLayoutManager(context)
        binding.rvSearchResults.adapter = adapter

        adapter.setOnItemClickListener(
            object : SearchAdapter.OnItemClickListener {
                override fun onItemClick(position: Int) {
                    val id = adapter.results[position].id
                    val target =
                        activityViewModel.attractionById.value?.get(id)?.point?.toMapPoint()
                    if (target != null) {
                        mapFragment.moveToTarget(target)
                        viewModel.goToMap(id)
                    }
                }
            }
        )
    }

    private fun initSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                viewModel.submitSearchQuery(
                    newText,
                    activityViewModel.attractionById.value!!.values
                )
                return false
            }
        })

        binding.searchView.requestFocus()
    }

    private fun observeSearchEvent() = viewModel.searchEvent.observeEvent(viewLifecycleOwner) {
        adapter.results = it.toList()
        binding.tvNoResults.gone()
        binding.rvSearchResults.visible()
    }

    private fun observeFailureEvent() = viewModel.failureEvent.observeEvent(viewLifecycleOwner) {
        binding.rvSearchResults.gone()
        binding.tvNoResults.visible()
    }
}