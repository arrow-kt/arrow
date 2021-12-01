//[arrow-core-test](../../../index.md)/[arrow.core.test.laws](../index.md)/[FxLaws](index.md)/[suspended](suspended.md)

# suspended

[common]\
fun &lt;[Eff](suspended.md) : [Effect](../../../../arrow-continuations/arrow-continuations/arrow.continuations/-effect/index.md)&lt;*&gt;, [F](suspended.md), [A](suspended.md)&gt; [suspended](suspended.md)(pureArb: Arb&lt;[F](suspended.md)&gt;, G: Arb&lt;[F](suspended.md)&gt;, eq: ([F](suspended.md), [F](suspended.md)) -&gt; [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html), fxSuspend: [SuspendFxBlock](../../../../arrow-core-test/arrow.core.test.laws/-suspend-fx-block/index.md)&lt;[Eff](suspended.md), [F](suspended.md), [A](suspended.md)&gt;, invoke: suspend [Eff](suspended.md).([F](suspended.md)) -&gt; [A](suspended.md)): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[Law](../-law/index.md)&gt;
