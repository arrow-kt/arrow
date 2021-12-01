//[arrow-core](../../../index.md)/[arrow.core.computations](../index.md)/[EitherEffect](index.md)

# EitherEffect

[common]\
fun interface [EitherEffect](index.md)&lt;[E](index.md), [A](index.md)&gt; : [Effect](../../../../arrow-continuations/arrow-continuations/arrow.continuations/-effect/index.md)&lt;[Either](../../arrow.core/-either/index.md)&lt;[E](index.md), [A](index.md)&gt;&gt;

## Functions

| Name | Summary |
|---|---|
| [bind](bind.md) | [common]<br>open suspend fun &lt;[B](bind.md)&gt; [Either](../../arrow.core/-either/index.md)&lt;[E](index.md), [B](bind.md)&gt;.[bind](bind.md)(): [B](bind.md)<br>open suspend fun &lt;[B](bind.md)&gt; [Validated](../../arrow.core/-validated/index.md)&lt;[E](index.md), [B](bind.md)&gt;.[bind](bind.md)(): [B](bind.md)<br>open suspend fun &lt;[B](bind.md)&gt; [Result](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-result/index.html)&lt;[B](bind.md)&gt;.[bind](bind.md)(transform: ([Throwable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html)) -&gt; [E](index.md)): [B](bind.md) |
| [control](../-restricted-option-effect/index.md#-609838202%2FFunctions%2F-1961959459) | [common]<br>abstract fun [control](../-restricted-option-effect/index.md#-609838202%2FFunctions%2F-1961959459)(): [DelimitedScope](../../../../arrow-continuations/arrow-continuations/arrow.continuations.generic/-delimited-scope/index.md)&lt;[Either](../../arrow.core/-either/index.md)&lt;[E](index.md), [A](index.md)&gt;&gt; |
| [ensure](ensure.md) | [common]<br>open suspend fun [ensure](ensure.md)(value: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html), orLeft: () -&gt; [E](index.md))<br>Ensure check if the [value](ensure.md) is true, and if it is it allows the either { } binding to continue. In case it is false, then it short-circuits the binding and returns the provided value by [orLeft](ensure.md) inside an [Either.Left](../../arrow.core/-either/-left/index.md). |

## Inheritors

| Name |
|---|
| [RestrictedEitherEffect](../-restricted-either-effect/index.md) |

## Extensions

| Name | Summary |
|---|---|
| [ensureNotNull](../ensure-not-null.md) | [common]<br>suspend fun &lt;[E](../ensure-not-null.md), [B](../ensure-not-null.md) : [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)&gt; [EitherEffect](index.md)&lt;[E](../ensure-not-null.md), *&gt;.[ensureNotNull](../ensure-not-null.md)(value: [B](../ensure-not-null.md)?, orLeft: () -&gt; [E](../ensure-not-null.md)): [B](../ensure-not-null.md)<br>Ensures that [value](../ensure-not-null.md) is not null. When the value is not null, then it will be returned as non null and the check value is now smart-checked to non-null. Otherwise, if the [value](../ensure-not-null.md) is null then the [either](../either/index.md) binding will short-circuit with [orLeft](../ensure-not-null.md) inside of [Either.Left](../../arrow.core/-either/-left/index.md). |
