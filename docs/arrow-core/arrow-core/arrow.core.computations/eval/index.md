//[arrow-core](../../../index.md)/[arrow.core.computations](../index.md)/[eval](index.md)

# eval

[common]\
object [eval](index.md)

## Functions

| Name | Summary |
|---|---|
| [eager](eager.md) | [common]<br>inline fun &lt;[A](eager.md)&gt; [eager](eager.md)(crossinline func: suspend [RestrictedEvalEffect](../-restricted-eval-effect/index.md)&lt;[A](eager.md)&gt;.() -&gt; [A](eager.md)): [Eval](../../arrow.core/-eval/index.md)&lt;[A](eager.md)&gt; |
| [invoke](invoke.md) | [common]<br>inline suspend operator fun &lt;[A](invoke.md)&gt; [invoke](invoke.md)(crossinline func: suspend [EvalEffect](../-eval-effect/index.md)&lt;*&gt;.() -&gt; [A](invoke.md)): [Eval](../../arrow.core/-eval/index.md)&lt;[A](invoke.md)&gt; |
