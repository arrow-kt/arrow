//[arrow-optics-test](../../../index.md)/[arrow.optics.test.laws](../index.md)/[LensLaws](index.md)/[lensGetSet](lens-get-set.md)

# lensGetSet

[common]\
suspend fun &lt;[A](lens-get-set.md), [B](lens-get-set.md)&gt; [lensGetSet](lens-get-set.md)(lensGen: Arb&lt;[Lens](../../../../arrow-annotations/arrow.optics/-lens/index.md)&lt;[A](lens-get-set.md), [B](lens-get-set.md)&gt;&gt;, aGen: Arb&lt;[A](lens-get-set.md)&gt;, eq: ([A](lens-get-set.md), [A](lens-get-set.md)) -&gt; [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)): PropertyContext
