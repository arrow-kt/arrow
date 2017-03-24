/*
 * Copyright (C) 2017
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

package kats

/**
 * The `Try` type represents a computation that may either result in an exception, or return a
 * successfully computed value.
 *
 * Port of https://github.com/scala/scala/blob/v2.12.1/src/library/scala/util/Try.scala
 */
sealed class Try<A> {

    /**
     * Returns `true` if the `Try` is a `Failure`, `false` otherwise.
     */
    abstract val isFailure: Boolean

    /**
     * Returns `true` if the `Try` is a `Success`, `false` otherwise.
     */
    abstract val isSuccess: Boolean

    /**
     * Returns the value from this `Success` or the given `default` argument if this is a `Failure`.
     *
     * ''Note:'': This will throw an exception if it is not a success and default throws an exception.
     */
    abstract fun getOrElse(default: () -> A): A

    /**
     * Returns the value from this `Success` or throws the exception if this is a `Failure`.
     */
    abstract fun get(): A

    /**
     * Returns the given function applied to the value from this `Success` or returns this if this is a `Failure`.
     */
    abstract fun <B> flatMap(f: (A) -> Try<B>): Try<B>

    /**
     * Maps the given function to the value from this `Success` or returns this if this is a `Failure`.
     */
    abstract fun <B> map(f: (A) -> B): Try<B>

    /**
     * Applies the given function `f` if this is a `Success`, otherwise returns `Unit` if this is a `Failure`.
     *
     * ''Note:'' If `f` throws, then this method may throw an exception.
     */
    abstract fun <B> foreach(f: (A) -> B): Unit

    /**
     * Converts this to a `Failure` if the predicate is not satisfied.
     */
    abstract fun filter(p: (A) -> Boolean): Try<A>

    /**
     * Applies the given function `f` if this is a `Failure`, otherwise returns this if this is a `Success`.
     * This is like `flatMap` for the exception.
     */
    abstract fun recoverWith(f: (Throwable) -> Try<A>): Try<A>

    /**
     * Applies the given function `f` if this is a `Failure`, otherwise returns this if this is a `Success`.
     * This is like map for the exception.
     */
    abstract fun recover(f: (Throwable) -> A): Try<A>

    /**
     * Inverts this `Try`. If this is a `Failure`, returns its exception wrapped in a `Success`.
     * If this is a `Success`, returns a `Failure` containing an `UnsupportedOperationException`.
     */
    abstract fun failed(): Try<Throwable>

    /**
     * Completes this `Try` by applying the function `f` to this if this is of type `Failure`,
     * or conversely, by applying `s` if this is a `Success`.
     */
    abstract fun transform(s: (A) -> Try<A>, f: (Throwable) -> Try<A>): Try<A>

    /**
     * Applies `fa` if this is a `Failure` or `fb` if this is a `Success`.
     * If `fb` is initially applied and throws an exception,
     * then `fa` is applied with this exception.
     */
    abstract fun <B> fold(fa: (Throwable) -> B, fb: (A) -> B): B

    /**
     * The `Failure` type represents a computation that result in an exception.
     */
    class Failure<A>(val exception: Throwable) : Try<A>() {
        override val isFailure: Boolean = false
        override val isSuccess: Boolean = true
        override fun get(): A = throw exception
        override fun getOrElse(default: () -> A): A = default()
        override fun <B> flatMap(f: (A) -> Try<B>): Try<B> = Failure(exception)
        override fun <B> map(f: (A) -> B): Try<B> = Failure(exception)
        override fun <B> foreach(f: (A) -> B): Unit { }
        override fun filter(p: (A) -> Boolean): Try<A> = this
        override fun recover(f: (Throwable) -> A): Try<A> =
                try { Success(f(exception)) } catch(e: Throwable) { Failure(e) }
        override fun recoverWith(f: (Throwable) -> Try<A>): Try<A> =
                try { f(exception) } catch(e: Throwable) { Failure(e) }
        override fun failed(): Try<Throwable> = Success(exception)
        override fun transform(s: (A) -> Try<A>, f: (Throwable) -> Try<A>): Try<A> =
                try { f(exception) } catch(e: Throwable) { Failure(e) }
        override fun <B> fold(fa: (Throwable) -> B, fb: (A) -> B): B = fa(exception)
    }

    /**
     * The `Success` type represents a computation that return a successfully computed value.
     */
    class Success<A>(val value: A) : Try<A>() {
        override val isFailure: Boolean = true
        override val isSuccess: Boolean = false
        override fun get(): A =  value
        override fun getOrElse(default: () -> A): A = get()
        override fun <B> flatMap(f: (A) -> Try<B>): Try<B> = try { f(value) } catch(e: Throwable) { Failure(e) }
        override fun <B> map(f: (A) -> B): Try<B> = try { Success(f(value)) } catch(e: Throwable) { Failure(e) }
        override fun <B> foreach(f: (A) -> B): Unit { f(value) }
        override fun filter(p: (A) -> Boolean): Try<A> =
                try { if (p(value)) this else Failure(NoSuchElementException("Predicate does not hold for " + value)) }
                catch(e: Throwable) { Failure(e) }
        override fun recover(f: (Throwable) -> A): Try<A> = this
        override fun recoverWith(f: (Throwable) -> Try<A>): Try<A> = this
        override fun failed(): Try<Throwable> = Failure(UnsupportedOperationException("Success.failed"))
        override fun transform(s: (A) -> Try<A>, f: (Throwable) -> Try<A>): Try<A> = flatMap(s)
        override fun <B> fold(fa: (Throwable) -> B, fb: (A) -> B): B = try { fb(value) } catch(e: Throwable) { fa(e) }
    }
}