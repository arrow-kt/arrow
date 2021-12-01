//[arrow-fx-coroutines](../../../index.md)/[arrow.fx.coroutines](../index.md)/[Schedule](index.md)/[jittered](jittered.md)

# jittered

[common]\
fun [jittered](jittered.md)(genRand: suspend () -&gt; [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)): [Schedule](index.md)&lt;[Input](index.md), [Output](index.md)&gt;

@[JvmName](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.jvm/-jvm-name/index.html)(name = "jitteredDuration")

@[ExperimentalTime](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.time/-experimental-time/index.html)

fun [jittered](jittered.md)(genRand: suspend () -&gt; [Duration](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.time/-duration/index.html)): [Schedule](index.md)&lt;[Input](index.md), [Output](index.md)&gt;

[common]\
fun [jittered](jittered.md)(random: [Random](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.random/-random/index.html) = Random.Default): [Schedule](index.md)&lt;[Input](index.md), [Output](index.md)&gt;

Add random jitter to a schedule.

By requiring Kotlin's [Random](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.random/-random/index.html) as a parameter, this function is deterministic and testable. The result returned by [Random.nextDouble](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.random/-random/next-double.html) between 0.0 and 1.0 is multiplied with the current duration.
