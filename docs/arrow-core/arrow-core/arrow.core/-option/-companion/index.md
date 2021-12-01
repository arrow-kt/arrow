//[arrow-core](../../../../index.md)/[arrow.core](../../index.md)/[Option](../index.md)/[Companion](index.md)

# Companion

[common]\
object [Companion](index.md)

## Functions

| Name | Summary |
|---|---|
| [catch](catch.md) | [common]<br>@[JvmStatic](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.jvm/-jvm-static/index.html)<br>@[JvmName](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.jvm/-jvm-name/index.html)(name = "tryCatchOrNone")<br>inline fun &lt;[A](catch.md)&gt; [catch](catch.md)(f: () -&gt; [A](catch.md)): [Option](../index.md)&lt;[A](catch.md)&gt;<br>Ignores exceptions and returns None if one is thrown<br>[common]<br>@[JvmStatic](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.jvm/-jvm-static/index.html)<br>@[JvmName](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.jvm/-jvm-name/index.html)(name = "tryCatch")<br>inline fun &lt;[A](catch.md)&gt; [catch](catch.md)(recover: ([Throwable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html)) -&gt; [Option](../index.md)&lt;[A](catch.md)&gt;, f: () -&gt; [A](catch.md)): [Option](../index.md)&lt;[A](catch.md)&gt; |
| [fromNullable](from-nullable.md) | [common]<br>@[JvmStatic](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.jvm/-jvm-static/index.html)<br>fun &lt;[A](from-nullable.md)&gt; [fromNullable](from-nullable.md)(a: [A](from-nullable.md)?): [Option](../index.md)&lt;[A](from-nullable.md)&gt; |
| [invoke](invoke.md) | [common]<br>@[JvmStatic](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.jvm/-jvm-static/index.html)<br>operator fun &lt;[A](invoke.md)&gt; [invoke](invoke.md)(a: [A](invoke.md)): [Option](../index.md)&lt;[A](invoke.md)&gt; |
| [lift](lift.md) | [common]<br>@[JvmStatic](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.jvm/-jvm-static/index.html)<br>fun &lt;[A](lift.md), [B](lift.md)&gt; [lift](lift.md)(f: ([A](lift.md)) -&gt; [B](lift.md)): ([Option](../index.md)&lt;[A](lift.md)&gt;) -&gt; [Option](../index.md)&lt;[B](lift.md)&gt; |
