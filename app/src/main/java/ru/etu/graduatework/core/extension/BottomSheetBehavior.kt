package ru.etu.graduatework.core.extension

import com.google.android.material.bottomsheet.BottomSheetBehavior

fun BottomSheetBehavior<*>.hide() {
    changeState(BottomSheetBehavior.STATE_HIDDEN)
}

fun BottomSheetBehavior<*>.expand() {
    changeState(BottomSheetBehavior.STATE_EXPANDED)
}

fun BottomSheetBehavior<*>.collapse() {
    changeState(BottomSheetBehavior.STATE_COLLAPSED)
}

fun BottomSheetBehavior<*>.isHidden(): Boolean {
    return this.state == BottomSheetBehavior.STATE_HIDDEN
}

fun BottomSheetBehavior<*>.isExpanded(): Boolean {
    return this.state == BottomSheetBehavior.STATE_EXPANDED
}

fun BottomSheetBehavior<*>.isCollapsed(): Boolean {
    return this.state == BottomSheetBehavior.STATE_COLLAPSED
}

private fun BottomSheetBehavior<*>.changeState(state: Int) {
    if (this.state != state)
        this.state = state
}