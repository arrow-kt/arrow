package arrow.effects

import arrow.Kind
import arrow.TC
import arrow.core.Either
import arrow.typeclass
import arrow.typeclasses.*

@typeclass(syntax = false)
interface Effect<F, E> : Async<F, E>, TC {
    fun <A> runAsync(fa: Kind<F, A>, cb: (Either<Throwable, A>) -> Kind<F, Unit>): Kind<F, Unit>
}

interface EffectSyntax<F, E> : AsyncSyntax<F, E> {

    fun effect(): Effect<F, E>

    override fun async() : Async <F, E> = effect()

    override fun monadSuspend() : MonadSuspend <F, E> = effect()

    override fun monadError() : MonadError<F, E> = effect()

    override fun applicativeError() : ApplicativeError<F, E> = effect()

    override fun applicative() : Applicative<F> = effect()

    override fun functor() : Functor<F> = effect()

    override fun monad() : Monad<F> = effect()

    fun <A> Kind<F, A>.`runAsync`(dummy: Unit = Unit, cb: Function1<Either<Throwable, A>, Kind<F, Unit>>): Kind<F, Unit> =
            this@EffectSyntax.effect().`runAsync`(this, cb)
}