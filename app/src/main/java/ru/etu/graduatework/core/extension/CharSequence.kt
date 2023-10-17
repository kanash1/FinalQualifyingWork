package ru.etu.graduatework.core.extension

import android.util.Patterns
import java.util.regex.Pattern

private const val USERNAME_PATTERN = "^[a-zA-Z\\d.,'!&*_-]{5,32}$"
private const val PASSWORD_PATTERN = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])[\\da-zA-Z.,'!&*_-]{5,32}$"

fun CharSequence?.isValidEmail() =
    !isNullOrEmpty() && Patterns.EMAIL_ADDRESS.matcher(this).matches()

fun CharSequence?.isValidUsername() =
    !isNullOrEmpty() && Pattern.compile(USERNAME_PATTERN).matcher(this).matches()

fun CharSequence?.isValidPassword() =
    !isNullOrEmpty() && Pattern.compile(PASSWORD_PATTERN).matcher(this).matches()
