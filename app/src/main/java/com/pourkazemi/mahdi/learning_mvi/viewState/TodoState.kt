package com.pourkazemi.mahdi.learning_mvi.viewState

import com.pourkazemi.mahdi.learning_mvi.model.Todo

data class TodoState(
    val isLoading: Boolean = false,
    val todos: List<Todo> = emptyList(),
    val error: String = ""
)
