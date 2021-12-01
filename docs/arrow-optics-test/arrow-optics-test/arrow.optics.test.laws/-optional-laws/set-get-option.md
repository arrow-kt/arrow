//[arrow-optics-test](../../../index.md)/[arrow.optics.test.laws](../index.md)/[OptionalLaws](index.md)/[setGetOption](set-get-option.md)

# setGetOption

[common]\
suspend fun &lt;[A](set-get-option.md), [B](set-get-option.md)&gt; [setGetOption](set-get-option.md)(optionalGen: Arb&lt;[Optional](../../../../arrow-annotations/arrow.optics/-optional/index.md)&lt;[A](set-get-option.md), [B](set-get-option.md)&gt;&gt;, aGen: Arb&lt;[A](set-get-option.md)&gt;, bGen: Arb&lt;[B](set-get-option.md)&gt;, eq: ([B](set-get-option.md)?, [B](set-get-option.md)?) -&gt; [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)): PropertyContext
