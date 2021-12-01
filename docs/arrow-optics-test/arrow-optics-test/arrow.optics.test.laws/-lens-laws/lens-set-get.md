//[arrow-optics-test](../../../index.md)/[arrow.optics.test.laws](../index.md)/[LensLaws](index.md)/[lensSetGet](lens-set-get.md)

# lensSetGet

[common]\
suspend fun &lt;[A](lens-set-get.md), [B](lens-set-get.md)&gt; [lensSetGet](lens-set-get.md)(lensGen: Arb&lt;[Lens](../../../../arrow-annotations/arrow.optics/-lens/index.md)&lt;[A](lens-set-get.md), [B](lens-set-get.md)&gt;&gt;, aGen: Arb&lt;[A](lens-set-get.md)&gt;, bGen: Arb&lt;[B](lens-set-get.md)&gt;, eq: ([B](lens-set-get.md), [B](lens-set-get.md)) -&gt; [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)): PropertyContext
