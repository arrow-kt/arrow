package arrow.effects

import arrow.Kind
import arrow.TC
import arrow.core.Either
import arrow.typeclass
import arrow.typeclasses.*

        /** An asynchronous computation that might fail. **/
typealias Proc<A> = ((Either<Throwable, A>) -> Unit) -> Unit

/** The context required to run an asynchronous computation that may fail. **/
@typeclass(syntax = false)
interface Async<F> : MonadSuspend<F>, TC {
    fun <A> async(fa: Proc<A>): Kind<F, A>

    fun <A> never(): Kind<F, A> =
            async { }
}

interface AsyncSyntax<F> : MonadSuspendSyntax<F> {

    fun async(): Async<F>

    override fun monadSuspend(): MonadSuspend<F> = async()

    override fun monadError(): MonadError<F, Throwable> = async()

    override fun applicativeError(): ApplicativeError<F, Throwable> = async()

    override fun applicative(): Applicative<F> = async()

    override fun functor(): Functor<F> = async()

    override fun monad(): Monad<F> = async()

    fun <A> Proc<A>.`async`(dummy: Unit = Unit): Kind<F, A> =
            this@AsyncSyntax.async().`async`(this)

    fun <A> `never`(dummy: Unit = Unit): Kind<F, A> =
            this@AsyncSyntax.async().`never`()
}