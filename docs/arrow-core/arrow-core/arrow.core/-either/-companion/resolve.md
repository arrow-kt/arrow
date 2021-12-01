//[arrow-core](../../../../index.md)/[arrow.core](../../index.md)/[Either](../index.md)/[Companion](index.md)/[resolve](resolve.md)

# resolve

[common]\

@[JvmStatic](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.jvm/-jvm-static/index.html)

inline fun &lt;[E](resolve.md), [A](resolve.md), [B](resolve.md)&gt; [resolve](resolve.md)(f: () -&gt; [Either](../index.md)&lt;[E](resolve.md), [A](resolve.md)&gt;, success: ([A](resolve.md)) -&gt; [Either](../index.md)&lt;[Throwable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html), [B](resolve.md)&gt;, error: ([E](resolve.md)) -&gt; [Either](../index.md)&lt;[Throwable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html), [B](resolve.md)&gt;, throwable: (throwable: [Throwable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html)) -&gt; [Either](../index.md)&lt;[Throwable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html), [B](resolve.md)&gt;, unrecoverableState: (throwable: [Throwable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html)) -&gt; [Either](../index.md)&lt;[Throwable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html), [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)&gt;): [B](resolve.md)

The resolve function can resolve any suspended function that yields an Either into one type of value.

#### Return

the result of applying the [resolve](resolve.md) function.

## Parameters

common

| | |
|---|---|
| f | the function that needs to be resolved. |
| success | the function to apply if [f](resolve.md) yields a success of type [A](resolve.md). |
| error | the function to apply if [f](resolve.md) yields an error of type [E](resolve.md). |
| throwable | the function to apply if [f](resolve.md) throws a [Throwable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html). Throwing any [Throwable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html) in the [throwable](resolve.md) function will render the [resolve](resolve.md) function nondeterministic. |
| unrecoverableState | the function to apply if [resolve](resolve.md) is in an unrecoverable state. |
