package com.pourkazemi.mahdi.learning_mvi

import com.pourkazemi.mahdi.learning_mvi.effect.TodoEffect
import com.pourkazemi.mahdi.learning_mvi.intent.TodoIntent
import com.pourkazemi.mahdi.learning_mvi.model.Todo
import com.pourkazemi.mahdi.learning_mvi.viewState.TodoState

class TodoViewModel : BaseViewModel<TodoIntent, TodoState, TodoEffect>(TodoState()) {

    private var nextId = 1

    override fun processIntent(intent: TodoIntent) {
        when (intent) {
            is TodoIntent.LoadTodos -> {
                setState { copy(isLoading = true) }
                setState { copy(isLoading = false, todos = emptyList()) }
            }
            is TodoIntent.AddTodo -> {
                val newTodo = Todo(
                    id = nextId++,
                    title = intent.title
                )
                setState { copy(todos = todos + newTodo) }
                setEffect { TodoEffect.ShowMessage("Added: ${newTodo.title}") }
            }
            is TodoIntent.DeleteTodo -> {
                setState { copy(todos = todos.filterNot { it.id == intent.id }) }
                setEffect { TodoEffect.ShowMessage("Deleted task") }
            }
        }
    }
}
