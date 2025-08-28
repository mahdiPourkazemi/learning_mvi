package com.pourkazemi.mahdi.learning_mvi

import androidx.lifecycle.viewModelScope
import com.pourkazemi.mahdi.learning_mvi.effect.TodoEffect
import com.pourkazemi.mahdi.learning_mvi.intent.TodoIntent
import com.pourkazemi.mahdi.learning_mvi.model.Todo
import com.pourkazemi.mahdi.learning_mvi.repo.TodoRepository
import com.pourkazemi.mahdi.learning_mvi.viewState.TodoState
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class TodoViewModel(
    private val repository: TodoRepository // بعداً inject کن
) : BaseViewModel<TodoIntent, TodoState, TodoEffect>(TodoState()) {

    init {
        processIntent(TodoIntent.LoadTodos) // لود اولیه
    }
    override fun processIntent(intent: TodoIntent) {
        when (intent) {
            is TodoIntent.LoadTodos -> {
                setState { copy(isLoading = true) }
                repository.getAllTodos()
                    .onEach { todos ->
                        setState { copy(isLoading = false, todos = todos, error = "") }
                    }
                    .catch { e ->
                        setState { copy(isLoading = false, error = e.message ?: "Error loading todos") }
                    }
                    .launchIn(viewModelScope)
            }
            is TodoIntent.AddTodo -> {
                viewModelScope.launch {
                    try {
                        repository.addTodo(intent.title)
                        setEffect { TodoEffect.ShowMessage("Added: ${intent.title}") }
                    } catch (e: Exception) {
                        setState { copy(error = e.message ?: "Error adding todo") }
                    }
                }
            }
            is TodoIntent.DeleteTodo -> {
                viewModelScope.launch {
                    try {
                        repository.deleteTodo(intent.id)
                        setEffect { TodoEffect.ShowMessage("Deleted task") }
                    } catch (e: Exception) {
                        setState { copy(error = e.message ?: "Error deleting todo") }
                    }
                }
            }
        }
    }
}
