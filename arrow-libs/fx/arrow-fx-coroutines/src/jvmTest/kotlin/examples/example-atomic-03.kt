// This file was automatically generated from Atomic.kt by Knit tool. Do not edit.
package arrow.fx.coroutines.examples.exampleAtomic03

import arrow.fx.coroutines.*

data class Preference(val isEnabled: Boolean)
data class User(val name: String, val age: Int, val preference: Preference)
data class ViewState(val user: User)

suspend fun main(): Unit {
  //sampleStart
  val state: Atomic<ViewState> = Atomic(ViewState(User("Simon", 27, Preference(false))))
  val isEnabled: Atomic<Boolean> =
    state.lens(
      { it.user.preference.isEnabled },
      { state, isEnabled ->
        state.copy(
          user =
          state.user.copy(
            preference =
            state.user.preference.copy(isEnabled = isEnabled)
          )
        )
      }
    )
  isEnabled.set(true)
  println(state.get())
}
