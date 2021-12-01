//[arrow-optics-test](../../../index.md)/[arrow.optics.test.laws](../index.md)/[OptionalLaws](index.md)/[modifyIdentity](modify-identity.md)

# modifyIdentity

[common]\
suspend fun &lt;[A](modify-identity.md), [B](modify-identity.md)&gt; [modifyIdentity](modify-identity.md)(optionalGen: Arb&lt;[Optional](../../../../arrow-annotations/arrow.optics/-optional/index.md)&lt;[A](modify-identity.md), [B](modify-identity.md)&gt;&gt;, aGen: Arb&lt;[A](modify-identity.md)&gt;, eq: ([A](modify-identity.md), [A](modify-identity.md)) -&gt; [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)): PropertyContext
