package ru.etu.graduatework.core.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import ru.etu.graduatework.core.navigation.NavigationCommand
import ru.etu.graduatework.core.utils.observeEvent

// базовый класс для фрагментов в виде диалогового окна
abstract class BaseDialogFragment<ViewModel : BaseViewModel> :
    DialogFragment() {
    protected abstract val viewModel: ViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeNavigation()
    }

    // установка отслеживания события навигации
    private fun observeNavigation() =
        viewModel.navigationEvent.observeEvent(viewLifecycleOwner) {
        handleNavigation(it)
    }

    // обработка навигации
    private fun handleNavigation(command: NavigationCommand) {
        when (command) {
            is NavigationCommand.ToDirection -> {
                findNavController().navigate(command.directions)
            }
            is NavigationCommand.Back -> {
                findNavController().navigateUp()
            }
        }
    }
}