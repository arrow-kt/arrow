//[arrow-core](../../../../index.md)/[arrow.core](../../index.md)/[Ior](../index.md)/[Companion](index.md)/[fromNullables](from-nullables.md)

# fromNullables

[common]\

@[JvmStatic](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.jvm/-jvm-static/index.html)

fun &lt;[A](from-nullables.md), [B](from-nullables.md)&gt; [fromNullables](from-nullables.md)(a: [A](from-nullables.md)?, b: [B](from-nullables.md)?): [Ior](../index.md)&lt;[A](from-nullables.md), [B](from-nullables.md)&gt;?

Create an [Ior](../index.md) from two nullables if at least one of them is defined.

#### Return

null if both [a](from-nullables.md) and [b](from-nullables.md) are null. Otherwise an [Ior.Left](../-left/index.md), [Ior.Right](../-right/index.md), or [Ior.Both](../-both/index.md) if [a](from-nullables.md), [b](from-nullables.md), or both are defined (respectively).

## Parameters

common

| | |
|---|---|
| a | an element (nullable) for the left side of the [Ior](../index.md) |
| b | an element (nullable) for the right side of the [Ior](../index.md) |
