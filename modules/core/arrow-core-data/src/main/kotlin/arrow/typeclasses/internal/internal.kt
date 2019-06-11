package arrow.typeclasses.internal

import arrow.Kind
import arrow.core.Either
import arrow.core.ForId
import arrow.core.Id
import arrow.core.fix
import arrow.typeclasses.Bimonad

val IdBimonad: Bimonad<ForId> = object : Bimonad<ForId> {
  override fun <A, B> Kind<ForId, A>.coflatMap(f: (Kind<ForId, A>) -> B): Kind<ForId, B> =
    fix().coflatMap(f)

  override fun <A> Kind<ForId, A>.extract(): A =
    fix().extract()

  override fun <A> just(a: A): Kind<ForId, A> =
    Id(a)

  override fun <A, B> Kind<ForId, A>.ap(ff: Kind<ForId, (A) -> B>): Kind<ForId, B> =
    fix().ap(ff)

  override fun <A, B> Kind<ForId, A>.map(f: (A) -> B): Kind<ForId, B> =
    fix().map(f)

  override fun <A, B> Kind<ForId, A>.flatMap(f: (A) -> Kind<ForId, B>): Kind<ForId, B> =
    fix().flatMap(f)

  override fun <A, B> tailRecM(a: A, f: (A) -> Kind<ForId, Either<A, B>>): Kind<ForId, B> =
    Id.tailRecM(a, f)
}
