//[arrow-core](../../index.md)/[arrow.core](index.md)/[findValid](find-valid.md)

# findValid

[common]\
inline fun &lt;[E](find-valid.md), [A](find-valid.md)&gt; [Validated](-validated/index.md)&lt;[E](find-valid.md), [A](find-valid.md)&gt;.[findValid](find-valid.md)(SE: [Semigroup](../arrow.typeclasses/-semigroup/index.md)&lt;[E](find-valid.md)&gt;, that: () -&gt; [Validated](-validated/index.md)&lt;[E](find-valid.md), [A](find-valid.md)&gt;): [Validated](-validated/index.md)&lt;[E](find-valid.md), [A](find-valid.md)&gt;

If this is valid return this, otherwise if that is valid return that, otherwise combine the failures. This is similar to [orElse](or-else.md) except that here failures are accumulated.
