package ru.etu.graduatework.core.utils

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

// класс события, содержимое которого
// можно получить единажды
class Event<T>(value: T) {
    private var _value: T? = value
    fun get(): T? = _value.also { _value = null }
}

typealias MutableLiveEvent<T> = MutableLiveData<Event<T>>
typealias LiveEvent<T> = LiveData<Event<T>>
typealias EventListener<T> = (T) -> Unit

fun <T> MutableLiveEvent<T>.publishEvent(value: T) {
    this.value = Event(value)
}

// расширение для установки отслеживания события
// для контролирование того, что не будет обращения к null значению
fun <T> LiveEvent<T>.observeEvent(
    lifecycleOwner: LifecycleOwner,
    listener: EventListener<T>
) {
    this.observe(lifecycleOwner) {
        it?.get()?.let { value ->
            listener(value)
        }
    }
}

typealias MutableUnitLiveEvent = MutableLiveEvent<Unit>
typealias UnitLiveEvent = LiveEvent<Unit>
typealias UnitEventListener = () -> Unit

fun MutableUnitLiveEvent.publishEvent() = publishEvent(Unit)
fun UnitLiveEvent.observeEvent(
    lifecycleOwner: LifecycleOwner,
    listener: UnitEventListener)
{
    observeEvent(lifecycleOwner) { _ ->
        listener()
    }
}