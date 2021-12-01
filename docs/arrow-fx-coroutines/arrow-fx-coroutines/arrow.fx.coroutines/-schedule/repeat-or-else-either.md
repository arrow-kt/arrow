//[arrow-fx-coroutines](../../../index.md)/[arrow.fx.coroutines](../index.md)/[Schedule](index.md)/[repeatOrElseEither](repeat-or-else-either.md)

# repeatOrElseEither

[common]\
abstract suspend fun &lt;[C](repeat-or-else-either.md)&gt; [repeatOrElseEither](repeat-or-else-either.md)(fa: suspend () -&gt; [Input](index.md), orElse: suspend ([Throwable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html), [Output](index.md)?) -&gt; [C](repeat-or-else-either.md)): [Either](../../../../arrow-core/arrow-core/arrow.core/-either/index.md)&lt;[C](repeat-or-else-either.md), [Output](index.md)&gt;
