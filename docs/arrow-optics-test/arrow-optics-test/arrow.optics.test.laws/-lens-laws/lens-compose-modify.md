//[arrow-optics-test](../../../index.md)/[arrow.optics.test.laws](../index.md)/[LensLaws](index.md)/[lensComposeModify](lens-compose-modify.md)

# lensComposeModify

[common]\
suspend fun &lt;[A](lens-compose-modify.md), [B](lens-compose-modify.md)&gt; [lensComposeModify](lens-compose-modify.md)(lensGen: Arb&lt;[Lens](../../../../arrow-annotations/arrow.optics/-lens/index.md)&lt;[A](lens-compose-modify.md), [B](lens-compose-modify.md)&gt;&gt;, aGen: Arb&lt;[A](lens-compose-modify.md)&gt;, funcGen: Arb&lt;([B](lens-compose-modify.md)) -&gt; [B](lens-compose-modify.md)&gt;, eq: ([A](lens-compose-modify.md), [A](lens-compose-modify.md)) -&gt; [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)): PropertyContext
