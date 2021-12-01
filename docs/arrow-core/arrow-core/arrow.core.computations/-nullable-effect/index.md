//[arrow-core](../../../index.md)/[arrow.core.computations](../index.md)/[NullableEffect](index.md)

# NullableEffect

[common]\
fun interface [NullableEffect](index.md)&lt;[A](index.md)&gt; : [Effect](../../../../arrow-continuations/arrow-continuations/arrow.continuations/-effect/index.md)&lt;[A](index.md)?&gt;

## Functions

| Name | Summary |
|---|---|
| [bind](bind.md) | [common]<br>open suspend fun &lt;[B](bind.md)&gt; [B](bind.md)?.[bind](bind.md)(): [B](bind.md)<br>open suspend fun &lt;[B](bind.md)&gt; [Option](../../arrow.core/-option/index.md)&lt;[B](bind.md)&gt;.[bind](bind.md)(): [B](bind.md) |
| [control](../-restricted-option-effect/index.md#-609838202%2FFunctions%2F-1961959459) | [common]<br>abstract fun [control](../-restricted-option-effect/index.md#-609838202%2FFunctions%2F-1961959459)(): [DelimitedScope](../../../../arrow-continuations/arrow-continuations/arrow.continuations.generic/-delimited-scope/index.md)&lt;[A](index.md)?&gt; |
| [ensure](ensure.md) | [common]<br>open suspend fun [ensure](ensure.md)(value: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html))<br>Ensure check if the [value](ensure.md) is true, and if it is it allows the nullable { } binding to continue. In case it is false, then it short-circuits the binding and returns null. |

## Inheritors

| Name |
|---|
| [RestrictedNullableEffect](../-restricted-nullable-effect/index.md) |

## Extensions

| Name | Summary |
|---|---|
| [ensureNotNull](../ensure-not-null.md) | [common]<br>suspend fun &lt;[B](../ensure-not-null.md) : [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)&gt; [NullableEffect](index.md)&lt;*&gt;.[ensureNotNull](../ensure-not-null.md)(value: [B](../ensure-not-null.md)?): [B](../ensure-not-null.md)<br>Ensures that [value](../ensure-not-null.md) is not null. When the value is not null, then it will be returned as non null and the check value is now smart-checked to non-null. Otherwise, if the [value](../ensure-not-null.md) is null then the [option](../option/index.md) binding will short-circuit with [None](../../arrow.core/-none/index.md). |
