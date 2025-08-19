package com.pourkazemi.mahdi.learning_mvi

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pourkazemi.mahdi.learning_mvi.effect.TodoEffect
import com.pourkazemi.mahdi.learning_mvi.intent.TodoIntent
import kotlinx.coroutines.flow.collectLatest
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import kotlinx.coroutines.launch
import androidx.compose.ui.tooling.preview.Preview
import com.pourkazemi.mahdi.learning_mvi.model.Todo
import com.pourkazemi.mahdi.learning_mvi.viewState.TodoState
import com.pourkazemi.mahdi.learning_mvi.viewState.theme.Learning_mviTheme


@Composable
fun TodoScreen(
    modifier: Modifier = Modifier,
    viewModel: TodoViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // جمع‌آوری Effectها
    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is TodoEffect.ShowMessage -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        TodoScreenContent(
            modifier = modifier.padding(padding),
            state = state,
            onAddTask = { title ->
                if (title.isNotBlank()) {
                    viewModel.processIntent(TodoIntent.AddTodo(title))
                } else {
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("Task cannot be empty")
                    }
                }
            },
            onDeleteTask = { id ->
                viewModel.processIntent(TodoIntent.DeleteTodo(id))
            }
        )
    }
}

// Composable اصلی برای محتوا (Slot API)
@Composable
private fun TodoScreenContent(
    modifier: Modifier = Modifier,
    state: TodoState,
    onAddTask: (String) -> Unit,
    onDeleteTask: (Int) -> Unit
) {
    var newTask by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // ورودی برای اضافه کردن ToDo
        TaskInputField(
            value = newTask,
            onValueChange = { newTask = it },
            onAddClick = {
                onAddTask(newTask)
                newTask = ""
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // نمایش وضعیت‌ها (لودینگ، خطا، لیست)
        TaskStateContent(
            state = state,
            onDeleteTask = onDeleteTask
        )
    }
}

// Composable برای TextField ورودی
@Composable
private fun TaskInputField(
    value: String,
    onValueChange: (String) -> Unit,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text("Enter new task") },
        modifier = modifier.fillMaxWidth(),
        trailingIcon = {
            IconButton(onClick = onAddClick) {
                Icon(Icons.Default.Add, contentDescription = "Add Task")
            }
        },
        shape = RoundedCornerShape(8.dp)
    )
}

// Composable برای نمایش وضعیت‌ها (لودینگ، خطا، لیست)
@Composable
private fun TaskStateContent(
    state: TodoState,
    onDeleteTask: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    when {
        state.isLoading -> {
            CircularProgressIndicator(
                modifier = modifier.fillMaxSize()
            )
        }
        state.error.isNotEmpty() -> {
            Text(
                text = state.error,
                color = Color.Red,
                textAlign = TextAlign.Center,
                modifier = modifier.fillMaxWidth()
            )
        }
        state.todos.isEmpty() -> {
            Text(
                text = "No tasks yet. Add one!",
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = modifier.fillMaxWidth()
            )
        }
        else -> {
            TaskList(
                todos = state.todos,
                onDeleteTask = onDeleteTask,
                modifier = modifier.fillMaxWidth()
            )
        }
    }
}

// Composable برای لیست ToDoها
@Composable
private fun TaskList(
    todos: List<Todo>,
    onDeleteTask: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier) {
        items(todos) { todo ->
            TaskItem(
                todo = todo,
                onDeleteClick = { onDeleteTask(todo.id) }
            )
            Divider()
        }
    }
}

// Composable برای هر آیتم ToDo
@Composable
private fun TaskItem(
    todo: Todo,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = todo.title,
            modifier = Modifier.weight(1f)
        )
        IconButton(onClick = onDeleteClick) {
            Icon(Icons.Default.Delete, contentDescription = "Delete Task")
        }
    }
}

// Preview برای حالت‌های مختلف
@Preview(showBackground = true)
@Composable
fun TodoScreenPreview_Loading() {
    Learning_mviTheme {
        TodoScreenContent(
            state = TodoState(isLoading = true),
            onAddTask = {},
            onDeleteTask = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TodoScreenPreview_Error() {
    Learning_mviTheme {
        TodoScreenContent(
            state = TodoState(error = "Failed to load tasks"),
            onAddTask = {},
            onDeleteTask = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TodoScreenPreview_Empty() {
    Learning_mviTheme {
        TodoScreenContent(
            state = TodoState(todos = emptyList()),
            onAddTask = {},
            onDeleteTask = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TodoScreenPreview_WithTasks() {
    Learning_mviTheme {
        TodoScreenContent(
            state = TodoState(todos = listOf(
                Todo(1, "Task 1"),
                Todo(2, "Task 2")
            )),
            onAddTask = {},
            onDeleteTask = {}
        )
    }
}