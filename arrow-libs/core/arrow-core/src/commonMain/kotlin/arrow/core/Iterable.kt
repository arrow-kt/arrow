@file:Suppress("unused", "FunctionName")

/**
 * <!--- TEST_NAME IterableKnitTest -->
 */
package arrow.core

import arrow.core.Either.Left
import arrow.core.Either.Right
import arrow.core.raise.Raise
import arrow.core.raise.either
import arrow.core.raise.RaiseAccumulate
import arrow.core.raise.mapOrAccumulate
import arrow.core.raise.nullable
import arrow.core.raise.option
import arrow.core.raise.result
import arrow.typeclasses.Monoid
import arrow.typeclasses.MonoidDeprecation
import arrow.typeclasses.Semigroup
import arrow.typeclasses.SemigroupDeprecation
import arrow.typeclasses.combine
import kotlin.experimental.ExperimentalTypeInference
import kotlin.jvm.JvmName
import kotlin.collections.unzip as stdlibUnzip

public inline fun <B, C, D, E> Iterable<B>.zip(
  c: Iterable<C>,
  d: Iterable<D>,
  transform: (B, C, D) -> E
): List<E> {
  val bb = iterator()
  val cc = c.iterator()
  val dd = d.iterator()

  val size = minOf(
    collectionSizeOrDefault(10),
    c.collectionSizeOrDefault(10),
    d.collectionSizeOrDefault(10)
  )
  val list = ArrayList<E>(size)
  while (bb.hasNext() && cc.hasNext() && dd.hasNext()) {
    list.add(transform(bb.next(), cc.next(), dd.next()))
  }
  return list
}

public inline fun <B, C, D, E, F> Iterable<B>.zip(
  c: Iterable<C>,
  d: Iterable<D>,
  e: Iterable<E>,
  transform: (B, C, D, E) -> F
): List<F> {
  val bb = iterator()
  val cc = c.iterator()
  val dd = d.iterator()
  val ee = e.iterator()
  val size = minOf(
    collectionSizeOrDefault(10),
    c.collectionSizeOrDefault(10),
    d.collectionSizeOrDefault(10),
    e.collectionSizeOrDefault(10)
  )
  val list = ArrayList<F>(size)
  while (bb.hasNext() && cc.hasNext() && dd.hasNext() && ee.hasNext()) {
    list.add(transform(bb.next(), cc.next(), dd.next(), ee.next()))
  }
  return list
}

public inline fun <B, C, D, E, F, G> Iterable<B>.zip(
  c: Iterable<C>,
  d: Iterable<D>,
  e: Iterable<E>,
  f: Iterable<F>,
  transform: (B, C, D, E, F) -> G
): List<G> {
  val bb = iterator()
  val cc = c.iterator()
  val dd = d.iterator()
  val ee = e.iterator()
  val ff = f.iterator()
  val size = minOf(
    collectionSizeOrDefault(10),
    c.collectionSizeOrDefault(10),
    d.collectionSizeOrDefault(10),
    e.collectionSizeOrDefault(10),
    f.collectionSizeOrDefault(10)
  )
  val list = ArrayList<G>(size)
  while (bb.hasNext() && cc.hasNext() && dd.hasNext() && ee.hasNext() && ff.hasNext()) {
    list.add(transform(bb.next(), cc.next(), dd.next(), ee.next(), ff.next()))
  }
  return list
}

public inline fun <B, C, D, E, F, G, H> Iterable<B>.zip(
  c: Iterable<C>,
  d: Iterable<D>,
  e: Iterable<E>,
  f: Iterable<F>,
  g: Iterable<G>,
  transform: (B, C, D, E, F, G) -> H
): List<H> {
  val bb = iterator()
  val cc = c.iterator()
  val dd = d.iterator()
  val ee = e.iterator()
  val ff = f.iterator()
  val gg = g.iterator()
  val size = minOf(
    collectionSizeOrDefault(10),
    c.collectionSizeOrDefault(10),
    d.collectionSizeOrDefault(10),
    e.collectionSizeOrDefault(10),
    f.collectionSizeOrDefault(10),
    g.collectionSizeOrDefault(10)
  )
  val list = ArrayList<H>(size)
  while (bb.hasNext() && cc.hasNext() && dd.hasNext() && ee.hasNext() && ff.hasNext() && gg.hasNext()) {
    list.add(transform(bb.next(), cc.next(), dd.next(), ee.next(), ff.next(), gg.next()))
  }
  return list
}

public inline fun <B, C, D, E, F, G, H, I> Iterable<B>.zip(
  c: Iterable<C>,
  d: Iterable<D>,
  e: Iterable<E>,
  f: Iterable<F>,
  g: Iterable<G>,
  h: Iterable<H>,
  transform: (B, C, D, E, F, G, H) -> I
): List<I> {
  val bb = iterator()
  val cc = c.iterator()
  val dd = d.iterator()
  val ee = e.iterator()
  val ff = f.iterator()
  val gg = g.iterator()
  val hh = h.iterator()
  val size = minOf(
    collectionSizeOrDefault(10),
    c.collectionSizeOrDefault(10),
    d.collectionSizeOrDefault(10),
    e.collectionSizeOrDefault(10),
    f.collectionSizeOrDefault(10),
    g.collectionSizeOrDefault(10),
    h.collectionSizeOrDefault(10)
  )
  val list = ArrayList<I>(size)
  while (bb.hasNext() && cc.hasNext() && dd.hasNext() && ee.hasNext() && ff.hasNext() && gg.hasNext() && hh.hasNext()) {
    list.add(transform(bb.next(), cc.next(), dd.next(), ee.next(), ff.next(), gg.next(), hh.next()))
  }
  return list
}

public inline fun <B, C, D, E, F, G, H, I, J> Iterable<B>.zip(
  c: Iterable<C>,
  d: Iterable<D>,
  e: Iterable<E>,
  f: Iterable<F>,
  g: Iterable<G>,
  h: Iterable<H>,
  i: Iterable<I>,
  transform: (B, C, D, E, F, G, H, I) -> J
): List<J> {
  val bb = iterator()
  val cc = c.iterator()
  val dd = d.iterator()
  val ee = e.iterator()
  val ff = f.iterator()
  val gg = g.iterator()
  val hh = h.iterator()
  val ii = i.iterator()
  val size = minOf(
    collectionSizeOrDefault(10),
    c.collectionSizeOrDefault(10),
    d.collectionSizeOrDefault(10),
    e.collectionSizeOrDefault(10),
    f.collectionSizeOrDefault(10),
    g.collectionSizeOrDefault(10),
    h.collectionSizeOrDefault(10),
    i.collectionSizeOrDefault(10)
  )
  val list = ArrayList<J>(size)
  while (bb.hasNext() && cc.hasNext() && dd.hasNext() && ee.hasNext() && ff.hasNext() && gg.hasNext() && hh.hasNext() && ii.hasNext()) {
    list.add(transform(bb.next(), cc.next(), dd.next(), ee.next(), ff.next(), gg.next(), hh.next(), ii.next()))
  }
  return list
}

public inline fun <B, C, D, E, F, G, H, I, J, K> Iterable<B>.zip(
  c: Iterable<C>,
  d: Iterable<D>,
  e: Iterable<E>,
  f: Iterable<F>,
  g: Iterable<G>,
  h: Iterable<H>,
  i: Iterable<I>,
  j: Iterable<J>,
  transform: (B, C, D, E, F, G, H, I, J) -> K
): List<K> {
  val bb = iterator()
  val cc = c.iterator()
  val dd = d.iterator()
  val ee = e.iterator()
  val ff = f.iterator()
  val gg = g.iterator()
  val hh = h.iterator()
  val ii = i.iterator()
  val jj = j.iterator()
  val size = minOf(
    collectionSizeOrDefault(10),
    c.collectionSizeOrDefault(10),
    d.collectionSizeOrDefault(10),
    e.collectionSizeOrDefault(10),
    f.collectionSizeOrDefault(10),
    g.collectionSizeOrDefault(10),
    h.collectionSizeOrDefault(10),
    i.collectionSizeOrDefault(10),
    j.collectionSizeOrDefault(10)
  )
  val list = ArrayList<K>(size)
  while (bb.hasNext() && cc.hasNext() && dd.hasNext() && ee.hasNext() && ff.hasNext() && gg.hasNext() && hh.hasNext() && ii.hasNext() && jj.hasNext()) {
    list.add(
      transform(
        bb.next(),
        cc.next(),
        dd.next(),
        ee.next(),
        ff.next(),
        gg.next(),
        hh.next(),
        ii.next(),
        jj.next()
      )
    )
  }
  return list
}

public inline fun <B, C, D, E, F, G, H, I, J, K, L> Iterable<B>.zip(
  c: Iterable<C>,
  d: Iterable<D>,
  e: Iterable<E>,
  f: Iterable<F>,
  g: Iterable<G>,
  h: Iterable<H>,
  i: Iterable<I>,
  j: Iterable<J>,
  k: Iterable<K>,
  transform: (B, C, D, E, F, G, H, I, J, K) -> L
): List<L> {
  val bb = iterator()
  val cc = c.iterator()
  val dd = d.iterator()
  val ee = e.iterator()
  val ff = f.iterator()
  val gg = g.iterator()
  val hh = h.iterator()
  val ii = i.iterator()
  val jj = j.iterator()
  val kk = k.iterator()
  val size = minOf(
    collectionSizeOrDefault(10),
    c.collectionSizeOrDefault(10),
    d.collectionSizeOrDefault(10),
    e.collectionSizeOrDefault(10),
    f.collectionSizeOrDefault(10),
    g.collectionSizeOrDefault(10),
    h.collectionSizeOrDefault(10),
    i.collectionSizeOrDefault(10),
    j.collectionSizeOrDefault(10),
    k.collectionSizeOrDefault(10)
  )
  val list = ArrayList<L>(size)
  while (bb.hasNext() && cc.hasNext() && dd.hasNext() && ee.hasNext() && ff.hasNext() && gg.hasNext() && hh.hasNext() && ii.hasNext() && jj.hasNext() && kk.hasNext()) {
    list.add(
      transform(
        bb.next(),
        cc.next(),
        dd.next(),
        ee.next(),
        ff.next(),
        gg.next(),
        hh.next(),
        ii.next(),
        jj.next(),
        kk.next()
      )
    )
  }
  return list
}

public fun <T> Iterable<T>.collectionSizeOrDefault(default: Int): Int =
  if (this is Collection<*>) this.size else default

@Deprecated(
  "Traverse for Either is being deprecated in favor of Either DSL + Iterable.map.\n$NicheAPI",
  ReplaceWith("let<Iterable<A>, Either<E, List<B>>> { l -> either<E, List<B>> { l.map<A, B> { f(it).bind<B>() } } }", "arrow.core.raise.either")
)
public inline fun <E, A, B> Iterable<A>.traverseEither(f: (A) -> Either<E, B>): Either<E, List<B>> =
  let { l -> either { l.map { f(it).bind() } } }

@Deprecated(
  "Traverse for Either is being deprecated in favor of Either DSL + Iterable.map.\n$NicheAPI",
  ReplaceWith("let<Iterable<A>, Either<E, List<B>>> { l -> either<E, List<B>> { l.map<A, B> { f(it).bind<B>() } } }", "arrow.core.raise.either")
)
@OptIn(ExperimentalTypeInference::class)
@OverloadResolutionByLambdaReturnType
public inline fun <E, A, B> Iterable<A>.traverse(f: (A) -> Either<E, B>): Either<E, List<B>> =
  let { l -> either { l.map { f(it).bind() } } }

@Deprecated(
  "The sequence extension function is being deprecated in favor of the either DSL.",
  ReplaceWith("let { l -> either<E, List<A>> { l.bindAll() } }", "arrow.core.raise.either")
)
public fun <E, A> Iterable<Either<E, A>>.sequenceEither(): Either<E, List<A>> =
  let { l -> either { l.bindAll() } }

@Deprecated(
  "The sequence extension function is being deprecated in favor of the either DSL.",
  ReplaceWith("let { l -> either<E, List<A>> { l.bindAll() } }", "arrow.core.raise.either")
)
public fun <E, A> Iterable<Either<E, A>>.sequence(): Either<E, List<A>> =
  let { l -> either { l.bindAll() } }

@Deprecated(
  "Traverse for Result is being deprecated in favor of Result DSL + Iterable.map.\n$NicheAPI",
  ReplaceWith("let<Iterable<A>, Result<List<B>>> { l -> result<List<B>> { l.map<A, B> { f(it).bind<B>() } } }", "arrow.core.raise.result")
)
@OptIn(ExperimentalTypeInference::class)
@OverloadResolutionByLambdaReturnType
public inline fun <A, B> Iterable<A>.traverse(f: (A) -> Result<B>): Result<List<B>> =
  let { l -> result { l.map { f(it).bind() } } }

@Deprecated(
  "Traverse for Result is being deprecated in favor of Result DSL + Iterable.map.\n$NicheAPI",
  ReplaceWith("let<Iterable<A>, Result<List<B>>> { l -> result<List<B>> { l.map<A, B> { f(it).bind<B>() } } }", "arrow.core.raise.result")
)
public inline fun <A, B> Iterable<A>.traverseResult(f: (A) -> Result<B>): Result<List<B>> =
  let { l -> result { l.map { f(it).bind() } } }

@Deprecated(
  "The sequence extension function is being deprecated in favor of the result DSL.",
  ReplaceWith("let { l -> result<List<A>> { l.bindAll() } }", "arrow.core.raise.result")
)
public fun <A> Iterable<Result<A>>.sequenceResult(): Result<List<A>> =
  let { l -> result { l.bindAll() } }

@Deprecated(
  "The sequence extension function is being deprecated in favor of the result DSL.",
  ReplaceWith("let { l -> result<List<A>> { l.bindAll() } }", "arrow.core.raise.result")
)
public fun <A> Iterable<Result<A>>.sequence(): Result<List<A>> =
  let { l -> result { l.bindAll() } }

@Deprecated(
  ValidatedDeprMsg + "Use the mapOrAccumulate API instead",
  ReplaceWith(
    "mapOrAccumulate({ a, b -> semigroup.run { a.combine(b) } }) { f(it).bind() }.toValidated()",
    "arrow.core.mapOrAccumulate"
  )
)
public inline fun <E, A, B> Iterable<A>.traverseValidated(
  semigroup: Semigroup<E>,
  f: (A) -> Validated<E, B>
): Validated<E, List<B>> =
  mapOrAccumulate({ a, b -> semigroup.run { a.combine(b) } }) { f(it).bind() }.toValidated()

@Deprecated(
  ValidatedDeprMsg + "Use the mapOrAccumulate API instead",
  ReplaceWith(
    "mapOrAccumulate({ a, b -> semigroup.run { a.combine(b)  } }) { f(it).bind() }.toValidated()",
    "arrow.core.mapOrAccumulate"
  )
)
@OptIn(ExperimentalTypeInference::class)
@OverloadResolutionByLambdaReturnType
public inline fun <E, A, B> Iterable<A>.traverse(
  semigroup: Semigroup<E>,
  f: (A) -> Validated<E, B>
): Validated<E, List<B>> =
  mapOrAccumulate({ a, b -> semigroup.run { a.combine(b) } }) { f(it).bind() }.toValidated()

@Deprecated(
  ValidatedDeprMsg + "Use the mapOrAccumulate API instead",
  ReplaceWith(
    "mapOrAccumulate<E, A, B> { f(it).bindNel() }.toValidated()",
    "arrow.core.mapOrAccumulate"
  )
)
public inline fun <E, A, B> Iterable<A>.traverseValidated(f: (A) -> ValidatedNel<E, B>): ValidatedNel<E, List<B>> =
  mapOrAccumulate { f(it).bindNel() }.toValidated()

@Deprecated(
  ValidatedDeprMsg + "Use the mapOrAccumulate API instead",
  ReplaceWith(
    "mapOrAccumulate<E, A, B> { f(it).bindNel() }.toValidated()",
    "arrow.core.mapOrAccumulate"
  )
)
@OptIn(ExperimentalTypeInference::class)
@OverloadResolutionByLambdaReturnType
public inline fun <E, A, B> Iterable<A>.traverse(f: (A) -> ValidatedNel<E, B>): ValidatedNel<E, List<B>> =
  mapOrAccumulate { f(it).bindNel() }.toValidated()

@Deprecated(
  ValidatedDeprMsg + "Use the mapOrAccumulate API instead",
  ReplaceWith(
    "mapOrAccumulate({ a, b -> semigroup.run { a.combine(b) } }) { it.bind() }.toValidated()",
    "arrow.core.mapOrAccumulate"
  )
)
public fun <E, A> Iterable<Validated<E, A>>.sequenceValidated(semigroup: Semigroup<E>): Validated<E, List<A>> =
  mapOrAccumulate({ a, b -> semigroup.run { a.combine(b) } }) { it.bind() }.toValidated()

@Deprecated(
  ValidatedDeprMsg + "Use the mapOrAccumulate API instead",
  ReplaceWith(
    "mapOrAccumulate({ a, b -> semigroup.run { a.combine(b)  } }) { it.bind() }.toValidated()",
    "arrow.core.mapOrAccumulate"
  )
)
public fun <E, A> Iterable<Validated<E, A>>.sequence(semigroup: Semigroup<E>): Validated<E, List<A>> =
  mapOrAccumulate({ a, b -> semigroup.run { a.combine(b) } }) { it.bind() }.toValidated()

@Deprecated(
  ValidatedDeprMsg + "Use the mapOrAccumulate API instead",
  ReplaceWith(
    "mapOrAccumulate<E, ValidatedNel<E, A>, A> { it.bindNel() }.toValidated()",
    "arrow.core.mapOrAccumulate"
  )
)
public fun <E, A> Iterable<ValidatedNel<E, A>>.sequenceValidated(): ValidatedNel<E, List<A>> =
  mapOrAccumulate { it.bindNel() }.toValidated()

@Deprecated(
  ValidatedDeprMsg + "Use the mapOrAccumulate API instead",
  ReplaceWith(
    "mapOrAccumulate<E, ValidatedNel<E, A>, A> { it.bindNel() }.toValidated()",
    "arrow.core.mapOrAccumulate"
  )
)
public fun <E, A> Iterable<ValidatedNel<E, A>>.sequence(): ValidatedNel<E, List<A>> =
  mapOrAccumulate { it.bindNel() }.toValidated()

@Deprecated(
  "Traverse for Option is being deprecated in favor of Option DSL + Iterable.map.\n$NicheAPI",
  ReplaceWith("let<Iterable<A>, Option<List<B>>> { l -> option<List<B>> { l.map<A, B> { f(it).bind<B>() } } }", "arrow.core.raise.option")
)
public inline fun <A, B> Iterable<A>.traverseOption(f: (A) -> Option<B>): Option<List<B>> =
  let { l -> option { l.map { f(it).bind() } } }

@Deprecated(
  "Traverse for Option is being deprecated in favor of Option DSL + Iterable.map.\n$NicheAPI",
  ReplaceWith("let<Iterable<A>, Option<List<B>>> { l -> option<List<B>> { l.map<A, B> { f(it).bind<B>() } } }", "arrow.core.raise.option")
)
@OptIn(ExperimentalTypeInference::class)
@OverloadResolutionByLambdaReturnType
public inline fun <A, B> Iterable<A>.traverse(f: (A) -> Option<B>): Option<List<B>> =
  let { l -> option { l.map { f(it).bind() } } }

@Deprecated(
  "The sequence extension function is being deprecated in favor of the option DSL.",
  ReplaceWith("let { l -> option<List<A>> { l.bindAll() } }", "arrow.core.raise.option")
)
public fun <A> Iterable<Option<A>>.sequenceOption(): Option<List<A>> =
  let { l -> option { l.bindAll() } }

@Deprecated(
  "The sequence extension function is being deprecated in favor of the option DSL.",
  ReplaceWith("let { l -> option<List<A>> { l.bindAll() } }", "arrow.core.raise.option")
)
public fun <A> Iterable<Option<A>>.sequence(): Option<List<A>> =
  let { l -> option { l.bindAll() } }

@Deprecated(
  "Traverse for nullable is being deprecated in favor of Nullable DSL + Iterable.map.\n$NicheAPI",
  ReplaceWith(
    "let<Iterable<A>, List<B & Any>?> { l -> nullable<List<B & Any>> { l.map<A, B & Any> { f(it).bind<B & Any>() } } }",
    "arrow.core.raise.nullable"
  )
)
public inline fun <A, B> Iterable<A>.traverseNullable(f: (A) -> B?): List<B>? =
  traverse(f)

@Deprecated(
  "Traverse for nullable is being deprecated in favor of Nullable DSL + Iterable.map.\n$NicheAPI",
  ReplaceWith(
    "let<Iterable<A>, List<B & Any>?> { l -> nullable<List<B & Any>> { l.map<A, B & Any> { f(it).bind<B & Any>() } } }",
    "arrow.core.raise.nullable"
  )
)
@OptIn(ExperimentalTypeInference::class)
@OverloadResolutionByLambdaReturnType
public inline fun <A, B> Iterable<A>.traverse(f: (A) -> B?): List<B>? =
  let { l -> nullable { l.map { f(it).bind() } } }

@Deprecated(
  "The sequence extension function is being deprecated in favor of the nullable DSL.",
  ReplaceWith("let { l -> nullable<List<A>> { l.bindAll() } }", "arrow.core.raise.nullable")
)
public fun <A> Iterable<A?>.sequenceNullable(): List<A>? =
  let { l -> nullable { l.bindAll() } }

@Deprecated(
  "The sequence extension function is being deprecated in favor of the nullable DSL.",
  ReplaceWith("let { l -> nullable<List<A>> { l.bindAll() } }", "arrow.core.raise.nullable")
)
public fun <A> Iterable<A?>.sequence(): List<A>? =
  let { l -> nullable { l.bindAll() } }

/**
 * Returns [Either] a [List] containing the results of applying the given [transform] function to each element in the original collection,
 * **or** accumulate all the _logical errors_ that were _raised_ while transforming the collection using the [combine] function is used to accumulate all the _logical errors_.
 *
 * Within this DSL you can `bind` both [Either], and [EitherNel] values and invoke [Raise] based function of _logical error_ type [Error]. Let's see an example of all the different cases:
 * <!--- INCLUDE
 * import arrow.core.left
 * import arrow.core.leftNel
 * import arrow.core.nonEmptyListOf
 * import arrow.core.mapOrAccumulate
 * import io.kotest.matchers.shouldBe
 * -->
 * ```kotlin
 * fun test() {
 *   listOf(1, 2, 3, 4).mapOrAccumulate({ a, b -> "$a, $b" }) { i ->
 *     when(i) {
 *       1 -> "Either - $i".left().bind()
 *       2 -> "EitherNel - $i".leftNel().bindNel()
 *       3 -> raise("Raise - $i")
 *       else -> withNel { raise(nonEmptyListOf("RaiseNel - $i")) }
 *     }
 *   } shouldBe "Either - 1, EitherNel - 2, Raise - 3, RaiseNel - 4".left()
 * }
 * ```
 * <!--- KNIT example-iterable-01.kt -->
 * <!--- TEST lines.isEmpty() -->
 */
@OptIn(ExperimentalTypeInference::class)
public inline fun <Error, A, B> Iterable<A>.mapOrAccumulate(
  combine: (Error, Error) -> Error,
  @BuilderInference transform: RaiseAccumulate<Error>.(A) -> B,
): Either<Error, List<B>> = either {
  mapOrAccumulate(this@mapOrAccumulate, combine, transform)
}

/**
 * Returns [Either] a [List] containing the results of applying the given [transform] function to each element in the original collection,
 * **or** accumulate all the _logical errors_ into a [NonEmptyList] that were _raised_ while applying the [transform] function.
 *
 * Let's see an example of all the different cases:
 * <!--- INCLUDE
 * import arrow.core.left
 * import arrow.core.leftNel
 * import arrow.core.nonEmptyListOf
 * import arrow.core.mapOrAccumulate
 * import io.kotest.matchers.shouldBe
 * -->
 * ```kotlin
 * fun test() {
 *   listOf(1, 2, 3, 4).mapOrAccumulate { i ->
 *     when(i) {
 *       1 -> "Either - $i".left().bind()
 *       2 -> "EitherNel - $i".leftNel().bindNel()
 *       3 -> raise("Raise - $i")
 *       else -> withNel { raise(nonEmptyListOf("RaiseNel - $i")) }
 *     }
 *   } shouldBe nonEmptyListOf("Either - 1", "EitherNel - 2", "Raise - 3", "RaiseNel - 4").left()
 * }
 * ```
 * <!--- KNIT example-iterable-02.kt -->
 * <!--- TEST lines.isEmpty() -->
 */
@OptIn(ExperimentalTypeInference::class)
public inline fun <Error, A, B> Iterable<A>.mapOrAccumulate(
  @BuilderInference transform: RaiseAccumulate<Error>.(A) -> B,
): Either<NonEmptyList<Error>, List<B>> = either {
  mapOrAccumulate(this@mapOrAccumulate, transform)
}

/**
 * Flatten an [Iterable] of [Either].
 * Alias for [mapOrAccumulate] over an [Iterable] of computed [Either].
 * Either returns a [List] containing all [Either.Right] values, or [Either.Left] values accumulated using [combine].
 */
public inline fun <Error, A> Iterable<Either<Error, A>>.flattenOrAccumulate(combine: (Error, Error) -> Error): Either<Error, List<A>> =
  mapOrAccumulate(combine) { it.bind() }

/**
 * Flatten an [Iterable] of [Either].
 * Alias for [mapOrAccumulate] over an [Iterable] of computed [Either].
 * Either returns a [List] containing all [Either.Right] values, or [EitherNel] [Left] values accumulated using [combine].
 */
@JvmName("flattenNelOrAccumulate")
public fun <Error, A> Iterable<EitherNel<Error, A>>.flattenOrAccumulate(combine: (Error, Error) -> Error): Either<Error, List<A>> =
  mapOrAccumulate(combine) { it.bindNel() }

/**
 * Flatten an [Iterable] of [Either].
 * Alias for [mapOrAccumulate] over an [Iterable] of computed [Either].
 * Either returns a [List] containing all [Either.Right] values, or a [NonEmptyList] of all [Either.Left] values.
 */
public fun <Error, A> Iterable<Either<Error, A>>.flattenOrAccumulate(): Either<NonEmptyList<Error>, List<A>> =
  mapOrAccumulate { it.bind() }

/**
 * Flatten an [Iterable] of [Either].
 * Alias for [mapOrAccumulate] over an [Iterable] of computed [Either].
 * Either returns a [List] containing all [Either.Right] values, or a [NonEmptyList] of all [EitherNel] [Left] values.
 */
@JvmName("flattenNelOrAccumulate")
public fun <Error, A> Iterable<EitherNel<Error, A>>.flattenOrAccumulate(): Either<NonEmptyList<Error>, List<A>> =
  mapOrAccumulate { it.bindNel() }

@Deprecated(
  "Void is being deprecated in favor of simple Iterable.map.\n$NicheAPI",
  ReplaceWith("map { }")
)
public fun <A> Iterable<A>.void(): List<Unit> =
  map { }

public inline fun <A, B> Iterable<A>.reduceOrNull(initial: (A) -> B, operation: (acc: B, A) -> B): B? {
  val iterator = this.iterator()
  if (!iterator.hasNext()) return null
  var accumulator: B = initial(iterator.next())
  while (iterator.hasNext()) {
    accumulator = operation(accumulator, iterator.next())
  }
  return accumulator
}

public inline fun <A, B> List<A>.reduceRightNull(
  initial: (A) -> B,
  operation: (A, acc: B) -> B
): B? {
  val iterator = listIterator(size)
  if (!iterator.hasPrevious()) return null
  var accumulator: B = initial(iterator.previous())
  while (iterator.hasPrevious()) {
    accumulator = operation(iterator.previous(), accumulator)
  }
  return accumulator
}

/**
 * Returns a [List] containing the zipped values of the two lists with null for padding.
 *
 * ```kotlin
 * import arrow.core.*
 * import io.kotest.matchers.shouldBe
 *
 * fun test() {
 *   listOf(1, 2).padZip(listOf("a")) shouldBe listOf(1 to "a", 2 to null)
 *   listOf(1).padZip(listOf("a", "b")) shouldBe listOf(1 to "a", null to "b")
 *   listOf(1, 2).padZip(listOf("a", "b")) shouldBe listOf(1 to "a", 2 to "b")
 * }
 * ```
 * <!--- KNIT example-iterable-03.kt -->
 * <!--- TEST lines.isEmpty() -->
 */
public fun <A, B> Iterable<A>.padZip(other: Iterable<B>): List<Pair<A?, B?>> =
  padZip(other) { a, b -> a to b }

/**
 * Returns a [List] containing the result of applying some transformation `(A?, B?) -> C` on a zip.
 *
 * ```kotlin
 * import arrow.core.*
 * import io.kotest.matchers.shouldBe
 *
 * fun test() {
 *   listOf(1, 2).padZip(listOf("a")) { l, r -> l to r } shouldBe listOf(1 to "a", 2 to null)
 *   listOf(1).padZip(listOf("a", "b")) { l, r -> l to r } shouldBe listOf(1 to "a", null to "b")
 *   listOf(1, 2).padZip(listOf("a", "b")) { l, r -> l to r } shouldBe listOf(1 to "a", 2 to "b")
 * }
 * ```
 * <!--- KNIT example-iterable-04.kt -->
 * <!--- TEST lines.isEmpty() -->
 */
public inline fun <A, B, C> Iterable<A>.padZip(other: Iterable<B>, fa: (A?, B?) -> C): List<C> =
  padZip(other, { fa(it, null) }, { fa(null, it) }) { a, b -> fa(a, b) }

public inline fun <A, B, C> Iterable<A>.padZip(other: Iterable<B>, left: (A) -> C, right: (B) -> C, both: (A, B) -> C): List<C> =
  buildList(maxOf(this.collectionSizeOrDefault(10), other.collectionSizeOrDefault(10))) {
    val first = this@padZip.iterator()
    val second = other.iterator()
    while (first.hasNext() || second.hasNext()) {
      when {
        first.hasNext() && second.hasNext() -> add(both(first.next(), second.next()))
        first.hasNext() -> add(left(first.next()))
        second.hasNext() -> add(right(second.next()))
      }
    }
  }

/**
 * Returns a [List<C>] containing the result of applying some transformation `(A?, B) -> C` on a zip,
 * excluding all cases where the right value is null.
 *
 * ```kotlin
 * import arrow.core.*
 * import io.kotest.matchers.shouldBe
 *
 * fun test() {
 *   listOf(1, 2).leftPadZip(listOf("a")) { l, r -> l to r } shouldBe listOf(1 to "a")
 *   listOf(1).leftPadZip(listOf("a", "b")) { l, r -> l to r } shouldBe listOf(1 to "a", null to "b")
 *   listOf(1, 2).leftPadZip(listOf("a", "b")) { l, r -> l to r } shouldBe listOf(1 to "a", 2 to "b")
 * }
 * ```
 * <!--- KNIT example-iterable-05.kt -->
 * <!--- TEST lines.isEmpty() -->
 */
public inline fun <A, B, C> Iterable<A>.leftPadZip(other: Iterable<B>, fab: (A?, B) -> C): List<C> =
  buildList(maxOf(this.collectionSizeOrDefault(10), other.collectionSizeOrDefault(10))) {
    val first = this@leftPadZip.iterator()
    other.forEach { b ->
      val c: C = when {
        first.hasNext() -> fab(first.next(), b)
        else -> fab(null, b)
      }
      add(c)
    }
  }

/**
 * Returns a [List] containing the zipped values of the two lists with null for padding on the left.
 *
 * ```kotlin
 * import arrow.core.*
 * import io.kotest.matchers.shouldBe
 *
 * fun test() {
 *   listOf(1, 2).leftPadZip(listOf("a")) shouldBe listOf(1 to "a")
 *   listOf(1).leftPadZip(listOf("a", "b")) shouldBe listOf(1 to "a", null to "b")
 *   listOf(1, 2).leftPadZip(listOf("a", "b")) shouldBe listOf(1 to "a", 2 to "b")
 * }
 * ```
 * <!--- KNIT example-iterable-06.kt -->
 * <!--- TEST lines.isEmpty() -->
 */
public fun <A, B> Iterable<A>.leftPadZip(other: Iterable<B>): List<Pair<A?, B>> =
  this.leftPadZip(other) { a, b -> a to b }

/**
 * Returns a [List] containing the result of applying some transformation `(A, B?) -> C` on a zip,
 * excluding all cases where the left value is null.
 *
 * ```kotlin
 * import arrow.core.*
 * import io.kotest.matchers.shouldBe
 *
 * fun test() {
 *   listOf(1, 2).rightPadZip(listOf("a")) { l, r -> l to r } shouldBe listOf(1 to "a", 2 to null)
 *   listOf(1).rightPadZip(listOf("a", "b")) { l, r -> l to r } shouldBe listOf(1 to "a")
 *   listOf(1, 2).rightPadZip(listOf("a", "b")) { l, r -> l to r } shouldBe listOf(1 to "a", 2 to "b")
 * }
 * ```
 * <!--- KNIT example-iterable-07.kt -->
 * <!--- TEST lines.isEmpty() -->
 */
public inline fun <A, B, C> Iterable<A>.rightPadZip(other: Iterable<B>, fa: (A, B?) -> C): List<C> =
  other.leftPadZip(this) { a, b -> fa(b, a) }

/**
 * Returns a [List<Pair<A, B?>>] containing the zipped values of the two lists with null for padding on the right.
 *
 * ```kotlin
 * import arrow.core.*
 * import io.kotest.matchers.shouldBe
 *
 * fun test() {
 *   listOf(1, 2).rightPadZip(listOf("a")) shouldBe listOf(1 to "a", 2 to null)
 *   listOf(1).rightPadZip(listOf("a", "b")) shouldBe listOf(1 to "a")
 *   listOf(1, 2).rightPadZip(listOf("a", "b")) shouldBe listOf(1 to "a", 2 to "b")
 * }
 * ```
 * <!--- KNIT example-iterable-08.kt -->
 * <!--- TEST lines.isEmpty() -->
 */
public fun <A, B> Iterable<A>.rightPadZip(other: Iterable<B>): List<Pair<A, B?>> =
  this.rightPadZip(other) { a, b -> a to b }

/**
 * Combines two structures by taking the union of their shapes and combining the elements with the given function.
 *
 * ```kotlin
 * import arrow.core.*
 * import io.kotest.matchers.shouldBe
 *
 * fun test() {
 *   listOf("A", "B").align(listOf(1, 2, 3)) {
 *      "$it"
 *   } shouldBe listOf("Ior.Both(A, 1)", "Ior.Both(B, 2)", "Ior.Right(3)")
 * }
 * ```
 * <!--- KNIT example-iterable-09.kt -->
 * <!--- TEST lines.isEmpty() -->
 */
public inline fun <A, B, C> Iterable<A>.align(b: Iterable<B>, fa: (Ior<A, B>) -> C): List<C> =
  padZip(b, { fa(Ior.Left(it)) }, { fa(Ior.Right(it)) }) { a, bb -> fa(Ior.Both(a, bb)) }

/**
 * Combines two structures by taking the union of their shapes and using Ior to hold the elements.
 *
 * ```kotlin
 * import arrow.core.*
 * import io.kotest.matchers.shouldBe
 *
 * fun test() {
 *   listOf("A", "B")
 *     .align(listOf(1, 2, 3)) shouldBe listOf(Ior.Both("A", 1), Ior.Both("B", 2), Ior.Right(3))
 * }
 * ```
 * <!--- KNIT example-iterable-10.kt -->
 * <!--- TEST lines.isEmpty() -->
 */
public fun <A, B> Iterable<A>.align(b: Iterable<B>): List<Ior<A, B>> =
  this.align(b, ::identity)

/**
 * aligns two structures and combine them with the given [Semigroup.combine]
 */
@Deprecated(SemigroupDeprecation, ReplaceWith("this.padZip<A, A, A>(other, { it }, { it }, {a1, a2 -> a1 + a2})"))
public fun <A> Iterable<A>.salign(
  SG: Semigroup<A>,
  other: Iterable<A>
): Iterable<A> = padZip(other, { it }, { it }, SG::combine)

/**
 * unzips the structure holding the resulting elements in an `Pair`
 *
 * ```kotlin
 * import arrow.core.*
 * import io.kotest.matchers.shouldBe
 *
 * fun test() {
 *   listOf("A" to 1, "B" to 2)
 *     .unzip() shouldBe Pair(listOf("A", "B"), listOf(1, 2))
 * }
 * ```
 * <!--- KNIT example-iterable-11.kt -->
 * <!--- TEST lines.isEmpty() -->
 */
@Deprecated(
  "Unzip is being deprecated in favor of the standard library version.\n$NicheAPI",
  ReplaceWith("unzip()", "kotlin.collections.unzip")
)
public fun <A, B> Iterable<Pair<A, B>>.unzip(): Pair<List<A>, List<B>> =
   stdlibUnzip()

/**
 * after applying the given function unzip the resulting structure into its elements.
 *
 * ```kotlin
 * import arrow.core.*
 * import io.kotest.matchers.shouldBe
 *
 * fun test() {
 *   listOf("A:1", "B:2", "C:3").unzip { e ->
 *     e.split(":").let {
 *       it.first() to it.last()
 *     }
 *   } shouldBe Pair(listOf("A", "B", "C"), listOf("1", "2", "3"))
 * }
 * ```
 * <!--- KNIT example-iterable-12.kt -->
 * <!--- TEST lines.isEmpty() -->
 */
@Deprecated(
  "Unzip is being deprecated in favor of the standard library version.\n$NicheAPI",
  ReplaceWith("map(fc).unzip()", "kotlin.collections.unzip")
)
public inline fun <A, B, C> Iterable<C>.unzip(fc: (C) -> Pair<A, B>): Pair<List<A>, List<B>> =
  map(fc).stdlibUnzip()

/**
 * splits a union into its component parts.
 *
 * <!--- INCLUDE
 * import arrow.core.*
 * import io.kotest.matchers.shouldBe
 * -->
 * ```kotlin
 * fun test() {
 *    listOf(
 *      Pair("A", 1).bothIor(),
 *      Pair("B", 2).bothIor(),
 *      "C".leftIor()
 *    ).separateIor() shouldBe Pair(listOf("A", "B", "C"), listOf(1, 2))
 * }
 * ```
 * <!--- KNIT example-iterable-13.kt -->
 * <!--- TEST lines.isEmpty() -->
 */
public fun <A, B> Iterable<Ior<A, B>>.separateIor(): Pair<List<A>, List<B>> =
  fold(emptyList<A>() to emptyList<B>()) { (l, r), x ->
    x.fold(
      { l + it to r },
      { l to r + it },
      { a, b -> l + a to r + b }
    )
  }

@Deprecated(
  "The current unalign function is renamed to separateIor, and a new unalign function is going to be added to Arrow 2.0.0.",
  ReplaceWith("separateIor()", "arrow.core.separateIor")
)
public fun <A, B> Iterable<Ior<A, B>>.unalign(): Pair<List<A>, List<B>> = separateIor()

/**
 * after applying the given function, splits the resulting union shaped structure into its components parts
 *
 * ```kotlin
 * import arrow.core.*
 * import io.kotest.matchers.shouldBe
 *
 * fun test() {
 *    listOf(1, 2, 3, 4).unalign {
 *      if(it % 2 == 0) it.rightIor()
 *      else it.leftIor()
 *    } shouldBe Pair(listOf(1, 3), listOf(2, 4))
 * }
 * ```
 * <!--- KNIT example-iterable-14.kt -->
 * <!--- TEST lines.isEmpty() -->
 */
@Deprecated(
  "The current unalign function is renamed to separateIor, and a new unalign function is going to be added to Arrow 2.0.0.",
  ReplaceWith("map(fa).separateIor()", "arrow.core.separateIor")
)
public inline fun <A, B, C> Iterable<C>.unalign(fa: (C) -> Ior<A, B>): Pair<List<A>, List<B>> =
  map(fa).unalign()

@Deprecated("Use fold from Kotlin Std instead", ReplaceWith("this.fold(initial){a1, a2 -> a1 + a2}"))
public fun <A> Iterable<A>.combineAll(MA: Monoid<A>): A =
  fold(MA.empty(), MA::combine)

/**
 * Returns the first element as [Some], or [None] if the iterable is empty.
 */
public fun <T> Iterable<T>.firstOrNone(): Option<T> =
  when (this) {
    is Collection -> if (!isEmpty()) Some(first()) else None
    else -> iterator().nextOrNone()
  }

private fun <T> Iterator<T>.nextOrNone(): Option<T> =
  if (hasNext()) Some(next()) else None

/**
 * Returns the first element as [Some] matching the given [predicate], or [None] if element was not found.
 */
public inline fun <T> Iterable<T>.firstOrNone(predicate: (T) -> Boolean): Option<T> {
  for (element in this) {
    if (predicate(element)) {
      return Some(element)
    }
  }
  return None
}

/**
 * Returns single element as [Some], or [None] if the iterable is empty or has more than one element.
 */
public fun <T> Iterable<T>.singleOrNone(): Option<T> =
  when (this) {
    is Collection -> when (size) {
      1 -> firstOrNone()
      else -> None
    }

    else -> iterator().run { nextOrNone().filter { !hasNext() } }
  }

/**
 * Returns the single element as [Some] matching the given [predicate], or [None] if element was not found or more than one element was found.
 */
public inline fun <T> Iterable<T>.singleOrNone(predicate: (T) -> Boolean): Option<T> {
  val list = mutableListOf<T>()
  for (element in this) {
    if (predicate(element)) {
      if (list.isNotEmpty()) {
        return None
      }
      list.add(element)
    }
  }
  return list.firstOrNone()
}

/**
 * Returns the last element as [Some], or [None] if the iterable is empty.
 */
public fun <T> Iterable<T>.lastOrNone(): Option<T> =
  when (this) {
    is Collection -> if (!isEmpty()) Some(last()) else None
    else -> iterator().run {
      if (hasNext()) {
        var last: T
        do last = next() while (hasNext())
        Some(last)
      } else {
        None
      }
    }
  }

/**
 * Returns the last element as [Some] matching the given [predicate], or [None] if no such element was found.
 */
public inline fun <T> Iterable<T>.lastOrNone(predicate: (T) -> Boolean): Option<T> {
  var value: Any? = EmptyValue
  for (element in this) {
    if (predicate(element)) {
      value = element
    }
  }
  return if (value === EmptyValue) None else Some(EmptyValue.unbox(value))
}

/**
 * Returns an element as [Some] at the given [index] or [None] if the [index] is out of bounds of this iterable.
 */
public fun <T> Iterable<T>.elementAtOrNone(index: Int): Option<T> =
  when {
    index < 0 -> None
    this is Collection -> when (index) {
      in indices -> Some(elementAt(index))
      else -> None
    }

    else -> iterator().skip(index).nextOrNone()
  }

private tailrec fun <T> Iterator<T>.skip(count: Int): Iterator<T> =
  when {
    count > 0 && hasNext() -> {
      next()
      skip(count - 1)
    }

    else -> this
  }

/**
 * Attempt to split the [Iterable] into the tail and the first element.
 * Returns `null` if the [Iterable] is empty,
 * otherwise returns a [Pair] of the tail and the first element.
 *
 * ```kotlin
 * import arrow.core.*
 * import io.kotest.matchers.shouldBe
 *
 * fun test() {
 *   emptyList<Int>().split() shouldBe null
 *   listOf("A", "B", "C").split() shouldBe Pair(listOf("B", "C"), "A")
 * }
 * ```
 * <!--- KNIT example-iterable-15.kt -->
 * <!--- TEST lines.isEmpty() -->
 */
public fun <A> Iterable<A>.split(): Pair<List<A>, A>? =
  firstOrNull()?.let { first ->
    tail() to first
  }

/** Alias for drop(1) */
public fun <A> Iterable<A>.tail(): List<A> =
  drop(1)

/**
 * Interleaves the elements of `this` [Iterable] with those of [other] [Iterable].
 * Elements of `this` and [other] are taken in turn, and the resulting list is the concatenation of the interleaved elements.
 * If one [Iterable] is longer than the other, the remaining elements are appended to the end.
 *
 * ```kotlin
 * import arrow.core.*
 * import io.kotest.matchers.shouldBe
 *
 * fun test() {
 *   val list1 = listOf(1, 2, 3)
 *   val list2 = listOf(4, 5, 6, 7, 8)
 *   list1.interleave(list2) shouldBe listOf(1, 4, 2, 5, 3, 6, 7, 8)
 * }
 * ```
 * <!--- KNIT example-iterable-16.kt -->
 * <!--- TEST lines.isEmpty() -->
 */
public fun <A> Iterable<A>.interleave(other: Iterable<A>): List<A> =
  this.split()?.let { (fa, a) ->
    listOf(a) + other.interleave(fa)
  } ?: other.toList()

/**
 * [interleave]s the elements produced by applying [ffa] to every element of `this` [Iterable].
 *
 * ```kotlin
 * import arrow.core.*
 * import io.kotest.matchers.shouldBe
 *
 * fun test() {
 *   val ints = listOf(1, 2)
 *   val res = ints.unweave { i -> listOf(i, i + 1, i + 2) }
 *   res shouldBe listOf(1, 2, 2, 3, 3, 4)
 *   res shouldBe ints.interleave(ints.flatMap { listOf(it + 1, it + 2) })
 * }
 * ```
 * <!--- KNIT example-iterable-17.kt -->
 */
public fun <A, B> Iterable<A>.unweave(ffa: (A) -> Iterable<B>): List<B> =
  split()?.let { (fa, a) ->
    ffa(a).interleave(fa.unweave(ffa))
  } ?: emptyList()

/**
 * Logical conditional. The equivalent of Prolog's soft-cut.
 * If its first argument succeeds at all, then the results will be
 * fed into the success branch. Otherwise, the failure branch is taken.
 *
 * ```kotlin
 * import arrow.core.*
 *
 * fun main(args: Array<String>) {
 *   //sampleStart
 *   val result =
 *    listOf(1,2,3).ifThen(listOf("empty")) { i ->
 *      listOf("$i, ${i + 1}")
 *    }
 *   //sampleEnd
 *   println(result)
 * }
 * ```
 * <!--- KNIT example-iterable-18.kt -->
 */
@Deprecated(
  "Use flatMap and ifEmpty instead.\n$NicheAPI",
  ReplaceWith("flatMap(ffa).ifEmpty<List<B>, Iterable<B>> { fb }")
)
public inline fun <A, B> Iterable<A>.ifThen(fb: Iterable<B>, ffa: (A) -> Iterable<B>): Iterable<B> =
  flatMap(ffa).ifEmpty { fb }

@Deprecated(
  "Use mapNotNull and getOrNull instead.\n$NicheAPI",
  ReplaceWith("mapNotNull { it.getOrNull() }")
)
public fun <A, B> Iterable<Either<A, B>>.uniteEither(): List<B> =
  mapNotNull { it.getOrNull() }

@Deprecated(
  "${ValidatedDeprMsg}Use mapNotNull and orNull instead.",
  ReplaceWith("mapNotNull { it.orNull() }", "arrow.core.orNull")
)
public fun <A, B> Iterable<Validated<A, B>>.uniteValidated(): List<B> =
  mapNotNull { it.orNull() }

/**
 * Separate the inner [Either] values into the [Either.Left] and [Either.Right].
 *
 * ```kotlin
 * import arrow.core.*
 * import io.kotest.matchers.shouldBe
 *
 * fun test() {
 *   listOf("A".left(), 2.right(), "C".left(), 4.right())
 *     .separateEither() shouldBe Pair(listOf("A", "C"), listOf(2, 4))
 * }
 * ```
 * <!--- KNIT example-iterable-19.kt -->
 * <!--- TEST lines.isEmpty() -->
 */
public fun <A, B> Iterable<Either<A, B>>.separateEither(): Pair<List<A>, List<B>> =
  separateEither(::identity)

/**
 * Applies a function [f] to each element and returns a pair of arrays:
 * the first one made of those values returned by [f] that were wrapped in [Either.Left],
 * and the second one made of those wrapped in [Either.Right].
 * <!--- INCLUDE
 * import arrow.core.*
 * import io.kotest.matchers.shouldBe
 * -->
 * ```kotlin
 * fun test() {
 *   listOf(1, 2, 3, 4)
 *     .separateEither {
 *       if (it % 2 == 0) "even: $it".right() else "odd: $it".left()
 *     } shouldBe Pair(listOf("odd: 1", "odd: 3"), listOf("even: 2", "even: 4"))
 * }
 * ```
 * <!--- KNIT example-iterable-20.kt -->
 * <!--- TEST lines.isEmpty() -->
 */
public inline fun <T, A, B> Iterable<T>.separateEither(f: (T) -> Either<A, B>): Pair<List<A>, List<B>> {
  val left = mutableListOf<A>()
  val right = mutableListOf<B>()

  for (item in this) {
    when (val either = f(item)) {
      is Left -> left.add(either.value)
      is Right -> right.add(either.value)
    }
  }

  return Pair(left, right)
}

/**
 * Separate the inner [Validated] values into the [Validated.Invalid] and [Validated.Valid].
 *
 * @receiver Iterable of Validated
 * @return a tuple containing List with [Validated.Invalid] and another List with its [Validated.Valid] values.
 */
@Deprecated(
  "${ValidatedDeprMsg}Use separateEither instead.",
  ReplaceWith("map { it.toEither() }.separateEither()")
)
public fun <A, B> Iterable<Validated<A, B>>.separateValidated(): Pair<List<A>, List<B>> {
  val invalids = ArrayList<A>(collectionSizeOrDefault(10))
  val valids = ArrayList<B>(collectionSizeOrDefault(10))

  for (validated in this)
    when (validated) {
      is Invalid -> invalids.add(validated.value)
      is Valid -> valids.add(validated.value)
    }

  return Pair(invalids, valids)
}

public fun <A> Iterable<Iterable<A>>.flatten(): List<A> =
  flatMap(::identity)

@Deprecated(DeprecatedWiden, ReplaceWith("this"))
/**
 *  Given [A] is a subtype of [B], re-type this value from Iterable<A> to Iterable<B>
 *
 * ```kotlin
 * import arrow.core.*
 *
 * fun test() {
 *   val original: List<String> = listOf("Hello", ",", "World")
 *   val result: Iterable<CharSequence> = original.widen()
 * }
 * ```
 */
public fun <B, A : B> Iterable<A>.widen(): Iterable<B> =
  this

@Deprecated(DeprecatedWiden, ReplaceWith("this"))
/**
 *  Given [A] is a subtype of [B], re-type this value from List<A> to List<B>
 *
 * ```kotlin
 * import arrow.core.*
 *
 * fun main() {
 *   val original: List<String> = listOf("Hello", ",", "World")
 *   val result: Iterable<CharSequence> = original.widen()
 * }
 * ```
 */
public fun <B, A : B> List<A>.widen(): List<B> =
  this

@Deprecated(MonoidDeprecation, ReplaceWith("this.fold(initial, {a1, a2 -> a1 + a2})"))
public fun <A> Iterable<A>.fold(MA: Monoid<A>): A =
  fold(MA.empty(), MA::combine)

@Deprecated(MonoidDeprecation, ReplaceWith("this.fold(initial) { acc, a -> combine(acc, f(a)) }"))
public fun <A, B> Iterable<A>.foldMap(MB: Monoid<B>, f: (A) -> B): B =
  fold(MB.empty()) { acc, a -> MB.run { acc.combine(f(a)) } }

public fun <A, B> Iterable<A>.crosswalk(f: (A) -> Iterable<B>): List<List<B>> =
  fold(emptyList()) { bs, a ->
    f(a).align(bs) { ior ->
      ior.fold(
        { listOf(it) },
        ::identity,
        { l, r -> listOf(l) + r }
      )
    }
  }

public fun <A, K, V> Iterable<A>.crosswalkMap(f: (A) -> Map<K, V>): Map<K, List<V>> =
  fold(emptyMap()) { bs, a ->
    f(a).align(bs) { (_, ior) ->
      ior.fold(
        { listOf(it) },
        ::identity,
        { l, r -> listOf(l) + r }
      )
    }
  }

public fun <A, B> Iterable<A>.crosswalkNull(f: (A) -> B?): List<B>? =
  fold<A, List<B>?>(emptyList()) { bs, a ->
    Ior.fromNullables(f(a), bs)?.fold(
      { listOf(it) },
      ::identity,
      { l, r -> listOf(l) + r }
    )
  }

@Deprecated("Not being used anymore. Will be removed from the binary in 2.0.0")
@PublishedApi
internal val listUnit: List<Unit> =
  listOf(Unit)

@Deprecated(
  NicheAPI,
  ReplaceWith("toList().let { l -> List(n) { l } }")
)
public fun <A> Iterable<A>.replicate(n: Int): List<List<A>> =
  toList().let { l -> List(n) { l } }

@Deprecated(NicheAPI)
public fun <A> Iterable<A>.replicate(n: Int, MA: Monoid<A>): List<A> =
  if (n <= 0) listOf(MA.empty())
  else this@replicate.zip(replicate(n - 1, MA)) { a, xs -> MA.run { a + xs } }

public operator fun <A : Comparable<A>> Iterable<A>.compareTo(other: Iterable<A>): Int =
  align(other) { ior -> ior.fold({ 1 }, { -1 }, { a1, a2 -> a1.compareTo(a2) }) }
    .fold(0) { acc, i ->
      when (acc) {
        0 -> i
        else -> acc
      }
    }

public infix fun <T> T.prependTo(list: Iterable<T>): List<T> =
  listOf(this) + list

public fun <T> Iterable<Option<T>>.filterOption(): List<T> =
  flatMap { it.fold(::emptyList, ::listOf) }

public fun <T> Iterable<Option<T>>.flattenOption(): List<T> = filterOption()
