//[arrow-optics-test](../../../index.md)/[arrow.optics.test.laws](../index.md)/[LensLaws](index.md)/[lensSetIdempotent](lens-set-idempotent.md)

# lensSetIdempotent

[common]\
suspend fun &lt;[A](lens-set-idempotent.md), [B](lens-set-idempotent.md)&gt; [lensSetIdempotent](lens-set-idempotent.md)(lensGen: Arb&lt;[Lens](../../../../arrow-annotations/arrow.optics/-lens/index.md)&lt;[A](lens-set-idempotent.md), [B](lens-set-idempotent.md)&gt;&gt;, aGen: Arb&lt;[A](lens-set-idempotent.md)&gt;, bGen: Arb&lt;[B](lens-set-idempotent.md)&gt;, eq: ([A](lens-set-idempotent.md), [A](lens-set-idempotent.md)) -&gt; [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)): PropertyContext
