//[arrow-core](../../index.md)/[arrow.core](index.md)/[orNull](or-null.md)

# orNull

[common]\
fun &lt;[B](or-null.md)&gt; [Either](-either/index.md)&lt;*, [B](or-null.md)&gt;.[orNull](or-null.md)(): [B](or-null.md)?

Returns the value from this [Right](-either/-right/index.md) or null if this is a [Left](-either/-left/index.md).

Example:

&lt;!--- KNIT example-either-55.kt --&gt;\
Right(12).orNull() // Result: 12\
Left(12).orNull()  // Result: null<!--- KNIT example-either-56.kt -->

[common]\
fun &lt;[E](or-null.md), [A](or-null.md)&gt; [Validated](-validated/index.md)&lt;[E](or-null.md), [A](or-null.md)&gt;.[orNull](or-null.md)(): [A](or-null.md)?

Return the Valid value, or null if Invalid
