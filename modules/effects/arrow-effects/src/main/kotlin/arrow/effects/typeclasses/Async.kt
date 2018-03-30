package arrow.effects.typeclasses

import arrow.Kind
import arrow.core.Either

/** An asynchronous computation that might fail. **/
typealias Proc<A> = ((Either<Throwable, A>) -> Unit) -> Unit

inline operator fun <F, A> Async<F>.invoke(ff: Async<F>.() -> A) =
        run(ff)

/** The context required to run an asynchronous computation that may fail. **/
interface Async<F> : MonadSuspend<F> {
    fun <A> async(fa: Proc<A>): Kind<F, A>

    fun <A> never(): Kind<F, A> =
            async { }
}
