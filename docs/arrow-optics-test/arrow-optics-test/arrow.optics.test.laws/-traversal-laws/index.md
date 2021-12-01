//[arrow-optics-test](../../../index.md)/[arrow.optics.test.laws](../index.md)/[TraversalLaws](index.md)

# TraversalLaws

[common]\
object [TraversalLaws](index.md)

## Functions

| Name | Summary |
|---|---|
| [composeModify](compose-modify.md) | [common]<br>suspend fun &lt;[A](compose-modify.md), [B](compose-modify.md)&gt; [Traversal](../../../../arrow-annotations/arrow.optics/-traversal/index.md)&lt;[A](compose-modify.md), [B](compose-modify.md)&gt;.[composeModify](compose-modify.md)(aGen: Arb&lt;[A](compose-modify.md)&gt;, funcGen: Arb&lt;([B](compose-modify.md)) -&gt; [B](compose-modify.md)&gt;, eq: ([A](compose-modify.md), [A](compose-modify.md)) -&gt; [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)): PropertyContext |
| [laws](laws.md) | [common]<br>fun &lt;[A](laws.md), [B](laws.md) : [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)&gt; [laws](laws.md)(traversal: [Traversal](../../../../arrow-annotations/arrow.optics/-traversal/index.md)&lt;[A](laws.md), [B](laws.md)&gt;, aGen: Arb&lt;[A](laws.md)&gt;, bGen: Arb&lt;[B](laws.md)&gt;, funcGen: Arb&lt;([B](laws.md)) -&gt; [B](laws.md)&gt;, eq: ([A](laws.md), [A](laws.md)) -&gt; [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) = { a, b -&gt; a == b }): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[Law](../../../../arrow-core-test/arrow-core-test/arrow.core.test.laws/-law/index.md)&gt; |
| [modifyIdentity](modify-identity.md) | [common]<br>suspend fun &lt;[A](modify-identity.md), [B](modify-identity.md)&gt; [Traversal](../../../../arrow-annotations/arrow.optics/-traversal/index.md)&lt;[A](modify-identity.md), [B](modify-identity.md)&gt;.[modifyIdentity](modify-identity.md)(aGen: Arb&lt;[A](modify-identity.md)&gt;, eq: ([A](modify-identity.md), [A](modify-identity.md)) -&gt; [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)): PropertyContext |
| [setIdempotent](set-idempotent.md) | [common]<br>suspend fun &lt;[A](set-idempotent.md), [B](set-idempotent.md)&gt; [Traversal](../../../../arrow-annotations/arrow.optics/-traversal/index.md)&lt;[A](set-idempotent.md), [B](set-idempotent.md)&gt;.[setIdempotent](set-idempotent.md)(aGen: Arb&lt;[A](set-idempotent.md)&gt;, bGen: Arb&lt;[B](set-idempotent.md)&gt;, eq: ([A](set-idempotent.md), [A](set-idempotent.md)) -&gt; [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)): PropertyContext |
