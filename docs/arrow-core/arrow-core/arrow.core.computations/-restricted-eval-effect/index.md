//[arrow-core](../../../index.md)/[arrow.core.computations](../index.md)/[RestrictedEvalEffect](index.md)

# RestrictedEvalEffect

[common]\
fun interface [RestrictedEvalEffect](index.md)&lt;[A](index.md)&gt; : [EvalEffect](../-eval-effect/index.md)&lt;[A](index.md)&gt;

## Functions

| Name | Summary |
|---|---|
| [bind](../-eval-effect/bind.md) | [common]<br>open suspend fun &lt;[B](../-eval-effect/bind.md)&gt; [Eval](../../arrow.core/-eval/index.md)&lt;[B](../-eval-effect/bind.md)&gt;.[bind](../-eval-effect/bind.md)(): [B](../-eval-effect/bind.md) |
| [control](../-restricted-option-effect/index.md#-609838202%2FFunctions%2F-1961959459) | [common]<br>abstract fun [control](../-restricted-option-effect/index.md#-609838202%2FFunctions%2F-1961959459)(): [DelimitedScope](../../../../arrow-continuations/arrow-continuations/arrow.continuations.generic/-delimited-scope/index.md)&lt;[Eval](../../arrow.core/-eval/index.md)&lt;[A](index.md)&gt;&gt; |
