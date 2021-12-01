//[arrow-fx-coroutines](../../../index.md)/[arrow.fx.coroutines](../index.md)/[Schedule](index.md)

# Schedule

[common]\
sealed class [Schedule](index.md)&lt;[Input](index.md), [Output](index.md)&gt;

# Retrying and repeating effects

A common demand when working with effects is to retry or repeat them when certain circumstances happen. Usually, the retrial or repetition does not happen right away; rather, it is done based on a policy. For instance, when fetching content from a network request, we may want to retry it when it fails, using an exponential backoff algorithm, for a maximum of 15 seconds or 5 attempts, whatever happens first.

[Schedule](index.md) allows you to define and compose powerful yet simple policies, which can be used to either repeat or retry computation.

The two core methods of running a schedule are:

<ul><li>**retry**: The effect is executed once, and if it fails, it will be reattempted based on the scheduling policy passed as an argument. It will stop if the effect ever succeeds, or the policy determines it should not be reattempted again.</li><li>**repeat**: The effect is executed once, and if it succeeds, it will be executed again based on the scheduling policy passed as an argument. It will stop if the effect ever fails, or the policy determines it should not be executed again. It will return the last internal state of the scheduling policy, or the error that happened running the effect.</li></ul>

##  Constructing a policy:

Constructing a simple schedule which recurs 10 times until it succeeds:

import arrow.fx.coroutines.*\
\
fun &lt;A&gt; recurTenTimes() = Schedule.recurs&lt;A&gt;(10)<!--- KNIT example-schedule-01.kt -->

A more complex schedule

import kotlin.time.seconds\
import kotlin.time.milliseconds\
import kotlin.time.ExperimentalTime\
import arrow.fx.coroutines.*\
\
@ExperimentalTime\
fun &lt;A&gt; complexPolicy(): Schedule&lt;A, List&lt;A&gt;&gt; =\
  Schedule.exponential&lt;A&gt;(10.milliseconds).whileOutput { it.inNanoseconds &lt; 60.seconds.inNanoseconds }\
    .andThen(Schedule.spaced&lt;A&gt;(60.seconds) and Schedule.recurs(100)).jittered()\
    .zipRight(Schedule.identity&lt;A&gt;().collect())<!--- KNIT example-schedule-02.kt -->

This policy will recur with exponential backoff as long as the delay is less than 60 seconds and then continue with a spaced delay of 60 seconds. The delay is also randomized slightly to avoid coordinated backoff from multiple services. Finally we also collect every input to the schedule and return it. When used with [retry](../retry.md) this will return a list of exceptions that occured on failed attempts.

##  Common use cases

Common use cases Once we have building blocks and ways to combine them, let’s see how we can use them to solve some use cases.

###  Repeating an effect and dealing with its result

When we repeat an effect, we do it as long as it keeps providing successful results and the scheduling policy tells us to keep recursing. But then, there is a question on what to do with the results provided by each iteration of the repetition.

There are at least 3 possible things we would like to do:

<ul><li>Discard all results; i.e., return Unit.</li><li>Discard all intermediate results and just keep the last produced result.</li><li>Keep all intermediate results.</li></ul>

Assuming we have a suspend effect in, and we want to repeat it 3 times after its first successful execution, we can do:

import arrow.fx.coroutines.*\
\
suspend fun main(): Unit {\
  var counter = 0\
  //sampleStart\
  val res = Schedule.recurs&lt;Unit&gt;(3).repeat {\
    println("Run: ${counter++}")\
  }\
  //sampleEnd\
  println(res)\
}<!--- KNIT example-schedule-03.kt -->

However, when running this new effect, its output will be the number of iterations it has performed, as stated in the documentation of the function. Also notice that we did not handle the error case, there are overloads [repeatOrElse](repeat-or-else.md) and [repeatOrElseEither](repeat-or-else-either.md) which offer that capability, [repeat](repeat.md) will just rethrow any error encountered.

If we want to discard the values provided by the repetition of the effect, we can combine our policy with [Schedule.unit](-companion/unit.md), using the [zipLeft](zip-left.md) or [zipRight](zip-right.md) combinators, which will keep just the output of one of the policies:

import arrow.fx.coroutines.*\
\
suspend fun main(): Unit {\
  var counter = 0\
  //sampleStart\
  val res = (Schedule.unit&lt;Unit&gt;() zipLeft Schedule.recurs(3)).repeat {\
    println("Run: ${counter++}")\
  }\
  // equal to\
  val res2 = (Schedule.recurs&lt;Unit&gt;(3) zipRight Schedule.unit()).repeat {\
    println("Run: ${counter++}")\
  }\
  //sampleEnd\
  println(res)\
  println(res2)\
}<!--- KNIT example-schedule-04.kt -->

Following the same strategy, we can zip it with the [Schedule.identity](-companion/identity.md) policy to keep only the last provided result by the effect.

import arrow.fx.coroutines.*\
\
suspend fun main(): Unit {\
  var counter = 0\
  //sampleStart\
  val res = (Schedule.identity&lt;Int&gt;() zipLeft Schedule.recurs(3)).repeat {\
    println("Run: ${counter++}"); counter\
  }\
  // equal to\
  val res2 = (Schedule.recurs&lt;Int&gt;(3) zipRight Schedule.identity&lt;Int&gt;()).repeat {\
    println("Run: ${counter++}"); counter\
  }\
  //sampleEnd\
  println(res)\
  println(res2)\
}<!--- KNIT example-schedule-05.kt -->

Finally, if we want to keep all intermediate results, we can zip the policy with [Schedule.collect](collect.md):

import arrow.fx.coroutines.*\
\
suspend fun main(): Unit {\
  var counter = 0\
  //sampleStart\
  val res = (Schedule.collect&lt;Int&gt;() zipLeft Schedule.recurs(3)).repeat {\
    println("Run: ${counter++}")\
    counter\
  }\
  // equal to\
  val res2 = (Schedule.recurs&lt;Int&gt;(3) zipRight Schedule.collect&lt;Int&gt;()).repeat {\
    println("Run: ${counter++}")\
    counter\
  }\
  //sampleEnd\
  println(res)\
  println(res2)\
}<!--- KNIT example-schedule-06.kt -->

##  Repeating an effect until/while it produces a certain value

We can make use of the policies doWhile or doUntil to repeat an effect while or until its produced result matches a given predicate.

import arrow.fx.coroutines.*\
\
suspend fun main(): Unit {\
  var counter = 0\
  //sampleStart\
  val res = Schedule.doWhile&lt;Int&gt;{ it &lt;= 3 }.repeat {\
    println("Run: ${counter++}"); counter\
  }\
  //sampleEnd\
  println(res)\
}<!--- KNIT example-schedule-07.kt -->

##  Exponential backoff retries

A common algorithm to retry effectful operations, as network requests, is the exponential backoff algorithm. There is a scheduling policy that implements this algorithm and can be used as:

import kotlin.time.milliseconds\
import kotlin.time.ExperimentalTime\
import arrow.fx.coroutines.*\
\
@ExperimentalTime\
val exponential = Schedule.exponential&lt;Unit&gt;(250.milliseconds)<!--- KNIT example-schedule-08.kt -->

## Types

| Name | Summary |
|---|---|
| [Companion](-companion/index.md) | [common]<br>object [Companion](-companion/index.md) |
| [Decision](-decision/index.md) | [common]<br>data class [Decision](-decision/index.md)&lt;out [A](-decision/index.md), out [B](-decision/index.md)&gt;(cont: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html), delayInNanos: [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html), state: [A](-decision/index.md), finish: [Eval](../../../../arrow-core/arrow-core/arrow.core/-eval/index.md)&lt;[B](-decision/index.md)&gt;)<br>A single decision. Contains the decision to continue, the delay, the new state and the (lazy) result of a Schedule. |

## Functions

| Name | Summary |
|---|---|
| [and](and.md) | [common]<br>infix fun &lt;[A](and.md) : [Input](index.md), [B](and.md)&gt; [and](and.md)(other: [Schedule](index.md)&lt;[A](and.md), [B](and.md)&gt;): [Schedule](index.md)&lt;[A](and.md), [Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)&lt;[Output](index.md), [B](and.md)&gt;&gt;<br>Combines two schedules. Continues only when both continue and chooses the maximum delay. |
| [andThen](and-then.md) | [common]<br>abstract infix fun &lt;[A](and-then.md) : [Input](index.md), [B](and-then.md)&gt; [andThen](and-then.md)(other: [Schedule](index.md)&lt;[A](and-then.md), [B](and-then.md)&gt;): [Schedule](index.md)&lt;[A](and-then.md), [Either](../../../../arrow-core/arrow-core/arrow.core/-either/index.md)&lt;[Output](index.md), [B](and-then.md)&gt;&gt;<br>Executes one schedule after the other. When the first schedule ends, it continues with the second. |
| [check](check.md) | [common]<br>abstract fun &lt;[A](check.md) : [Input](index.md)&gt; [check](check.md)(pred: suspend ([A](check.md), [Output](index.md)) -&gt; [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)): [Schedule](index.md)&lt;[A](check.md), [Output](index.md)&gt;<br>Conditionally checks on both the input and the output whether or not to continue. |
| [choose](choose.md) | [common]<br>abstract infix fun &lt;[A](choose.md), [B](choose.md)&gt; [choose](choose.md)(other: [Schedule](index.md)&lt;[A](choose.md), [B](choose.md)&gt;): [Schedule](index.md)&lt;[Either](../../../../arrow-core/arrow-core/arrow.core/-either/index.md)&lt;[Input](index.md), [A](choose.md)&gt;, [Either](../../../../arrow-core/arrow-core/arrow.core/-either/index.md)&lt;[Output](index.md), [B](choose.md)&gt;&gt;<br>Combines two schedules with different input and output and conditionally choose between the two. Continues when the chosen schedule continues and uses the chosen schedules delay. |
| [collect](collect.md) | [common]<br>fun [collect](collect.md)(): [Schedule](index.md)&lt;[Input](index.md), [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[Output](index.md)&gt;&gt;<br>Accumulates the results of every execution into a list. |
| [combine](combine.md) | [common]<br>@[ExperimentalTime](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.time/-experimental-time/index.html)<br>fun &lt;[A](combine.md) : [Input](index.md), [B](combine.md), [C](combine.md)&gt; [combine](combine.md)(other: [Schedule](index.md)&lt;[A](combine.md), [B](combine.md)&gt;, zipContinue: (cont: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html), otherCont: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)) -&gt; [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html), zipDuration: (duration: [Duration](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.time/-duration/index.html), otherDuration: [Duration](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.time/-duration/index.html)) -&gt; [Duration](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.time/-duration/index.html), zip: ([Output](index.md), [B](combine.md)) -&gt; [C](combine.md)): [Schedule](index.md)&lt;[A](combine.md), [C](combine.md)&gt;<br>Combines with another schedule by combining the result and the delay of the [Decision](-decision/index.md) with the [zipContinue](combine.md), [zipDuration](combine.md) and a [zip](combine.md) functions |
| [combineNanos](combine-nanos.md) | [common]<br>abstract fun &lt;[A](combine-nanos.md) : [Input](index.md), [B](combine-nanos.md), [C](combine-nanos.md)&gt; [combineNanos](combine-nanos.md)(other: [Schedule](index.md)&lt;[A](combine-nanos.md), [B](combine-nanos.md)&gt;, zipContinue: (cont: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html), otherCont: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)) -&gt; [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html), zipDuration: (duration: [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html), otherDuration: [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)) -&gt; [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html), zip: ([Output](index.md), [B](combine-nanos.md)) -&gt; [C](combine-nanos.md)): [Schedule](index.md)&lt;[A](combine-nanos.md), [C](combine-nanos.md)&gt;<br>Combines with another schedule by combining the result and the delay of the [Decision](-decision/index.md) with the functions [zipContinue](combine-nanos.md), [zipDuration](combine-nanos.md) and a [zip](combine-nanos.md) function |
| [compose](compose.md) | [common]<br>infix fun &lt;[B](compose.md)&gt; [compose](compose.md)(other: [Schedule](index.md)&lt;[B](compose.md), [Input](index.md)&gt;): [Schedule](index.md)&lt;[B](compose.md), [Output](index.md)&gt;<br>Infix variant of pipe with reversed order. |
| [const](const.md) | [common]<br>fun &lt;[B](const.md)&gt; [const](const.md)(b: [B](const.md)): [Schedule](index.md)&lt;[Input](index.md), [B](const.md)&gt;<br>Changes the result of a [Schedule](index.md) to always be [b](const.md). |
| [contramap](contramap.md) | [common]<br>abstract fun &lt;[B](contramap.md)&gt; [contramap](contramap.md)(f: suspend ([B](contramap.md)) -&gt; [Input](index.md)): [Schedule](index.md)&lt;[B](contramap.md), [Output](index.md)&gt;<br>Changes the input of the schedule. May alter a schedule's decision if it is based on input. |
| [delay](delay.md) | [common]<br>@[ExperimentalTime](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.time/-experimental-time/index.html)<br>fun [delay](delay.md)(f: suspend (duration: [Duration](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.time/-duration/index.html)) -&gt; [Duration](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.time/-duration/index.html)): [Schedule](index.md)&lt;[Input](index.md), [Output](index.md)&gt; |
| [delayedNanos](delayed-nanos.md) | [common]<br>fun [delayedNanos](delayed-nanos.md)(f: suspend (duration: [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)) -&gt; [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)): [Schedule](index.md)&lt;[Input](index.md), [Output](index.md)&gt; |
| [dimap](dimap.md) | [common]<br>fun &lt;[B](dimap.md), [C](dimap.md)&gt; [dimap](dimap.md)(f: suspend ([B](dimap.md)) -&gt; [Input](index.md), g: ([Output](index.md)) -&gt; [C](dimap.md)): [Schedule](index.md)&lt;[B](dimap.md), [C](dimap.md)&gt; |
| [fold](fold.md) | [common]<br>fun &lt;[C](fold.md)&gt; [fold](fold.md)(initial: [C](fold.md), f: suspend ([C](fold.md), [Output](index.md)) -&gt; [C](fold.md)): [Schedule](index.md)&lt;[Input](index.md), [C](fold.md)&gt;<br>Non-effectful version of foldM. |
| [foldLazy](fold-lazy.md) | [common]<br>abstract fun &lt;[C](fold-lazy.md)&gt; [foldLazy](fold-lazy.md)(initial: suspend () -&gt; [C](fold-lazy.md), f: suspend ([C](fold-lazy.md), [Output](index.md)) -&gt; [C](fold-lazy.md)): [Schedule](index.md)&lt;[Input](index.md), [C](fold-lazy.md)&gt;<br>Accumulates the results of a schedule by folding over them effectfully. |
| [forever](forever.md) | [common]<br>abstract fun [forever](forever.md)(): [Schedule](index.md)&lt;[Input](index.md), [Output](index.md)&gt;<br>Always retries a schedule regardless of the decision made prior to invoking this method. |
| [jittered](jittered.md) | [common]<br>fun [jittered](jittered.md)(genRand: suspend () -&gt; [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)): [Schedule](index.md)&lt;[Input](index.md), [Output](index.md)&gt;<br>@[JvmName](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.jvm/-jvm-name/index.html)(name = "jitteredDuration")<br>@[ExperimentalTime](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.time/-experimental-time/index.html)<br>fun [jittered](jittered.md)(genRand: suspend () -&gt; [Duration](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.time/-duration/index.html)): [Schedule](index.md)&lt;[Input](index.md), [Output](index.md)&gt;<br>[common]<br>fun [jittered](jittered.md)(random: [Random](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.random/-random/index.html) = Random.Default): [Schedule](index.md)&lt;[Input](index.md), [Output](index.md)&gt;<br>Add random jitter to a schedule. |
| [logInput](log-input.md) | [common]<br>abstract fun [logInput](log-input.md)(f: suspend ([Input](index.md)) -&gt; [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)): [Schedule](index.md)&lt;[Input](index.md), [Output](index.md)&gt;<br>Runs an effectful handler on every input. Does not alter the decision. |
| [logOutput](log-output.md) | [common]<br>abstract fun [logOutput](log-output.md)(f: suspend ([Output](index.md)) -&gt; [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)): [Schedule](index.md)&lt;[Input](index.md), [Output](index.md)&gt;<br>Runs an effectful handler on every output. Does not alter the decision. |
| [map](map.md) | [common]<br>abstract fun &lt;[B](map.md)&gt; [map](map.md)(f: ([Output](index.md)) -&gt; [B](map.md)): [Schedule](index.md)&lt;[Input](index.md), [B](map.md)&gt;<br>Changes the output of a schedule. Does not alter the decision of the schedule. |
| [modify](modify.md) | [common]<br>@[ExperimentalTime](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.time/-experimental-time/index.html)<br>fun [modify](modify.md)(f: suspend ([Output](index.md), [Duration](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.time/-duration/index.html)) -&gt; [Duration](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.time/-duration/index.html)): [Schedule](index.md)&lt;[Input](index.md), [Output](index.md)&gt;<br>Changes the delay of a resulting [Decision](-decision/index.md) based on the [Output](index.md) and the produced delay. |
| [modifyNanos](modify-nanos.md) | [common]<br>abstract fun [modifyNanos](modify-nanos.md)(f: suspend ([Output](index.md), [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)) -&gt; [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)): [Schedule](index.md)&lt;[Input](index.md), [Output](index.md)&gt; |
| [not](not.md) | [common]<br>abstract operator fun [not](not.md)(): [Schedule](index.md)&lt;[Input](index.md), [Output](index.md)&gt;<br>Inverts the decision of a schedule. |
| [or](or.md) | [common]<br>infix fun &lt;[A](or.md) : [Input](index.md), [B](or.md)&gt; [or](or.md)(other: [Schedule](index.md)&lt;[A](or.md), [B](or.md)&gt;): [Schedule](index.md)&lt;[A](or.md), [Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)&lt;[Output](index.md), [B](or.md)&gt;&gt;<br>Combines two schedules. Continues if one continues and chooses the minimum delay. |
| [pipe](pipe.md) | [common]<br>abstract infix fun &lt;[B](pipe.md)&gt; [pipe](pipe.md)(other: [Schedule](index.md)&lt;[Output](index.md), [B](pipe.md)&gt;): [Schedule](index.md)&lt;[Input](index.md), [B](pipe.md)&gt;<br>Composes this schedule with the other schedule by piping the output of this schedule into the input of the other. |
| [repeat](repeat.md) | [common]<br>suspend fun [repeat](repeat.md)(fa: suspend () -&gt; [Input](index.md)): [Output](index.md)<br>Runs this effect once and, if it succeeded, decide using the provided policy if the effect should be repeated and if so, with how much delay. Returns the last output from the policy or raises an error if a repeat failed. |
| [repeatOrElse](repeat-or-else.md) | [common]<br>suspend fun [repeatOrElse](repeat-or-else.md)(fa: suspend () -&gt; [Input](index.md), orElse: suspend ([Throwable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html), [Output](index.md)?) -&gt; [Output](index.md)): [Output](index.md)<br>Runs this effect once and, if it succeeded, decide using the provided policy if the effect should be repeated and if so, with how much delay. Also offers a function to handle errors if they are encountered during repetition. |
| [repeatOrElseEither](repeat-or-else-either.md) | [common]<br>abstract suspend fun &lt;[C](repeat-or-else-either.md)&gt; [repeatOrElseEither](repeat-or-else-either.md)(fa: suspend () -&gt; [Input](index.md), orElse: suspend ([Throwable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html), [Output](index.md)?) -&gt; [C](repeat-or-else-either.md)): [Either](../../../../arrow-core/arrow-core/arrow.core/-either/index.md)&lt;[C](repeat-or-else-either.md), [Output](index.md)&gt; |
| [untilInput](until-input.md) | [common]<br>fun &lt;[A](until-input.md) : [Input](index.md)&gt; [untilInput](until-input.md)(f: suspend ([A](until-input.md)) -&gt; [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)): [Schedule](index.md)&lt;[A](until-input.md), [Output](index.md)&gt;<br>untilInput(f) = whileInput(f).not() |
| [untilOutput](until-output.md) | [common]<br>fun [untilOutput](until-output.md)(f: suspend ([Output](index.md)) -&gt; [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)): [Schedule](index.md)&lt;[Input](index.md), [Output](index.md)&gt;<br>untilOutput(f) = whileOutput(f).not() |
| [void](void.md) | [common]<br>fun [void](void.md)(): [Schedule](index.md)&lt;[Input](index.md), [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)&gt; |
| [whileInput](while-input.md) | [common]<br>fun &lt;[A](while-input.md) : [Input](index.md)&gt; [whileInput](while-input.md)(f: suspend ([A](while-input.md)) -&gt; [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)): [Schedule](index.md)&lt;[A](while-input.md), [Output](index.md)&gt;<br>Continues or stops the schedule based on the input. |
| [whileOutput](while-output.md) | [common]<br>fun [whileOutput](while-output.md)(f: suspend ([Output](index.md)) -&gt; [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)): [Schedule](index.md)&lt;[Input](index.md), [Output](index.md)&gt;<br>Continues or stops the schedule based on the output. |
| [zip](zip.md) | [common]<br>infix fun &lt;[A](zip.md), [B](zip.md)&gt; [zip](zip.md)(other: [Schedule](index.md)&lt;[A](zip.md), [B](zip.md)&gt;): [Schedule](index.md)&lt;[Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)&lt;[Input](index.md), [A](zip.md)&gt;, [Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)&lt;[Output](index.md), [B](zip.md)&gt;&gt;<br>abstract fun &lt;[A](zip.md), [B](zip.md), [C](zip.md)&gt; [zip](zip.md)(other: [Schedule](index.md)&lt;[A](zip.md), [B](zip.md)&gt;, f: ([Output](index.md), [B](zip.md)) -&gt; [C](zip.md)): [Schedule](index.md)&lt;[Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)&lt;[Input](index.md), [A](zip.md)&gt;, [C](zip.md)&gt;<br>Combines two with different input and output using and. Continues when both continue and uses the maximum delay. |
| [zipLeft](zip-left.md) | [common]<br>infix fun &lt;[A](zip-left.md) : [Input](index.md), [B](zip-left.md)&gt; [zipLeft](zip-left.md)(other: [Schedule](index.md)&lt;[A](zip-left.md), [B](zip-left.md)&gt;): [Schedule](index.md)&lt;[A](zip-left.md), [Output](index.md)&gt;<br>Combines two schedules with [and](and.md) but throws away the right schedule's result. |
| [zipRight](zip-right.md) | [common]<br>infix fun &lt;[A](zip-right.md) : [Input](index.md), [B](zip-right.md)&gt; [zipRight](zip-right.md)(other: [Schedule](index.md)&lt;[A](zip-right.md), [B](zip-right.md)&gt;): [Schedule](index.md)&lt;[A](zip-right.md), [B](zip-right.md)&gt;<br>Combines two schedules with [and](and.md) but throws away the left schedule's result. |

## Extensions

| Name | Summary |
|---|---|
| [retry](../retry.md) | [common]<br>suspend fun &lt;[A](../retry.md), [B](../retry.md)&gt; [Schedule](index.md)&lt;[Throwable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html), [B](../retry.md)&gt;.[retry](../retry.md)(fa: suspend () -&gt; [A](../retry.md)): [A](../retry.md)<br>Runs an effect and, if it fails, decide using the provided policy if the effect should be retried and if so, with how much delay. Returns the result of the effect if if it was successful or re-raises the last error encountered when the schedule ends. |
| [retryOrElse](../retry-or-else.md) | [common]<br>suspend fun &lt;[A](../retry-or-else.md), [B](../retry-or-else.md)&gt; [Schedule](index.md)&lt;[Throwable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html), [B](../retry-or-else.md)&gt;.[retryOrElse](../retry-or-else.md)(fa: suspend () -&gt; [A](../retry-or-else.md), orElse: suspend ([Throwable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html), [B](../retry-or-else.md)) -&gt; [A](../retry-or-else.md)): [A](../retry-or-else.md)<br>Runs an effect and, if it fails, decide using the provided policy if the effect should be retried and if so, with how much delay. Also offers a function to handle errors if they are encountered during retrial. |
| [retryOrElseEither](../retry-or-else-either.md) | [common]<br>suspend fun &lt;[A](../retry-or-else-either.md), [B](../retry-or-else-either.md), [C](../retry-or-else-either.md)&gt; [Schedule](index.md)&lt;[Throwable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html), [B](../retry-or-else-either.md)&gt;.[retryOrElseEither](../retry-or-else-either.md)(fa: suspend () -&gt; [A](../retry-or-else-either.md), orElse: suspend ([Throwable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html), [B](../retry-or-else-either.md)) -&gt; [C](../retry-or-else-either.md)): [Either](../../../../arrow-core/arrow-core/arrow.core/-either/index.md)&lt;[C](../retry-or-else-either.md), [A](../retry-or-else-either.md)&gt;<br>Runs an effect and, if it fails, decide using the provided policy if the effect should be retried and if so, with how much delay. Also offers a function to handle errors if they are encountered during retrial. |
