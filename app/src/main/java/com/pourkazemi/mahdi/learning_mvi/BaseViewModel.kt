package com.pourkazemi.mahdi.learning_mvi

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import androidx.lifecycle.viewModelScope

abstract class BaseViewModel<Intent, State, Effect>(
    initialState: State
) : ViewModel() {

    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<State> = _state.asStateFlow()

    private val _effect = Channel<Effect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    fun setState(reducer: State.() -> State) {
        _state.value = _state.value.reducer()
    }

    fun setEffect(builder: () -> Effect) {
        viewModelScope.launch {
            _effect.send(builder())
        }
    }

    abstract fun processIntent(intent: Intent)
}
