//[arrow-fx-coroutines](../../../index.md)/[arrow.fx.coroutines](../index.md)/[Schedule](index.md)/[untilOutput](until-output.md)

# untilOutput

[common]\
fun [untilOutput](until-output.md)(f: suspend ([Output](index.md)) -&gt; [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)): [Schedule](index.md)&lt;[Input](index.md), [Output](index.md)&gt;

untilOutput(f) = whileOutput(f).not()
