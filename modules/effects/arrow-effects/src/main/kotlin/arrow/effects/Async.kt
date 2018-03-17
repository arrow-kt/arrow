package arrow.effects

import arrow.Kind
import arrow.TC
import arrow.core.Either
import arrow.effects.continuations.AsyncContinuation
import arrow.typeclass
import arrow.typeclasses.*
import arrow.typeclasses.continuations.BindingCatchContinuation
import arrow.typeclasses.continuations.BindingContinuation
import kotlin.coroutines.experimental.CoroutineContext

/** An asynchronous computation that might fail. **/
typealias Proc<A> = ((Either<Throwable, A>) -> Unit) -> Unit

/** The context required to run an asynchronous computation that may fail. **/
@typeclass(syntax = false)
interface Async<F, E> : MonadSuspend<F, E>, TC {
    fun <A> async(fa: Proc<A>): Kind<F, A>

    fun <A> never(): Kind<F, A> =
            async { }

    override fun <B> binding(context: CoroutineContext, c: suspend BindingContinuation<F, *>.() -> B): Kind<F, B> =
            AsyncContinuation.binding(::catch, this, context, c)

    override fun <B> bindingCatch(context: CoroutineContext, catch: (Throwable) -> E, c: suspend BindingCatchContinuation<F, E, *>.() -> B): Kind<F, B> =
            AsyncContinuation.binding(catch, this, context, c)

}

interface AsyncSyntax<F, E> : MonadSuspendSyntax<F, E> {

    fun async(): Async<F, E>

    override fun monadSuspend(): MonadSuspend<F, E> = async()

    override fun monadError(): MonadError<F, E> = async()

    override fun applicativeError(): ApplicativeError<F, E> = async()

    override fun applicative(): Applicative<F> = async()

    override fun functor(): Functor<F> = async()

    override fun monad(): Monad<F> = async()

    fun <A> Proc<A>.`async`(dummy: Unit = Unit): Kind<F, A> =
            this@AsyncSyntax.async().`async`(this)

    fun <A> `never`(dummy: Unit = Unit): Kind<F, A> =
            this@AsyncSyntax.async().`never`()
}
