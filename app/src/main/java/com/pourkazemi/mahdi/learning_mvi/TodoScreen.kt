package com.pourkazemi.mahdi.learning_mvi

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pourkazemi.mahdi.learning_mvi.effect.TodoEffect
import com.pourkazemi.mahdi.learning_mvi.intent.TodoIntent
import kotlinx.coroutines.flow.collectLatest

@Composable
fun TodoScreen(
    modifier: Modifier = Modifier,
    viewModel: TodoViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    var newTask by remember { mutableStateOf("") }

    // Collect effects (e.g. Snackbar)
    val snackbarHostState = remember { SnackbarHostState() }
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Row {
                BasicTextField(
                    value = newTask,
                    onValueChange = { newTask = it },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = {
                    if (newTask.isNotBlank()) {
                        viewModel.processIntent(TodoIntent.AddTodo(newTask))
                        newTask = ""
                    }
                }) {
                    Text("Add")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (state.isLoading) {
                CircularProgressIndicator()
            } else {
                Column {
                    state.todos.forEach { todo ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(todo.title)
                            Button(onClick = {
                                viewModel.processIntent(TodoIntent.DeleteTodo(todo.id))
                            }) {
                                Text("Delete")
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}
