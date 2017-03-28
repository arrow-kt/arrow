/*
 * Copyright (C) 2017 The Kats Authors
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

/**
 * Basic implementation of the Reader monad. Provides an "implicit" context (configuration) for
 * function execution. Intended to provide Dependency Injection.
 */
class Reader<C : Any, out A : Any>(val run: (C) -> A) {

    inline fun <B : Any> map(crossinline fa: (A) -> B): Reader<C, B> = Reader { c -> fa(run(c)) }

    inline fun <B : Any> flatMap(crossinline fa: (A) -> Reader<C, B>): Reader<C, B> = Reader { c ->
        fa(run(c)).run(c)
    }

    fun <B : Any> zip(other: Reader<C, B>): Reader<C, Pair<A, B>> =
            this.flatMap { a ->
                other.map { b -> Pair(a, b) }
            }

    /**
     * local combinator allows switching the environment to unify two different dependency types, so
     * you can compose readers with different type dependencies.
     *
     * D: type represents a bigger context than C.
     * @param fd: function to convert from the bigger context D to a context of type C.
     */
    inline fun <D : Any> local(crossinline fd: (D) -> C): Reader<D, A> = Reader { d ->
        run(fd(d))
    }

    companion object Factory {

        /**
         * Lifts an A value to Reader wrapping it in a supplier function with a Nothing argument.
         */
        fun <C : Any, A : Any> pure(a: A): Reader<C, A> = Reader { _ -> a }
    }
}

fun <A : Any, B : Any> ((A) -> B).reader() : Reader<A, B> = Reader(this)
