package arrow.optics

import arrow.core.Const
import arrow.core.None
import arrow.core.Option
import arrow.typeclasses.Monoid

@PublishedApi
internal object AndMonoid : Monoid<Boolean> {
  override fun Boolean.combine(b: Boolean): Boolean = this && b
  override fun empty(): Boolean = true
}

internal sealed class First
internal sealed class Last

@PublishedApi
internal fun <A> firstOptionMonoid(): Monoid<Const<Option<A>, First>> = object : Monoid<Const<Option<A>, First>> {

  override fun empty(): Const<Option<A>, First> = Const(None)

  override fun Const<Option<A>, First>.combine(b: Const<Option<A>, First>): Const<Option<A>, First> =
    if (value().fold({ false }, { true })) this else b
}

internal fun <A> lastOptionMonoid(): Monoid<Const<Option<A>, Last>> = object : Monoid<Const<Option<A>, Last>> {

  override fun empty(): Const<Option<A>, Last> = Const(None)

  override fun Const<Option<A>, Last>.combine(b: Const<Option<A>, Last>): Const<Option<A>, Last> =
    if (b.value().fold({ false }, { true })) b else this
}
