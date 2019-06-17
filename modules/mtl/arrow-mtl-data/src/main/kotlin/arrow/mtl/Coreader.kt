package arrow.mtl

import arrow.core.Id
import arrow.core.ForId
import arrow.core.IdOf
import arrow.core.value
import arrow.typeclasses.Comonad
import arrow.typeclasses.internal.IdBimonad

fun <A, B> ((A) -> B).coreader(): CoreaderT<ForId, A, B> = Coreader(this)

fun <A, B> CoreaderT<ForId, A, B>.runId(d: A): B = this.run(Id(d))

object Coreader {
  operator fun <A, B> invoke(run: (A) -> B): CoreaderT<ForId, A, B> = Cokleisli(IdBimonad) { a: IdOf<A> -> run(a.value()) }

  fun <A, B> just(MF: Comonad<ForId>, x: B): CoreaderT<ForId, A, B> = Cokleisli.just(MF, x)

  fun <B> ask(MF: Comonad<ForId>): CoreaderT<ForId, B, B> = Cokleisli.ask(MF)
}
