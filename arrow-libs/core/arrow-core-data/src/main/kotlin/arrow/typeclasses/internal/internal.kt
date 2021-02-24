package arrow.typeclasses.internal

import arrow.Kind
import arrow.typeclasses.Applicative

internal data class Id<out A>(val value: A) : Kind<Id.Companion, A> {
  internal companion object
}

internal fun <A> Kind<Id.Companion, A>.fix(): Id<A> = this as Id<A>

internal object idApplicative : Applicative<Id.Companion> {
  override fun <A> just(a: A): Kind<Id.Companion, A> = Id(a)
  override fun <A, B> Kind<Id.Companion, A>.ap(ff: Kind<Id.Companion, (A) -> B>): Kind<Id.Companion, B> =
    Id(ff.fix().value(fix().value))
}
