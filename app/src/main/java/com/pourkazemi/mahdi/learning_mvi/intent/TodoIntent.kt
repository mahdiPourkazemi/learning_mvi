package com.pourkazemi.mahdi.learning_mvi.intent

sealed class TodoIntent {
    object LoadTodos : TodoIntent()
    data class AddTodo(val title: String) : TodoIntent()
    data class DeleteTodo(val id: Int) : TodoIntent()
}
