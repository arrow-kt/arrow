//[arrow-core-test](../../../index.md)/[arrow.core.test.laws](../index.md)/[MonoidLaws](index.md)

# MonoidLaws

[common]\
object [MonoidLaws](index.md)

## Functions

| Name | Summary |
|---|---|
| [combineAllIsDerived](combine-all-is-derived.md) | [common]<br>suspend fun &lt;[F](combine-all-is-derived.md)&gt; [Monoid](../../../../arrow-core/arrow-core/arrow.typeclasses/-monoid/index.md)&lt;[F](combine-all-is-derived.md)&gt;.[combineAllIsDerived](combine-all-is-derived.md)(GEN: Arb&lt;[F](combine-all-is-derived.md)&gt;, eq: ([F](combine-all-is-derived.md), [F](combine-all-is-derived.md)) -&gt; [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)): PropertyContext |
| [combineAllOfEmptyIsEmpty](combine-all-of-empty-is-empty.md) | [common]<br>fun &lt;[F](combine-all-of-empty-is-empty.md)&gt; [Monoid](../../../../arrow-core/arrow-core/arrow.typeclasses/-monoid/index.md)&lt;[F](combine-all-of-empty-is-empty.md)&gt;.[combineAllOfEmptyIsEmpty](combine-all-of-empty-is-empty.md)(eq: ([F](combine-all-of-empty-is-empty.md), [F](combine-all-of-empty-is-empty.md)) -&gt; [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)) |
| [laws](laws.md) | [common]<br>fun &lt;[F](laws.md)&gt; [laws](laws.md)(M: [Monoid](../../../../arrow-core/arrow-core/arrow.typeclasses/-monoid/index.md)&lt;[F](laws.md)&gt;, GEN: Arb&lt;[F](laws.md)&gt;, eq: ([F](laws.md), [F](laws.md)) -&gt; [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) = { a, b -&gt; a == b }): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[Law](../-law/index.md)&gt; |
| [monoidLeftIdentity](monoid-left-identity.md) | [common]<br>suspend fun &lt;[F](monoid-left-identity.md)&gt; [Monoid](../../../../arrow-core/arrow-core/arrow.typeclasses/-monoid/index.md)&lt;[F](monoid-left-identity.md)&gt;.[monoidLeftIdentity](monoid-left-identity.md)(GEN: Arb&lt;[F](monoid-left-identity.md)&gt;, eq: ([F](monoid-left-identity.md), [F](monoid-left-identity.md)) -&gt; [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)): PropertyContext |
| [monoidRightIdentity](monoid-right-identity.md) | [common]<br>suspend fun &lt;[F](monoid-right-identity.md)&gt; [Monoid](../../../../arrow-core/arrow-core/arrow.typeclasses/-monoid/index.md)&lt;[F](monoid-right-identity.md)&gt;.[monoidRightIdentity](monoid-right-identity.md)(GEN: Arb&lt;[F](monoid-right-identity.md)&gt;, eq: ([F](monoid-right-identity.md), [F](monoid-right-identity.md)) -&gt; [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)): PropertyContext |
