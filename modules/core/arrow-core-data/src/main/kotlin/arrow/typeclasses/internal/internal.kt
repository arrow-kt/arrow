package arrow.typeclasses.internal

import arrow.core.Either
import arrow.core.IdOf
import arrow.core.ForId
import arrow.core.Id
import arrow.typeclasses.Bimonad
import arrow.core.fix

val IdBimonad: Bimonad<ForId> = object : Bimonad<ForId> {
  override fun <A, B> IdOf<A>.coflatMap(f: (IdOf<A>) -> B): IdOf<B> =
    fix().coflatMap(f)

  override fun <A> IdOf<A>.extract(): A =
    fix().extract()

  override fun <A> just(a: A): IdOf<A> =
    Id(a)

  override fun <A, B> IdOf<A>.ap(ff: IdOf<(A) -> B>): IdOf<B> =
    fix().ap(ff)

  override fun <A, B> IdOf<A>.map(f: (A) -> B): IdOf<B> =
    fix().map(f)

  override fun <A, B> IdOf<A>.flatMap(f: (A) -> IdOf<B>): IdOf<B> =
    fix().flatMap(f)

  override fun <A, B> tailRecM(a: A, f: (A) -> IdOf<Either<A, B>>): Id<B> =
    Id.tailRecM(a, f)
}
