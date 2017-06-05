package katz

fun <A, B> ((A) -> B).coreader(): CoreaderT<Id.F, A, B> =
        Coreader(this)

fun <A, B> CoreaderT<Id.F, A, B>.runId(d: A): B =
        this.run(Id(d))

object Coreader {
    operator fun <A, B> invoke(run: (A) -> B): CoreaderT<Id.F, A, B> =
            Cokleisli({ a: IdKind<A> -> run(a.ev().value) })

    fun <A, B> pure(x: B): CoreaderT<Id.F, A, B> =
            Cokleisli.pure<Id.F, A, B>(x)

    fun <B> ask(): CoreaderT<Id.F, B, B> =
            Cokleisli.ask<Id.F, B>()
}