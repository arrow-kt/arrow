//[arrow-fx-coroutines](../../../index.md)/[arrow.fx.coroutines](../index.md)/[Schedule](index.md)/[combineNanos](combine-nanos.md)

# combineNanos

[common]\
abstract fun &lt;[A](combine-nanos.md) : [Input](index.md), [B](combine-nanos.md), [C](combine-nanos.md)&gt; [combineNanos](combine-nanos.md)(other: [Schedule](index.md)&lt;[A](combine-nanos.md), [B](combine-nanos.md)&gt;, zipContinue: (cont: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html), otherCont: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)) -&gt; [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html), zipDuration: (duration: [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html), otherDuration: [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)) -&gt; [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html), zip: ([Output](index.md), [B](combine-nanos.md)) -&gt; [C](combine-nanos.md)): [Schedule](index.md)&lt;[A](combine-nanos.md), [C](combine-nanos.md)&gt;

Combines with another schedule by combining the result and the delay of the [Decision](-decision/index.md) with the functions [zipContinue](combine-nanos.md), [zipDuration](combine-nanos.md) and a [zip](combine-nanos.md) function
