//[arrow-continuations](../../../index.md)/[arrow.continuations.generic](../index.md)/[ControlThrowable](index.md)

# ControlThrowable

[common, native]\
open class [ControlThrowable](index.md) : [Throwable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html)

A [Throwable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html) class intended for control flow. Instance of [ControlThrowable](index.md) should **not** be caught, and arrow.core.NonFatal does not catch this [Throwable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html). Thus by extension Either.catch and Validated.catch also don't catch [ControlThrowable](index.md).

[js, jvm]\
open class [ControlThrowable](index.md) : [Throwable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html)

A [Throwable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html) class intended for control flow. Instance of ControlThrowable.kt should **not** be caught, and arrow.core.NonFatal does not catch this [Throwable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html). Thus by extension Either.catch and Validated.catch also don't catch ControlThrowable.kt.

## Constructors

| | |
|---|---|
| [ControlThrowable](-control-throwable.md) | [js]<br>fun [ControlThrowable](-control-throwable.md)() |
| [ControlThrowable](-control-throwable.md) | [jvm]<br>fun [ControlThrowable](-control-throwable.md)() |
| [ControlThrowable](-control-throwable.md) | [native]<br>fun [ControlThrowable](-control-throwable.md)() |
| [ControlThrowable](-control-throwable.md) | [common]<br>fun [ControlThrowable](-control-throwable.md)() |

## Functions

| Name | Summary |
|---|---|
| [addSuppressed](index.md#282858770%2FFunctions%2F1445001142) | [jvm]<br>fun [addSuppressed](index.md#282858770%2FFunctions%2F1445001142)(p0: [Throwable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html)) |
| [fillInStackTrace](fill-in-stack-trace.md) | [jvm]<br>open override fun [fillInStackTrace](fill-in-stack-trace.md)(): [Throwable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html) |
| [getLocalizedMessage](index.md#1043865560%2FFunctions%2F1445001142) | [jvm]<br>open fun [getLocalizedMessage](index.md#1043865560%2FFunctions%2F1445001142)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [getStackTrace](index.md#2050903719%2FFunctions%2F1445001142) | [jvm]<br>open fun [getStackTrace](index.md#2050903719%2FFunctions%2F1445001142)(): [Array](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-array/index.html)&lt;[StackTraceElement](https://docs.oracle.com/javase/8/docs/api/java/lang/StackTraceElement.html)&gt; |
| [getSuppressed](index.md#672492560%2FFunctions%2F1445001142) | [jvm]<br>fun [getSuppressed](index.md#672492560%2FFunctions%2F1445001142)(): [Array](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-array/index.html)&lt;[Throwable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html)&gt; |
| [initCause](index.md#-418225042%2FFunctions%2F1445001142) | [jvm]<br>open fun [initCause](index.md#-418225042%2FFunctions%2F1445001142)(p0: [Throwable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html)): [Throwable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html) |
| [printStackTrace](index.md#-1769529168%2FFunctions%2F1445001142) | [jvm]<br>open fun [printStackTrace](index.md#-1769529168%2FFunctions%2F1445001142)()<br>open fun [printStackTrace](index.md#1841853697%2FFunctions%2F1445001142)(p0: [PrintStream](https://docs.oracle.com/javase/8/docs/api/java/io/PrintStream.html))<br>open fun [printStackTrace](index.md#1175535278%2FFunctions%2F1445001142)(p0: [PrintWriter](https://docs.oracle.com/javase/8/docs/api/java/io/PrintWriter.html)) |
| [setStackTrace](index.md#2135801318%2FFunctions%2F1445001142) | [jvm]<br>open fun [setStackTrace](index.md#2135801318%2FFunctions%2F1445001142)(p0: [Array](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-array/index.html)&lt;[StackTraceElement](https://docs.oracle.com/javase/8/docs/api/java/lang/StackTraceElement.html)&gt;) |

## Properties

| Name | Summary |
|---|---|
| [cause](index.md#-654012527%2FProperties%2F1378859840) | [common]<br>open val [cause](index.md#-654012527%2FProperties%2F1378859840): [Throwable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html)? |
| [cause](index.md#-654012527%2FProperties%2F606036706) | [js]<br>open val [cause](index.md#-654012527%2FProperties%2F606036706): [Throwable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html)? |
| [cause](index.md#-654012527%2FProperties%2F1445001142) | [jvm]<br>open val [cause](index.md#-654012527%2FProperties%2F1445001142): [Throwable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html)? |
| [cause](index.md#-654012527%2FProperties%2F-588295404) | [native]<br>open val [cause](index.md#-654012527%2FProperties%2F-588295404): [Throwable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html)? |
| [message](index.md#1824300659%2FProperties%2F1378859840) | [common]<br>open val [message](index.md#1824300659%2FProperties%2F1378859840): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)? |
| [message](index.md#1824300659%2FProperties%2F606036706) | [js]<br>open val [message](index.md#1824300659%2FProperties%2F606036706): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)? |
| [message](index.md#1824300659%2FProperties%2F1445001142) | [jvm]<br>open val [message](index.md#1824300659%2FProperties%2F1445001142): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)? |
| [message](index.md#1824300659%2FProperties%2F-588295404) | [native]<br>open val [message](index.md#1824300659%2FProperties%2F-588295404): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)? |

## Inheritors

| Name |
|---|
| [ShortCircuit](../-short-circuit/index.md) |
