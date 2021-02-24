package arrow.core.test.laws.internal

import arrow.Kind
import arrow.typeclasses.Applicative
import io.kotlintest.properties.Gen

internal data class Id<out A>(val value: A) : Kind<Id.Companion, A> {
  internal companion object
}

internal fun <A> Kind<Id.Companion, A>.fix(): Id<A> = this as Id<A>

internal object idApplicative : Applicative<Id.Companion> {
  override fun <A> just(a: A): Kind<Id.Companion, A> = Id(a)
  override fun <A, B> Kind<Id.Companion, A>.ap(ff: Kind<Id.Companion, (A) -> B>): Kind<Id.Companion, B> =
    Id(ff.fix().value(fix().value))
}

@PublishedApi
internal fun <T> Gen.Companion.id(gen: Gen<T>): Gen<Id<T>> = object : Gen<Id<T>> {
  override fun constants(): Iterable<Id<T>> =
    gen.constants().map { Id(it) }

  override fun random(): Sequence<Id<T>> =
    gen.random().map { Id(it) }
}
