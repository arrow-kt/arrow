package arrow.optics

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State

/**
 * Exposes the value of [this] through the optic.
 */
public fun <T, A> State<T>.optic(g: Getter<T, A>): State<A> = object : State<A> {
  override val value: A
    get() = g.get(this@optic.value)
}

/**
 * Exposes the value of [this] through the optic.
 * Any change made to [value] is reflected in the original [MutableState].
 */
public fun <T, A> MutableState<T>.optic(lens: Lens<T, A>): MutableState<A> = object : MutableState<A> {
  override var value: A
    get() = lens.get(this@optic.value)
    set(newValue) {
      this@optic.value = lens.set(this@optic.value, newValue)
    }

  override fun component1(): A = value
  override fun component2(): (A) -> Unit = { value = it }
}
