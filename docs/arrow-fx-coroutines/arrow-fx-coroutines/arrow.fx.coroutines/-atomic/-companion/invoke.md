//[arrow-fx-coroutines](../../../../index.md)/[arrow.fx.coroutines](../../index.md)/[Atomic](../index.md)/[Companion](index.md)/[invoke](invoke.md)

# invoke

[common]\
suspend operator fun &lt;[A](invoke.md)&gt; [invoke](invoke.md)(a: [A](invoke.md)): [Atomic](../index.md)&lt;[A](invoke.md)&gt;

Creates an [AtomicRef](../../../../../arrow-continuations/arrow-continuations/arrow.continuations.generic/-atomic-ref/index.md) with an initial value of [A](invoke.md).

Data type on top of atomic to use in parallel functions.

import arrow.fx.coroutines.*\
\
suspend fun main() {\
  val count = Atomic(0)\
  (0 until 20_000).parTraverse {\
    count.update(Int::inc)\
  }\
  println(count.get())\
}<!--- KNIT example-atomic-04.kt -->
