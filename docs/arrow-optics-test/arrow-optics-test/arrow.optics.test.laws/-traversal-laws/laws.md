//[arrow-optics-test](../../../index.md)/[arrow.optics.test.laws](../index.md)/[TraversalLaws](index.md)/[laws](laws.md)

# laws

[common]\
fun &lt;[A](laws.md), [B](laws.md) : [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)&gt; [laws](laws.md)(traversal: [Traversal](../../../../arrow-annotations/arrow.optics/-traversal/index.md)&lt;[A](laws.md), [B](laws.md)&gt;, aGen: Arb&lt;[A](laws.md)&gt;, bGen: Arb&lt;[B](laws.md)&gt;, funcGen: Arb&lt;([B](laws.md)) -&gt; [B](laws.md)&gt;, eq: ([A](laws.md), [A](laws.md)) -&gt; [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) = { a, b -&gt; a == b }): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[Law](../../../../arrow-core-test/arrow-core-test/arrow.core.test.laws/-law/index.md)&gt;
