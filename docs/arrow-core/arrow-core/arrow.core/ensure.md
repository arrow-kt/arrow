//[arrow-core](../../index.md)/[arrow.core](index.md)/[ensure](ensure.md)

# ensure

[common]\
inline fun &lt;[A](ensure.md), [B](ensure.md)&gt; [Either](-either/index.md)&lt;[A](ensure.md), [B](ensure.md)&gt;.[ensure](ensure.md)(error: () -&gt; [A](ensure.md), predicate: ([B](ensure.md)) -&gt; [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)): [Either](-either/index.md)&lt;[A](ensure.md), [B](ensure.md)&gt;

inline fun &lt;[A](ensure.md)&gt; [Option](-option/index.md)&lt;[A](ensure.md)&gt;.[ensure](ensure.md)(error: () -&gt; [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html), predicate: ([A](ensure.md)) -&gt; [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)): [Option](-option/index.md)&lt;[A](ensure.md)&gt;
