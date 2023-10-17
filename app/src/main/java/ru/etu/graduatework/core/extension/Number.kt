package ru.etu.graduatework.core.extension

import android.content.res.Resources
import android.util.TypedValue

// преобразование аппаратно-независимых пикселей
// в реальные пиксели
fun Number.dpToPx() = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP,
    this.toFloat(),
    Resources.getSystem().displayMetrics).toInt()

// преобразование масштабируемых пикселей
// в реальные пиксели
fun Number.spToPx() = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_SP,
    this.toFloat(),
    Resources.getSystem().displayMetrics).toInt()

// преобразование логического типа
// в целочисленный
fun Boolean.toInt(): Int {
    return if (this) 1 else 0
}