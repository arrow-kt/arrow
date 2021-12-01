//[arrow-meta](../../../index.md)/[arrow.common.utils](../index.md)/[KnownException](index.md)

# KnownException

[jvm]\
class [KnownException](index.md)(message: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), element: [Element](https://docs.oracle.com/javase/8/docs/api/javax/lang/model/element/Element.html)?) : [RuntimeException](https://docs.oracle.com/javase/8/docs/api/java/lang/RuntimeException.html)

## Functions

| Name | Summary |
|---|---|
| [addSuppressed](index.md#282858770%2FFunctions%2F-35121544) | [jvm]<br>fun [addSuppressed](index.md#282858770%2FFunctions%2F-35121544)(p0: [Throwable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html)) |
| [component1](component1.md) | [jvm]<br>operator fun [component1](component1.md)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [component2](component2.md) | [jvm]<br>operator fun [component2](component2.md)(): [Element](https://docs.oracle.com/javase/8/docs/api/javax/lang/model/element/Element.html)? |
| [fillInStackTrace](index.md#-1102069925%2FFunctions%2F-35121544) | [jvm]<br>open fun [fillInStackTrace](index.md#-1102069925%2FFunctions%2F-35121544)(): [Throwable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html) |
| [getLocalizedMessage](index.md#1043865560%2FFunctions%2F-35121544) | [jvm]<br>open fun [getLocalizedMessage](index.md#1043865560%2FFunctions%2F-35121544)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [getStackTrace](index.md#2050903719%2FFunctions%2F-35121544) | [jvm]<br>open fun [getStackTrace](index.md#2050903719%2FFunctions%2F-35121544)(): [Array](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-array/index.html)&lt;[StackTraceElement](https://docs.oracle.com/javase/8/docs/api/java/lang/StackTraceElement.html)&gt; |
| [getSuppressed](index.md#672492560%2FFunctions%2F-35121544) | [jvm]<br>fun [getSuppressed](index.md#672492560%2FFunctions%2F-35121544)(): [Array](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-array/index.html)&lt;[Throwable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html)&gt; |
| [initCause](index.md#-418225042%2FFunctions%2F-35121544) | [jvm]<br>open fun [initCause](index.md#-418225042%2FFunctions%2F-35121544)(p0: [Throwable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html)): [Throwable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html) |
| [printStackTrace](index.md#-1769529168%2FFunctions%2F-35121544) | [jvm]<br>open fun [printStackTrace](index.md#-1769529168%2FFunctions%2F-35121544)()<br>open fun [printStackTrace](index.md#1841853697%2FFunctions%2F-35121544)(p0: [PrintStream](https://docs.oracle.com/javase/8/docs/api/java/io/PrintStream.html))<br>open fun [printStackTrace](index.md#1175535278%2FFunctions%2F-35121544)(p0: [PrintWriter](https://docs.oracle.com/javase/8/docs/api/java/io/PrintWriter.html)) |
| [setStackTrace](index.md#2135801318%2FFunctions%2F-35121544) | [jvm]<br>open fun [setStackTrace](index.md#2135801318%2FFunctions%2F-35121544)(p0: [Array](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-array/index.html)&lt;[StackTraceElement](https://docs.oracle.com/javase/8/docs/api/java/lang/StackTraceElement.html)&gt;) |

## Properties

| Name | Summary |
|---|---|
| [cause](index.md#-654012527%2FProperties%2F-35121544) | [jvm]<br>open val [cause](index.md#-654012527%2FProperties%2F-35121544): [Throwable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html)? |
| [element](element.md) | [jvm]<br>val [element](element.md): [Element](https://docs.oracle.com/javase/8/docs/api/javax/lang/model/element/Element.html)? |
| [message](message.md) | [jvm]<br>open override val [message](message.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
