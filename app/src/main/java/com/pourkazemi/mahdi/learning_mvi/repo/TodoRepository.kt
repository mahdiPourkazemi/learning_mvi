package com.pourkazemi.mahdi.learning_mvi.repo


import com.pourkazemi.mahdi.learning_mvi.data.AppDatabase
import com.pourkazemi.mahdi.learning_mvi.data.TodoDao
import com.pourkazemi.mahdi.learning_mvi.model.Todo
import com.pourkazemi.mahdi.learning_mvi.data.TodoEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TodoRepository(private val todoDao: TodoDao) {

    fun getAllTodos(): Flow<List<Todo>> {
        return todoDao.getAllTodos().map { entities ->
            entities.map { Todo(id = it.id, title = it.title) } // Mapper: Entity to Model
        }
    }

    suspend fun addTodo(title: String) {
        val entity = TodoEntity(title = title)
        todoDao.insertTodo(entity)
    }

    suspend fun deleteTodo(id: Int) {
        todoDao.deleteTodoById(id)
    }
}