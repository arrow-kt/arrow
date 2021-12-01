//[arrow-core-test](../../../index.md)/[arrow.core.test.laws](../index.md)/[MonoidLaws](index.md)/[laws](laws.md)

# laws

[common]\
fun &lt;[F](laws.md)&gt; [laws](laws.md)(M: [Monoid](../../../../arrow-core/arrow-core/arrow.typeclasses/-monoid/index.md)&lt;[F](laws.md)&gt;, GEN: Arb&lt;[F](laws.md)&gt;, eq: ([F](laws.md), [F](laws.md)) -&gt; [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) = { a, b -&gt; a == b }): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[Law](../-law/index.md)&gt;
