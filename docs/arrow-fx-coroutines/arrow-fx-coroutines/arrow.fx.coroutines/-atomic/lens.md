//[arrow-fx-coroutines](../../../index.md)/[arrow.fx.coroutines](../index.md)/[Atomic](index.md)/[lens](lens.md)

# lens

[common]\
open fun &lt;[B](lens.md)&gt; [lens](lens.md)(get: ([A](index.md)) -&gt; [B](lens.md), set: ([A](index.md), [B](lens.md)) -&gt; [A](index.md)): [Atomic](index.md)&lt;[B](lens.md)&gt;

Creates an [AtomicRef](../../../../arrow-continuations/arrow-continuations/arrow.continuations.generic/-atomic-ref/index.md) for [B](lens.md) based on provided a [get](lens.md) and [set](lens.md) operation.

This is useful when you have an [AtomicRef](../../../../arrow-continuations/arrow-continuations/arrow.continuations.generic/-atomic-ref/index.md) of a data class and need to work with with certain properties individually, or want to hide parts of your domain from a dependency.

import arrow.fx.coroutines.*\
\
data class Preference(val isEnabled: Boolean)\
data class User(val name: String, val age: Int, val preference: Preference)\
data class ViewState(val user: User)\
\
suspend fun main(): Unit {\
  //sampleStart\
  val state: Atomic&lt;ViewState&gt; = Atomic(ViewState(User("Simon", 27, Preference(false))))\
  val isEnabled: Atomic&lt;Boolean&gt; =\
    state.lens(\
      { it.user.preference.isEnabled },\
      { state, isEnabled -&gt;\
        state.copy(\
          user =\
          state.user.copy(\
            preference =\
            state.user.preference.copy(isEnabled = isEnabled)\
          )\
        )\
      }\
    )\
  isEnabled.set(true)\
  println(state.get())\
}<!--- KNIT example-atomic-03.kt -->
