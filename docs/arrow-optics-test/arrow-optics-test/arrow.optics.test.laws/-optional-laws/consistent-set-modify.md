//[arrow-optics-test](../../../index.md)/[arrow.optics.test.laws](../index.md)/[OptionalLaws](index.md)/[consistentSetModify](consistent-set-modify.md)

# consistentSetModify

[common]\
suspend fun &lt;[A](consistent-set-modify.md), [B](consistent-set-modify.md)&gt; [consistentSetModify](consistent-set-modify.md)(optionalGen: Arb&lt;[Optional](../../../../arrow-annotations/arrow.optics/-optional/index.md)&lt;[A](consistent-set-modify.md), [B](consistent-set-modify.md)&gt;&gt;, aGen: Arb&lt;[A](consistent-set-modify.md)&gt;, bGen: Arb&lt;[B](consistent-set-modify.md)&gt;, eq: ([A](consistent-set-modify.md), [A](consistent-set-modify.md)) -&gt; [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)): PropertyContext
