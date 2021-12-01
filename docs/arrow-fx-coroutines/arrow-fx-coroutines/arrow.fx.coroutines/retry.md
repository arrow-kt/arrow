//[arrow-fx-coroutines](../../index.md)/[arrow.fx.coroutines](index.md)/[retry](retry.md)

# retry

[common]\
suspend fun &lt;[A](retry.md), [B](retry.md)&gt; [Schedule](-schedule/index.md)&lt;[Throwable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html), [B](retry.md)&gt;.[retry](retry.md)(fa: suspend () -&gt; [A](retry.md)): [A](retry.md)

Runs an effect and, if it fails, decide using the provided policy if the effect should be retried and if so, with how much delay. Returns the result of the effect if if it was successful or re-raises the last error encountered when the schedule ends.

[common]\
fun &lt;[A](retry.md), [B](retry.md)&gt; Flow&lt;[A](retry.md)&gt;.[retry](retry.md)(schedule: [Schedule](-schedule/index.md)&lt;[Throwable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html), [B](retry.md)&gt;): Flow&lt;[A](retry.md)&gt;

Retries collection of the given flow when an exception occurs in the upstream flow based on a decision by the [schedule](retry.md). This operator is *transparent* to exceptions that occur in downstream flow and does not retry on exceptions that are thrown to cancel the flow.

## See also

common

| | |
|---|---|
| [arrow.fx.coroutines.Schedule](-schedule/index.md) | for how to build a schedule.<br>import kotlinx.coroutines.flow.*<br>import arrow.fx.coroutines.*<br>suspend fun main(): Unit {<br>  var counter = 0<br>  val flow = flow {<br>   emit(counter)<br>   if (++counter &lt;= 5) throw RuntimeException("Bang!")<br>  }<br>  //sampleStart<br> val sum = flow.retry(Schedule.recurs(5))<br>   .reduce(Int::plus)<br>  //sampleEnd<br>  println(sum)<br>}<!--- KNIT example-flow-01.kt --> |

## Parameters

common

| | |
|---|---|
| schedule | <ul><li>the [Schedule](-schedule/index.md) used for retrying the collection of the flow</li></ul> |
