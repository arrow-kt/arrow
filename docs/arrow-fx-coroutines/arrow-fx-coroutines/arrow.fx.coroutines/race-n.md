//[arrow-fx-coroutines](../../index.md)/[arrow.fx.coroutines](index.md)/[raceN](race-n.md)

# raceN

[common]\
inline suspend fun &lt;[A](race-n.md), [B](race-n.md)&gt; [raceN](race-n.md)(crossinline fa: suspend CoroutineScope.() -&gt; [A](race-n.md), crossinline fb: suspend CoroutineScope.() -&gt; [B](race-n.md)): [Either](../../../arrow-core/arrow-core/arrow.core/-either/index.md)&lt;[A](race-n.md), [B](race-n.md)&gt;

Races the participants [fa](race-n.md), [fb](race-n.md) in parallel on the Dispatchers.Default. The winner of the race cancels the other participants. Cancelling the operation cancels all participants. An uncancellable participant will back-pressure the result of [raceN](race-n.md).

import arrow.core.Either\
import arrow.fx.coroutines.*\
import kotlinx.coroutines.suspendCancellableCoroutine\
\
suspend fun main(): Unit {\
  suspend fun loser(): Int =\
    guaranteeCase({ never() }) { exitCase -&gt;\
      println("I can never win the race. Finished with $exitCase.")\
    }\
\
  val winner = raceN({ loser() }, { 5 })\
\
  val res = when(winner) {\
    is Either.Left -&gt; "Never always loses race"\
    is Either.Right -&gt; "Race was won with ${winner.value}"\
  }\
  //sampleEnd\
  println(res)\
}<!--- KNIT example-race2-01.kt -->

#### Return

either [Either.Left](../../../arrow-core/arrow-core/arrow.core/-either/-left/index.md) if [fa](race-n.md) won the race, or [Either.Right](../../../arrow-core/arrow-core/arrow.core/-either/-right/index.md) if [fb](race-n.md) won the race.

## See also

common

| | |
|---|---|
| racePair | for a version that does not automatically cancel the loser. |
| [raceN](race-n.md) | for the same function that can race on any [CoroutineContext](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.coroutines/-coroutine-context/index.html). |

## Parameters

common

| | |
|---|---|
| fa | task to participate in the race |
| fb | task to participate in the race |

[common]\
inline suspend fun &lt;[A](race-n.md), [B](race-n.md)&gt; [raceN](race-n.md)(ctx: [CoroutineContext](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.coroutines/-coroutine-context/index.html) = EmptyCoroutineContext, crossinline fa: suspend CoroutineScope.() -&gt; [A](race-n.md), crossinline fb: suspend CoroutineScope.() -&gt; [B](race-n.md)): [Either](../../../arrow-core/arrow-core/arrow.core/-either/index.md)&lt;[A](race-n.md), [B](race-n.md)&gt;

Races the participants [fa](race-n.md), [fb](race-n.md) on the provided [CoroutineContext](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.coroutines/-coroutine-context/index.html). The winner of the race cancels the other participants. Cancelling the operation cancels all participants.

Coroutine context is inherited from a CoroutineScope, additional context elements can be specified with [ctx](race-n.md) argument. If the combined context does not have any dispatcher nor any other [ContinuationInterceptor](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.coroutines/-continuation-interceptor/index.html), then Dispatchers.Default is used. **WARNING** If the combined context has a single threaded [ContinuationInterceptor](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.coroutines/-continuation-interceptor/index.html), this function will not run [fa](race-n.md)&[fb](race-n.md) in parallel.

import arrow.core.Either\
import arrow.fx.coroutines.*\
import kotlinx.coroutines.Dispatchers\
import kotlinx.coroutines.suspendCancellableCoroutine\
\
suspend fun main(): Unit {\
  suspend fun loser(): Int =\
    guaranteeCase({ never() }) { exitCase -&gt;\
      println("I can never win the race. Finished with $exitCase.")\
    }\
\
  val winner = raceN(Dispatchers.IO, { loser() }, { 5 })\
\
  val res = when(winner) {\
    is Either.Left -&gt; "Never always loses race"\
    is Either.Right -&gt; "Race was won with ${winner.value}"\
  }\
  //sampleEnd\
  println(res)\
}<!--- KNIT example-race2-02.kt -->

#### Return

either [Either.Left](../../../arrow-core/arrow-core/arrow.core/-either/-left/index.md) if [fa](race-n.md) won the race, or [Either.Right](../../../arrow-core/arrow-core/arrow.core/-either/-right/index.md) if [fb](race-n.md) won the race.

## See also

common

| | |
|---|---|
| [raceN](race-n.md) | for a function that ensures it runs in parallel on the Dispatchers.Default. |

## Parameters

common

| | |
|---|---|
| fa | task to participate in the race |
| fb | task to participate in the race |

[common]\
inline suspend fun &lt;[A](race-n.md), [B](race-n.md), [C](race-n.md)&gt; [raceN](race-n.md)(crossinline fa: suspend CoroutineScope.() -&gt; [A](race-n.md), crossinline fb: suspend CoroutineScope.() -&gt; [B](race-n.md), crossinline fc: suspend CoroutineScope.() -&gt; [C](race-n.md)): [Race3](-race3/index.md)&lt;[A](race-n.md), [B](race-n.md), [C](race-n.md)&gt;

Races the participants [fa](race-n.md), [fb](race-n.md)&[fc](race-n.md) in parallel on the Dispatchers.Default. The winner of the race cancels the other participants. Cancelling the operation cancels all participants.

## See also

common

| | |
|---|---|
| [raceN](race-n.md) | for the same function that can race on any [CoroutineContext](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.coroutines/-coroutine-context/index.html). |

[common]\
inline suspend fun &lt;[A](race-n.md), [B](race-n.md), [C](race-n.md)&gt; [raceN](race-n.md)(ctx: [CoroutineContext](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.coroutines/-coroutine-context/index.html) = EmptyCoroutineContext, crossinline fa: suspend CoroutineScope.() -&gt; [A](race-n.md), crossinline fb: suspend CoroutineScope.() -&gt; [B](race-n.md), crossinline fc: suspend CoroutineScope.() -&gt; [C](race-n.md)): [Race3](-race3/index.md)&lt;[A](race-n.md), [B](race-n.md), [C](race-n.md)&gt;

Races the participants [fa](race-n.md), [fb](race-n.md)&[fc](race-n.md) on the provided [CoroutineContext](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.coroutines/-coroutine-context/index.html). The winner of the race cancels the other participants. Cancelling the operation cancels all participants.

Coroutine context is inherited from a CoroutineScope, additional context elements can be specified with [ctx](race-n.md) argument. If the combined context does not have any dispatcher nor any other [ContinuationInterceptor](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.coroutines/-continuation-interceptor/index.html), then Dispatchers.Default is used. **WARNING** If the combined context has a single threaded [ContinuationInterceptor](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.coroutines/-continuation-interceptor/index.html), this function will not run [fa](race-n.md), [fb](race-n.md)&[fc](race-n.md) in parallel.

## See also

common

| | |
|---|---|
| [raceN](race-n.md) | for a function that ensures operations run in parallel on the Dispatchers.Default. |
