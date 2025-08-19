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

    private val _effect = MutableSharedFlow<Effect>(
        replay = 0,                // هیچ رویدادی ذخیره نمی‌شود
        extraBufferCapacity = 1     // اجازه بافر یک رویداد اضافی
    )
    val effect: SharedFlow<Effect> = _effect

    fun setState(reducer: State.() -> State) {
        _state.value = _state.value.reducer()
    }

    fun setEffect(builder: () -> Effect) {
        viewModelScope.launch {
            _effect.tryEmit(builder())
        }
    }

    abstract fun processIntent(intent: Intent)
}
