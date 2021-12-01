//[arrow-fx-stm](../../../index.md)/[arrow.fx.stm](../index.md)/[STM](index.md)/[isEmpty](is-empty.md)

# isEmpty

[common]\
open fun &lt;[A](is-empty.md)&gt; [TMVar](../-t-m-var/index.md)&lt;[A](is-empty.md)&gt;.[isEmpty](is-empty.md)(): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)

Check if a [TMVar](../-t-m-var/index.md) is empty. This function never retries.

import arrow.fx.stm.TMVar\
import arrow.fx.stm.atomically\
\
suspend fun main() {\
  //sampleStart\
  val tmvar = TMVar.empty&lt;Int&gt;()\
  val result = atomically {\
    tmvar.isEmpty()\
  }\
  //sampleEnd\
  println("Result $result")\
}<!--- KNIT example-stm-17.kt -->

Because the state of a transaction is constant there can never be a race condition between checking if a TMVar is empty and subsequent reads in the *same* transaction.

[common]\
open fun &lt;[A](is-empty.md)&gt; [TQueue](../-t-queue/index.md)&lt;[A](is-empty.md)&gt;.[isEmpty](is-empty.md)(): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)

Check if a [TQueue](../-t-queue/index.md) is empty.

import arrow.fx.stm.TQueue\
import arrow.fx.stm.atomically\
\
suspend fun main() {\
  //sampleStart\
  val tq = TQueue.new&lt;Int&gt;()\
  val result = atomically {\
    tq.isEmpty()\
  }\
  //sampleEnd\
  println("Result $result")\
}<!--- KNIT example-stm-35.kt -->

This function never retries.

This function has to access both [TVar](../-t-var/index.md)'s and thus may lead to increased contention, use sparingly.
