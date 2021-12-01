//[arrow-core-test](../../../index.md)/[arrow.core.test.laws](../index.md)/[FxLaws](index.md)/[eager](eager.md)

# eager

[common]\
fun &lt;[Eff](eager.md) : [Effect](../../../../arrow-continuations/arrow-continuations/arrow.continuations/-effect/index.md)&lt;*&gt;, [F](eager.md), [A](eager.md)&gt; [eager](eager.md)(pureArb: Arb&lt;[F](eager.md)&gt;, G: Arb&lt;[F](eager.md)&gt;, eq: ([F](eager.md), [F](eager.md)) -&gt; [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html), fxEager: [EagerFxBlock](../../../../arrow-core-test/arrow.core.test.laws/-eager-fx-block/index.md)&lt;[Eff](eager.md), [F](eager.md), [A](eager.md)&gt;, invoke: suspend [Eff](eager.md).([F](eager.md)) -&gt; [A](eager.md)): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[Law](../-law/index.md)&gt;
