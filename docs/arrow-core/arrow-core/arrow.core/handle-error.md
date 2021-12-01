//[arrow-core](../../index.md)/[arrow.core](index.md)/[handleError](handle-error.md)

# handleError

[common]\
inline fun &lt;[A](handle-error.md), [B](handle-error.md)&gt; [Either](-either/index.md)&lt;[A](handle-error.md), [B](handle-error.md)&gt;.[handleError](handle-error.md)(f: ([A](handle-error.md)) -&gt; [B](handle-error.md)): [Either](-either/index.md)&lt;[A](handle-error.md), [B](handle-error.md)&gt;

inline fun &lt;[A](handle-error.md)&gt; [Option](-option/index.md)&lt;[A](handle-error.md)&gt;.[handleError](handle-error.md)(f: ([Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)) -&gt; [A](handle-error.md)): [Option](-option/index.md)&lt;[A](handle-error.md)&gt;

inline fun &lt;[E](handle-error.md), [A](handle-error.md)&gt; [Validated](-validated/index.md)&lt;[E](handle-error.md), [A](handle-error.md)&gt;.[handleError](handle-error.md)(f: ([E](handle-error.md)) -&gt; [A](handle-error.md)): [Validated](-validated/index.md)&lt;[Nothing](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-nothing/index.html), [A](handle-error.md)&gt;
