//[arrow-optics-test](../../../index.md)/[arrow.optics.test.laws](../index.md)/[OptionalLaws](index.md)/[composeModify](compose-modify.md)

# composeModify

[common]\
suspend fun &lt;[A](compose-modify.md), [B](compose-modify.md)&gt; [composeModify](compose-modify.md)(optionalGen: Arb&lt;[Optional](../../../../arrow-annotations/arrow.optics/-optional/index.md)&lt;[A](compose-modify.md), [B](compose-modify.md)&gt;&gt;, aGen: Arb&lt;[A](compose-modify.md)&gt;, funcGen: Arb&lt;([B](compose-modify.md)) -&gt; [B](compose-modify.md)&gt;, eq: ([A](compose-modify.md), [A](compose-modify.md)) -&gt; [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)): PropertyContext
