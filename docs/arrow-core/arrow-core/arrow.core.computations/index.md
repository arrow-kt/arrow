//[arrow-core](../../index.md)/[arrow.core.computations](index.md)

# Package arrow.core.computations

## Types

| Name | Summary |
|---|---|
| [either](either/index.md) | [common]<br>object [either](either/index.md) |
| [EitherEffect](-either-effect/index.md) | [common]<br>fun interface [EitherEffect](-either-effect/index.md)&lt;[E](-either-effect/index.md), [A](-either-effect/index.md)&gt; : [Effect](../../../arrow-continuations/arrow-continuations/arrow.continuations/-effect/index.md)&lt;[Either](../arrow.core/-either/index.md)&lt;[E](-either-effect/index.md), [A](-either-effect/index.md)&gt;&gt; |
| [eval](eval/index.md) | [common]<br>object [eval](eval/index.md) |
| [EvalEffect](-eval-effect/index.md) | [common]<br>fun interface [EvalEffect](-eval-effect/index.md)&lt;[A](-eval-effect/index.md)&gt; : [Effect](../../../arrow-continuations/arrow-continuations/arrow.continuations/-effect/index.md)&lt;[Eval](../arrow.core/-eval/index.md)&lt;[A](-eval-effect/index.md)&gt;&gt; |
| [nullable](nullable/index.md) | [common]<br>object [nullable](nullable/index.md) |
| [NullableEffect](-nullable-effect/index.md) | [common]<br>fun interface [NullableEffect](-nullable-effect/index.md)&lt;[A](-nullable-effect/index.md)&gt; : [Effect](../../../arrow-continuations/arrow-continuations/arrow.continuations/-effect/index.md)&lt;[A](-nullable-effect/index.md)?&gt; |
| [option](option/index.md) | [common]<br>object [option](option/index.md) |
| [OptionEffect](-option-effect/index.md) | [common]<br>fun interface [OptionEffect](-option-effect/index.md)&lt;[A](-option-effect/index.md)&gt; : [Effect](../../../arrow-continuations/arrow-continuations/arrow.continuations/-effect/index.md)&lt;[Option](../arrow.core/-option/index.md)&lt;[A](-option-effect/index.md)&gt;&gt; |
| [RestrictedEitherEffect](-restricted-either-effect/index.md) | [common]<br>fun interface [RestrictedEitherEffect](-restricted-either-effect/index.md)&lt;[E](-restricted-either-effect/index.md), [A](-restricted-either-effect/index.md)&gt; : [EitherEffect](-either-effect/index.md)&lt;[E](-restricted-either-effect/index.md), [A](-restricted-either-effect/index.md)&gt; |
| [RestrictedEvalEffect](-restricted-eval-effect/index.md) | [common]<br>fun interface [RestrictedEvalEffect](-restricted-eval-effect/index.md)&lt;[A](-restricted-eval-effect/index.md)&gt; : [EvalEffect](-eval-effect/index.md)&lt;[A](-restricted-eval-effect/index.md)&gt; |
| [RestrictedNullableEffect](-restricted-nullable-effect/index.md) | [common]<br>fun interface [RestrictedNullableEffect](-restricted-nullable-effect/index.md)&lt;[A](-restricted-nullable-effect/index.md)&gt; : [NullableEffect](-nullable-effect/index.md)&lt;[A](-restricted-nullable-effect/index.md)&gt; |
| [RestrictedOptionEffect](-restricted-option-effect/index.md) | [common]<br>fun interface [RestrictedOptionEffect](-restricted-option-effect/index.md)&lt;[A](-restricted-option-effect/index.md)&gt; : [OptionEffect](-option-effect/index.md)&lt;[A](-restricted-option-effect/index.md)&gt; |
| [result](result/index.md) | [common]<br>object [result](result/index.md) |
| [ResultEffect](-result-effect/index.md) | [common]<br>object [ResultEffect](-result-effect/index.md)<br>DSL Receiver Syntax for [result](result/index.md). |

## Functions

| Name | Summary |
|---|---|
| [ensureNotNull](ensure-not-null.md) | [common]<br>suspend fun &lt;[B](ensure-not-null.md) : [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)&gt; [NullableEffect](-nullable-effect/index.md)&lt;*&gt;.[ensureNotNull](ensure-not-null.md)(value: [B](ensure-not-null.md)?): [B](ensure-not-null.md)<br>suspend fun &lt;[B](ensure-not-null.md) : [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)&gt; [OptionEffect](-option-effect/index.md)&lt;*&gt;.[ensureNotNull](ensure-not-null.md)(value: [B](ensure-not-null.md)?): [B](ensure-not-null.md)<br>Ensures that [value](ensure-not-null.md) is not null. When the value is not null, then it will be returned as non null and the check value is now smart-checked to non-null. Otherwise, if the [value](ensure-not-null.md) is null then the [option](option/index.md) binding will short-circuit with [None](../arrow.core/-none/index.md).<br>[common]<br>suspend fun &lt;[E](ensure-not-null.md), [B](ensure-not-null.md) : [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)&gt; [EitherEffect](-either-effect/index.md)&lt;[E](ensure-not-null.md), *&gt;.[ensureNotNull](ensure-not-null.md)(value: [B](ensure-not-null.md)?, orLeft: () -&gt; [E](ensure-not-null.md)): [B](ensure-not-null.md)<br>Ensures that [value](ensure-not-null.md) is not null. When the value is not null, then it will be returned as non null and the check value is now smart-checked to non-null. Otherwise, if the [value](ensure-not-null.md) is null then the [either](either/index.md) binding will short-circuit with [orLeft](ensure-not-null.md) inside of [Either.Left](../arrow.core/-either/-left/index.md). |
