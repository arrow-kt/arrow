//[arrow-optics-test](../../../index.md)/[arrow.optics.test.laws](../index.md)/[OptionalLaws](index.md)/[setIdempotent](set-idempotent.md)

# setIdempotent

[common]\
suspend fun &lt;[A](set-idempotent.md), [B](set-idempotent.md)&gt; [setIdempotent](set-idempotent.md)(optionalGen: Arb&lt;[Optional](../../../../arrow-annotations/arrow.optics/-optional/index.md)&lt;[A](set-idempotent.md), [B](set-idempotent.md)&gt;&gt;, aGen: Arb&lt;[A](set-idempotent.md)&gt;, bGen: Arb&lt;[B](set-idempotent.md)&gt;, eq: ([A](set-idempotent.md), [A](set-idempotent.md)) -&gt; [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)): PropertyContext
