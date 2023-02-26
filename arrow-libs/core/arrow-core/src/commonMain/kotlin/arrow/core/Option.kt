@file:OptIn(ExperimentalContracts::class)
package arrow.core

import arrow.core.Option.Companion.fromNullable
import arrow.core.raise.OptionRaise
import arrow.core.raise.option
import arrow.typeclasses.Monoid
import arrow.typeclasses.Monoid.Companion.OptionMonoid
import arrow.typeclasses.Semigroup
import arrow.typeclasses.Semigroup.Companion.OptionSemigroup
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.jvm.JvmInline
import kotlin.jvm.JvmName
import kotlin.jvm.JvmStatic

@RequiresOptIn(
  message =
  "This declaration is part of the internals of Option. Great caution must be taken to avoid ClassCastExceptions"
)
internal annotation class OptionInternals

/**
 * <!--- TEST_NAME OptionKnitTest -->
 *
 * If you have worked with Java at all in the past, it is very likely that you have come across a `NullPointerException` at some time (other languages will throw similarly named errors in such a case). Usually this happens because some method returns `null` when you weren't expecting it and, thus, isn't dealing with that possibility in your client code. A value of `null` is often abused to represent an absent optional value.
 * Kotlin tries to solve the problem by getting rid of `null` values altogether, and providing its own special syntax [Null-safety machinery based on `?`](https://kotlinlang.org/docs/reference/null-safety.html).
 *
 * Arrow models the absence of values through the `Option` datatype similar to how Scala, Haskell, and other FP languages handle optional values.
 *
 * `Option<A>` is a container for an optional value of type `A`. If the value of type `A` is present, the `Option<A>` is an instance of `Some<A>`, containing the present value of type `A`. If the value is absent, the `Option<A>` is the object `None`.
 *
 * ```kotlin
 * import arrow.core.*
 *
 * //sampleStart
 * val someValue: Option<String> = Some("I am wrapped in something")
 * val emptyValue: Option<String> = none()
 * //sampleEnd
 * fun main() {
 *  println("value = $someValue")
 *  println("emptyValue = $emptyValue")
 * }
 * ```
 * <!--- KNIT example-option-01.kt -->
 *
 * Let's write a function that may or may not give us a string, thus returning `Option<String>`:
 *
 * ```kotlin
 * import arrow.core.*
 *
 * //sampleStart
 * fun maybeItWillReturnSomething(flag: Boolean): Option<String> =
 *  if (flag) Some("Found value") else None
 * //sampleEnd
 * ```
 * <!--- KNIT example-option-02.kt -->
 *
 * Using `getOrElse`, we can provide a default value `"No value"` when the optional argument `None` does not exist:
 *
 * ```kotlin
 * import arrow.core.*
 *
 * fun maybeItWillReturnSomething(flag: Boolean): Option<String> =
 *  if (flag) Some("Found value") else None
 *
 * val value1 =
 * //sampleStart
 *  maybeItWillReturnSomething(true)
 *     .getOrElse { "No value" }
 * //sampleEnd
 * fun main() {
 *  println(value1)
 * }
 * ```
 * <!--- KNIT example-option-03.kt -->
 *
 * ```kotlin
 * import arrow.core.*
 *
 * fun maybeItWillReturnSomething(flag: Boolean): Option<String> =
 *  if (flag) Some("Found value") else None
 *
 * val value2 =
 * //sampleStart
 *  maybeItWillReturnSomething(false)
 *   .getOrElse { "No value" }
 * //sampleEnd
 * fun main() {
 *  println(value2)
 * }
 * ```
 * <!--- KNIT example-option-04.kt -->
 *
 * Checking whether option has value:
 *
 * ```kotlin
 * import arrow.core.*
 *
 * fun maybeItWillReturnSomething(flag: Boolean): Option<String> =
 *  if (flag) Some("Found value") else None
 *
 *  //sampleStart
 * val valueSome = maybeItWillReturnSomething(true).isEmpty()
 * val valueNone = maybeItWillReturnSomething(false).isEmpty()
 * //sampleEnd
 * fun main() {
 *  println("valueSome = $valueSome")
 *  println("valueNone = $valueNone")
 * }
 * ```
 * <!--- KNIT example-option-05.kt -->
 * Creating a `Option<T>` of a `T?`. Useful for working with values that can be nullable:
 *
 * ```kotlin
 * import arrow.core.*
 *
 * //sampleStart
 * val myString: String? = "Nullable string"
 * val option: Option<String> = Option.fromNullable(myString)
 * //sampleEnd
 * fun main () {
 *  println("option = $option")
 * }
 * ```
 * <!--- KNIT example-option-06.kt -->
 *
 * Option can also be used with when statements:
 *
 * ```kotlin
 * import arrow.core.*
 *
 * //sampleStart
 * val someValue: Option<Double> = Some(20.0)
 * val value = someValue.fold(
 *  { 0.0 },
 *  { it }
 * )
 * //sampleEnd
 * fun main () {
 *  println("value = $value")
 * }
 * ```
 * <!--- KNIT example-option-07.kt -->
 *
 * ```kotlin
 * import arrow.core.*
 *
 * //sampleStart
 * val noValue: Option<Double> = None
 * val value = noValue.fold(
 *  { 0.0 },
 *  { it }
 * )
 * //sampleEnd
 * fun main () {
 *  println("value = $value")
 * }
 * ```
 * <!--- KNIT example-option-08.kt -->
 *
 * An alternative for pattern matching is folding. This is possible because an option could be looked at as a collection or foldable structure with either one or zero elements.
 *
 * One of these operations is `map`. This operation allows us to map the inner value to a different type while preserving the option:
 *
 * ```kotlin
 * import arrow.core.*
 *
 * //sampleStart
 * val number: Option<Int> = Some(3)
 * val noNumber: Option<Int> = None
 * val mappedResult1 = number.map { it * 1.5 }
 * val mappedResult2 = noNumber.map { it * 1.5 }
 * //sampleEnd
 * fun main () {
 *  println("number = $number")
 *  println("noNumber = $noNumber")
 *  println("mappedResult1 = $mappedResult1")
 *  println("mappedResult2 = $mappedResult2")
 * }
 * ```
 * <!--- KNIT example-option-09.kt -->
 * Another operation is `fold`. This operation will extract the value from the option, or provide a default if the value is `None`
 *
 * ```kotlin
 * import arrow.core.*
 *
 * val fold =
 * //sampleStart
 *  Some(3).fold({ 1 }, { it * 3 })
 * //sampleEnd
 * fun main () {
 *  println(fold)
 * }
 * ```
 * <!--- KNIT example-option-10.kt -->
 *
 * ```kotlin
 * import arrow.core.Option
 * import arrow.core.none
 * import arrow.core.fold
 *
 * val fold =
 * //sampleStart
 *  none<Int>().fold({ 1 }, { it * 3 })
 * //sampleEnd
 * fun main () {
 *  println(fold)
 * }
 * ```
 * <!--- KNIT example-option-11.kt -->
 *
 * Arrow also adds syntax to all datatypes so you can easily lift them into the context of `Option` where needed.
 *
 * ```kotlin
 * import arrow.core.*
 *
 * //sampleStart
 *  val some = 1.some()
 *  val none = none<String>()
 * //sampleEnd
 * fun main () {
 *  println("some = $some")
 *  println("none = $none")
 * }
 * ```
 * <!--- KNIT example-option-12.kt -->
 *
 * ```kotlin
 * import arrow.core.*
 *
 * //sampleStart
 * val nullString: String? = null
 * val valueFromNull = nullString.toOption()
 *
 * val helloString: String? = "Hello"
 * val valueFromStr = helloString.toOption()
 * //sampleEnd
 * fun main () {
 *  println("valueFromNull = $valueFromNull")
 *  println("valueFromStr = $valueFromStr")
 * }
 * ```
 * <!--- KNIT example-option-13.kt -->
 *
 * You can easily convert between `A?` and `Option<A>` by using the `toOption()` extension or `Option.fromNullable` constructor.
 *
 * ```kotlin
 * import arrow.core.*
 *
 * //sampleStart
 * val foxMap = mapOf(1 to "The", 2 to "Quick", 3 to "Brown", 4 to "Fox")
 *
 * val empty = foxMap.entries.firstOrNull { it.key == 5 }?.value.let { it?.toCharArray() }.toOption()
 * val filled = Option.fromNullable(foxMap.entries.firstOrNull { it.key == 5 }?.value.let { it?.toCharArray() })
 *
 * //sampleEnd
 * fun main() {
 *  println("empty = $empty")
 *  println("filled = $filled")
 * }
 * ```
 * <!--- KNIT example-option-14.kt -->
 *
 * ### Transforming the inner contents
 *
 * ```kotlin
 * import arrow.core.*
 *
 * fun main() {
 * val value =
 *  //sampleStart
 *    Some(1).map { it + 1 }
 *  //sampleEnd
 *  println(value)
 * }
 * ```
 * <!--- KNIT example-option-15.kt -->
 *
 * ### Computing over independent values
 *
 * ```kotlin
 * import arrow.core.*
 *
 *  val value =
 * //sampleStart
 *  Some(1).zip(Some("Hello"), Some(20.0), ::Triple)
 * //sampleEnd
 * fun main() {
 *  println(value)
 * }
 * ```
 * <!--- KNIT example-option-16.kt -->
 *
 * ## Credits
 *
 * Contents partially adapted from [Scala Exercises Option Tutorial](https://www.scala-exercises.org/std_lib/options)
 * Originally based on the Scala Koans.
 */
@JvmInline
public value class Option<out A> @OptionInternals private constructor(@property:OptionInternals @PublishedApi internal val underlying: Any) {

  public companion object {

    // This is simply an alias to constructor-impl
    @PublishedApi
    @OptionInternals
    internal fun <A> construct(value: Any): Option<A> = Option(value)

    @JvmStatic
    public inline fun <reified A> fromNullable(a: A?): Option<A> = if (a != null) Some(a) else None

    @JvmStatic
    public inline operator fun <reified A> invoke(a: A): Option<A> = Some(a)

    @JvmStatic
    @JvmName("tryCatchOrNone")
    /**
     * Ignores exceptions and returns None if one is thrown
     */
    public inline fun <reified A> catch(f: () -> A): Option<A> {
      contract { callsInPlace(f, InvocationKind.AT_MOST_ONCE) }
      val recover: (Throwable) -> Option<A> = { None }
      return catch(recover, f)
    }

    @JvmStatic
    @JvmName("tryCatch")
    public inline fun <reified A> catch(recover: (Throwable) -> Option<A>, f: () -> A): Option<A> {
      contract {
        callsInPlace(f, InvocationKind.EXACTLY_ONCE)
        callsInPlace(recover, InvocationKind.AT_MOST_ONCE)
      }
      return try {
        Some(f())
      } catch (t: Throwable) {
        recover(t.nonFatalOrThrow())
      }
    }

    @JvmStatic
    public inline fun <reified A, reified B> lift(crossinline f: (A) -> B): (Option<A>) -> Option<B> =
      { it.map(f) }
  }

  override fun toString(): String = fold<Any?, String>(
    { "Option.None" },
    { "Option.Some($it)" }
  )
}

@PublishedApi
internal inline fun <reified T> isTypeOption(): Boolean =
  boxedSomeUnit is T && Unit !is T // Checks that a known Option value inhertis from that type but that a regular Any value doesn't

@PublishedApi
@OptionInternals
internal val <A> Option<A>.value: A
  get() =
    when (underlying) {
      is NestedNull -> if (underlying.nesting == 0U) null else NestedNull(underlying.nesting - 1U)
      is NestedNone ->
        if (underlying.nesting == 0U) error("Cannot unpack the value of an Option.None")
        else NestedNone(underlying.nesting - 1U)

      else -> underlying
    } as A

public inline fun <reified A, reified B> Option<A>.zip(other: Option<B>): Option<Pair<A, B>> =
  zip(other, ::Pair)

public inline fun <reified A, reified B, reified C> Option<A>.zip(
  b: Option<B>,
  map: (A, B) -> C
): Option<C> {
  contract { callsInPlace(map, InvocationKind.AT_MOST_ONCE) }
  return zip(
    b,
    someUnit,
    someUnit,
    someUnit,
    someUnit,
    someUnit,
    someUnit,
    someUnit,
    someUnit
  ) { b, c, _, _, _, _, _, _, _, _ -> map(b, c) }
}

public inline fun <reified A, reified B, reified C, reified D> Option<A>.zip(
  b: Option<B>,
  c: Option<C>,
  map: (A, B, C) -> D
): Option<D> {
  contract { callsInPlace(map, InvocationKind.AT_MOST_ONCE) }
  return zip(
    b,
    c,
    someUnit,
    someUnit,
    someUnit,
    someUnit,
    someUnit,
    someUnit,
    someUnit
  ) { b, c, d, _, _, _, _, _, _, _ -> map(b, c, d) }
}

/**
 * The given function is applied as a fire and forget effect
 * if this is a `None`.
 * When applied the result is ignored and the original
 * None value is returned
 *
 * Example:
 * ```kotlin
 * import arrow.core.Some
 * import arrow.core.none
 * import arrow.core.tapNone
 *
 * fun main() {
 *   Some(12).tapNone { println("flower") } // Result: Some(12)
 *   none<Int>().tapNone { println("flower") }  // Result: prints "flower" and returns: None
 * }
 * ```
 * <!--- KNIT example-option-17.kt -->
 */
public inline fun <A> Option<A>.tapNone(f: () -> Unit): Option<A> {
  contract { callsInPlace(f, InvocationKind.AT_MOST_ONCE) }
  if (this == None) f()
  return this
}

/**
 * The given function is applied as a fire and forget effect
 * if this is a `some`.
 * When applied the result is ignored and the original
 * Some value is returned
 *
 * Example:
 * ```kotlin
 * import arrow.core.Some
 * import arrow.core.none
 * import arrow.core.tap
 *
 * fun main() {
 *   Some(12).tap { println("flower") } // Result: prints "flower" and returns: Some(12)
 *   none<Int>().tap { println("flower") }  // Result: None
 * }
 * ```
 * <!--- KNIT example-option-18.kt -->
 */
public inline fun <reified A> Option<A>.tap(f: (A) -> Unit): Option<A> {
  contract { callsInPlace(f, InvocationKind.AT_MOST_ONCE) }
  return fold(
    {
      this
    },
    { value ->
      f(value)
      this
    }
  )
}

public inline fun <reified A, reified B, reified C, reified D, reified E> Option<A>.zip(
  b: Option<B>,
  c: Option<C>,
  d: Option<D>,
  map: (A, B, C, D) -> E
): Option<E> {
  contract { callsInPlace(map, InvocationKind.AT_MOST_ONCE) }
  return zip(
    b,
    c,
    d,
    someUnit,
    someUnit,
    someUnit,
    someUnit,
    someUnit,
    someUnit
  ) { a, b, c, d, _, _, _, _, _, _ -> map(a, b, c, d) }
}

public inline fun <reified A, reified B, reified C, reified D, reified E, reified F> Option<A>.zip(
  b: Option<B>,
  c: Option<C>,
  d: Option<D>,
  e: Option<E>,
  map: (A, B, C, D, E) -> F
): Option<F> {
  contract { callsInPlace(map, InvocationKind.AT_MOST_ONCE) }
  return zip(b, c, d, e, someUnit, someUnit, someUnit, someUnit, someUnit) { a, b, c, d, e, f, _, _, _, _ ->
    map(
      a,
      b,
      c,
      d,
      e
    )
  }
}

public inline fun <reified A, reified B, reified C, reified D, reified E, reified F, reified G> Option<A>.zip(
  b: Option<B>,
  c: Option<C>,
  d: Option<D>,
  e: Option<E>,
  f: Option<F>,
  map: (A, B, C, D, E, F) -> G
): Option<G> {
  contract { callsInPlace(map, InvocationKind.AT_MOST_ONCE) }
  return zip(b, c, d, e, f, someUnit, someUnit, someUnit, someUnit) { a, b, c, d, e, f, _, _, _, _ ->
    map(
      a,
      b,
      c,
      d,
      e,
      f
    )
  }
}

public inline fun <reified A, reified B, reified C, reified D, reified E, reified F, reified G, reified H> Option<A>.zip(
  b: Option<B>,
  c: Option<C>,
  d: Option<D>,
  e: Option<E>,
  f: Option<F>,
  g: Option<G>,
  map: (A, B, C, D, E, F, G) -> H
): Option<H> {
  contract { callsInPlace(map, InvocationKind.AT_MOST_ONCE) }
  return zip(b, c, d, e, f, g, someUnit, someUnit, someUnit) { a, b, c, d, e, f, g, _, _, _ ->
    map(
      a,
      b,
      c,
      d,
      e,
      f,
      g
    )
  }
}

public inline fun <reified A, reified B, reified C, reified D, reified E, reified F, reified G, reified H, reified I> Option<A>.zip(
  b: Option<B>,
  c: Option<C>,
  d: Option<D>,
  e: Option<E>,
  f: Option<F>,
  g: Option<G>,
  h: Option<H>,
  map: (A, B, C, D, E, F, G, H) -> I
): Option<I> {
  contract { callsInPlace(map, InvocationKind.AT_MOST_ONCE) }
  return zip(b, c, d, e, f, g, h, someUnit, someUnit) { a, b, c, d, e, f, g, h, _, _ ->
    map(
      a,
      b,
      c,
      d,
      e,
      f,
      g,
      h
    )
  }
}

public inline fun <reified A, reified B, reified C, reified D, reified E, reified F, reified G, reified H, reified I, reified J> Option<A>.zip(
  b: Option<B>,
  c: Option<C>,
  d: Option<D>,
  e: Option<E>,
  f: Option<F>,
  g: Option<G>,
  h: Option<H>,
  i: Option<I>,
  map: (A, B, C, D, E, F, G, H, I) -> J
): Option<J> {
  contract { callsInPlace(map, InvocationKind.AT_MOST_ONCE) }
  return zip(b, c, d, e, f, g, h, i, someUnit) { a, b, c, d, e, f, g, h, i, _ -> map(a, b, c, d, e, f, g, h, i) }
}

public inline fun <reified A, reified B, reified C, reified D, reified E, reified F, reified G, reified H, reified I, reified J, reified K> Option<A>.zip(
  b: Option<B>,
  c: Option<C>,
  d: Option<D>,
  e: Option<E>,
  f: Option<F>,
  g: Option<G>,
  h: Option<H>,
  i: Option<I>,
  j: Option<J>,
  map: (A, B, C, D, E, F, G, H, I, J) -> K
): Option<K> {
  contract { callsInPlace(map, InvocationKind.AT_MOST_ONCE) }
  val a =
    this.getOrElse {
      return None
    }
  val b =
    b.getOrElse {
      return None
    }
  val c =
    c.getOrElse {
      return None
    }
  val d =
    d.getOrElse {
      return None
    }
  val e =
    e.getOrElse {
      return None
    }
  val f =
    f.getOrElse {
      return None
    }
  val g =
    g.getOrElse {
      return None
    }
  val h =
    h.getOrElse {
      return None
    }
  val i =
    i.getOrElse {
      return None
    }
  val j =
    j.getOrElse {
      return None
    }
  return Some(map(a, b, c, d, e, f, g, h, i, j))
}

/**
 * Returns true if the option is [None], false otherwise.
 * @note Used only for performance instead of fold.
 */
public fun <A> Option<A>.isEmpty(): Boolean = this == None

public fun <A> Option<A>.isNotEmpty(): Boolean = !isEmpty()

/**
 * alias for [isDefined]
 */
public fun <A> Option<A>.nonEmpty(): Boolean = isDefined()

/**
 * Returns true if the option is an instance of [Some], false otherwise.
 * @note Used only for performance instead of fold.
 */
public fun <A> Option<A>.isDefined(): Boolean = !isEmpty()

public inline fun <reified A> Option<A>.orNull(): A? {
  return fold({ null }, ::identity)
}

/**
 * Returns a [Some<$B>] containing the result of applying $f to this $option's
 * value if this $option is nonempty. Otherwise return $none.
 *
 * @note This is similar to `flatMap` except here,
 * $f does not need to wrap its result in an $option.
 *
 * @param f the function to apply
 * @see flatMap
 */
public inline fun <reified A, reified B> Option<A>.map(f: (A) -> B): Option<B> {
  contract { callsInPlace(f, InvocationKind.AT_MOST_ONCE) }
  return flatMap { a -> Some(f(a)) }
}

@OptIn(OptionInternals::class)
public inline fun <reified A, R> Option<A>.fold(ifEmpty: () -> R, ifSome: (A) -> R): R {
  contract {
    callsInPlace(ifEmpty, InvocationKind.AT_MOST_ONCE)
    callsInPlace(ifSome, InvocationKind.AT_MOST_ONCE)
  }
  return when (this) {
    None -> ifEmpty()
    else -> {
      val value = value
      if (isTypeOption<A>() && value != null) Option.construct<Any>(value).invokeBlockOnOption(ifSome)
      else ifSome(value)
    }
  }
}

/**
 * This function is used to ensure that an Option doesn't get boxed when it gets passed
 * to the $block. It unsafely casts it inside, but because it is inline, the compiler ultimately doesn't box
 * the Option.
 */
@PublishedApi
internal inline fun <R, A> Option<*>.invokeBlockOnOption(block: (A) -> R): R {
  return block(this as A)
}

/**
 * Returns $none if the result of applying $f to this $option's value is null.
 * Otherwise returns the result.
 *
 * @note This is similar to `.flatMap { Option.fromNullable(null)) }`
 * and primarily for convenience.
 *
 * @param f the function to apply.
 * */
public inline fun <reified A, reified B> Option<A>.mapNotNull(f: (A) -> B?): Option<B> {
  contract { callsInPlace(f, InvocationKind.AT_MOST_ONCE) }
  return flatMap { a -> fromNullable(f(a)) }
}

/**
 * Returns the result of applying $f to this $option's value if
 * this $option is nonempty.
 * Returns $none if this $option is empty.
 * Slightly different from `map` in that $f is expected to
 * return an $option (which could be $none).
 *
 * @param f the function to apply
 * @see map
 */
public inline fun <reified A, B> Option<A>.flatMap(f: (A) -> Option<B>): Option<B> {
  contract { callsInPlace(f, InvocationKind.AT_MOST_ONCE) }
  return fold({ None }, f)
}

/**
 * Align two options (`this` on the left and [b] on the right) as one Option of [Ior].
 */
public inline infix fun <reified A, reified B> Option<A>.align(b: Option<B>): Option<Ior<A, B>> =
  fold({
    b.fold({ None }) { Some(it.rightIor()) }
  },
    { a ->
      b.fold({
        Some(a.leftIor())
      },
        {
          Some(Pair(a, it).bothIor())
        })
    })

/**
 * Align two options (`this` on the left and [b] on the right) as one Option of [Ior], and then, if it's not [None], map it using [f].
 *
 * @note This function works like a regular `align` function, but is then mapped by the `map` function.
 */
public inline fun <reified A, reified B, reified C> Option<A>.align(b: Option<B>, f: (Ior<A, B>) -> C): Option<C> {
  contract { callsInPlace(f, InvocationKind.AT_MOST_ONCE) }
  return align(b).map(f)
}

/**
 * Returns true if this option is empty '''or''' the predicate
 * $predicate returns true when applied to this $option's value.
 *
 * @param predicate the predicate to test
 */
public inline fun <reified A> Option<A>.all(predicate: (A) -> Boolean): Boolean {
  contract { callsInPlace(predicate, InvocationKind.AT_MOST_ONCE) }
  return fold({ true }, predicate)
}

public inline fun <reified A, reified B> Option<A>.crosswalk(f: (A) -> Option<B>): Option<Option<B>> =
  fold({ None }, { f(it).map(::Some) })

public inline fun <reified A, K, reified V> Option<A>.crosswalkMap(f: (A) -> Map<K, V>): Map<K, Option<V>> =
  fold({ emptyMap() }) { value ->
    f(value).mapValues { Some(it.value) }
  }

public inline fun <reified A, reified B> Option<A>.crosswalkNull(f: (A) -> B?): Option<B>? =
  fold({ null }) {
    f(it)?.let(::Some)
  }

/**
 * Returns this $option if it is nonempty '''and''' applying the predicate $p to
 * this $option's value returns true. Otherwise, return $none.
 *
 *  @param predicate the predicate used for testing.
 */
public inline fun <reified A> Option<A>.filter(predicate: (A) -> Boolean): Option<A> {
  contract { callsInPlace(predicate, InvocationKind.AT_MOST_ONCE) }
  return flatMap { a -> if (predicate(a)) Some(a) else None }
}

/**
 * Returns this $option if it is nonempty '''and''' applying the predicate $p to
 * this $option's value returns false. Otherwise, return $none.
 *
 * @param predicate the predicate used for testing.
 */
public inline fun <reified A> Option<A>.filterNot(predicate: (A) -> Boolean): Option<A> {
  contract { callsInPlace(predicate, InvocationKind.AT_MOST_ONCE) }
  return flatMap { a -> if (!predicate(a)) Some(a) else None }
}

/**
 * Returns true if this option is nonempty '''and''' the predicate
 * $p returns true when applied to this $option's value.
 * Otherwise, returns false.
 *
 * Example:
 * ```kotlin
 * import arrow.core.Some
 * import arrow.core.None
 * import arrow.core.Option
 * import arrow.core.exists
 *
 * fun main() {
 *   Some(12).exists { it > 10 } // Result: true
 *   Some(7).exists { it > 10 }  // Result: false
 *
 *   val none: Option<Int> = None
 *   none.exists { it > 10 }      // Result: false
 * }
 * ```
 * <!--- KNIT example-option-19.kt -->
 *
 * @param predicate the predicate to test
 */
public inline fun <reified A> Option<A>.exists(predicate: (A) -> Boolean): Boolean {
  contract { callsInPlace(predicate, InvocationKind.AT_MOST_ONCE) }
  return fold({ false }, predicate)
}

/**
 * Returns the $option's value if this option is nonempty '''and''' the predicate
 * $p returns true when applied to this $option's value.
 * Otherwise, returns null.
 *
 * Example:
 * ```kotlin
 * import arrow.core.Some
 * import arrow.core.None
 * import arrow.core.Option
 * import arrow.core.exists
 *
 * fun main() {
 *   Some(12).exists { it > 10 } // Result: 12
 *   Some(7).exists { it > 10 }  // Result: null
 *
 *   val none: Option<Int> = None
 *   none.exists { it > 10 }      // Result: null
 * }
 * ```
 * <!--- KNIT example-option-20.kt -->
 */
public inline fun <reified A> Option<A>.findOrNull(predicate: (A) -> Boolean): A? {
  contract { callsInPlace(predicate, InvocationKind.AT_MOST_ONCE) }
  return fold({ null }) {
    it.takeIf(predicate)
  }
}

public inline fun <reified A, B> Option<A>.foldMap(MB: Monoid<B>, f: (A) -> B): B = MB.run {
  foldLeft(empty()) { b, a -> b.combine(f(a)) }
}

public inline fun <reified A, B> Option<A>.foldLeft(initial: B, operation: (B, A) -> B): B =
  fold({ initial }) {
    operation(initial, it)
  }

public inline fun <reified A, reified B> Option<A>.padZip(other: Option<B>): Option<Pair<A?, B?>> =
  align(other) { ior ->
    ior.fold(
      { it to null },
      { null to it },
      { a, b -> a to b }
    )
  }

public inline fun <reified A, reified B, reified C> Option<A>.padZip(other: Option<B>, f: (A?, B?) -> C): Option<C> =
  align(other) { ior ->
    ior.fold(
      { f(it, null) },
      { f(null, it) },
      { a, b -> f(a, b) }
    )
  }

public inline fun <reified A, B> Option<A>.reduceOrNull(initial: (A) -> B, operation: (acc: B, A) -> B): B? =
  fold({ null }) {
    operation(initial(it), it)
  }

public inline fun <reified A> Option<A>.replicate(n: Int): Option<List<A>> =
  if (n <= 0) Some(emptyList()) else map { a -> List(n) { a } }

public inline fun <reified A, L> Option<A>.toEither(ifEmpty: () -> L): Either<L, A> {
  contract { callsInPlace(ifEmpty, InvocationKind.AT_MOST_ONCE) }
  return fold({ ifEmpty().left() }, { it.right() })
}

public inline fun <reified A> Option<A>.toList(): List<A> = fold(::emptyList) { listOf(it) }

public fun <A> Option<A>.void(): Option<Unit> =
  someUnit

public inline fun <reified A, L> Option<A>.pairLeft(left: L): Option<Pair<L, A>> = this.map { left to it }

public inline fun <reified A, R> Option<A>.pairRight(right: R): Option<Pair<A, R>> = this.map { it to right }

public infix fun <A, X> Option<A>.and(value: Option<X>): Option<X> = if (isEmpty()) {
  None
} else {
  value
}

@OptIn(OptionInternals::class)
public val None: Option<Nothing> = Option.construct(NestedNone(0U))

@PublishedApi
internal data class NestedNone internal constructor(val nesting: UInt) {
  companion object {
    private val cache = Array(22) { NestedNone(it.toUInt()) }
    operator fun invoke(nesting: UInt) =
      cache.getOrElse(nesting.toInt()) { NestedNone(it.toUInt()) }
  }

  override fun toString(): String =
    nesting.toInt().coerceAtLeast(0).let { nesting ->
      buildString("Option.Some()".length * nesting + "Option.None".length) {
        repeat(nesting) { this.append("Option.Some(") }
        append("Option.None")
        repeat(nesting) { this.append(")") }
      }
    }
}

@PublishedApi
internal data class NestedNull internal constructor(val nesting: UInt) {
  companion object {
    private val cache = Array(22) { NestedNull(it.toUInt()) }
    operator fun invoke(nesting: UInt) =
      cache.getOrElse(nesting.toInt()) { NestedNull(it.toUInt()) }
  }

  override fun toString(): String =
    nesting.toInt().coerceAtLeast(0).let { nesting ->
      buildString("Option.Some()".length * nesting + "null".length) {
        repeat(nesting) { this.append("Option.Some(") }
        append("null")
        repeat(nesting) { this.append(")") }
      }
    }
}

@OptIn(OptionInternals::class)
public inline fun <reified T> Some(value: T): Option<T> {
  // This `is` check gets inlined by the compiler if `value` is statically known to be an Option
  val trueValue = if (value is Option<*>) value.underlying else value
  return _Some(if (isTypeOption<T>()) trueValue else value)
}

@OptionInternals
@PublishedApi
internal fun <T> _Some(trueValue: Any?): Option<T> =
  Option.construct(
    when (trueValue) {
      is NestedNone -> NestedNone(trueValue.nesting + 1U)
      is NestedNull -> NestedNull(trueValue.nesting + 1U)
      else -> trueValue ?: NestedNull(0U)
    }
  )

@PublishedApi
internal val someUnit: Option<Unit> = Some(Unit)

@PublishedApi
internal val boxedSomeUnit: Any = someUnit


/**
 * Returns the option's value if the option is nonempty, otherwise
 * return the result of evaluating `default`.
 *
 * @param default the default expression.
 */
public inline fun <reified T> Option<T>.getOrElse(default: () -> T): T {
  contract { callsInPlace(default, InvocationKind.AT_MOST_ONCE) }
  return fold({ default() }, ::identity)
}

/**
 * Returns the option's value if the option is nonempty, otherwise
 * return the result of evaluating `default`.
 *
 * @param default the default expression.
 */
@JvmName("getOrElseOption")
public inline fun <reified T> Option<Option<T>>.getOrElse(default: () -> Option<T>): Option<T> {
  contract { callsInPlace(default, InvocationKind.AT_MOST_ONCE) }
  return fold({ default() }, ::identity)
}

/**
 * Returns this option's if the option is nonempty, otherwise
 * returns another option provided lazily by `default`.
 *
 * @param alternative the default option if this is empty.
 */
public inline fun <A> Option<A>.orElse(alternative: () -> Option<A>): Option<A> {
  contract { callsInPlace(alternative, InvocationKind.AT_MOST_ONCE) }
  return if (isEmpty()) alternative() else this
}

public infix fun <T> Option<T>.or(value: Option<T>): Option<T> = if (isEmpty()) {
  value
} else {
  this
}

public inline fun <reified T> T?.toOption(): Option<T> = this?.let { Some(it) } ?: None

public inline fun <reified A> Boolean.maybe(f: () -> A): Option<A> {
  contract { callsInPlace(f, InvocationKind.AT_MOST_ONCE) }
  return if (this) {
    Some(f())
  } else {
    None
  }
}

public inline fun <reified A> A.some(): Option<A> = Some(this)

public fun <A> none(): Option<A> = None

@Deprecated("use fold instead", ReplaceWith("fold(Monoid.option(MA))", "arrow.core.fold", "arrow.typeclasses.Monoid"))
public fun <A> Iterable<Option<A>>.combineAll(MA: Monoid<A>): Option<A> =
  fold(Monoid.option(MA))

@Deprecated("use getOrElse instead", ReplaceWith("getOrElse { MA.empty() }"))
public inline fun <reified A> Option<A>.combineAll(MA: Monoid<A>): A =
  getOrElse { MA.empty() }

public inline fun <reified A> Option<A>.ensure(error: () -> Unit, predicate: (A) -> Boolean): Option<A> {
  contract {
    callsInPlace(predicate, InvocationKind.AT_MOST_ONCE)
    callsInPlace(error, InvocationKind.AT_MOST_ONCE)
  }
  return fold({ None }) {
    if (predicate(it)) this
    else {
      error()
      None
    }
  }
}

/**
 * Returns an Option containing all elements that are instances of specified type parameter [B].
 */
public inline fun <reified B> Option<*>.filterIsInstance(): Option<B> =
  flatMap {
    when (it) {
      is B -> Some(it)
      else -> None
    }
  }

public inline fun <reified A> Option<A>.handleError(f: (Unit) -> A): Option<A> {
  contract { callsInPlace(f, InvocationKind.AT_MOST_ONCE) }
  return handleErrorWith { Some(f(Unit)) }
}

public inline fun <A> Option<A>.handleErrorWith(f: (Unit) -> Option<A>): Option<A> {
  contract { callsInPlace(f, InvocationKind.AT_MOST_ONCE) }
  return if (isEmpty()) f(Unit) else this
}

public fun <A> Option<Option<A>>.flatten(): Option<A> =
  flatMap(::identity)

public inline fun <reified A, reified B> Option<A>.redeem(fe: (Unit) -> B, fb: (A) -> B): Option<B> {
  contract {
    callsInPlace(fe, InvocationKind.AT_MOST_ONCE)
    callsInPlace(fb, InvocationKind.AT_MOST_ONCE)
  }
  return map(fb).handleError(fe)
}

public inline fun <reified A, B> Option<A>.redeemWith(fe: (Unit) -> Option<B>, fb: (A) -> Option<B>): Option<B> {
  contract {
    callsInPlace(fe, InvocationKind.AT_MOST_ONCE)
    callsInPlace(fb, InvocationKind.AT_MOST_ONCE)
  }
  return flatMap(fb).handleErrorWith(fe)
}

public inline fun <reified A> Option<A>.replicate(n: Int, MA: Monoid<A>): Option<A> = MA.run {
  if (n <= 0) Some(empty())
  else map { a -> List(n) { a }.fold(empty()) { acc, v -> acc + v } }
}

public inline fun <reified A> Option<Either<Unit, A>>.rethrow(): Option<A> =
  flatMap { it.fold({ None }, { a -> Some(a) }) }

public inline fun <reified A> Option<A>.salign(SA: Semigroup<A>, b: Option<A>): Option<A> =
  align(b) {
    it.fold(::identity, ::identity) { a, b ->
      SA.run { a.combine(b) }
    }
  }

/**
 * Separate the inner [Either] value into the [Either.Left] and [Either.Right].
 *
 * @receiver Option of Either
 * @return a tuple containing Option of [Either.Left] and another Option of its [Either.Right] value.
 */
public inline fun <reified A, reified B> Option<Either<A, B>>.separateEither(): Pair<Option<A>, Option<B>> {
  val asep = flatMap { gab -> gab.fold({ Some(it) }, { None }) }
  val bsep = flatMap { gab -> gab.fold({ None }, { Some(it) }) }
  return asep to bsep
}

public inline fun <reified A, reified B> Option<Ior<A, B>>.unalign(): Pair<Option<A>, Option<B>> =
  unalign(::identity)

public inline fun <reified A, reified B, reified C> Option<C>.unalign(f: (C) -> Ior<A, B>): Pair<Option<A>, Option<B>> =
  map(f).fold({ None to None }) {
    when (val v = it) {
      is Ior.Left -> Some(v.value) to None
      is Ior.Right -> None to Some(v.value)
      is Ior.Both -> Some(v.leftValue) to Some(v.rightValue)
    }
  }

public inline fun <reified A> Option<Iterable<A>>.unite(MA: Monoid<A>): Option<A> =
  map { iterable ->
    iterable.fold(MA)
  }

public inline fun <A, reified B> Option<Either<A, B>>.uniteEither(): Option<B> =
  flatMap { either ->
    either.fold({ None }, { b -> Some(b) })
  }

public inline fun <reified A, reified B> Option<Pair<A, B>>.unzip(): Pair<Option<A>, Option<B>> =
  unzip(::identity)

public inline fun <reified A, reified B, reified C> Option<C>.unzip(f: (C) -> Pair<A, B>): Pair<Option<A>, Option<B>> =
  fold(
    { None to None },
    { f(it).let { pair -> Some(pair.first) to Some(pair.second) } }
  )

/**
 *  Given [A] is a sub type of [B], re-type this value from Option<A> to Option<B>
 *
 *  Option<A> -> Option<B>
 *
 *  ```kotlin
 *  import arrow.core.Option
 *  import arrow.core.some
 *  import arrow.core.widen
 *
 *  fun main(args: Array<String>) {
 *   val result: Option<CharSequence> =
 *   //sampleStart
 *   "Hello".some().map({ "$it World" }).widen()
 *   //sampleEnd
 *   println(result)
 *  }
 *  ```
 * <!--- KNIT example-option-21.kt -->
 */
public fun <B, A : B> Option<A>.widen(): Option<B> =
  this

public fun <K, V> Option<Pair<K, V>>.toMap(): Map<K, V> = this.toList().toMap()

public inline fun <reified A> Option<A>.combine(SGA: Semigroup<A>, b: Option<A>): Option<A> =
  fold({ b }) { a ->
    b.fold({ this }) { b ->
      SGA.combineToOption(a, b)
    }
  }

// The purpose of this function is that, within the implementation of OptionSemigroup/Monoid
// We can't know whether A inside the Option is meant to be a Option<Option<*>>. The impl just casts the Semigroup<A> to
// a Semigroup<Any?> and lets nature take its course. If the values are  meant to be a nested Option, then the impl would fail
// because it would pass the raw value of the nested Option instead of a boxed value. We deal with this here in the else branch
// by ensuring that we coerce our values to be an Option if $this is an OptionSemigroup/Monoid. Also, in the if branch
// we have some minor optimisations to ensure that first and second aren't boxed if they're intended to be passed to OS/OM
@OptIn(OptionInternals::class)
@Suppress("UNCHECKED_CAST")
@PublishedApi
internal inline fun <reified A> Semigroup<A>.combineToOption(first: A, second: A): Option<A> =
  if (isTypeOption<A>() && null !is A) {
    val first = first as Option<*>
    val second = second as Option<*>
    // It's important here to only cast the value as A when it's inside the option, otherwise we get unnecessary boxing
    when {
      this is OptionMonoid<*> ->
        Some((this as OptionMonoid<Any?>).append(first, second)) as Option<A>

      this is OptionSemigroup<*> ->
        Some((this as OptionSemigroup<Any?>).append(first, second)) as Option<A>

      else ->
        Some((this as Semigroup<Option<*>>).append(first.rebox(), second.rebox()) as A)
    }
  } else {
    when (this) {
      is OptionMonoid<*> -> {
        val first = if (first is Option<*>) first else Option.construct<Any>(first!!)
        val second = if (second is Option<*>) second else Option.construct<Any>(second!!)
        Some((this as OptionMonoid<Any?>).append(first, second)) as Option<A>
      }

      is OptionSemigroup<*> -> {
        val first = if (first is Option<*>) first else Option.construct<Any>(first!!)
        val second = if (second is Option<*>) second else Option.construct<Any>(second!!)
        Some((this as OptionSemigroup<Any?>).append(first, second)) as Option<A>
      }
      else -> Some(first.combine(second))
    }
  }

/**
 * Purposefully NOT inline because, as the name suggests, this will cause the [this] to be reboxed,
 * which convinces the compiler that [this] Maybe is not being used in an Any or generic context
 */
@PublishedApi
internal fun <A> Option<A>.rebox(): Option<A> = this

public inline operator fun <reified A : Comparable<A>> Option<A>.compareTo(other: Option<A>): Int = fold(
  { other.fold({ 0 }, { -1 }) },
  { a1 ->
    other.fold({ 1 }, { a2 -> a1.compareTo(a2) })
  }
)

/**
 * Recover from any [None] if encountered.
 *
 * The recover DSL allows you to recover from any [None] value by:
 *  - Computing a fallback value [A]
 *  - Shifting a _new error_ of [None] into the [Option].
 *
 * ```kotlin
 * import arrow.core.Option
 * import arrow.core.none
 * import arrow.core.Some
 * import arrow.core.recover
 * import io.kotest.matchers.shouldBe
 *
 * fun test() {
 *   val error: Option<Int> = none()
 *   val fallback: Option<Int> = error.recover { 5 }
 *   fallback shouldBe Some(5)
 * }
 * ```
 * <!--- KNIT example-option-22.kt -->
 * <!--- TEST lines.isEmpty() -->
 *
 * When shifting a new error [None] into the [Option]:
 *
 * ```kotlin
 * import arrow.core.Option
 * import arrow.core.none
 * import arrow.core.Some
 * import arrow.core.recover
 * import io.kotest.matchers.shouldBe
 *
 * fun test() {
 *   val error: Option<Int> = none()
 *   fun fallback(): Option<Int> = Some(5)
 *   fun failure(): Option<Int> = none()
 *
 *   error.recover { fallback().bind() } shouldBe Some(5)
 *   error.recover { failure().bind() } shouldBe none()
 * }
 * ```
 * <!--- KNIT example-option-23.kt -->
 * <!--- TEST lines.isEmpty() -->
 */
public inline fun <reified A> Option<A>.recover(recover: OptionRaise.(Option<Nothing>) -> A): Option<A> =
  fold({ option { recover(this, None) } }) { this }
