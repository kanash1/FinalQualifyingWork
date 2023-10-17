package ru.etu.graduatework.core.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import ru.etu.graduatework.core.navigation.NavigationCommand
import ru.etu.graduatework.core.utils.observeEvent

// базовый класс для всех активностей
abstract class BaseFragment<Binding : ViewBinding, ViewModel : BaseViewModel> :
    Fragment() {
    private var _binding: Binding? = null
    protected val binding get() = _binding!!
    protected abstract val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> Binding
    protected abstract val viewModel: ViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // создание и установка макета окна
        _binding = bindingInflater(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // обнуляется для предотвращения утечки памяти
        _binding = null
    }

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