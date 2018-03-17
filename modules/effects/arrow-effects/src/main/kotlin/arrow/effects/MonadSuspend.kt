package arrow.effects

import arrow.Kind
import arrow.TC
import arrow.core.Either
import arrow.typeclass
import arrow.typeclasses.MonadError
import arrow.typeclasses.MonadErrorSyntax

/** The context required to defer evaluating a safe computation. **/
@typeclass(syntax = false)
interface MonadSuspend<F, E> : MonadError<F, E>, TC {
    fun catch(catch: Throwable): E

    fun <A> defer(fa: () -> Kind<F, A>): Kind<F, A>

    operator fun <A> invoke(fa: () -> A): Kind<F, A> =
            defer {
                try {
                    pure(fa())
                } catch (t: Throwable) {
                    raiseError<A>(catch(t))
                }
            }

    fun lazy(): Kind<F, Unit> = invoke { }

    fun <A> deferUnsafe(f: () -> Either<Throwable, A>): Kind<F, A> =
            defer { f().fold({ raiseError<A>(catch(it)) }, { pure(it) }) }
}

interface MonadSuspendSyntax<F, E> : MonadErrorSyntax<F, E> {

    fun monadSuspend(): MonadSuspend<F, E>

    override fun monadError() : MonadError <F, E> = monadSuspend()

    fun <A> Function0<Either<Throwable, A>>.deferUnsafe(dummy: Unit = Unit): arrow.Kind<F, A> =
            this@MonadSuspendSyntax.monadSuspend().`deferUnsafe`(this)

    fun <A> Function0<A>.`invoke`(dummy: Unit = Unit): arrow.Kind<F, A> =
            this@MonadSuspendSyntax.monadSuspend().`invoke`(this)

    fun `lazy`(): arrow.Kind<F, Unit> =
            this@MonadSuspendSyntax.monadSuspend().`lazy`()

    fun <A> Function0<arrow.Kind<F, A>>.`suspend`(dummy: Unit = Unit): arrow.Kind<F, A> =
            this@MonadSuspendSyntax.monadSuspend().defer(this)
}
