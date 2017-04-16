/*
 * Copyright (C) 2017 The Katz Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package katz

import java.io.Serializable
import kotlin.coroutines.experimental.RestrictsSuspension
import kotlin.coroutines.experimental.startCoroutine

interface MonadError<F, E> : ApplicativeError<F, E>, Monad<F>, Typeclass {

    fun <A> ensure(fa: HK<F, A>, error: () -> E, predicate: (A) -> Boolean): HK<F, A> =
            flatMap(fa, {
                if (predicate(it)) pure(it)
                else raiseError(error())
            })

}

@RestrictsSuspension
class MonadErrorContinuation<F, A>(val ME : MonadError<F, Throwable>) : Serializable, MonadContinuation<F, A>(ME) {

    override fun resumeWithException(exception: Throwable) {
        returnedMonad = ME.raiseError(exception)
    }
}

/**
 * Entry point for monad bindings which enables for comprehensions. The underlying impl is based on coroutines.
 * A coroutines is initiated and inside `MonadErrorContinuation` suspended yielding to `flatMap` once all the flatMap binds are completed
 * the underlying monad is returned from the act of executing the coroutine
 *
 * This one operates over MonadError instances that can support `Throwable` in their error type automatically lifting
 * errors as failed computations in their monadic context and not letting exceptions thrown as the regular monad binding does.
 */
fun <F, B> MonadError<F, Throwable>.bindingE(c: suspend MonadErrorContinuation<F, *>.() -> HK<F, B>): HK<F, B> {
    val continuation = MonadErrorContinuation<F, B>(this)
    val f: suspend MonadErrorContinuation<F, *>.() -> HK<F, B> = { c() }
    f.startCoroutine(continuation, continuation)
    return continuation.returnedMonad
}

inline fun <reified F, reified E> monadError(): MonadError<F, E> =
        instance(InstanceParametrizedType(Functor::class.java, listOf(F::class.java, E::class.java)))