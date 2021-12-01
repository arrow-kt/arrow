//[arrow-core](../../../../index.md)/[arrow.core](../../index.md)/[Eval](../index.md)/[Companion](index.md)

# Companion

[common]\
object [Companion](index.md)

## Functions

| Name | Summary |
|---|---|
| [always](always.md) | [common]<br>@[JvmStatic](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.jvm/-jvm-static/index.html)<br>inline fun &lt;[A](always.md)&gt; [always](always.md)(crossinline f: () -&gt; [A](always.md)): [Eval.Always](../-always/index.md)&lt;[A](always.md)&gt;<br>Creates an Eval instance from a function deferring it's evaluation until .value() is invoked recomputing each time .value() is invoked. |
| [defer](defer.md) | [common]<br>@[JvmStatic](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.jvm/-jvm-static/index.html)<br>inline fun &lt;[A](defer.md)&gt; [defer](defer.md)(crossinline f: () -&gt; [Eval](../index.md)&lt;[A](defer.md)&gt;): [Eval](../index.md)&lt;[A](defer.md)&gt; |
| [later](later.md) | [common]<br>@[JvmStatic](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.jvm/-jvm-static/index.html)<br>inline fun &lt;[A](later.md)&gt; [later](later.md)(crossinline f: () -&gt; [A](later.md)): [Eval.Later](../-later/index.md)&lt;[A](later.md)&gt;<br>Creates an Eval instance from a function deferring it's evaluation until .value() is invoked memoizing the computed value. |
| [now](now.md) | [common]<br>@[JvmStatic](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.jvm/-jvm-static/index.html)<br>fun &lt;[A](now.md)&gt; [now](now.md)(a: [A](now.md)): [Eval](../index.md)&lt;[A](now.md)&gt;<br>Creates an Eval instance from an already constructed value but still defers evaluation when chaining expressions with map and flatMap |
| [raise](raise.md) | [common]<br>@[JvmStatic](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.jvm/-jvm-static/index.html)<br>fun [raise](raise.md)(t: [Throwable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html)): [Eval](../index.md)&lt;[Nothing](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-nothing/index.html)&gt; |
