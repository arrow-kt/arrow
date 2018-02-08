package arrow.data

import arrow.core.*

fun <A, B> ((A) -> B).coreader(): CoreaderT<ForId, A, B> = Coreader(this)

fun <A, B> CoreaderT<ForId, A, B>.runId(d: A): B = this.run(Id(d))

object Coreader {
    operator fun <A, B> invoke(run: (A) -> B): CoreaderT<ForId, A, B> = Cokleisli({ a: IdOf<A> -> run(a.reify().value) })

    fun <A, B> pure(x: B): CoreaderT<ForId, A, B> = Cokleisli.pure<ForId, A, B>(x)

    fun <B> ask(): CoreaderT<ForId, B, B> = Cokleisli.ask<ForId, B>()
}