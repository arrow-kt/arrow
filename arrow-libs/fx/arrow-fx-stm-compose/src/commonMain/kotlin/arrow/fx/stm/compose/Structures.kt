package arrow.fx.stm.compose

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import kotlin.jvm.JvmInline

@JvmInline
public value class TVar<A>(internal val variable: MutableState<A>) {
  public fun unsafeRead(): A = variable.value

  public companion object {
    public fun <A> new(value: A): TVar<A> = TVar(mutableStateOf(value))
  }
}
