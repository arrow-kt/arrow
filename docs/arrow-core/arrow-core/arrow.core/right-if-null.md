//[arrow-core](../../index.md)/[arrow.core](index.md)/[rightIfNull](right-if-null.md)

# rightIfNull

[common]\
inline fun &lt;[A](right-if-null.md)&gt; [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?.[rightIfNull](right-if-null.md)(default: () -&gt; [A](right-if-null.md)): [Either](-either/index.md)&lt;[A](right-if-null.md), [Nothing](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-nothing/index.html)?&gt;

Returns [Right](-either/-right/index.md) if the value of type Any? is null, otherwise the specified A value wrapped into an [Left](-either/-left/index.md).
