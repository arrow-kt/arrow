package arrow.core

import arrow.typeclasses.Monoid
import arrow.typeclasses.Semigroup
import arrow.core.Either.Left
import arrow.core.Either.Right
import arrow.typeclasses.MonoidDeprecation
import arrow.typeclasses.combine
import kotlin.experimental.ExperimentalTypeInference
import kotlin.jvm.JvmName
import kotlin.jvm.JvmStatic

@Deprecated(
  ValidatedDeprMsg + "ValidatedNel is being replaced by EitherNel",
  ReplaceWith("EitherNel<E, A>", "arrow.core.EitherNel")
)
public typealias ValidatedNel<E, A> = Validated<Nel<E>, A>

@Deprecated(
  ValidatedDeprMsg + "Use Right to construct Either values instead",
  ReplaceWith("Either.Right(value)", "arrow.core.Either")
)
public typealias Valid<A> = Validated.Valid<A>

@Deprecated(
  ValidatedDeprMsg + "Use Left to construct Either values instead",
  ReplaceWith("Either.Left(value)", "arrow.core.Either")
)
public typealias Invalid<E> = Validated.Invalid<E>

@Deprecated(ValidatedDeprMsg + "You can find more details about how to migrate on the Github release page, or the 1.2.0 release post.")
public sealed class Validated<out E, out A> {

  public companion object {

    @Deprecated(
      ValidatedDeprMsg + "Use leftNel instead to construct the equivalent Either value",
      ReplaceWith("e.leftNel()", "arrow.core.leftNel")
    )
    @JvmStatic
    public fun <E, A> invalidNel(e: E): ValidatedNel<E, A> = Invalid(nonEmptyListOf(e))

    @Deprecated(
      ValidatedDeprMsg + "Use right instead to construct the equivalent Either value",
      ReplaceWith("a.right()", "arrow.core.right")
    )
    @JvmStatic
    public fun <E, A> validNel(a: A): ValidatedNel<E, A> = Valid(a)

    /**
     * Converts an `Either<E, A>` to a `Validated<E, A>`.
     */
    @Deprecated(ValidatedDeprMsg)
    @JvmStatic
    public fun <E, A> fromEither(e: Either<E, A>): Validated<E, A> = e.fold({ Invalid(it) }, { Valid(it) })

    /**
     * Converts an `Option<A>` to a `Validated<E, A>`, where the provided `ifNone` output value is returned as [Invalid]
     * when the specified `Option` is `None`.
     */
    @Deprecated(
      DeprAndNicheMsg + "Prefer using toEither on Option instead",
      ReplaceWith("o.toEither(ifNone).toValidated()")
    )
    @JvmStatic
    public inline fun <E, A> fromOption(o: Option<A>, ifNone: () -> E): Validated<E, A> =
      o.fold(
        { Invalid(ifNone()) },
        { Valid(it) }
      )

    /**
     * Converts a nullable `A?` to a `Validated<E, A>`, where the provided `ifNull` output value is returned as [Invalid]
     * when the specified value is null.
     */
    @Deprecated(
      DeprAndNicheMsg + "Prefer Kotlin nullable syntax, or ensureNotNull inside Either DSL",
      ReplaceWith("value?.valid() ?: ifNull().invalid()")
    )
    @JvmStatic
    public inline fun <E, A> fromNullable(value: A?, ifNull: () -> E): Validated<E, A> =
      value?.let(::Valid) ?: Invalid(ifNull())

    @Deprecated(
      ValidatedDeprMsg + "Use Either.catch instead",
      ReplaceWith("Either.catch(f).toValidated()")
    )
    @JvmStatic
    @JvmName("tryCatch")
    public inline fun <A> catch(f: () -> A): Validated<Throwable, A> =
      try {
        f().valid()
      } catch (e: Throwable) {
        e.nonFatalOrThrow().invalid()
      }

    @Deprecated(
      DeprAndNicheMsg + "Use Either.catch and mapLeft instead",
      ReplaceWith("Either.catch(f).mapLeft(recover).toValidated()")
    )
    @JvmStatic
    @JvmName("tryCatch")
    public inline fun <E, A> catch(recover: (Throwable) -> E, f: () -> A): Validated<E, A> =
      catch(f).mapLeft(recover)

    @Deprecated(
      DeprAndNicheMsg + "Use Either.catch and toEitherNel instead",
      ReplaceWith("Either.catch(f).toEitherNel().toValidated()")
    )
    @JvmStatic
    public inline fun <A> catchNel(f: () -> A): ValidatedNel<Throwable, A> =
      try {
        f().validNel()
      } catch (e: Throwable) {
        e.nonFatalOrThrow().invalidNel()
      }

    @Deprecated(
      DeprAndNicheMsg + "Prefer creating explicit lambdas instead",
      ReplaceWith("{ it.map(f) }")
    )
    @JvmStatic
    public inline fun <E, A, B> lift(crossinline f: (A) -> B): (Validated<E, A>) -> Validated<E, B> =
      { fa -> fa.map(f) }

    /**
     * Lifts two functions to the Bifunctor type.
     *
     * ```kotlin
     * import arrow.core.*
     *
     * fun main(args: Array<String>) {
     *   //sampleStart
     *   val f = Validated.lift(String::toUpperCase, Int::inc)
     *   val res1 = f("test".invalid())
     *   val res2 = f(1.valid())
     *   //sampleEnd
     *   println("res1: $res1")
     *   println("res2: $res2")
     * }
     * ```
     * <!--- KNIT example-validated-01.kt -->
     */
    @Deprecated(
      DeprAndNicheMsg + "Prefer creating explicit lambdas instead",
      ReplaceWith("{ it.bimap(fl, fr) }")
    )
    @JvmStatic
    public inline fun <A, B, C, D> lift(
      crossinline fl: (A) -> C,
      crossinline fr: (B) -> D
    ): (Validated<A, B>) -> Validated<C, D> =
      { fa -> fa.bimap(fl, fr) }
  }

  /**
   * Discards the [A] value inside [Validated] signaling this container may be pointing to a noop
   * or an effect whose return value is deliberately ignored. The singleton value [Unit] serves as signal.
   *
   * ```kotlin
   * import arrow.core.*
   *
   * fun main(args: Array<String>) {
   *   val result =
   *   //sampleStart
   *   "Hello World".valid().void()
   *   //sampleEnd
   *   println(result)
   * }
   * ```
   * <!--- KNIT example-validated-02.kt -->
   */
  @Deprecated(
    DeprAndNicheMsg + "Use map on Either after refactoring instead",
    ReplaceWith("toEither().map { }.toValidated()")
  )
  public fun void(): Validated<E, Unit> =
    map { Unit }

  @Deprecated(
    DeprAndNicheMsg + "Prefer using the Either DSL, or explicit fold or when",
    ReplaceWith(
      "fold({ emptyList() }, { fa(it).map(::Valid) })",
      "arrow.core.Valid"
    )
  )
  @OptIn(ExperimentalTypeInference::class)
  @OverloadResolutionByLambdaReturnType
  public inline fun <B> traverse(fa: (A) -> Iterable<B>): List<Validated<E, B>> =
    fold({ emptyList() }, { a -> fa(a).map { Valid(it) } })

  @Deprecated(
    DeprAndNicheMsg + "Prefer using the Either DSL, or explicit fold or when",
    ReplaceWith(
      "fold({ it.invalid().right() }, { fa(it).map(::Valid) })",
      "arrow.core.invalid", "arrow.core.right", "arrow.core.Valid"
    )
  )
  @OptIn(ExperimentalTypeInference::class)
  @OverloadResolutionByLambdaReturnType
  public inline fun <EE, B> traverse(fa: (A) -> Either<EE, B>): Either<EE, Validated<E, B>> =
    when (this) {
      is Valid -> fa(this.value).map { Valid(it) }
      is Invalid -> this.right()
    }

  @Deprecated("traverseEither is being renamed to traverse to simplify the Arrow API", ReplaceWith("traverse(fa)"))
  public inline fun <EE, B> traverseEither(fa: (A) -> Either<EE, B>): Either<EE, Validated<E, B>> =
    traverse(fa)

  @Deprecated(
    DeprAndNicheMsg + "Prefer using the Either DSL, or explicit fold or when",
    ReplaceWith(
      "fold({ None }, { fa(it).map(::Valid) })",
      "arrow.core.None", "arrow.core.Valid"
    )
  )
  @OptIn(ExperimentalTypeInference::class)
  @OverloadResolutionByLambdaReturnType
  public inline fun <B> traverse(fa: (A) -> Option<B>): Option<Validated<E, B>> =
    when (this) {
      is Valid -> fa(this.value).map { Valid(it) }
      is Invalid -> None
    }

  @Deprecated("traverseOption is being renamed to traverse to simplify the Arrow API", ReplaceWith("traverse(fa)"))
  public inline fun <B> traverseOption(fa: (A) -> Option<B>): Option<Validated<E, B>> =
    traverse(fa)

  @Deprecated(
    DeprAndNicheMsg + "Use orNull() and Kotlin nullable types",
    ReplaceWith("orNull()?.let(fa)?.valid()", "arrow.core.valid")
  )
  public inline fun <B> traverseNullable(fa: (A) -> B?): Validated<E, B>? =
    when (this) {
      is Valid -> fa(this.value)?.let { Valid(it) }
      is Invalid -> null
    }

  @Deprecated(
    DeprAndNicheMsg + "Prefer when or fold instead",
    ReplaceWith("fold({ fe(c, it) }, { fa(c, it) })")
  )
  public inline fun <B> bifoldLeft(
    c: B,
    fe: (B, E) -> B,
    fa: (B, A) -> B
  ): B =
    fold({ fe(c, it) }, { fa(c, it) })

  @Deprecated(
    DeprAndNicheMsg + "Prefer when or fold instead",
    ReplaceWith("fold(g, f)")
  )
  public inline fun <B> bifoldMap(MN: Monoid<B>, g: (E) -> B, f: (A) -> B): B =
    fold(g, f)

  @Deprecated(
    DeprAndNicheMsg + "Prefer explicit fold instead",
    ReplaceWith(
      "fold({ fe(it).map { Invalid(it) } }, { fa(it).map { Valid(it) } })",
      "arrow.core.Valid", "arrow.core.Invalid"
    )
  )
  public inline fun <EE, B> bitraverse(fe: (E) -> Iterable<EE>, fa: (A) -> Iterable<B>): List<Validated<EE, B>> =
    fold({ fe(it).map { Invalid(it) } }, { fa(it).map { Valid(it) } })

  @Deprecated(
    DeprAndNicheMsg + "Prefer explicit fold instead",
    ReplaceWith(
      "fold({ fe(it).map { Invalid(it) } }, { fa(it).map { Valid(it) } })",
      "arrow.core.Valid", "arrow.core.Invalid"
    )
  )
  public inline fun <EE, B, C> bitraverseEither(
    fe: (E) -> Either<EE, B>,
    fa: (A) -> Either<EE, C>
  ): Either<EE, Validated<B, C>> =
    fold({ fe(it).map { Invalid(it) } }, { fa(it).map { Valid(it) } })

  @Deprecated(
    DeprAndNicheMsg + "Prefer explicit fold instead",
    ReplaceWith(
      "fold({ fe(it).map(::Invalid) }, { fa(it).map(::Valid) })",
      "arrow.core.Valid", "arrow.core.Invalid"
    )
  )
  public inline fun <B, C> bitraverseOption(
    fe: (E) -> Option<B>,
    fa: (A) -> Option<C>
  ): Option<Validated<B, C>> =
    fold({ fe(it).map(::Invalid) }, { fa(it).map(::Valid) })

  @Deprecated(
    DeprAndNicheMsg + "Prefer explicit fold instead",
    ReplaceWith(
      "fold({ fe(it)?.let(::Invalid) }, { fa(it)?.let(::Valid) })",
      "arrow.core.Valid", "arrow.core.Invalid"
    )
  )
  public inline fun <B, C> bitraverseNullable(
    fe: (E) -> B?,
    fa: (A) -> C?
  ): Validated<B, C>? =
    fold({ fe(it)?.let(::Invalid) }, { fa(it)?.let(::Valid) })

  @Deprecated(
    ValidatedDeprMsg + "Use fold on Either after refactoring instead",
    ReplaceWith("toEither().fold({ invalidValue }, f)")
  )
  public inline fun <B> foldMap(MB: Monoid<B>, f: (A) -> B): B =
    fold({ MB.empty() }, f)

  override fun toString(): String = fold(
    { "Validated.Invalid($it)" },
    { "Validated.Valid($it)" }
  )

  @Deprecated(
    ValidatedDeprMsg + "Use Right to construct Either values instead",
    ReplaceWith("Either.Right(value)", "arrow.core.Either")
  )
  public data class Valid<out A>(val value: A) : Validated<Nothing, A>() {
    override fun toString(): String = "Validated.Valid($value)"

    public companion object {
      @PublishedApi
      internal val unit: Validated<Nothing, Unit> =
        Validated.Valid(Unit)
    }
  }

  @Deprecated(
    ValidatedDeprMsg + "Use Left to construct Either values instead",
    ReplaceWith("Either.Left(value)", "arrow.core.Either")
  )
  public data class Invalid<out E>(val value: E) : Validated<E, Nothing>() {
    override fun toString(): String = "Validated.Invalid($value)"
  }

  @Deprecated(
    ValidatedDeprMsg + "Use fold on Either after refactoring",
    ReplaceWith("toEither().fold(fe, fa)")
  )
  public inline fun <B> fold(fe: (E) -> B, fa: (A) -> B): B =
    when (this) {
      is Valid -> fa(value)
      is Invalid -> (fe(value))
    }

  @Deprecated(
    ValidatedDeprMsg + "Use isRight on Either after refactoring",
    ReplaceWith("toEither().isRight()")
  )
  public val isValid: Boolean =
    fold({ false }, { true })

  @Deprecated(
    ValidatedDeprMsg + "Use isLeft on Either after refactoring",
    ReplaceWith("toEither().isLeft()")
  )
  public val isInvalid: Boolean =
    fold({ true }, { false })

  /**
   * Is this Valid and matching the given predicate
   */
  @Deprecated(
    ValidatedDeprMsg + "Use isRight on Either after refactoring",
    ReplaceWith("toEither().isRight(predicate)")
  )
  public inline fun exist(predicate: (A) -> Boolean): Boolean =
    fold({ false }, predicate)

  @Deprecated(
    DeprAndNicheMsg + "Use getOrNull and takeIf on Either after refactoring",
    ReplaceWith("toEither().getOrNull()?.takeIf(predicate)")
  )
  public inline fun findOrNull(predicate: (A) -> Boolean): A? =
    when (this) {
      is Valid -> if (predicate(this.value)) this.value else null
      is Invalid -> null
    }

  @Deprecated(
    DeprAndNicheMsg + "Use fold on Either after refactoring",
    ReplaceWith("toEither().fold({ true }, predicate)")
  )
  public inline fun all(predicate: (A) -> Boolean): Boolean =
    fold({ true }, predicate)

  @Deprecated(
    ValidatedDeprMsg + "Use isRight on Either after refactoring",
    ReplaceWith("toEither().isLeft()")
  )
  public fun isEmpty(): Boolean = isInvalid

  @Deprecated(
    ValidatedDeprMsg + "Use isRight on Either after refactoring",
    ReplaceWith("toEither().isRight()")
  )
  public fun isNotEmpty(): Boolean = isValid

  /**
   * Converts the value to an Either<E, A>
   */
  @Deprecated(ValidatedDeprMsg + "Drop this call after refactoring")
  public fun toEither(): Either<E, A> =
    fold(::Left, ::Right)

  /**
   * Returns Valid values wrapped in Some, and None for Invalid values
   */
  @Deprecated(
    ValidatedDeprMsg + "Use getOrNone on Either after refactoring",
    ReplaceWith("toEither().getOrNone()")
  )
  public fun toOption(): Option<A> =
    fold({ None }, ::Some)

  /**
   * Convert this value to a single element List if it is Valid,
   * otherwise return an empty List
   */
  @Deprecated(
    DeprAndNicheMsg + "Use fold instead",
    ReplaceWith("fold({ emptyList() }, ::listOf)")
  )
  public fun toList(): List<A> =
    fold({ listOf() }, ::listOf)

  /** Lift the Invalid value into a NonEmptyList. */
  @Deprecated(
    ValidatedDeprMsg + "Use toEitherNel directly instead",
    ReplaceWith("toEither().toEitherNel().toValidated()")
  )
  public fun toValidatedNel(): ValidatedNel<E, A> =
    fold({ invalidNel(it) }, ::Valid)

  /**
   * Convert to an Either, apply a function, convert back. This is handy
   * when you want to use the Monadic properties of the Either type.
   */
  @Deprecated(
    ValidatedDeprMsg + "Use Either directly instead",
    ReplaceWith("toEither().let(f).toValidated()")
  )
  public inline fun <EE, B> withEither(f: (Either<E, A>) -> Either<EE, B>): Validated<EE, B> =
    fromEither(f(toEither()))

  /**
   * From [arrow.typeclasses.Bifunctor], maps both types of this Validated.
   *
   * Apply a function to an Invalid or Valid value, returning a new Invalid or Valid value respectively.
   */
  @Deprecated(
    ValidatedDeprMsg + "Use map and mapLeft on Either after refactoring",
    ReplaceWith("toEither().mapLeft(fe).map(fa)")
  )
  public inline fun <EE, B> bimap(fe: (E) -> EE, fa: (A) -> B): Validated<EE, B> =
    fold({ Invalid(fe(it)) }, { Valid(fa(it)) })

  /**
   * Apply a function to a Valid value, returning a new Valid value
   */
  @Deprecated(
    ValidatedDeprMsg + "Use map on Either after refactoring",
    ReplaceWith("toEither().map(f).toValidated()")
  )
  public inline fun <B> map(f: (A) -> B): Validated<E, B> =
    bimap(::identity, f)

  /**
   * Apply a function to an Invalid value, returning a new Invalid value.
   * Or, if the original valid was Valid, return it.
   */
  @Deprecated(
    ValidatedDeprMsg + "Use mapLeft on Either after refactoring",
    ReplaceWith("toEither().mapLeft(f).toValidated()")
  )
  public inline fun <EE> mapLeft(f: (E) -> EE): Validated<EE, A> =
    bimap(f, ::identity)

  /**
   * The given function is applied as a fire and forget effect
   * if this is `Invalid`.
   * When applied the result is ignored and the original
   * Validated value is returned
   *
   * Example:
   * ```kotlin
   * import arrow.core.Validated
   *
   * fun main() {
   *   Validated.Valid(12).tapInvalid { println("flower") } // Result: Valid(12)
   *   Validated.Invalid(12).tapInvalid { println("flower") }  // Result: prints "flower" and returns: Invalid(12)
   * }
   * ```
   * <!--- KNIT example-validated-03.kt -->
   */
  @Deprecated(
    ValidatedDeprMsg + "Use onLeft on Either after refactoring",
    ReplaceWith("toEither().onLeft(f).toValidated()")
  )
  public inline fun tapInvalid(f: (E) -> Unit): Validated<E, A> =
    when (this) {
      is Invalid -> {
        f(this.value)
        this
      }

      is Valid -> this
    }

  /**
   * The given function is applied as a fire and forget effect
   * if this is `Valid`.
   * When applied the result is ignored and the original
   * Validated value is returned
   *
   * Example:
   * ```kotlin
   * import arrow.core.Validated
   *
   * fun main() {
   *   Validated.Valid(12).tap { println("flower") } // Result: prints "flower" and returns: Valid(12)
   *   Validated.Invalid(12).tap { println("flower") }  // Result: Invalid(12)
   * }
   * ```
   * <!--- KNIT example-validated-04.kt -->
   */
  @Deprecated(
    ValidatedDeprMsg + "Use onRight on Either after refactoring",
    ReplaceWith("toEither().onRight(f).toValidated()")
  )
  public inline fun tap(f: (A) -> Unit): Validated<E, A> =
    when (this) {
      is Invalid -> this
      is Valid -> {
        f(this.value)
        this
      }
    }

  /**
   * apply the given function to the value with the given B when
   * valid, otherwise return the given B
   */
  @Deprecated(
    ValidatedDeprMsg + "Use fold on Either after refactoring",
    ReplaceWith("toEither().fold({ b }) { f(b, it) }")
  )
  public inline fun <B> foldLeft(b: B, f: (B, A) -> B): B =
    fold({ b }, { f(b, it) })

  @Deprecated(
    ValidatedDeprMsg + "Use swap on Either after refactoring",
    ReplaceWith("toEither().swap()")
  )
  public fun swap(): Validated<A, E> =
    fold(::Valid, ::Invalid)
}

@Deprecated(
  ValidatedDeprMsg + "zipOrAccumulate for Either now exposes this same functionality",
  ReplaceWith(
    "Either.zipOrAccumulate({ e1, e2 -> e1 + e2 }, toEither(), fb.toEither(), ::Pair).toValidated()",
    "arrow.core.Either"
  )
)
public fun <E, A, B> Validated<E, A>.zip(SE: Semigroup<E>, fb: Validated<E, B>): Validated<E, Pair<A, B>> =
  Either.zipOrAccumulate(SE::combine, toEither(), fb.toEither(), ::Pair).toValidated()

@Deprecated(
  ValidatedDeprMsg + "zipOrAccumulate for Either now exposes this same functionality",
  ReplaceWith(
    "Either.zipOrAccumulate({ e1, e2 -> e1 + e2 }, toEither(), b.toEither(), f).toValidated()",
    "arrow.core.Either"
  )
)
public inline fun <E, A, B, Z> Validated<E, A>.zip(
  SE: Semigroup<E>,
  b: Validated<E, B>,
  f: (A, B) -> Z
): Validated<E, Z> =
  Either.zipOrAccumulate(SE::combine, toEither(), b.toEither(), f).toValidated()

@Deprecated(
  ValidatedDeprMsg + "zipOrAccumulate for Either now exposes this same functionality",
  ReplaceWith(
    "Either.zipOrAccumulate({ e1, e2 -> e1 + e2 }, toEither(), b.toEither(), c.toEither(), f).toValidated()",
    "arrow.core.Either"
  )
)
public inline fun <E, A, B, C, Z> Validated<E, A>.zip(
  SE: Semigroup<E>,
  b: Validated<E, B>,
  c: Validated<E, C>,
  f: (A, B, C) -> Z
): Validated<E, Z> =
  Either.zipOrAccumulate(SE::combine, toEither(), b.toEither(), c.toEither(), f).toValidated()

@Deprecated(
  ValidatedDeprMsg + "zipOrAccumulate for Either now exposes this same functionality",
  ReplaceWith(
    "Either.zipOrAccumulate({ e1, e2 -> e1 + e2 }, toEither(), b.toEither(), c.toEither(), d.toEither(), f).toValidated()",
    "arrow.core.Either"
  )
)
public inline fun <E, A, B, C, D, Z> Validated<E, A>.zip(
  SE: Semigroup<E>,
  b: Validated<E, B>,
  c: Validated<E, C>,
  d: Validated<E, D>,
  f: (A, B, C, D) -> Z
): Validated<E, Z> =
  Either.zipOrAccumulate(SE::combine, toEither(), b.toEither(), c.toEither(), d.toEither(), f).toValidated()

@Deprecated(
  ValidatedDeprMsg + "zipOrAccumulate for Either now exposes this same functionality",
  ReplaceWith(
    "Either.zipOrAccumulate({ e1, e2 -> e1 + e2 }, toEither(), b.toEither(), c.toEither(), d.toEither(), e.toEither(), f).toValidated()",
    "arrow.core.Either"
  )
)
public inline fun <E, A, B, C, D, EE, Z> Validated<E, A>.zip(
  SE: Semigroup<E>,
  b: Validated<E, B>,
  c: Validated<E, C>,
  d: Validated<E, D>,
  e: Validated<E, EE>,
  f: (A, B, C, D, EE) -> Z
): Validated<E, Z> =
  Either.zipOrAccumulate(SE::combine, toEither(), b.toEither(), c.toEither(), d.toEither(), e.toEither(), f)
    .toValidated()

@Deprecated(
  ValidatedDeprMsg + "zipOrAccumulate for Either now exposes this same functionality",
  ReplaceWith(
    "Either.zipOrAccumulate({ e1, e2 -> e1 + e2 }, toEither(), b.toEither(), c.toEither(), d.toEither(), e.toEither(), ff.toEither(), f).toValidated()",
    "arrow.core.Either"
  )
)
public inline fun <E, A, B, C, D, EE, FF, Z> Validated<E, A>.zip(
  SE: Semigroup<E>,
  b: Validated<E, B>,
  c: Validated<E, C>,
  d: Validated<E, D>,
  e: Validated<E, EE>,
  ff: Validated<E, FF>,
  f: (A, B, C, D, EE, FF) -> Z
): Validated<E, Z> =
  Either.zipOrAccumulate(
    SE::combine,
    toEither(),
    b.toEither(),
    c.toEither(),
    d.toEither(),
    e.toEither(),
    ff.toEither(),
    f
  ).toValidated()

@Deprecated(
  ValidatedDeprMsg + "zipOrAccumulate for Either now exposes this same functionality",
  ReplaceWith(
    "Either.zipOrAccumulate({ e1, e2 -> e1 + e2 }, toEither(), b.toEither(), c.toEither(), d.toEither(), e.toEither(), ff.toEither(), g.toEither(), f).toValidated()",
    "arrow.core.Either"
  )
)
public inline fun <E, A, B, C, D, EE, F, G, Z> Validated<E, A>.zip(
  SE: Semigroup<E>,
  b: Validated<E, B>,
  c: Validated<E, C>,
  d: Validated<E, D>,
  e: Validated<E, EE>,
  ff: Validated<E, F>,
  g: Validated<E, G>,
  f: (A, B, C, D, EE, F, G) -> Z
): Validated<E, Z> =
  Either.zipOrAccumulate(
    SE::combine,
    toEither(),
    b.toEither(),
    c.toEither(),
    d.toEither(),
    e.toEither(),
    ff.toEither(),
    g.toEither(),
    f
  ).toValidated()

@Deprecated(
  ValidatedDeprMsg + "zipOrAccumulate for Either now exposes this same functionality",
  ReplaceWith(
    "Either.zipOrAccumulate({ e1, e2 -> e1 + e2 }, toEither(), b.toEither(), c.toEither(), d.toEither(), e.toEither(), ff.toEither(), g.toEither(), h.toEither(), f).toValidated()",
    "arrow.core.Either"
  )
)
public inline fun <E, A, B, C, D, EE, F, G, H, Z> Validated<E, A>.zip(
  SE: Semigroup<E>,
  b: Validated<E, B>,
  c: Validated<E, C>,
  d: Validated<E, D>,
  e: Validated<E, EE>,
  ff: Validated<E, F>,
  g: Validated<E, G>,
  h: Validated<E, H>,
  f: (A, B, C, D, EE, F, G, H) -> Z
): Validated<E, Z> =
  Either.zipOrAccumulate(
    SE::combine,
    toEither(),
    b.toEither(),
    c.toEither(),
    d.toEither(),
    e.toEither(),
    ff.toEither(),
    g.toEither(),
    h.toEither(),
    f
  ).toValidated()

@Deprecated(
  ValidatedDeprMsg + "zipOrAccumulate for Either now exposes this same functionality",
  ReplaceWith(
    "Either.zipOrAccumulate({ e1, e2 -> e1 + e2 }, toEither(), b.toEither(), c.toEither(), d.toEither(), e.toEither(), ff.toEither(), g.toEither(), h.toEither(), i.toEither(), f).toValidated()",
    "arrow.core.Either"
  )
)
public inline fun <E, A, B, C, D, EE, F, G, H, I, Z> Validated<E, A>.zip(
  SE: Semigroup<E>,
  b: Validated<E, B>,
  c: Validated<E, C>,
  d: Validated<E, D>,
  e: Validated<E, EE>,
  ff: Validated<E, F>,
  g: Validated<E, G>,
  h: Validated<E, H>,
  i: Validated<E, I>,
  f: (A, B, C, D, EE, F, G, H, I) -> Z
): Validated<E, Z> =
  Either.zipOrAccumulate(
    SE::combine,
    toEither(),
    b.toEither(),
    c.toEither(),
    d.toEither(),
    e.toEither(),
    ff.toEither(),
    g.toEither(),
    h.toEither(),
    i.toEither(),
    f
  ).toValidated()

@Deprecated(
  ValidatedDeprMsg + "zipOrAccumulate for Either now exposes this same functionality",
  ReplaceWith(
    "Either.zipOrAccumulate({ e1, e2 -> e1 + e2 }, toEither(), b.toEither(), c.toEither(), d.toEither(), e.toEither(), ff.toEither(), g.toEither(), h.toEither(), i.toEither(), j.toEither(), f).toValidated()",
    "arrow.core.Either"
  )
)
public inline fun <E, A, B, C, D, EE, F, G, H, I, J, Z> Validated<E, A>.zip(
  SE: Semigroup<E>,
  b: Validated<E, B>,
  c: Validated<E, C>,
  d: Validated<E, D>,
  e: Validated<E, EE>,
  ff: Validated<E, F>,
  g: Validated<E, G>,
  h: Validated<E, H>,
  i: Validated<E, I>,
  j: Validated<E, J>,
  f: (A, B, C, D, EE, F, G, H, I, J) -> Z
): Validated<E, Z> =
  Either.zipOrAccumulate(
    SE::combine,
    toEither(),
    b.toEither(),
    c.toEither(),
    d.toEither(),
    e.toEither(),
    ff.toEither(),
    g.toEither(),
    h.toEither(),
    i.toEither(),
    j.toEither(),
    f
  ).toValidated()

@Deprecated(
  ValidatedDeprMsg + "zipOrAccumulate for Either now exposes this same functionality",
  ReplaceWith(
    "Either.zipOrAccumulate(toEither(), b.toEither(), f).toValidated()",
    "arrow.core.Either"
  )
)
public inline fun <E, A, B, Z> ValidatedNel<E, A>.zip(
  b: ValidatedNel<E, B>,
  f: (A, B) -> Z
): ValidatedNel<E, Z> =
  zip(Semigroup.nonEmptyList(), b, f)

@Deprecated(
  ValidatedDeprMsg + "zipOrAccumulate for Either now exposes this same functionality",
  ReplaceWith(
    "Either.zipOrAccumulate(toEither(), b.toEither(), c.toEither(), f).toValidated()",
    "arrow.core.Either"
  )
)
public inline fun <E, A, B, C, Z> ValidatedNel<E, A>.zip(
  b: ValidatedNel<E, B>,
  c: ValidatedNel<E, C>,
  f: (A, B, C) -> Z
): ValidatedNel<E, Z> =
  zip(Semigroup.nonEmptyList(), b, c, f)

@Deprecated(
  ValidatedDeprMsg + "zipOrAccumulate for Either now exposes this same functionality",
  ReplaceWith(
    "Either.zipOrAccumulate(toEither(), b.toEither(), c.toEither(), d.toEither(), f).toValidated()",
    "arrow.core.Either"
  )
)
public inline fun <E, A, B, C, D, Z> ValidatedNel<E, A>.zip(
  b: ValidatedNel<E, B>,
  c: ValidatedNel<E, C>,
  d: ValidatedNel<E, D>,
  f: (A, B, C, D) -> Z
): ValidatedNel<E, Z> =
  zip(Semigroup.nonEmptyList(), b, c, d, f)

@Deprecated(
  ValidatedDeprMsg + "zipOrAccumulate for Either now exposes this same functionality",
  ReplaceWith(
    "Either.zipOrAccumulate(toEither(), b.toEither(), c.toEither(), d.toEither(), e.toEither(), f).toValidated()",
    "arrow.core.Either"
  )
)
public inline fun <E, A, B, C, D, EE, Z> ValidatedNel<E, A>.zip(
  b: ValidatedNel<E, B>,
  c: ValidatedNel<E, C>,
  d: ValidatedNel<E, D>,
  e: ValidatedNel<E, EE>,
  f: (A, B, C, D, EE) -> Z
): ValidatedNel<E, Z> =
  zip(Semigroup.nonEmptyList(), b, c, d, e, f)

@Deprecated(
  ValidatedDeprMsg + "zipOrAccumulate for Either now exposes this same functionality",
  ReplaceWith(
    "Either.zipOrAccumulate(toEither(), b.toEither(), c.toEither(), d.toEither(), e.toEither(), ff.toEither(), f).toValidated()",
    "arrow.core.Either"
  )
)
public inline fun <E, A, B, C, D, EE, FF, Z> ValidatedNel<E, A>.zip(
  b: ValidatedNel<E, B>,
  c: ValidatedNel<E, C>,
  d: ValidatedNel<E, D>,
  e: ValidatedNel<E, EE>,
  ff: ValidatedNel<E, FF>,
  f: (A, B, C, D, EE, FF) -> Z
): ValidatedNel<E, Z> =
  zip(Semigroup.nonEmptyList(), b, c, d, e, ff, f)

@Deprecated(
  ValidatedDeprMsg + "zipOrAccumulate for Either now exposes this same functionality",
  ReplaceWith(
    "Either.zipOrAccumulate(toEither(), b.toEither(), c.toEither(), d.toEither(), e.toEither(), ff.toEither(), g.toEither(), f).toValidated()",
    "arrow.core.Either"
  )
)
public inline fun <E, A, B, C, D, EE, F, G, Z> ValidatedNel<E, A>.zip(
  b: ValidatedNel<E, B>,
  c: ValidatedNel<E, C>,
  d: ValidatedNel<E, D>,
  e: ValidatedNel<E, EE>,
  ff: ValidatedNel<E, F>,
  g: ValidatedNel<E, G>,
  f: (A, B, C, D, EE, F, G) -> Z
): ValidatedNel<E, Z> =
  zip(Semigroup.nonEmptyList(), b, c, d, e, ff, g, f)

@Deprecated(
  ValidatedDeprMsg + "zipOrAccumulate for Either now exposes this same functionality",
  ReplaceWith(
    "Either.zipOrAccumulate(toEither(), b.toEither(), c.toEither(), d.toEither(), e.toEither(), ff.toEither(), g.toEither(), h.toEither(), f).toValidated()",
    "arrow.core.Either"
  )
)
public inline fun <E, A, B, C, D, EE, F, G, H, Z> ValidatedNel<E, A>.zip(
  b: ValidatedNel<E, B>,
  c: ValidatedNel<E, C>,
  d: ValidatedNel<E, D>,
  e: ValidatedNel<E, EE>,
  ff: ValidatedNel<E, F>,
  g: ValidatedNel<E, G>,
  h: ValidatedNel<E, H>,
  f: (A, B, C, D, EE, F, G, H) -> Z
): ValidatedNel<E, Z> =
  zip(Semigroup.nonEmptyList(), b, c, d, e, ff, g, h, f)

@Deprecated(
  ValidatedDeprMsg + "zipOrAccumulate for Either now exposes this same functionality",
  ReplaceWith(
    "Either.zipOrAccumulate(toEither(), b.toEither(), c.toEither(), d.toEither(), e.toEither(), ff.toEither(), g.toEither(), h.toEither(), i.toEither(), f).toValidated()",
    "arrow.core.Either"
  )
)
public inline fun <E, A, B, C, D, EE, F, G, H, I, Z> ValidatedNel<E, A>.zip(
  b: ValidatedNel<E, B>,
  c: ValidatedNel<E, C>,
  d: ValidatedNel<E, D>,
  e: ValidatedNel<E, EE>,
  ff: ValidatedNel<E, F>,
  g: ValidatedNel<E, G>,
  h: ValidatedNel<E, H>,
  i: ValidatedNel<E, I>,
  f: (A, B, C, D, EE, F, G, H, I) -> Z
): ValidatedNel<E, Z> =
  zip(Semigroup.nonEmptyList(), b, c, d, e, ff, g, h, i, f)

@Deprecated(
  ValidatedDeprMsg + "zipOrAccumulate for Either now exposes this same functionality",
  ReplaceWith(
    "Either.zipOrAccumulate(toEither(), b.toEither(), c.toEither(), d.toEither(), e.toEither(), ff.toEither(), g.toEither(), h.toEither(), i.toEither(), j.toEither(), f).toValidated()",
    "arrow.core.Either"
  )
)
public inline fun <E, A, B, C, D, EE, F, G, H, I, J, Z> ValidatedNel<E, A>.zip(
  b: ValidatedNel<E, B>,
  c: ValidatedNel<E, C>,
  d: ValidatedNel<E, D>,
  e: ValidatedNel<E, EE>,
  ff: ValidatedNel<E, F>,
  g: ValidatedNel<E, G>,
  h: ValidatedNel<E, H>,
  i: ValidatedNel<E, I>,
  j: ValidatedNel<E, J>,
  f: (A, B, C, D, EE, F, G, H, I, J) -> Z
): ValidatedNel<E, Z> =
  zip(Semigroup.nonEmptyList(), b, c, d, e, ff, g, h, i, j, f)

/**
 * Given [A] is a sub type of [B], re-type this value from Validated<E, A> to Validated<E, B>
 *
 * ```kotlin
 * import arrow.core.*
 *
 * fun main(args: Array<String>) {
 *   //sampleStart
 *   val string: Validated<Int, String> = "Hello".valid()
 *   val chars: Validated<Int, CharSequence> =
 *     string.widen<Int, CharSequence, String>()
 *   //sampleEnd
 *   println(chars)
 * }
 * ```
 * <!--- KNIT example-validated-05.kt -->
 */
@Deprecated(
  ValidatedDeprMsg + "Use widen on Either after refactoring",
  ReplaceWith("toEither().widen()")
)
public fun <E, B, A : B> Validated<E, A>.widen(): Validated<E, B> =
  this

@Deprecated(
  ValidatedDeprMsg + "Use leftWiden on Either after refactoring",
  ReplaceWith("toEither().leftWiden()")
)
public fun <EE, E : EE, A> Validated<E, A>.leftWiden(): Validated<EE, A> =
  this

@Deprecated(
  DeprAndNicheMsg + "Prefer using the Either DSL, or map",
  ReplaceWith(
    "(0 until (n.coerceAtLeast(0))).mapOrAccumulate({ e1, e2 -> e1 + e2 }) { bind() }.toValidated()"
  )
)
public fun <E, A> Validated<E, A>.replicate(SE: Semigroup<E>, n: Int): Validated<E, List<A>> =
  if (n <= 0) emptyList<A>().valid()
  else this.zip(SE, replicate(SE, n - 1)) { a, xs -> listOf(a) + xs }

@Deprecated(DeprAndNicheMsg)
public fun <E, A> Validated<E, A>.replicate(SE: Semigroup<E>, n: Int, MA: Monoid<A>): Validated<E, A> =
  if (n <= 0) MA.empty().valid()
  else this@replicate.zip(SE, replicate(SE, n - 1, MA)) { a, xs -> MA.run { a + xs } }

@Deprecated(
  DeprAndNicheMsg + "Prefer explicit fold instead",
  ReplaceWith(
    "fold({ it.map { Invalid(it) } }, { it.map { Valid(it) } })",
    "arrow.core.Valid", "arrow.core.Invalid"
  )
)
public fun <E, A> Validated<Iterable<E>, Iterable<A>>.bisequence(): List<Validated<E, A>> =
  bitraverse(::identity, ::identity)

@Deprecated(
  DeprAndNicheMsg + "Prefer explicit fold instead",
  ReplaceWith(
    "fold({ it.map { Invalid(it) } }, { it.map { Valid(it) } })",
    "arrow.core.Valid", "arrow.core.Invalid"
  )
)
public fun <E, A, B> Validated<Either<E, A>, Either<E, B>>.bisequenceEither(): Either<E, Validated<A, B>> =
  bitraverseEither(::identity, ::identity)

@Deprecated(
  DeprAndNicheMsg + "Prefer explicit fold instead",
  ReplaceWith(
    "fold({ it.map(::Invalid) }, { it.map(::Valid) })",
    "arrow.core.Valid", "arrow.core.Invalid"
  )
)
public fun <A, B> Validated<Option<A>, Option<B>>.bisequenceOption(): Option<Validated<A, B>> =
  bitraverseOption(::identity, ::identity)

@Deprecated(
  DeprAndNicheMsg + "Prefer explicit fold instead",
  ReplaceWith(
    "fold({ it?.let(::Invalid) }, { it?.let(::Valid) })",
    "arrow.core.Valid", "arrow.core.Invalid"
  )
)
public fun <A, B> Validated<A?, B?>.bisequenceNullable(): Validated<A, B>? =
  bitraverseNullable(::identity, ::identity)

@Deprecated(
  "$MonoidDeprecation\n$DeprAndNicheMsg\nUse fold on Either after refactoring",
  ReplaceWith("fold({ invalidValue }, ::identity)")
)
public fun <E, A> Validated<E, A>.fold(MA: Monoid<A>): A =
  fold({ MA.empty() }, ::identity)

@Deprecated(
  "$MonoidDeprecation\n$DeprAndNicheMsg\nUse fold on Either after refactoring",
  ReplaceWith("fold({ invalidValue }, ::identity)", "arrow.core.fold")
)
public fun <E, A> Validated<E, A>.combineAll(MA: Monoid<A>): A =
  fold(MA)

@Deprecated(
  DeprAndNicheMsg + "Prefer using the Either DSL, or explicit fold or when",
  ReplaceWith(
    "fold({ emptyList() }, { it.map(::Valid) })",
    "arrow.core.Valid"
  )
)
public fun <E, A> Validated<E, Iterable<A>>.sequence(): List<Validated<E, A>> =
  traverse(::identity)

@Deprecated(
  "sequenceEither is being renamed to sequence to simplify the Arrow API",
  ReplaceWith("sequence()", "arrow.core.sequence")
)
public fun <E, A, B> Validated<A, Either<E, B>>.sequenceEither(): Either<E, Validated<A, B>> =
  sequence()

@Deprecated(
  DeprAndNicheMsg + "Prefer using the Either DSL, or explicit fold or when",
  ReplaceWith(
    "fold({ it.invalid().right() }, { it.map(::Valid) })",
    "arrow.core.invalid", "arrow.core.right", "arrow.core.Valid"
  )
)
public fun <E, A, B> Validated<A, Either<E, B>>.sequence(): Either<E, Validated<A, B>> =
  traverse(::identity)

@Deprecated(
  "sequenceOption is being renamed to sequence to simplify the Arrow API",
  ReplaceWith("sequence()", "arrow.core.sequence")
)
public fun <A, B> Validated<A, Option<B>>.sequenceOption(): Option<Validated<A, B>> =
  sequence()

@Deprecated(
  DeprAndNicheMsg + "Prefer using the Either DSL, or explicit fold or when",
  ReplaceWith(
    "fold({ None }, { it.map(::Valid) })",
    "arrow.core.None", "arrow.core.Valid"
  )
)
public fun <A, B> Validated<A, Option<B>>.sequence(): Option<Validated<A, B>> =
  traverse(::identity)

@Deprecated(
  "sequenceNullable is being renamed to sequence to simplify the Arrow API",
  ReplaceWith("sequence()", "arrow.core.sequence")
)
public fun <A, B> Validated<A, B?>.sequenceNullable(): Validated<A, B>? =
  sequence()

@Deprecated(
  DeprAndNicheMsg + "Use orNull() and Kotlin nullable types",
  ReplaceWith("orNull()?.valid()", "arrow.core.valid")
)
public fun <A, B> Validated<A, B?>.sequence(): Validated<A, B>? =
  traverseNullable(::identity)

@Deprecated(
  ValidatedDeprMsg + "Use compareTo on Either after refactoring",
  ReplaceWith("toEither(). compareTo(other.toEither())")
)
public operator fun <E : Comparable<E>, A : Comparable<A>> Validated<E, A>.compareTo(other: Validated<E, A>): Int =
  fold(
    { l1 -> other.fold({ l2 -> l1.compareTo(l2) }, { -1 }) },
    { r1 -> other.fold({ 1 }, { r2 -> r1.compareTo(r2) }) }
  )

/**
 * Return the Valid value, or the default if Invalid
 */
@Deprecated(
  ValidatedDeprMsg + "Use getOrElse on Either after refactoring",
  ReplaceWith("toEither().getOrElse { default() }")
)
public inline fun <E, A> Validated<E, A>.getOrElse(default: () -> A): A =
  fold({ default() }, ::identity)

/**
 * Return the Valid value, or null if Invalid
 */
@Deprecated(
  ValidatedDeprMsg + "Use getOrNull on Either after refactoring",
  ReplaceWith("toEither().getOrNull()")
)
public fun <E, A> Validated<E, A>.orNull(): A? =
  getOrElse { null }

@Deprecated(
  ValidatedDeprMsg + "Use getOrNone on Either after refactoring",
  ReplaceWith("toEither().getOrNone()")
)
public fun <E, A> Validated<E, A>.orNone(): Option<A> =
  fold({ None }, { Some(it) })

/**
 * Return the Valid value, or the result of f if Invalid
 */
@Deprecated(
  ValidatedDeprMsg + "Use getOrElse on Either after refactoring",
  ReplaceWith("toEither().getOrElse(f)")
)
public inline fun <E, A> Validated<E, A>.valueOr(f: (E) -> A): A =
  fold({ f(it) }, ::identity)

/**
 * If `this` is valid return `this`, otherwise if `that` is valid return `that`, otherwise combine the failures.
 * This is similar to [orElse] except that here failures are accumulated.
 */
@Deprecated(
  DeprAndNicheMsg + "Use recover on Either after refactoring",
  ReplaceWith(
    "toEither().recover { e -> that().mapLeft { ee -> e + ee }.bind() }.toValidated()",
    "arrow.core.recover"
  )
)
public inline fun <E, A> Validated<E, A>.findValid(SE: Semigroup<E>, that: () -> Validated<E, A>): Validated<E, A> =
  toEither().recover { e -> that().mapLeft { ee -> SE.combine(e, ee) }.bind() }.toValidated()

/**
 * Apply a function to a Valid value, returning a new Validation that may be valid or invalid
 *
 * Example:
 * ```kotlin
 * import arrow.core.Validated
 * import arrow.core.andThen
 *
 * fun main() {
 *   Validated.Valid(5).andThen { Validated.Valid(10) } // Result: Valid(10)
 *   Validated.Valid(5).andThen { Validated.Invalid(10) } // Result: Invalid(10)
 *   Validated.Invalid(5).andThen { Validated.Valid(10) } // Result: Invalid(5)
 * }
 * ```
 * <!--- KNIT example-validated-06.kt -->
 */
@Deprecated(
  ValidatedDeprMsg + "Use Either DSL or flatMap instead after refactoring.",
  ReplaceWith("toEither().flatMap { f(it).toEither() }.toValidated()")
)
public inline fun <E, A, B> Validated<E, A>.andThen(f: (A) -> Validated<E, B>): Validated<E, B> =
  when (this) {
    is Validated.Valid -> f(value)
    is Validated.Invalid -> this
  }

/**
 * Return this if it is Valid, or else fall back to the given default.
 * The functionality is similar to that of [findValid] except for failure accumulation,
 * where here only the error on the right is preserved and the error on the left is ignored.
 */
@Deprecated(
  ValidatedDeprMsg + "Use recover on Either instead after refactoring.",
  ReplaceWith("toEither().recover { default().bind() }.toValidated()")
)
public inline fun <E, A> Validated<E, A>.orElse(default: () -> Validated<E, A>): Validated<E, A> =
  fold(
    { default() },
    { Valid(it) }
  )

@Deprecated(
  ValidatedDeprMsg + "Use recover on Either instead after refactoring.",
  ReplaceWith("toEither().recover { e -> f(e).bind() }.toValidated()")
)
public inline fun <E, A> Validated<E, A>.handleErrorWith(f: (E) -> Validated<E, A>): Validated<E, A> =
  when (this) {
    is Validated.Valid -> this
    is Validated.Invalid -> f(this.value)
  }

@Deprecated(
  ValidatedDeprMsg + "Use recover on Either instead after refactoring.",
  ReplaceWith("toEither().recover<E, Nothing, A> { e -> f(e) }.toValidated()")
)
public inline fun <E, A> Validated<E, A>.handleError(f: (E) -> A): Validated<Nothing, A> =
  when (this) {
    is Validated.Valid -> this
    is Validated.Invalid -> Valid(f(this.value))
  }

@Deprecated(
  ValidatedDeprMsg + "Use fold on Either instead after refactoring.",
  ReplaceWith("fold(fe, fa).valid()")
)
public inline fun <E, A, B> Validated<E, A>.redeem(fe: (E) -> B, fa: (A) -> B): Validated<E, B> =
  when (this) {
    is Validated.Valid -> map(fa)
    is Validated.Invalid -> Valid(fe(this.value))
  }

@Deprecated(
  ValidatedDeprMsg + "Validated is deprecated in favor of Either",
  ReplaceWith("toEither().valid()")
)
public fun <E, A> Validated<E, A>.attempt(): Validated<Nothing, Either<E, A>> =
  map { Right(it) }.handleError { Left(it) }

@Deprecated(
  ValidatedDeprMsg + "Use merge() on Either instead after refactoring.",
  ReplaceWith("toEither().merge()")
)
public inline fun <A> Validated<A, A>.merge(): A =
  fold(::identity, ::identity)

@Deprecated(
  ValidatedDeprMsg + "Use Either.zipOrAccumulate instead",
  ReplaceWith("Either.zipOrAccumulate({e1, e2 -> e1 + e2}, this.toEither(), y.toEither(), {a1, a2 -> a1 + a2} ).toValidated()")
)
public fun <E, A> Validated<E, A>.combine(
  SE: Semigroup<E>,
  SA: Semigroup<A>,
  y: Validated<E, A>
): Validated<E, A> =
  Either.zipOrAccumulate(SE::combine, toEither(), y.toEither(), SA::combine).toValidated()

@Deprecated(
  DeprAndNicheMsg,
  ReplaceWith(
    "toEither().recover { e -> y.toEither().recover { ee -> raise { e + ee }} }.bind() }.toValidated()",
    "arrow.core.recover"
  )
)
public fun <E, A> Validated<E, A>.combineK(SE: Semigroup<E>, y: Validated<E, A>): Validated<E, A> =
  toEither().recover { e -> y.toEither().recover { ee -> raise(SE.combine(e, ee)) }.bind() }.toValidated()

/**
 * Converts the value to an Ior<E, A>
 */
@Deprecated(
  ValidatedDeprMsg + "Use toIor on Either after refactoring Validated to Either",
  ReplaceWith("toEither().toIor()")
)
public fun <E, A> Validated<E, A>.toIor(): Ior<E, A> =
  fold({ Ior.Left(it) }, { Ior.Right(it) })

@Deprecated(
  ValidatedDeprMsg + "Use right instead to construct the equivalent Either value",
  ReplaceWith("this.right()", "arrow.core.right")
)
public inline fun <A> A.valid(): Validated<Nothing, A> =
  Valid(this)

@Deprecated(
  ValidatedDeprMsg + "Use left instead to construct the equivalent Either value",
  ReplaceWith("this.left()", "arrow.core.left")
)
public inline fun <E> E.invalid(): Validated<E, Nothing> =
  Invalid(this)

@Deprecated(
  ValidatedDeprMsg + "Use right instead to construct the equivalent Either value",
  ReplaceWith("this.right()", "arrow.core.right")
)
public inline fun <A> A.validNel(): ValidatedNel<Nothing, A> =
  Validated.validNel(this)

@Deprecated(
  ValidatedDeprMsg + "Use leftNel instead to construct the equivalent Either value",
  ReplaceWith("this.leftNel()", "arrow.core.leftNel")
)
public inline fun <E> E.invalidNel(): ValidatedNel<E, Nothing> =
  Validated.invalidNel(this)

internal const val ValidatedDeprMsg = "Validated functionality is being merged into Either.\n"

private const val DeprAndNicheMsg =
  "Validated functionality is being merged into Either, but this API is niche and will be removed in the future. If this method is crucial for you, please let us know on the Arrow Github. Thanks!\n https://github.com/arrow-kt/arrow/issues\n"
