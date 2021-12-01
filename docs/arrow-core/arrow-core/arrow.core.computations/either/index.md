//[arrow-core](../../../index.md)/[arrow.core.computations](../index.md)/[either](index.md)

# either

[common]\
object [either](index.md)

## Functions

| Name | Summary |
|---|---|
| [eager](eager.md) | [common]<br>inline fun &lt;[E](eager.md), [A](eager.md)&gt; [eager](eager.md)(crossinline c: suspend [RestrictedEitherEffect](../-restricted-either-effect/index.md)&lt;[E](eager.md), *&gt;.() -&gt; [A](eager.md)): [Either](../../arrow.core/-either/index.md)&lt;[E](eager.md), [A](eager.md)&gt; |
| [invoke](invoke.md) | [common]<br>inline suspend operator fun &lt;[E](invoke.md), [A](invoke.md)&gt; [invoke](invoke.md)(crossinline c: suspend [EitherEffect](../-either-effect/index.md)&lt;[E](invoke.md), *&gt;.() -&gt; [A](invoke.md)): [Either](../../arrow.core/-either/index.md)&lt;[E](invoke.md), [A](invoke.md)&gt; |
