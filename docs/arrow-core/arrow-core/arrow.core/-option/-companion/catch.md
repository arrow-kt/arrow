//[arrow-core](../../../../index.md)/[arrow.core](../../index.md)/[Option](../index.md)/[Companion](index.md)/[catch](catch.md)

# catch

[common]\

@[JvmStatic](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.jvm/-jvm-static/index.html)

@[JvmName](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.jvm/-jvm-name/index.html)(name = "tryCatchOrNone")

inline fun &lt;[A](catch.md)&gt; [catch](catch.md)(f: () -&gt; [A](catch.md)): [Option](../index.md)&lt;[A](catch.md)&gt;

Ignores exceptions and returns None if one is thrown

[common]\

@[JvmStatic](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.jvm/-jvm-static/index.html)

@[JvmName](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.jvm/-jvm-name/index.html)(name = "tryCatch")

inline fun &lt;[A](catch.md)&gt; [catch](catch.md)(recover: ([Throwable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html)) -&gt; [Option](../index.md)&lt;[A](catch.md)&gt;, f: () -&gt; [A](catch.md)): [Option](../index.md)&lt;[A](catch.md)&gt;
