//[arrow-optics-test](../../../index.md)/[arrow.optics.test.laws](../index.md)/[LensLaws](index.md)/[lensModifyIdentity](lens-modify-identity.md)

# lensModifyIdentity

[common]\
suspend fun &lt;[A](lens-modify-identity.md), [B](lens-modify-identity.md)&gt; [lensModifyIdentity](lens-modify-identity.md)(lensGen: Arb&lt;[Lens](../../../../arrow-annotations/arrow.optics/-lens/index.md)&lt;[A](lens-modify-identity.md), [B](lens-modify-identity.md)&gt;&gt;, aGen: Arb&lt;[A](lens-modify-identity.md)&gt;, eq: ([A](lens-modify-identity.md), [A](lens-modify-identity.md)) -&gt; [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)): PropertyContext
