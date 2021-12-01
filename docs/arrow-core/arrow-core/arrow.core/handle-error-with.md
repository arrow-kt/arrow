//[arrow-core](../../index.md)/[arrow.core](index.md)/[handleErrorWith](handle-error-with.md)

# handleErrorWith

[common]\
inline fun &lt;[A](handle-error-with.md), [B](handle-error-with.md), [C](handle-error-with.md)&gt; [Either](-either/index.md)&lt;[A](handle-error-with.md), [B](handle-error-with.md)&gt;.[handleErrorWith](handle-error-with.md)(f: ([A](handle-error-with.md)) -&gt; [Either](-either/index.md)&lt;[C](handle-error-with.md), [B](handle-error-with.md)&gt;): [Either](-either/index.md)&lt;[C](handle-error-with.md), [B](handle-error-with.md)&gt;

Applies the given function f if this is a [Left](-either/-left/index.md), otherwise returns this if this is a [Right](-either/-right/index.md). This is like flatMap for the exception.

[common]\
inline fun &lt;[A](handle-error-with.md)&gt; [Option](-option/index.md)&lt;[A](handle-error-with.md)&gt;.[handleErrorWith](handle-error-with.md)(f: ([Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)) -&gt; [Option](-option/index.md)&lt;[A](handle-error-with.md)&gt;): [Option](-option/index.md)&lt;[A](handle-error-with.md)&gt;

inline fun &lt;[E](handle-error-with.md), [A](handle-error-with.md)&gt; [Validated](-validated/index.md)&lt;[E](handle-error-with.md), [A](handle-error-with.md)&gt;.[handleErrorWith](handle-error-with.md)(f: ([E](handle-error-with.md)) -&gt; [Validated](-validated/index.md)&lt;[E](handle-error-with.md), [A](handle-error-with.md)&gt;): [Validated](-validated/index.md)&lt;[E](handle-error-with.md), [A](handle-error-with.md)&gt;

[common]\
inline fun &lt;[A](handle-error-with.md)&gt; [Result](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-result/index.html)&lt;[A](handle-error-with.md)&gt;.[handleErrorWith](handle-error-with.md)(transform: (throwable: [Throwable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html)) -&gt; [Result](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-result/index.html)&lt;[A](handle-error-with.md)&gt;): [Result](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-result/index.html)&lt;[A](handle-error-with.md)&gt;

Compose a recovering [transform](handle-error-with.md) operation on the failure value [Throwable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html) whilst flattening [Result](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-result/index.html).

## See also

common

| | |
|---|---|
| [recoverCatching](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/index.html) | if you want run a function that catches and maps recovers with (Throwable) -&gt; A. |
