//[arrow-core](../../../index.md)/[arrow.core.computations](../index.md)/[nullable](index.md)

# nullable

[common]\
object [nullable](index.md)

## Functions

| Name | Summary |
|---|---|
| [eager](eager.md) | [common]<br>inline fun &lt;[A](eager.md)&gt; [eager](eager.md)(crossinline func: suspend [RestrictedNullableEffect](../-restricted-nullable-effect/index.md)&lt;[A](eager.md)&gt;.() -&gt; [A](eager.md)?): [A](eager.md)? |
| [invoke](invoke.md) | [common]<br>inline suspend operator fun &lt;[A](invoke.md)&gt; [invoke](invoke.md)(crossinline func: suspend [NullableEffect](../-nullable-effect/index.md)&lt;*&gt;.() -&gt; [A](invoke.md)?): [A](invoke.md)? |
