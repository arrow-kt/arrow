//[arrow-optics-test](../../../index.md)/[arrow.optics.test.laws](../index.md)/[LensLaws](index.md)/[lensConsistentSetModify](lens-consistent-set-modify.md)

# lensConsistentSetModify

[common]\
suspend fun &lt;[A](lens-consistent-set-modify.md), [B](lens-consistent-set-modify.md)&gt; [lensConsistentSetModify](lens-consistent-set-modify.md)(lensGen: Arb&lt;[Lens](../../../../arrow-annotations/arrow.optics/-lens/index.md)&lt;[A](lens-consistent-set-modify.md), [B](lens-consistent-set-modify.md)&gt;&gt;, aGen: Arb&lt;[A](lens-consistent-set-modify.md)&gt;, bGen: Arb&lt;[B](lens-consistent-set-modify.md)&gt;, eq: ([A](lens-consistent-set-modify.md), [A](lens-consistent-set-modify.md)) -&gt; [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)): PropertyContext
