package com.pourkazemi.mahdi.learning_mvi.effect

sealed class TodoEffect {
    data class ShowMessage(val message: String) : TodoEffect()
}
