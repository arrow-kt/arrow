//[arrow-fx-coroutines](../../../index.md)/[arrow.fx.coroutines](../index.md)/[Schedule](index.md)/[repeatOrElse](repeat-or-else.md)

# repeatOrElse

[common]\
suspend fun [repeatOrElse](repeat-or-else.md)(fa: suspend () -&gt; [Input](index.md), orElse: suspend ([Throwable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html), [Output](index.md)?) -&gt; [Output](index.md)): [Output](index.md)

Runs this effect once and, if it succeeded, decide using the provided policy if the effect should be repeated and if so, with how much delay. Also offers a function to handle errors if they are encountered during repetition.
