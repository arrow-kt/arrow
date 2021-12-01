//[arrow-core](../../../../index.md)/[arrow.core](../../index.md)/[Ior](../index.md)/[Companion](index.md)

# Companion

[common]\
object [Companion](index.md)

## Functions

| Name | Summary |
|---|---|
| [bothNel](both-nel.md) | [common]<br>@[JvmStatic](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.jvm/-jvm-static/index.html)<br>fun &lt;[A](both-nel.md), [B](both-nel.md)&gt; [bothNel](both-nel.md)(a: [A](both-nel.md), b: [B](both-nel.md)): [IorNel](../../index.md#765478045%2FClasslikes%2F-1961959459)&lt;[A](both-nel.md), [B](both-nel.md)&gt; |
| [fromNullables](from-nullables.md) | [common]<br>@[JvmStatic](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.jvm/-jvm-static/index.html)<br>fun &lt;[A](from-nullables.md), [B](from-nullables.md)&gt; [fromNullables](from-nullables.md)(a: [A](from-nullables.md)?, b: [B](from-nullables.md)?): [Ior](../index.md)&lt;[A](from-nullables.md), [B](from-nullables.md)&gt;?<br>Create an [Ior](../index.md) from two nullables if at least one of them is defined. |
| [leftNel](left-nel.md) | [common]<br>@[JvmStatic](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.jvm/-jvm-static/index.html)<br>fun &lt;[A](left-nel.md), [B](left-nel.md)&gt; [leftNel](left-nel.md)(a: [A](left-nel.md)): [IorNel](../../index.md#765478045%2FClasslikes%2F-1961959459)&lt;[A](left-nel.md), [B](left-nel.md)&gt; |
| [lift](lift.md) | [common]<br>@[JvmStatic](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.jvm/-jvm-static/index.html)<br>fun &lt;[A](lift.md), [B](lift.md), [C](lift.md)&gt; [lift](lift.md)(f: ([B](lift.md)) -&gt; [C](lift.md)): ([Ior](../index.md)&lt;[A](lift.md), [B](lift.md)&gt;) -&gt; [Ior](../index.md)&lt;[A](lift.md), [C](lift.md)&gt;<br>Lifts a function (B) -&gt; C to the [Ior](../index.md) structure returning a polymorphic function that can be applied over all [Ior](../index.md) values in the shape of Ior<br>[common]<br>@[JvmStatic](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.jvm/-jvm-static/index.html)<br>fun &lt;[A](lift.md), [B](lift.md), [C](lift.md), [D](lift.md)&gt; [lift](lift.md)(fa: ([A](lift.md)) -&gt; [C](lift.md), fb: ([B](lift.md)) -&gt; [D](lift.md)): ([Ior](../index.md)&lt;[A](lift.md), [B](lift.md)&gt;) -&gt; [Ior](../index.md)&lt;[C](lift.md), [D](lift.md)&gt; |
