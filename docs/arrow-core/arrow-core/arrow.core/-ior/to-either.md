//[arrow-core](../../../index.md)/[arrow.core](../index.md)/[Ior](index.md)/[toEither](to-either.md)

# toEither

[common]\
fun [toEither](to-either.md)(): [Either](../-either/index.md)&lt;[A](index.md), [B](index.md)&gt;

Returns a [Either.Right](../-either/-right/index.md) containing the [Right](-right/index.md) value or B if this is [Right](-right/index.md) or [Both](-both/index.md) and [Either.Left](../-either/-left/index.md) if this is a [Left](-left/index.md).

Example:

&lt;!--- KNIT example-ior-18.kt --&gt;\
Right(12).toEither() // Result: Either.Right(12)\
Left(12).toEither()  // Result: Either.Left(12)\
Both("power", 12).toEither()  // Result: Either.Right(12)<!--- KNIT example-ior-19.kt -->
