//[arrow-core](../../../index.md)/[arrow.core.computations](../index.md)/[EvalEffect](index.md)

# EvalEffect

[common]\
fun interface [EvalEffect](index.md)&lt;[A](index.md)&gt; : [Effect](../../../../arrow-continuations/arrow-continuations/arrow.continuations/-effect/index.md)&lt;[Eval](../../arrow.core/-eval/index.md)&lt;[A](index.md)&gt;&gt;

## Functions

| Name | Summary |
|---|---|
| [bind](bind.md) | [common]<br>open suspend fun &lt;[B](bind.md)&gt; [Eval](../../arrow.core/-eval/index.md)&lt;[B](bind.md)&gt;.[bind](bind.md)(): [B](bind.md) |
| [control](../-restricted-option-effect/index.md#-609838202%2FFunctions%2F-1961959459) | [common]<br>abstract fun [control](../-restricted-option-effect/index.md#-609838202%2FFunctions%2F-1961959459)(): [DelimitedScope](../../../../arrow-continuations/arrow-continuations/arrow.continuations.generic/-delimited-scope/index.md)&lt;[Eval](../../arrow.core/-eval/index.md)&lt;[A](index.md)&gt;&gt; |

## Inheritors

| Name |
|---|
| [RestrictedEvalEffect](../-restricted-eval-effect/index.md) |
