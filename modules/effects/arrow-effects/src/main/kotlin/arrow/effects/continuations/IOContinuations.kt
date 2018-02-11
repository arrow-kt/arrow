package arrow.effects.continuations

import arrow.Kind
import arrow.core.Either
import arrow.effects.ForIO
import arrow.effects.IOMonadErrorInstance
import arrow.effects.IOOf
import arrow.effects.fix
import arrow.typeclasses.continuations.BindingCatchContinuation
import kotlin.coroutines.experimental.CoroutineContext
import kotlin.coroutines.experimental.suspendCoroutine

class IOContinuation<A>(private val cb: (Either<Throwable, A>) -> Unit, override val context: CoroutineContext) :
        BindingCatchContinuation<ForIO, Throwable, A>, IOMonadErrorInstance {

    override fun resume(value: IOOf<A>) {
        value.fix().unsafeRunAsync(cb)
    }

    override fun resumeWithException(exception: Throwable) {
        cb(Either.left(exception))
    }

    override suspend fun <B> bind(m: () -> Kind<ForIO, B>): B = suspendCoroutine { cc ->
        m().fix().unsafeRunAsync { it.fold({ cc.resumeWithException(it) }, { cc.resume(it) }) }
    }
}
