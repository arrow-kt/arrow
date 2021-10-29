package arrow.generics.shallow.data

import arrow.generics.shallow.* // ktlint-disable no-wildcard-imports

// these are the "interpretations" of generic representations
public sealed interface Generic<out A : Repr>
// a sum is one choice or the other
public sealed interface Sum<out A : SumRepr> : Generic<A>
// and when we take a choice we also encode its name
// (think of it as having any further metadata)
public data class This<out A : ProductRepr, out Rest : SumRepr>(val choice: String, val value: Product<A>) :
  Sum<Choice<A, Rest>>
public data class That<out A : ProductRepr, out Rest : SumRepr>(val next: Sum<Rest>) :
  Sum<Choice<A, Rest>>
// and a product is a heterogenerous list
public sealed interface Product<out A : ProductRepr> : Generic<A>
public data class And<out A, out Rest : ProductRepr>(val name: String, val value: A, val rest: Product<Rest>) :
  Product<Field<A, Rest>>
public object Done : Product<End>
