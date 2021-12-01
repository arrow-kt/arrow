//[arrow-core](../../../../index.md)/[arrow.core](../../index.md)/[Either](../index.md)/[Companion](index.md)/[catch](catch.md)

# catch

[common]\

@[JvmStatic](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.jvm/-jvm-static/index.html)

@[JvmName](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.jvm/-jvm-name/index.html)(name = "tryCatch")

inline fun &lt;[R](catch.md)&gt; [catch](catch.md)(f: () -&gt; [R](catch.md)): [Either](../index.md)&lt;[Throwable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html), [R](catch.md)&gt;

@[JvmStatic](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.jvm/-jvm-static/index.html)

@[JvmName](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.jvm/-jvm-name/index.html)(name = "tryCatch")

inline fun &lt;[L](catch.md), [R](catch.md)&gt; [catch](catch.md)(fe: ([Throwable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html)) -&gt; [L](catch.md), f: () -&gt; [R](catch.md)): [Either](../index.md)&lt;[L](catch.md), [R](catch.md)&gt;
