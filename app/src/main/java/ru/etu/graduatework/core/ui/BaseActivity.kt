package ru.etu.graduatework.core.ui

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding

// базовый класс для всех активностей
abstract class BaseActivity<Binding: ViewBinding>: AppCompatActivity() {
    private var _binding: Binding? = null
    protected val binding get() = _binding!!
    protected abstract val bindingInflater: (LayoutInflater) -> Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = bindingInflater(layoutInflater)
        setContentView(binding.root)
    }
}