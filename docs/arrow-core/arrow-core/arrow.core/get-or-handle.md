//[arrow-core](../../index.md)/[arrow.core](index.md)/[getOrHandle](get-or-handle.md)

# getOrHandle

[common]\
inline fun &lt;[A](get-or-handle.md), [B](get-or-handle.md)&gt; [Either](-either/index.md)&lt;[A](get-or-handle.md), [B](get-or-handle.md)&gt;.[getOrHandle](get-or-handle.md)(default: ([A](get-or-handle.md)) -&gt; [B](get-or-handle.md)): [B](get-or-handle.md)

Returns the value from this [Right](-either/-right/index.md) or allows clients to transform [Left](-either/-left/index.md) to [Right](-either/-right/index.md) while providing access to the value of [Left](-either/-left/index.md).

Example:

&lt;!--- KNIT example-either-57.kt --&gt;\
Right(12).getOrHandle { 17 } // Result: 12\
Left(12).getOrHandle { it + 5 } // Result: 17<!--- KNIT example-either-58.kt -->
