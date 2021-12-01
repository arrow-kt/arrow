//[arrow-core](../../index.md)/[arrow.core](index.md)/[rightIfNotNull](right-if-not-null.md)

# rightIfNotNull

[common]\
inline fun &lt;[A](right-if-not-null.md), [B](right-if-not-null.md)&gt; [B](right-if-not-null.md)?.[rightIfNotNull](right-if-not-null.md)(default: () -&gt; [A](right-if-not-null.md)): [Either](-either/index.md)&lt;[A](right-if-not-null.md), [B](right-if-not-null.md)&gt;

Returns [Right](-either/-right/index.md) if the value of type B is not null, otherwise the specified A value wrapped into an [Left](-either/-left/index.md).

Example:

&lt;!--- KNIT example-either-66.kt --&gt;\
"value".rightIfNotNull { "left" } // Right(b="value")\
null.rightIfNotNull { "left" }    // Left(a="left")<!--- KNIT example-either-67.kt -->
