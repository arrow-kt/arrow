package arrow.optics

import androidx.compose.runtime.MutableState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

/**
 * Modifies the value in this [MutableState]
 * by applying the function [block] to the current value.
 */
public inline fun <T> MutableState<T>.update(block: (T) -> T) {
  value = block(value)
}

/**
 * Modifies the value in this [MutableState]
 * by performing the operations in the [Copy] [block].
 */
public fun <T> MutableState<T>.updateCopy(block: Copy<T>.() -> Unit) {
  update { it.copy(block) }
}

/**
 * Updates the value in this [MutableStateFlow]
 * by performing the operations in the [Copy] [block].
 */
public fun <T> MutableStateFlow<T>.updateCopy(block: Copy<T>.() -> Unit) {
  update { it.copy(block) }
}
