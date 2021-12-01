//[arrow-core](../../../index.md)/[arrow.core.computations](../index.md)/[RestrictedNullableEffect](index.md)

# RestrictedNullableEffect

[common]\
fun interface [RestrictedNullableEffect](index.md)&lt;[A](index.md)&gt; : [NullableEffect](../-nullable-effect/index.md)&lt;[A](index.md)&gt;

## Functions

| Name | Summary |
|---|---|
| [bind](../-nullable-effect/bind.md) | [common]<br>open suspend fun &lt;[B](../-nullable-effect/bind.md)&gt; [B](../-nullable-effect/bind.md)?.[bind](../-nullable-effect/bind.md)(): [B](../-nullable-effect/bind.md)<br>open suspend fun &lt;[B](../-nullable-effect/bind.md)&gt; [Option](../../arrow.core/-option/index.md)&lt;[B](../-nullable-effect/bind.md)&gt;.[bind](../-nullable-effect/bind.md)(): [B](../-nullable-effect/bind.md) |
| [control](../-restricted-option-effect/index.md#-609838202%2FFunctions%2F-1961959459) | [common]<br>abstract fun [control](../-restricted-option-effect/index.md#-609838202%2FFunctions%2F-1961959459)(): [DelimitedScope](../../../../arrow-continuations/arrow-continuations/arrow.continuations.generic/-delimited-scope/index.md)&lt;[A](index.md)?&gt; |
| [ensure](../-nullable-effect/ensure.md) | [common]<br>open suspend fun [ensure](../-nullable-effect/ensure.md)(value: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html))<br>Ensure check if the [value](../-nullable-effect/ensure.md) is true, and if it is it allows the nullable { } binding to continue. In case it is false, then it short-circuits the binding and returns null. |
