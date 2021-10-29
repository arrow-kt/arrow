package arrow.generics.recursive.examples

import arrow.generics.recursive.* // ktlint-disable no-wildcard-imports

public fun <R : TopRepr, S : SumRepr, A, B> Sum<R, S, A>.gmap(f: (A) -> B): Sum<R, S, B> = when (this) {
  is That<R, *, *, A> -> this.gmap(f) as Sum<R, S, B> // S = TyS<ThisCase, RestCases>
  is This<R, *, *, A> -> this.gmap(f) as Sum<R, S, B>
}

internal fun <R : TopRepr, G : ProductRepr, Rest : SumRepr, A, B> That<R, G, Rest, A>.gmap(f: (A) -> B): That<R, G, Rest, B> =
  That(next.gmap(f))

internal fun <R : TopRepr, G : ProductRepr, Rest : SumRepr, A, B> This<R, G, Rest, A>.gmap(f: (A) -> B): This<R, G, Rest, B> =
  This(choice, value.gmap(f))

internal fun <R : TopRepr, P : ProductRepr, A, B> Product<R, P, A>.gmap(f: (A) -> B): Product<R, P, B> = when (this) {
  is And<R, *, *, A> -> And(name, value.gmap(f), rest.gmap(f)) as Product<R, P, B>
  is EndD<R, A> -> EndD<R, B>(0) as Product<R, P, B>
}

internal fun <R : TopRepr, V : ValueRepr, A, B> Value<R, V, A>.gmap(f: (A) -> B): Value<R, V, B> = when (this) {
  is Field<R, *, A> -> this.gmap(f) as Value<R, V, B>
  is Par -> Par<R, B>(f(value)) as Value<R, V, B>
  is Rec -> Rec(value.gmap(f)) as Value<R, V, B>
}

internal fun <R : TopRepr, T, A, B> Field<R, T, A>.gmap(f: (A) -> B): Field<R, T, B> = Field(value)
