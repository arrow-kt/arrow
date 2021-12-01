//[arrow-core](../../../index.md)/[arrow.core.computations](../index.md)/[RestrictedEitherEffect](index.md)

# RestrictedEitherEffect

[common]\
fun interface [RestrictedEitherEffect](index.md)&lt;[E](index.md), [A](index.md)&gt; : [EitherEffect](../-either-effect/index.md)&lt;[E](index.md), [A](index.md)&gt;

## Functions

| Name | Summary |
|---|---|
| [bind](../-either-effect/bind.md) | [common]<br>open suspend fun &lt;[B](../-either-effect/bind.md)&gt; [Either](../../arrow.core/-either/index.md)&lt;[E](index.md), [B](../-either-effect/bind.md)&gt;.[bind](../-either-effect/bind.md)(): [B](../-either-effect/bind.md)<br>open suspend fun &lt;[B](../-either-effect/bind.md)&gt; [Validated](../../arrow.core/-validated/index.md)&lt;[E](index.md), [B](../-either-effect/bind.md)&gt;.[bind](../-either-effect/bind.md)(): [B](../-either-effect/bind.md)<br>open suspend fun &lt;[B](../-either-effect/bind.md)&gt; [Result](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-result/index.html)&lt;[B](../-either-effect/bind.md)&gt;.[bind](../-either-effect/bind.md)(transform: ([Throwable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html)) -&gt; [E](index.md)): [B](../-either-effect/bind.md) |
| [control](../-restricted-option-effect/index.md#-609838202%2FFunctions%2F-1961959459) | [common]<br>abstract fun [control](../-restricted-option-effect/index.md#-609838202%2FFunctions%2F-1961959459)(): [DelimitedScope](../../../../arrow-continuations/arrow-continuations/arrow.continuations.generic/-delimited-scope/index.md)&lt;[Either](../../arrow.core/-either/index.md)&lt;[E](index.md), [A](index.md)&gt;&gt; |
| [ensure](../-either-effect/ensure.md) | [common]<br>open suspend fun [ensure](../-either-effect/ensure.md)(value: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html), orLeft: () -&gt; [E](index.md))<br>Ensure check if the [value](../-either-effect/ensure.md) is true, and if it is it allows the either { } binding to continue. In case it is false, then it short-circuits the binding and returns the provided value by [orLeft](../-either-effect/ensure.md) inside an [Either.Left](../../arrow.core/-either/-left/index.md). |
