//[arrow-fx-coroutines](../../../index.md)/[arrow.fx.coroutines](../index.md)/[Schedule](index.md)/[untilInput](until-input.md)

# untilInput

[common]\
fun &lt;[A](until-input.md) : [Input](index.md)&gt; [untilInput](until-input.md)(f: suspend ([A](until-input.md)) -&gt; [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)): [Schedule](index.md)&lt;[A](until-input.md), [Output](index.md)&gt;

untilInput(f) = whileInput(f).not()
