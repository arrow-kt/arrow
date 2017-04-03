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

typealias ValidatedNel<E, A> = Validated<NonEmptyList<E>, A>

sealed class Validated<out E, out A> : HK2<Validated.F, E, A> {

    class F private constructor()

    companion object {

        fun <E, A> invalidNel(e: E): ValidatedNel<E, A> = Validated.Invalid(NonEmptyList(e, listOf()))

        /**
         * Converts a `Try<A>` to a `Validated<Throwable, A>`.
         */
        fun <A> fromTry(t: Try<A>): Validated<Throwable, A> = t.fold({ Invalid(it) }, { Valid(it) })

        /**
         * Converts an `Either<A, B>` to an `Validated<A, B>`.
         */
        fun <E, A> fromEither(e: Either<E, A>): Validated<E, A> = e.fold({ Invalid(it) }, { Valid(it) })

        /**
         * Converts an `Option<B>` to an `Validated<A, B>`, where the provided `ifNone` values is returned on
         * the invalid of the `Validated` when the specified `Option` is `None`.
         */
        fun <E, A> fromOption(o: Option<A>, ifNone: () -> E): Validated<E, A> = o.fold(
                { Invalid(ifNone()) },
                { Valid(it) }
        )
    }

    class Valid<out A>(val a: A) : Validated<Nothing, A>()

    class Invalid<out E>(val e: E) : Validated<E, Nothing>()

    fun <B> fold(fe: (E) -> B, fa: (A) -> B): B = when (this) {
        is Valid -> fa(a)
        is Invalid -> (fe(e))
    }

    val isValid = fold({ false }, { true })
    val isInvalid = fold({ true }, { false })

    /**
     * Run the side-effecting function on the value if it is Valid
     */
    fun forEach(f: (A) -> Unit): Unit = fold({ }, f)

    /**
     * Is this Valid and matching the given predicate
     */
    fun exist(predicate: (A) -> Boolean): Boolean = fold({ false }, { predicate(it) })

    /**
     * Converts the value to an Either<E, A>
     */
    fun toEither(): Either<E, A> = fold({ Either.Left(it) }, { Either.Right(it) })

    /**
     * Returns Valid values wrapped in Some, and None for Invalid values
     */
    fun toOption(): Option<A> = fold({ Option.None }, { Option.Some(it) })

    /**
     * Convert this value to a single element List if it is Valid,
     * otherwise return an empty List
     */
    fun toList(): List<A> = fold({ listOf() }, { listOf(it) })

    /** Lift the Invalid value into a NonEmptyList. */
    fun toValidatedNel(): ValidatedNel<E, A> = fold(
            { invalidNel(it) },
            { Valid(it) }
    )

    /**
     * Convert to an Either, apply a function, convert back. This is handy
     * when you want to use the Monadic properties of the Either type.
     */
    fun <EE, B> withEither(f: (Either<E, A>) -> Either<EE, B>): Validated<EE, B> = Validated.fromEither(f(toEither()))

    /**
     * Validated is a [[functor.Bifunctor]], this method applies one of the
     * given functions.
     */
    fun <EE, AA> bimap(fe: (E) -> EE, fa: (A) -> AA): Validated<EE, AA> = fold({ Invalid(fe(it)) }, { Valid(fa(it)) })

    /**
     * Apply a function to a Valid value, returning a new Valid value
     */
    fun <B> map(f: (A) -> B): Validated<E, B> = bimap({ it }, { f(it) })

    /**
     * Apply a function to an Invalid value, returning a new Invalid value.
     * Or, if the original valid was Valid, return it.
     */
    fun <EE> leftMap(f: (E) -> EE): Validated<EE, A> = bimap({ f(it) }, { it })

    /**
     * apply the given function to the value with the given B when
     * valid, otherwise return the given B
     */
    fun <B> foldLeft(b: B, f: (B, A) -> B): B = fold({ b }, { f(b, it) })

    fun swap(): Validated<A, E> = fold({ Valid(it) }, { Invalid(it) })
}

/**
 * Return the Valid value, or the default if Invalid
 */
fun <E, B> Validated<E, B>.getOrElse(default: () -> B): B = fold({ default() }, { it })

/**
 * Return the Valid value, or the result of f if Invalid
 */
fun <E, B> Validated<E, B>.valueOr(f: (E) -> B): B = fold({ f(it) }, { it })

/**
 * If `this` is valid return `this`, otherwise if `that` is valid return `that`, otherwise combine the failures.
 * This is similar to [[orElse]] except that here failures are accumulated.
 */
fun <E, A> Validated<E, A>.findValid(
        semigroup: Semigroup<E>,
        that: () -> Validated<E, A>): Validated<E, A> = fold(

        { e -> that().fold(
                { ee -> Validated.Invalid(semigroup.combine(e, ee)) },
                { Validated.Valid(it) }
        ) },
        { Validated.Valid(it) }
)

/**
 * Return this if it is Valid, or else fall back to the given default.
 * The functionality is similar to that of [[findValid]] except for failure accumulation,
 * where here only the error on the right is preserved and the error on the left is ignored.
 */
fun <E, A> Validated<E, A>.orElse(default: () -> Validated<E, A>): Validated<E, A> = fold(
        { default() },
        { Validated.Valid(it) }
)

/**
 * Apply a function (that returns a `Validated`) in the valid case.
 * Otherwise return the original `Validated`.
 *
 * This allows "chained" validation: the output of one validation can be fed
 * into another validation function.
 *
 * This function is similar to `flatMap` on `Either`. It's not called `flatMap`,
 * because by Cats convention, `flatMap` is a monadic bind that is consistent
 * with `ap`. This method is not consistent with [[ap]] (or other
 * `Apply`-based methods), because it has "fail-fast" behavior as opposed to
 * accumulating validation failures.
 */
fun <E, A, B> Validated<E, A>.andThen(f: (A) -> Validated<E, B>): Validated<E, B> = fold(
        { Validated.Invalid(it) },
        { f(it) }
)