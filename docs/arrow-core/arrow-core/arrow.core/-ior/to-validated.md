//[arrow-core](../../../index.md)/[arrow.core](../index.md)/[Ior](index.md)/[toValidated](to-validated.md)

# toValidated

[common]\
fun [toValidated](to-validated.md)(): [Validated](../-validated/index.md)&lt;[A](index.md), [B](index.md)&gt;

Returns a [Validated.Valid](../-validated/-valid/index.md) containing the [Right](-right/index.md) value or B if this is [Right](-right/index.md) or [Both](-both/index.md) and [Validated.Invalid](../-validated/-invalid/index.md) if this is a [Left](-left/index.md).

Example:

&lt;!--- KNIT example-ior-22.kt --&gt;\
Right(12).toValidated() // Result: Valid(12)\
Left(12).toValidated()  // Result: Invalid(12)\
Both(12, "power").toValidated()  // Result: Valid("power")<!--- KNIT example-ior-23.kt -->
