package kategory

import kotlin.coroutines.experimental.*
import kotlin.coroutines.experimental.intrinsics.COROUTINE_SUSPENDED
import kotlin.coroutines.experimental.intrinsics.suspendCoroutineOrReturn

@RestrictsSuspension
open class MonadErrorContinuation<F, A>(ME: MonadError<F, Throwable>, override val context: CoroutineContext = EmptyCoroutineContext) :
        MonadContinuation<F, A>(ME), MonadError<F, Throwable> by ME {

    override fun bindingInContextContinuation(context: CoroutineContext): Continuation<HK<F, A>> =
            object : Continuation<HK<F, A>> {
                override val context: CoroutineContext = context

                override fun resume(value: HK<F, A>) {
                    returnedMonad = value
                }

                override fun resumeWithException(exception: Throwable) {
                    returnedMonad = raiseError(exception)
                }
            }

    override fun resumeWithException(exception: Throwable) {
        returnedMonad = raiseError(exception)
    }

    override suspend fun <B> bindInM(context: CoroutineContext, m: () -> HK<F, B>): B = suspendCoroutineOrReturn { c ->
        val labelHere = c.stackLabels // save the whole coroutine stack labels

        val creation = MonadContinuation<F, A>(this, context)
        val execution = MonadContinuation<F, A>(this, context)

        val monadExecution: suspend (B) -> HK<F, A> = {
            c.stackLabels = labelHere
            c.resume(it)
            returnedMonad
        }
        val monadCreation: suspend () -> HK<F, A> = {
            returnedMonad = try {
                flatMap(m(), { xx: B ->
                    monadExecution.startCoroutine(xx, execution)
                    returnedMonad
                })
            } catch (t: Throwable) {
                raiseError(t)
            }
            returnedMonad
        }

        returnedMonad = flatMap(pure(Unit), {
            monadCreation.startCoroutine(creation)
            returnedMonad
        })
        COROUTINE_SUSPENDED
    }
}