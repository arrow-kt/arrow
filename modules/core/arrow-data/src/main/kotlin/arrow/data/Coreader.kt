package arrow.data

import arrow.core.ForId
import arrow.core.Id
import arrow.core.IdOf
import arrow.core.fix
import arrow.typeclasses.Comonad

fun <A, B> ((A) -> B).coreader(MF: Comonad<ForId>): CoreaderT<ForId, A, B> = Coreader(MF, this)

fun <A, B> CoreaderT<ForId, A, B>.runId(d: A): B = this.run(Id(d))

object Coreader {
    operator fun <A, B> invoke(MF: Comonad<ForId>, run: (A) -> B): CoreaderT<ForId, A, B> = Cokleisli(MF) { a: IdOf<A> -> run(a.fix().value) }

    fun <A, B> pure(MF: Comonad<ForId>, x: B): CoreaderT<ForId, A, B> = Cokleisli.pure(MF, x)

    fun <B> ask(MF: Comonad<ForId>): CoreaderT<ForId, B, B> = Cokleisli.ask(MF)
}
