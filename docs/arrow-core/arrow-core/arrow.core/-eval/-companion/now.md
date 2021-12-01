//[arrow-core](../../../../index.md)/[arrow.core](../../index.md)/[Eval](../index.md)/[Companion](index.md)/[now](now.md)

# now

[common]\

@[JvmStatic](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.jvm/-jvm-static/index.html)

fun &lt;[A](now.md)&gt; [now](now.md)(a: [A](now.md)): [Eval](../index.md)&lt;[A](now.md)&gt;

Creates an Eval instance from an already constructed value but still defers evaluation when chaining expressions with map and flatMap

## Parameters

common

| | |
|---|---|
| a | is an already computed value of type [A](now.md)<br>import arrow.core.*<br>fun main() {<br>//sampleStart<br>  val eager = Eval.now(1).map { it + 1 }<br>  println(eager.value())<br>//sampleEnd<br>}<!--- KNIT example-eval-02.kt --><br>It will return 2. |
