package arrow.optics

import arrow.core.Const
import arrow.core.None
import arrow.core.Option
import arrow.typeclasses.Monoid

internal sealed class First
internal sealed class Last

@PublishedApi
internal fun <A> firstOptionMonoid(): Monoid<Const<Option<A>, First>> = object : Monoid<Const<Option<A>, First>> {

  override fun empty(): Const<Option<A>, First> = Const(None)

  override fun Const<Option<A>, First>.combine(b: Const<Option<A>, First>): Const<Option<A>, First> =
    if (value().isDefined()) this else b
}

internal fun <A> lastOptionMonoid(): Monoid<Const<Option<A>, Last>> = object : Monoid<Const<Option<A>, Last>> {

  override fun empty(): Const<Option<A>, Last> = Const(None)

  override fun Const<Option<A>, Last>.combine(b: Const<Option<A>, Last>): Const<Option<A>, Last> =
    if (b.value().isDefined()) b else this
}
