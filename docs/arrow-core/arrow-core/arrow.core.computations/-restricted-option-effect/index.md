//[arrow-core](../../../index.md)/[arrow.core.computations](../index.md)/[RestrictedOptionEffect](index.md)

# RestrictedOptionEffect

[common]\
fun interface [RestrictedOptionEffect](index.md)&lt;[A](index.md)&gt; : [OptionEffect](../-option-effect/index.md)&lt;[A](index.md)&gt;

## Functions

| Name | Summary |
|---|---|
| [bind](../-option-effect/bind.md) | [common]<br>open suspend fun &lt;[B](../-option-effect/bind.md)&gt; [Option](../../arrow.core/-option/index.md)&lt;[B](../-option-effect/bind.md)&gt;.[bind](../-option-effect/bind.md)(): [B](../-option-effect/bind.md) |
| [control](index.md#-609838202%2FFunctions%2F-1961959459) | [common]<br>abstract fun [control](index.md#-609838202%2FFunctions%2F-1961959459)(): [DelimitedScope](../../../../arrow-continuations/arrow-continuations/arrow.continuations.generic/-delimited-scope/index.md)&lt;[Option](../../arrow.core/-option/index.md)&lt;[A](index.md)&gt;&gt; |
| [ensure](../-option-effect/ensure.md) | [common]<br>open suspend fun [ensure](../-option-effect/ensure.md)(value: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html))<br>Ensure check if the [value](../-option-effect/ensure.md) is true, and if it is it allows the option { } binding to continue. In case it is false, then it short-circuits the binding and returns [None](../../arrow.core/-none/index.md). |
