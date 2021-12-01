//[arrow-core](../../index.md)/[arrow.core](index.md)/[getOrElse](get-or-else.md)

# getOrElse

[common]\
inline fun &lt;[B](get-or-else.md)&gt; [Either](-either/index.md)&lt;*, [B](get-or-else.md)&gt;.[getOrElse](get-or-else.md)(default: () -&gt; [B](get-or-else.md)): [B](get-or-else.md)

Returns the value from this [Right](-either/-right/index.md) or the given argument if this is a [Left](-either/-left/index.md).

Example:

&lt;!--- KNIT example-either-53.kt --&gt;\
Right(12).getOrElse { 17 } // Result: 12\
Left(12).getOrElse { 17 }  // Result: 17<!--- KNIT example-either-54.kt -->

[common]\
inline fun &lt;[A](get-or-else.md), [B](get-or-else.md)&gt; [Ior](-ior/index.md)&lt;[A](get-or-else.md), [B](get-or-else.md)&gt;.[getOrElse](get-or-else.md)(default: () -&gt; [B](get-or-else.md)): [B](get-or-else.md)

[common]\
inline fun &lt;[T](get-or-else.md)&gt; [Option](-option/index.md)&lt;[T](get-or-else.md)&gt;.[getOrElse](get-or-else.md)(default: () -&gt; [T](get-or-else.md)): [T](get-or-else.md)

Returns the option's value if the option is nonempty, otherwise return the result of evaluating default.

## Parameters

common

| | |
|---|---|
| default | the default expression. |

[common]\
inline fun &lt;[E](get-or-else.md), [A](get-or-else.md)&gt; [Validated](-validated/index.md)&lt;[E](get-or-else.md), [A](get-or-else.md)&gt;.[getOrElse](get-or-else.md)(default: () -&gt; [A](get-or-else.md)): [A](get-or-else.md)

Return the Valid value, or the default if Invalid
