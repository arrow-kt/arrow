package arrow.effects

import arrow.Kind
import arrow.TC
import arrow.core.Either
import arrow.typeclass
import arrow.typeclasses.*

@typeclass(syntax = false)
interface Effect<F> : Async<F>, TC {
    fun <A> runAsync(fa: Kind<F, A>, cb: (Either<Throwable, A>) -> Kind<F, Unit>): Kind<F, Unit>
}

interface EffectSyntax<F> : AsyncSyntax<F> {

    fun effect(): Effect<F>

    override fun async() : Async <F> = effect()

    override fun monadSuspend() : MonadSuspend <F> = effect()

    override fun monadError() : MonadError<F, Throwable> = effect()

    override fun applicativeError() : ApplicativeError<F, Throwable> = effect()

    override fun applicative() : Applicative<F> = effect()

    override fun functor() : Functor<F> = effect()

    override fun monad() : Monad<F> = effect()

    fun <A> Kind<F, A>.`runAsync`(dummy: Unit = Unit, cb: kotlin.Function1<arrow.core.Either<kotlin.Throwable, A>, arrow.Kind<F, kotlin.Unit>>): Kind<F, kotlin.Unit> =
            this@EffectSyntax.effect().`runAsync`(this, cb)
}