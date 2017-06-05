package katz

typealias ValidatedKind<E, A> = HK2<Validated.F, E, A>
typealias ValidatedF<A> = HK<Validated.F, A>
typealias ValidatedNel<E, A> = Validated<NonEmptyList<E>, A>

fun <E, A> ValidatedKind<E, A>.ev(): Validated<E, A> = this as Validated<E, A>

/**
 * Port of https://github.com/typelevel/cats/blob/master/core/src/main/scala/cats/data/Validated.scala
 */
sealed class Validated<out E, out A> : ValidatedKind<E, A> {

    class F private constructor()

    companion object {

        @JvmStatic fun <E, A> invalidNel(e: E): ValidatedNel<E, A> =
                Validated.Invalid(NonEmptyList(e, listOf()))

        /**
         * Converts a `Try<A>` to a `Validated<Throwable, A>`.
         */
        @JvmStatic fun <A> fromTry(t: Try<A>): Validated<Throwable, A> =
                t.fold({ Invalid(it) }, { Valid(it) })

        /**
         * Converts an `Either<A, B>` to an `Validated<A, B>`.
         */
        @JvmStatic fun <E, A> fromEither(e: Either<E, A>): Validated<E, A> =
                e.fold({ Invalid(it) }, { Valid(it) })

        /**
         * Converts an `Option<B>` to an `Validated<A, B>`, where the provided `ifNone` values is returned on
         * the invalid of the `Validated` when the specified `Option` is `None`.
         */
        @JvmStatic fun <E, A> fromOption(o: Option<A>, ifNone: () -> E): Validated<E, A> =
                o.fold(
                        { Invalid(ifNone()) },
                        { Valid(it) }
                )
    }

    data class Valid<out A>(val a: A) : Validated<Nothing, A>()

    data class Invalid<out E>(val e: E) : Validated<E, Nothing>()

    inline fun <B> fold(fe: (E) -> B, fa: (A) -> B): B =
            when (this) {
                is Valid -> fa(a)
                is Invalid -> (fe(e))
            }

    val isValid =
            fold({ false }, { true })
    val isInvalid =
            fold({ true }, { false })

    /**
     * Is this Valid and matching the given predicate
     */
    fun exist(predicate: (A) -> Boolean): Boolean =
            fold({ false }, { predicate(it) })

    /**
     * Converts the value to an Either<E, A>
     */
    fun toEither(): Either<E, A> =
            fold({ Either.Left(it) }, { Either.Right(it) })

    /**
     * Returns Valid values wrapped in Some, and None for Invalid values
     */
    fun toOption(): Option<A> =
            fold({ Option.None }, { Option.Some(it) })

    /**
     * Convert this value to a single element List if it is Valid,
     * otherwise return an empty List
     */
    fun toList(): List<A> =
            fold({ listOf() }, { listOf(it) })

    /** Lift the Invalid value into a NonEmptyList. */
    fun toValidatedNel(): ValidatedNel<E, A> =
            fold(
                    { invalidNel(it) },
                    { Valid(it) }
            )

    /**
     * Convert to an Either, apply a function, convert back. This is handy
     * when you want to use the Monadic properties of the Either type.
     */
    fun <EE, B> withEither(f: (Either<E, A>) -> Either<EE, B>): Validated<EE, B> =
            Validated.fromEither(f(toEither()))

    /**
     * Validated is a [[functor.Bifunctor]], this method applies one of the
     * given functions.
     */
    fun <EE, AA> bimap(fe: (E) -> EE, fa: (A) -> AA): Validated<EE, AA> =
            fold({ Invalid(fe(it)) }, { Valid(fa(it)) })

    /**
     * Apply a function to a Valid value, returning a new Valid value
     */
    fun <B> map(f: (A) -> B): Validated<E, B> =
            bimap({ it }, { f(it) })

    /**
     * Apply a function to an Invalid value, returning a new Invalid value.
     * Or, if the original valid was Valid, return it.
     */
    fun <EE> leftMap(f: (E) -> EE): Validated<EE, A> =
            bimap({ f(it) }, { it })

    /**
     * apply the given function to the value with the given B when
     * valid, otherwise return the given B
     */
    fun <B> foldLeft(b: B, f: (B, A) -> B): B =
            fold({ b }, { f(b, it) })

    fun swap(): Validated<A, E> =
            fold({ Valid(it) }, { Invalid(it) })
}

/**
 * Return the Valid value, or the default if Invalid
 */
fun <E, B> Validated<E, B>.getOrElse(default: () -> B): B =
        fold({ default() }, { it })

/**
 * Return the Valid value, or the result of f if Invalid
 */
fun <E, B> Validated<E, B>.valueOr(f: (E) -> B): B =
        fold({ f(it) }, { it })

/**
 * If `this` is valid return `this`, otherwise if `that` is valid return `that`, otherwise combine the failures.
 * This is similar to [[orElse]] except that here failures are accumulated.
 */
fun <E, A> Validated<E, A>.findValid(SE: Semigroup<E>, that: () -> Validated<E, A>): Validated<E, A> =
        fold(

                { e ->
                    that().fold(
                            { ee -> Validated.Invalid(SE.combine(e, ee)) },
                            { Validated.Valid(it) }
                    )
                },
                { Validated.Valid(it) }
        )

/**
 * Return this if it is Valid, or else fall back to the given default.
 * The functionality is similar to that of [[findValid]] except for failure accumulation,
 * where here only the error on the right is preserved and the error on the left is ignored.
 */
fun <E, A> Validated<E, A>.orElse(default: () -> Validated<E, A>): Validated<E, A> =
        fold(
                { default() },
                { Validated.Valid(it) }
        )

/**
 * From Apply:
 * if both the function and this value are Valid, apply the function
 */
fun <E, A, B> Validated<E, A>.ap(f: Validated<E, (A) -> B>, SE: Semigroup<E>): Validated<E, B> =
        when (this) {
            is Validated.Valid -> {
                f.fold({ Validated.Invalid(it) }, { Validated.Valid(it(a)) })
            }
            is Validated.Invalid -> {
                f.fold({ Validated.Invalid(SE.combine(it, e)) }, { Validated.Invalid(e) })
            }
        }
