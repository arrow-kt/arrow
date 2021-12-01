//[arrow-core-test](../../../index.md)/[arrow.core.test.laws](../index.md)/[FxLaws](index.md)

# FxLaws

[common]\
object [FxLaws](index.md)

## Functions

| Name | Summary |
|---|---|
| [eager](eager.md) | [common]<br>fun &lt;[Eff](eager.md) : [Effect](../../../../arrow-continuations/arrow-continuations/arrow.continuations/-effect/index.md)&lt;*&gt;, [F](eager.md), [A](eager.md)&gt; [eager](eager.md)(pureArb: Arb&lt;[F](eager.md)&gt;, G: Arb&lt;[F](eager.md)&gt;, eq: ([F](eager.md), [F](eager.md)) -&gt; [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html), fxEager: [EagerFxBlock](../../../../arrow-core-test/arrow.core.test.laws/-eager-fx-block/index.md)&lt;[Eff](eager.md), [F](eager.md), [A](eager.md)&gt;, invoke: suspend [Eff](eager.md).([F](eager.md)) -&gt; [A](eager.md)): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[Law](../-law/index.md)&gt; |
| [suspended](suspended.md) | [common]<br>fun &lt;[Eff](suspended.md) : [Effect](../../../../arrow-continuations/arrow-continuations/arrow.continuations/-effect/index.md)&lt;*&gt;, [F](suspended.md), [A](suspended.md)&gt; [suspended](suspended.md)(pureArb: Arb&lt;[F](suspended.md)&gt;, G: Arb&lt;[F](suspended.md)&gt;, eq: ([F](suspended.md), [F](suspended.md)) -&gt; [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html), fxSuspend: [SuspendFxBlock](../../../../arrow-core-test/arrow.core.test.laws/-suspend-fx-block/index.md)&lt;[Eff](suspended.md), [F](suspended.md), [A](suspended.md)&gt;, invoke: suspend [Eff](suspended.md).([F](suspended.md)) -&gt; [A](suspended.md)): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[Law](../-law/index.md)&gt; |
