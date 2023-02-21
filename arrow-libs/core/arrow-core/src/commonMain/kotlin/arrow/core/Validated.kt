package arrow.core

import arrow.typeclasses.Monoid
import arrow.typeclasses.Semigroup
import arrow.core.Either.Left
import arrow.core.Either.Right
import kotlin.experimental.ExperimentalTypeInference
import kotlin.jvm.JvmName
import kotlin.jvm.JvmStatic

@Deprecated(DeprMsg + "ValidatedNel is being replaced by EitherNel")
public typealias ValidatedNel<E, A> = Validated<Nel<E>, A>

public typealias Valid<A> = Validated.Valid<A>

public typealias Invalid<E> = Validated.Invalid<E>

@Deprecated(DeprMsg + "You can find more details about how to migrate on the Github release page, or the 1.2.0 release post.")
public sealed class Validated<out E, out A> {

  public companion object {

    @Deprecated(
      DeprMsg + "Use leftNel instead to construct the equivalent Either value",
      ReplaceWith("e.leftNel()", "arrow.core.leftNel")
    )
    @JvmStatic
    public fun <E, A> invalidNel(e: E): ValidatedNel<E, A> = Invalid(nonEmptyListOf(e))

    @Deprecated(
      DeprMsg + "Use right instead to construct the equivalent Either value",
      ReplaceWith("a.right()", "arrow.core.right")
    )
    @JvmStatic
    public fun <E, A> validNel(a: A): ValidatedNel<E, A> = Valid(a)

    /**
     * Converts an `Either<E, A>` to a `Validated<E, A>`.
     */
    @Deprecated(DeprMsg)
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
      o.toEither(ifNone).toValidated()

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
      value?.valid() ?: ifNull().invalid()

    @Deprecated(
      DeprMsg + "Use Either.catch instead",
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

  @OptIn(ExperimentalTypeInference::class)
  @OverloadResolutionByLambdaReturnType
  public inline fun <B> traverse(fa: (A) -> Iterable<B>): List<Validated<E, B>> =
    fold({ emptyList() }, { a -> fa(a).map { Valid(it) } })

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

  public inline fun <B> traverseNullable(fa: (A) -> B?): Validated<E, B>? =
    when (this) {
      is Valid -> fa(this.value)?.let { Valid(it) }
      is Invalid -> null
    }

  public inline fun <B> bifoldLeft(
    c: B,
    fe: (B, E) -> B,
    fa: (B, A) -> B
  ): B =
    fold({ fe(c, it) }, { fa(c, it) })

  public inline fun <B> bifoldMap(MN: Monoid<B>, g: (E) -> B, f: (A) -> B): B = MN.run {
    bifoldLeft(MN.empty(), { c, b -> c.combine(g(b)) }) { c, a -> c.combine(f(a)) }
  }

  public inline fun <EE, B> bitraverse(fe: (E) -> Iterable<EE>, fa: (A) -> Iterable<B>): List<Validated<EE, B>> =
    fold({ fe(it).map { Invalid(it) } }, { fa(it).map { Valid(it) } })

  public inline fun <EE, B, C> bitraverseEither(
    fe: (E) -> Either<EE, B>,
    fa: (A) -> Either<EE, C>
  ): Either<EE, Validated<B, C>> =
    fold({ fe(it).map { Invalid(it) } }, { fa(it).map { Valid(it) } })

  public inline fun <B, C> bitraverseOption(
    fe: (E) -> Option<B>,
    fa: (A) -> Option<C>
  ): Option<Validated<B, C>> =
    fold({ fe(it).map(::Invalid) }, { fa(it).map(::Valid) })

  public inline fun <B, C> bitraverseNullable(
    fe: (E) -> B?,
    fa: (A) -> C?
  ): Validated<B, C>? =
    fold({ fe(it)?.let(::Invalid) }, { fa(it)?.let(::Valid) })

  @Deprecated(
    DeprMsg + "Use fold on Either after refactoring instead",
    ReplaceWith("toEither().fold({ MB.empty() }, f)")
  )
  public inline fun <B> foldMap(MB: Monoid<B>, f: (A) -> B): B =
    fold({ MB.empty() }, f)

  override fun toString(): String = fold(
    { "Validated.Invalid($it)" },
    { "Validated.Valid($it)" }
  )

  @Deprecated(
    DeprMsg + "Use Right to construct Either values instead",
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
    DeprMsg + "Use Left to construct Either values instead",
    ReplaceWith("Either.Left(value)", "arrow.core.Either")
  )
  public data class Invalid<out E>(val value: E) : Validated<E, Nothing>() {
    override fun toString(): String = "Validated.Invalid($value)"
  }

  @Deprecated(
    DeprMsg + "Use fold on Either after refactoring",
    ReplaceWith("fold(fe, fa)")
  )
  public inline fun <B> fold(fe: (E) -> B, fa: (A) -> B): B =
    when (this) {
      is Valid -> fa(value)
      is Invalid -> (fe(value))
    }

  @Deprecated(
    DeprMsg + "Use isRight on Either after refactoring",
    ReplaceWith("toEither().isRight()")
  )
  public val isValid: Boolean =
    fold({ false }, { true })

  @Deprecated(
    DeprMsg + "Use isLeft on Either after refactoring",
    ReplaceWith("toEither().isLeft()")
  )
  public val isInvalid: Boolean =
    fold({ true }, { false })

  /**
   * Is this Valid and matching the given predicate
   */
  @Deprecated(
    DeprMsg + "Use isRight on Either after refactoring",
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
    DeprMsg + "Use isRight on Either after refactoring",
    ReplaceWith("toEither().isLeft()")
  )
  public fun isEmpty(): Boolean = isInvalid

  @Deprecated(
    DeprMsg + "Use isRight on Either after refactoring",
    ReplaceWith("toEither().isRight()")
  )
  public fun isNotEmpty(): Boolean = isValid

  /**
   * Converts the value to an Either<E, A>
   */
  public fun toEither(): Either<E, A> =
    fold(::Left, ::Right)

  /**
   * Returns Valid values wrapped in Some, and None for Invalid values
   */
  @Deprecated(
    DeprMsg + "Use getOrNone on Either after refactoring",
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
    fold({ emptyList() }, ::listOf)

  /** Lift the Invalid value into a NonEmptyList. */
  @Deprecated(
    DeprMsg + "Use toEitherNel directly instead",
    ReplaceWith("toEither().toEitherNel().toValidated()")
  )
  public fun toValidatedNel(): ValidatedNel<E, A> =
    fold({ invalidNel(it) }, ::Valid)

  /**
   * Convert to an Either, apply a function, convert back. This is handy
   * when you want to use the Monadic properties of the Either type.
   */
  @Deprecated(
    DeprMsg + "Use Either directly instead",
    ReplaceWith("toEither().let(f).toValidated()")
  )
  public inline fun <EE, B> withEither(f: (Either<E, A>) -> Either<EE, B>): Validated<EE, B> =
    toEither().let(f).toValidated()

  /**
   * From [arrow.typeclasses.Bifunctor], maps both types of this Validated.
   *
   * Apply a function to an Invalid or Valid value, returning a new Invalid or Valid value respectively.
   */
  public inline fun <EE, B> bimap(fe: (E) -> EE, fa: (A) -> B): Validated<EE, B> =
    fold({ Invalid(fe(it)) }, { Valid(fa(it)) })

  /**
   * Apply a function to a Valid value, returning a new Valid value
   */
  @Deprecated(
    DeprMsg + "Use map on Either after refactoring",
    ReplaceWith("toEither().mapLeft(f).toValidated()")
  )
  public inline fun <B> map(f: (A) -> B): Validated<E, B> =
    bimap(::identity, f)

  /**
   * Apply a function to an Invalid value, returning a new Invalid value.
   * Or, if the original valid was Valid, return it.
   */
  @Deprecated(
    DeprMsg + "Use mapLeft on Either after refactoring",
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
    DeprMsg + "Use onLeft on Either after refactoring",
    ReplaceWith("toEither().onRight(f).toValidated()")
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
    DeprMsg + "Use onRight on Either after refactoring",
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
    DeprMsg + "Use fold on Either after refactoring",
    ReplaceWith("toEither().fold({ b }) { f(b, it) }")
  )
  public inline fun <B> foldLeft(b: B, f: (B, A) -> B): B =
    toEither().fold({ b }) { f(b, it) }

  @Deprecated(
    DeprMsg + "Use widen on Either after refactoring",
    ReplaceWith("toEither().swap()")
  )
  public fun swap(): Validated<A, E> =
    fold(::Valid, ::Invalid)
}

@Deprecated(
  DeprMsg + "zipOrAccumulate for Either now exposes this same functionality",
  ReplaceWith(
    "Either.zipOrAccumulate({ a, b -> SE.run<Semigroup<E>, E> { a.combine(b) } }, toEither(), fb.toEither(), ::Pair).toValidated()",
    "arrow.core.Either"
  )
)
public fun <E, A, B> Validated<E, A>.zip(SE: Semigroup<E>, fb: Validated<E, B>): Validated<E, Pair<A, B>> =
  Either.zipOrAccumulate({ a, b -> SE.run { a.combine(b) } }, toEither(), fb.toEither(), ::Pair).toValidated()

@Deprecated(
  DeprMsg + "zipOrAccumulate for Either now exposes this same functionality",
  ReplaceWith(
    "Either.zipOrAccumulate({ a, b -> SE.run<Semigroup<E>, E> { a.combine(b) } }, toEither(), b.toEither(), f).toValidated()",
    "arrow.core.Either"
  )
)
public inline fun <E, A, B, Z> Validated<E, A>.zip(
  SE: Semigroup<E>,
  b: Validated<E, B>,
  f: (A, B) -> Z
): Validated<E, Z> =
  zip(
    SE,
    b,
    Valid.unit,
    Valid.unit,
    Valid.unit,
    Valid.unit,
    Valid.unit,
    Valid.unit,
    Valid.unit,
    Valid.unit
  ) { a, b, _, _, _, _, _, _, _, _ ->
    f(a, b)
  }

@Deprecated(
  DeprMsg + "zipOrAccumulate for Either now exposes this same functionality",
  ReplaceWith(
    "Either.zipOrAccumulate({ a, b -> SE.run<Semigroup<E>, E> { a.combine(b) } }, toEither(), b.toEither(), c.toEither(), f).toValidated()",
    "arrow.core.Either"
  )
)
public inline fun <E, A, B, C, Z> Validated<E, A>.zip(
  SE: Semigroup<E>,
  b: Validated<E, B>,
  c: Validated<E, C>,
  f: (A, B, C) -> Z
): Validated<E, Z> =
  zip(
    SE,
    b,
    c,
    Valid.unit,
    Valid.unit,
    Valid.unit,
    Valid.unit,
    Valid.unit,
    Valid.unit,
    Valid.unit
  ) { a, b, c, _, _, _, _, _, _, _ ->
    f(a, b, c)
  }

@Deprecated(
  DeprMsg + "zipOrAccumulate for Either now exposes this same functionality",
  ReplaceWith(
    "Either.zipOrAccumulate({ a, b -> SE.run<Semigroup<E>, E> { a.combine(b) } }, toEither(), b.toEither(), c.toEither(), d.toEither(), f).toValidated()",
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
  zip(
    SE,
    b,
    c,
    d,
    Valid.unit,
    Valid.unit,
    Valid.unit,
    Valid.unit,
    Valid.unit,
    Valid.unit
  ) { a, b, c, d, _, _, _, _, _, _ ->
    f(a, b, c, d)
  }

@Deprecated(
  DeprMsg + "zipOrAccumulate for Either now exposes this same functionality",
  ReplaceWith(
    "Either.zipOrAccumulate({ a, b -> SE.run<Semigroup<E>, E> { a.combine(b) } }, toEither(), b.toEither(), c.toEither(), d.toEither(), e.toEither(), f).toValidated()",
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
  zip(
    SE,
    b,
    c,
    d,
    e,
    Valid.unit,
    Valid.unit,
    Valid.unit,
    Valid.unit,
    Valid.unit
  ) { a, b, c, d, e, _, _, _, _, _ ->
    f(a, b, c, d, e)
  }

@Deprecated(
  DeprMsg + "zipOrAccumulate for Either now exposes this same functionality",
  ReplaceWith(
    "Either.zipOrAccumulate({ a, b -> SE.run<Semigroup<E>, E> { a.combine(b) } }, toEither(), b.toEither(), c.toEither(), d.toEither(), e.toEither(), ff.toEither(), f).toValidated()",
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
  zip(
    SE,
    b,
    c,
    d,
    e,
    ff,
    Valid.unit,
    Valid.unit,
    Valid.unit,
    Valid.unit
  ) { a, b, c, d, e, ff, _, _, _, _ ->
    f(a, b, c, d, e, ff)
  }

@Deprecated(
  DeprMsg + "zipOrAccumulate for Either now exposes this same functionality",
  ReplaceWith(
    "Either.zipOrAccumulate({ a, b -> SE.run<Semigroup<E>, E> { a.combine(b) } }, toEither(), b.toEither(), c.toEither(), d.toEither(), e.toEither(), ff.toEither(), g.toEither(), f).toValidated()",
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
  zip(SE, b, c, d, e, ff, g, Valid.unit, Valid.unit, Valid.unit) { a, b, c, d, e, ff, g, _, _, _ ->
    f(a, b, c, d, e, ff, g)
  }

@Deprecated(
  DeprMsg + "zipOrAccumulate for Either now exposes this same functionality",
  ReplaceWith(
    "Either.zipOrAccumulate({ a, b -> SE.run<Semigroup<E>, E> { a.combine(b) } }, toEither(), b.toEither(), c.toEither(), d.toEither(), e.toEither(), ff.toEither(), g.toEither(), h.toEither(), f).toValidated()",
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
  zip(SE, b, c, d, e, ff, g, h, Valid.unit, Valid.unit) { a, b, c, d, e, ff, g, h, _, _ ->
    f(a, b, c, d, e, ff, g, h)
  }

@Deprecated(
  DeprMsg + "zipOrAccumulate for Either now exposes this same functionality",
  ReplaceWith(
    "Either.zipOrAccumulate({ a, b -> SE.run<Semigroup<E>, E> { a.combine(b) } }, toEither(), b.toEither(), c.toEither(), d.toEither(), e.toEither(), ff.toEither(), g.toEither(), h.toEither(), i.toEither(), f).toValidated()",
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
  zip(SE, b, c, d, e, ff, g, h, i, Valid.unit) { a, b, c, d, e, ff, g, h, i, _ ->
    f(a, b, c, d, e, ff, g, h, i)
  }

@Deprecated(
  DeprMsg + "zipOrAccumulate for Either now exposes this same functionality",
  ReplaceWith(
    "Either.zipOrAccumulate({ a, b -> SE.run<Semigroup<E>, E> { a.combine(b) } }, toEither(), b.toEither(), c.toEither(), d.toEither(), e.toEither(), ff.toEither(), g.toEither(), h.toEither(), i.toEither(), j.toEither(), f).toValidated()",
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
  if (this is Validated.Valid && b is Validated.Valid && c is Validated.Valid && d is Validated.Valid && e is Validated.Valid && ff is Validated.Valid && g is Validated.Valid && h is Validated.Valid && i is Validated.Valid && j is Validated.Valid) {
    Validated.Valid(f(this.value, b.value, c.value, d.value, e.value, ff.value, g.value, h.value, i.value, j.value))
  } else SE.run {
    var accumulatedError: Any? = EmptyValue
    accumulatedError =
      if (this@zip is Validated.Invalid) this@zip.value else accumulatedError
    accumulatedError =
      if (b is Validated.Invalid) emptyCombine(accumulatedError, b.value) else accumulatedError
    accumulatedError =
      if (c is Validated.Invalid) emptyCombine(accumulatedError, c.value) else accumulatedError
    accumulatedError =
      if (d is Validated.Invalid) emptyCombine(accumulatedError, d.value) else accumulatedError
    accumulatedError =
      if (e is Validated.Invalid) emptyCombine(accumulatedError, e.value) else accumulatedError
    accumulatedError =
      if (ff is Validated.Invalid) emptyCombine(accumulatedError, ff.value) else accumulatedError
    accumulatedError =
      if (g is Validated.Invalid) emptyCombine(accumulatedError, g.value) else accumulatedError
    accumulatedError =
      if (h is Validated.Invalid) emptyCombine(accumulatedError, h.value) else accumulatedError
    accumulatedError =
      if (i is Validated.Invalid) emptyCombine(accumulatedError, i.value) else accumulatedError
    accumulatedError =
      if (j is Validated.Invalid) emptyCombine(accumulatedError, j.value) else accumulatedError

    Validated.Invalid(accumulatedError as E)
  }

@Deprecated(
  DeprMsg + "zipOrAccumulate for Either now exposes this same functionality",
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
  DeprMsg + "zipOrAccumulate for Either now exposes this same functionality",
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
  DeprMsg + "zipOrAccumulate for Either now exposes this same functionality",
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
  DeprMsg + "zipOrAccumulate for Either now exposes this same functionality",
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
  DeprMsg + "zipOrAccumulate for Either now exposes this same functionality",
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
  DeprMsg + "zipOrAccumulate for Either now exposes this same functionality",
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
  DeprMsg + "zipOrAccumulate for Either now exposes this same functionality",
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
  DeprMsg + "zipOrAccumulate for Either now exposes this same functionality",
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
  DeprMsg + "zipOrAccumulate for Either now exposes this same functionality",
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
 * Given [A] is a subtype of [B], re-type this value from Validated<E, A> to Validated<E, B>
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
  DeprMsg + "Use widen on Either after refactoring",
  ReplaceWith("toEither().widen()")
)
public fun <E, B, A : B> Validated<E, A>.widen(): Validated<E, B> =
  this

@Deprecated(
  DeprMsg + "Use leftWiden on Either after refactoring",
  ReplaceWith("toEither().leftWiden()")
)
public fun <EE, E : EE, A> Validated<E, A>.leftWiden(): Validated<EE, A> =
  this

public fun <E, A> Validated<E, A>.replicate(SE: Semigroup<E>, n: Int): Validated<E, List<A>> =
  if (n <= 0) emptyList<A>().valid()
  else this.zip(SE, replicate(SE, n - 1)) { a, xs -> listOf(a) + xs }

public fun <E, A> Validated<E, A>.replicate(SE: Semigroup<E>, n: Int, MA: Monoid<A>): Validated<E, A> =
  if (n <= 0) MA.empty().valid()
  else this@replicate.zip(SE, replicate(SE, n - 1, MA)) { a, xs -> MA.run { a + xs } }

public fun <E, A> Validated<Iterable<E>, Iterable<A>>.bisequence(): List<Validated<E, A>> =
  bitraverse(::identity, ::identity)

public fun <E, A, B> Validated<Either<E, A>, Either<E, B>>.bisequenceEither(): Either<E, Validated<A, B>> =
  bitraverseEither(::identity, ::identity)

public fun <A, B> Validated<Option<A>, Option<B>>.bisequenceOption(): Option<Validated<A, B>> =
  bitraverseOption(::identity, ::identity)

public fun <A, B> Validated<A?, B?>.bisequenceNullable(): Validated<A, B>? =
  bitraverseNullable(::identity, ::identity)

public fun <E, A> Validated<E, A>.fold(MA: Monoid<A>): A = MA.run {
  foldLeft(empty()) { acc, a -> acc.combine(a) }
}

@Deprecated("use fold instead", ReplaceWith("fold(MA)", "arrow.core.fold"))
public fun <E, A> Validated<E, A>.combineAll(MA: Monoid<A>): A =
  fold(MA)

public fun <E, A> Validated<E, Iterable<A>>.sequence(): List<Validated<E, A>> =
  traverse(::identity)

@Deprecated(
  "sequenceEither is being renamed to sequence to simplify the Arrow API",
  ReplaceWith("sequence()", "arrow.core.sequence")
)
public fun <E, A, B> Validated<A, Either<E, B>>.sequenceEither(): Either<E, Validated<A, B>> =
  sequence()

public fun <E, A, B> Validated<A, Either<E, B>>.sequence(): Either<E, Validated<A, B>> =
  traverse(::identity)

@Deprecated(
  "sequenceOption is being renamed to sequence to simplify the Arrow API",
  ReplaceWith("sequence()", "arrow.core.sequence")
)
public fun <A, B> Validated<A, Option<B>>.sequenceOption(): Option<Validated<A, B>> =
  sequence()

public fun <A, B> Validated<A, Option<B>>.sequence(): Option<Validated<A, B>> =
  traverse(::identity)

@Deprecated(
  "sequenceNullable is being renamed to sequence to simplify the Arrow API",
  ReplaceWith("sequence()", "arrow.core.sequence")
)
public fun <A, B> Validated<A, B?>.sequenceNullable(): Validated<A, B>? =
  sequence()

public fun <A, B> Validated<A, B?>.sequence(): Validated<A, B>? =
  traverseNullable(::identity)

@Deprecated(
  DeprMsg + "Use compareTo on Either after refactoring",
  ReplaceWith("toEither().getOrElse { default() }")
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
  DeprMsg + "Use getOrElse on Either after refactoring",
  ReplaceWith("toEither().getOrElse { default() }")
)
public inline fun <E, A> Validated<E, A>.getOrElse(default: () -> A): A =
  toEither().getOrElse { default() }

/**
 * Return the Valid value, or null if Invalid
 */
@Deprecated(
  DeprMsg + "Use getOrNull on Either after refactoring",
  ReplaceWith("toEither().getOrNull()")
)
public fun <E, A> Validated<E, A>.orNull(): A? =
  toEither().getOrNull()

@Deprecated(
  DeprMsg + "Use getOrNone on Either after refactoring",
  ReplaceWith("toEither().getOrNone()")
)
public fun <E, A> Validated<E, A>.orNone(): Option<A> =
  toEither().getOrNone()

/**
 * Return the Valid value, or the result of f if Invalid
 */
@Deprecated(
  DeprMsg + "Use getOrElse on Either after refactoring",
  ReplaceWith("toEither().getOrElse(f)")
)
public inline fun <E, A> Validated<E, A>.valueOr(f: (E) -> A): A =
  toEither().getOrElse(f)

/**
 * If `this` is valid return `this`, otherwise if `that` is valid return `that`, otherwise combine the failures.
 * This is similar to [orElse] except that here failures are accumulated.
 */
@Deprecated(
  DeprAndNicheMsg + "Use recover on Either after refactoring",
  ReplaceWith(
    "toEither().recover { e -> that().mapLeft { ee -> SE.run { e.combine(ee) } }.bind() }.toValidated()",
    "arrow.core.recover"
  )
)
public inline fun <E, A> Validated<E, A>.findValid(SE: Semigroup<E>, that: () -> Validated<E, A>): Validated<E, A> =
  toEither().recover { e -> that().mapLeft { ee -> SE.run { e.combine(ee) } }.bind() }.toValidated()
//  fold(
//    { e ->
//      that().fold(
//        { ee -> Invalid(SE.run { e.combine(ee) }) },
//        { Valid(it) }
//      )
//    },
//    { Valid(it) }
//  )

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
  DeprMsg + "Use Either DSL or flatMap instead after refactoring.",
  ReplaceWith("toEither().flatMap { f(it).toEither() }.toValidated()")
)
public inline fun <E, A, B> Validated<E, A>.andThen(f: (A) -> Validated<E, B>): Validated<E, B> =
  toEither().flatMap { f(it).toEither() }.toValidated()

/**
 * Return this if it is Valid, or else fall back to the given default.
 * The functionality is similar to that of [findValid] except for failure accumulation,
 * where here only the error on the right is preserved and the error on the left is ignored.
 */
@Deprecated(
  DeprMsg + "Use recover on Either instead after refactoring.",
  ReplaceWith("toEither().recover { default().bind() }.toValidated()")
)
public inline fun <E, A> Validated<E, A>.orElse(default: () -> Validated<E, A>): Validated<E, A> =
  toEither().recover { default().bind() }.toValidated()

@Deprecated(
  DeprMsg + "Use recover on Either instead after refactoring.",
  ReplaceWith("toEither().recover { e -> f(e).bind() }.toValidated()")
)
public inline fun <E, A> Validated<E, A>.handleErrorWith(f: (E) -> Validated<E, A>): Validated<E, A> =
  toEither().recover { e -> f(e).bind() }.toValidated()

@Deprecated(
  DeprMsg + "Use recover on Either instead after refactoring.",
  ReplaceWith("toEither().recover<E, Nothing, A> { e -> f(e) }.toValidated()")
)
public inline fun <E, A> Validated<E, A>.handleError(f: (E) -> A): Validated<Nothing, A> =
  toEither().recover<E, Nothing, A> { e -> f(e) }.toValidated()

@Deprecated(
  DeprMsg + "Use fold on Either instead after refactoring.",
  ReplaceWith("fold(fe, fa).valid()")
)
public inline fun <E, A, B> Validated<E, A>.redeem(fe: (E) -> B, fa: (A) -> B): Validated<E, B> =
  fold(fe, fa).valid()

@Deprecated(
  DeprMsg + "Validated is deprecated in favor of Either",
  ReplaceWith("toEither().valid()")
)
public fun <E, A> Validated<E, A>.attempt(): Validated<Nothing, Either<E, A>> =
  toEither().valid()

@Deprecated(
  DeprMsg + "Use merge() on Either instead after refactoring.",
  ReplaceWith("toEither().merge()")
)
public inline fun <A> Validated<A, A>.merge(): A =
  toEither().merge()

@Deprecated(
  DeprMsg + "Use Either.zipOrAccumulate instead",
  ReplaceWith("Either.zipOrAccumulate({ a, b -> SE.run { a.combine(b) } }, toEither(), y.toEither(), { a, b -> SA.run { a.combine(b) } }).toValidated()")
)
public fun <E, A> Validated<E, A>.combine(
  SE: Semigroup<E>,
  SA: Semigroup<A>,
  y: Validated<E, A>
): Validated<E, A> =
  Either.zipOrAccumulate(
    { a, b -> SE.run { a.combine(b) } },
    toEither(),
    y.toEither(),
    { a, b -> SA.run { a.combine(b) } }).toValidated()

@Deprecated(
  DeprAndNicheMsg,
  ReplaceWith(
    "toEither().recover { e -> y.toEither().recover { ee -> raise(SE.run { e.combine(ee) })) }.bind() }.toValidated()",
    "arrow.core.recover"
  )
)
public fun <E, A> Validated<E, A>.combineK(SE: Semigroup<E>, y: Validated<E, A>): Validated<E, A> {
  return toEither().recover { e -> y.toEither().recover { ee -> raise(SE.run { e.combine(ee) }) }.bind() }
    .toValidated()
}

/**
 * Converts the value to an Ior<E, A>
 */
@Deprecated(
  DeprMsg + "Use toIor on Either after refactoring Validated to Either",
  ReplaceWith("toEither().toIor()")
)
public fun <E, A> Validated<E, A>.toIor(): Ior<E, A> =
  toEither().toIor()

@Deprecated(
  DeprMsg + "Use right instead to construct the equivalent Either value",
  ReplaceWith("this.right()", "arrow.core.right")
)
public inline fun <A> A.valid(): Validated<Nothing, A> =
  Valid(this)

@Deprecated(
  DeprMsg + "Use left instead to construct the equivalent Either value",
  ReplaceWith("this.left()", "arrow.core.left")
)
public inline fun <E> E.invalid(): Validated<E, Nothing> =
  Invalid(this)

@Deprecated(
  DeprMsg + "Use right instead to construct the equivalent Either value",
  ReplaceWith("this.right()", "arrow.core.right")
)
public inline fun <A> A.validNel(): ValidatedNel<Nothing, A> =
  Validated.validNel(this)

@Deprecated(
  DeprMsg + "Use leftNel instead to construct the equivalent Either value",
  ReplaceWith("this.leftNel()", "arrow.core.leftNel")
)
public inline fun <E> E.invalidNel(): ValidatedNel<E, Nothing> =
  Validated.invalidNel(this)

private const val DeprMsg = "Validated functionally is being merged into Either.\n"

private const val DeprAndNicheMsg =
  "Validated functionaliy is being merged into Either, but this API is niche and will be removed in the future. If this method is crucial for you, please let us know on the Arrow Github. Thanks!\n https://github.com/arrow-kt/arrow/issues\n"
