package arrow.effects

import arrow.Kind
import arrow.TC
import arrow.core.Either
import arrow.typeclass
import arrow.typeclasses.MonadError
import arrow.typeclasses.MonadErrorSyntax

/** The context required to defer evaluating a safe computation. **/
@typeclass(syntax = false)
interface MonadSuspend<F> : MonadError<F, Throwable>, TC {
    fun <A> suspend(fa: () -> Kind<F, A>): Kind<F, A>

    operator fun <A> invoke(fa: () -> A): Kind<F, A> =
            suspend() {
                try {
                    pure(fa())
                } catch (t: Throwable) {
                    raiseError<A>(t)
                }
            }

    fun lazy(): Kind<F, Unit> = invoke { }

    fun <A> deferUnsafe(f: () -> Either<Throwable, A>): Kind<F, A> =
            suspend() { f().fold({ raiseError<A>(it) }, { pure(it) }) }
}

interface MonadSuspendSyntax<F> : MonadErrorSyntax<F, Throwable> {

    fun monadSuspend(): MonadSuspend<F>

    override fun monadError() : MonadError <F, Throwable> = monadSuspend()

    fun <A> kotlin.Function0<arrow.core.Either<kotlin.Throwable, A>>.`deferUnsafe`(dummy: Unit = Unit): arrow.Kind<F, A> =
            this@MonadSuspendSyntax.monadSuspend().`deferUnsafe`(this)

    fun <A> kotlin.Function0<A>.`invoke`(dummy: Unit = Unit): arrow.Kind<F, A> =
            this@MonadSuspendSyntax.monadSuspend().`invoke`(this)

    fun `lazy`(): arrow.Kind<F, kotlin.Unit> =
            this@MonadSuspendSyntax.monadSuspend().`lazy`()

    fun <A> kotlin.Function0<arrow.Kind<F, A>>.`suspend`(dummy: Unit = Unit): arrow.Kind<F, A> =
            this@MonadSuspendSyntax.monadSuspend().`suspend`(this)
}

inline fun <reified F, A> (() -> A).defer(SC: MonadSuspend<F> = monadSuspend()): Kind<F, A> = SC(this)

inline fun <reified F, A> (() -> Either<Throwable, A>).deferUnsafe(SC: MonadSuspend<F> = monadSuspend()): Kind<F, A> =
        SC.deferUnsafe(this)
