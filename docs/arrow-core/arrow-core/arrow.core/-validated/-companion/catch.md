//[arrow-core](../../../../index.md)/[arrow.core](../../index.md)/[Validated](../index.md)/[Companion](index.md)/[catch](catch.md)

# catch

[common]\

@[JvmStatic](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.jvm/-jvm-static/index.html)

@[JvmName](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.jvm/-jvm-name/index.html)(name = "tryCatch")

inline fun &lt;[A](catch.md)&gt; [catch](catch.md)(f: () -&gt; [A](catch.md)): [Validated](../index.md)&lt;[Throwable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html), [A](catch.md)&gt;

@[JvmStatic](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.jvm/-jvm-static/index.html)

@[JvmName](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.jvm/-jvm-name/index.html)(name = "tryCatch")

inline fun &lt;[E](catch.md), [A](catch.md)&gt; [catch](catch.md)(recover: ([Throwable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html)) -&gt; [E](catch.md), f: () -&gt; [A](catch.md)): [Validated](../index.md)&lt;[E](catch.md), [A](catch.md)&gt;
