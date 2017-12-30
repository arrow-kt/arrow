package arrow.data

import arrow.core.*

fun <A, B> ((A) -> B).coreader(): CoreaderT<IdHK, A, B> = Coreader(this)

fun <A, B> CoreaderT<IdHK, A, B>.runId(d: A): B = this.run(Id(d))

object Coreader {
    operator fun <A, B> invoke(run: (A) -> B): CoreaderT<IdHK, A, B> = Cokleisli({ a: IdKind<A> -> run(a.ev().value) })

    fun <A, B> pure(x: B): CoreaderT<IdHK, A, B> = Cokleisli.pure<IdHK, A, B>(x)

    fun <B> ask(): CoreaderT<IdHK, B, B> = Cokleisli.ask<IdHK, B>()
}