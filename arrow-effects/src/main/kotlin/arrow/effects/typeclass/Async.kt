package arrow.effects

import arrow.HK
import arrow.TC
import arrow.core.Either
import arrow.typeclass

        /** An asynchronous computation that might fail. **/
typealias Proc<A> = ((Either<Throwable, A>) -> Unit) -> Unit

/** The context required to run an asynchronous computation that may fail. **/
@typeclass
interface Async<F> : Sync<F>, TC {
    fun <A> async(fa: Proc<A>): HK<F, A>

    fun <A> never(): HK<F, A> =
            async { }
}
