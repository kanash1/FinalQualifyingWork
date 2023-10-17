package ru.etu.graduatework.ui.splash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import ru.etu.graduatework.core.ui.BaseFragment
import ru.etu.graduatework.core.utils.observeEvent
import ru.etu.graduatework.databinding.FragmentSplashBinding
import ru.etu.graduatework.ui.MainViewModel

class SplashFragment : BaseFragment<FragmentSplashBinding, SplashViewModel>() {
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentSplashBinding
        get() = { layoutInflater, viewGroup, attachToParent ->
            FragmentSplashBinding.inflate(
                layoutInflater,
                viewGroup,
                attachToParent
            )
        }

    override val viewModel: SplashViewModel by viewModels()
    private val activityViewModel: MainViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activityViewModel.launchMainScreenEvent.observeEvent(viewLifecycleOwner) {
            viewModel.goToMap()
        }
        activityViewModel.failureEvent.observeEvent(viewLifecycleOwner) {
            viewModel.goToError()
        }
        activityViewModel.loadAttractions()
    }
}