package arrow.core

import arrow.higherkind

/**
 * Represents optional values. Instances of `Option`
 * are either an instance of $some or the object $none.
 */
@higherkind
sealed class Option<out A> : OptionOf<A> {

  companion object {

    /**
     * Lifts a pure [A] value to [Option]
     *
     * {: data-executable='true'}
     *
     * ```kotlin:ank
     * import arrow.core.Option
     * fun main(args: Array<String>) {
     * //sampleStart
     * val result: Option<Int> = Option.just(1)
     * //sampleEnd
     * println(result)
     * }
     * ```
     *
     */
    fun <A> just(a: A): Option<A> = Some(a)

    tailrec fun <A, B> tailRecM(a: A, f: (A) -> OptionOf<Either<A, B>>): Option<B> {
      val option = f(a).fix()
      return when (option) {
        is Some -> {
          when (option.t) {
            is Either.Left -> tailRecM(option.t.a, f)
            is Either.Right -> Some(option.t.b)
          }
        }
        is None -> None
      }
    }

    fun <A> fromNullable(a: A?): Option<A> = if (a != null) Some(a) else None

    operator fun <A> invoke(a: A): Option<A> = Some(a)

    fun <A> empty(): Option<A> = None

  }

  /**
   * Returns true if the option is [None], false otherwise.
   * @note Used only for performance instead of fold.
   */
  abstract fun isEmpty(): Boolean

  /**
   * alias for [isDefined]
   */
  fun nonEmpty(): Boolean = isDefined()

  /**
   * Returns true if the option is an instance of [Some], false otherwise.
   * @note Used only for performance instead of fold.
   */
  fun isDefined(): Boolean = !isEmpty()

  fun orNull(): A? = fold({ null }, ::identity)

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
  inline fun <B> map(f: (A) -> B): Option<B> =
    flatMap { a -> Some(f(a)) }

  fun <B> mapFilter(f: (A) -> Option<B>): Option<B> =
    flatMap { a -> f(a).fold({ empty<B>() }, { just(it) }) }

  inline fun <R> fold(ifEmpty: () -> R, ifSome: (A) -> R): R = when (this) {
    is None -> ifEmpty()
    is Some<A> -> ifSome(t)
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
  inline fun <B> flatMap(f: (A) -> OptionOf<B>): Option<B> =
    when (this) {
      is None -> this
      is Some -> f(t).fix()
    }

  fun <B> ap(ff: OptionOf<(A) -> B>): Option<B> =
    ff.fix().flatMap { this.fix().map(it) }

  /**
   * Returns this $option if it is nonempty '''and''' applying the predicate $p to
   * this $option's value returns true. Otherwise, return $none.
   *
   *  @param predicate the predicate used for testing.
   */
  fun filter(predicate: Predicate<A>): Option<A> =
    flatMap { a -> if (predicate(a)) Some(a) else None }

  /**
   * Returns this $option if it is nonempty '''and''' applying the predicate $p to
   * this $option's value returns false. Otherwise, return $none.
   *
   * @param predicate the predicate used for testing.
   */
  fun filterNot(predicate: Predicate<A>): Option<A> =
    flatMap { a -> if (!predicate(a)) Some(a) else None }

  /**
   * Returns true if this option is nonempty '''and''' the predicate
   * $p returns true when applied to this $option's value.
   * Otherwise, returns false.
   *
   * @param predicate the predicate to test
   */
  fun exists(predicate: Predicate<A>): Boolean = fold({ false }, { a -> predicate(a) })

  /**
   * Returns true if this option is empty '''or''' the predicate
   * $p returns true when applied to this $option's value.
   *
   * @param p the predicate to test
   */
  fun forall(p: Predicate<A>): Boolean = fold({ true }, p)

  fun <B> foldLeft(initial: B, operation: (B, A) -> B): B =
    fix().let { option ->
      when (option) {
        is Some -> operation(initial, option.t)
        is None -> initial
      }
    }

  fun <B> foldRight(initial: Eval<B>, operation: (A, Eval<B>) -> Eval<B>): Eval<B> =
    fix().let { option ->
      when (option) {
        is Some -> operation(option.t, initial)
        is None -> initial
      }
    }

  fun <L> toEither(ifEmpty: () -> L): Either<L, A> =
    fold({ ifEmpty().left() }, { it.right() })

  fun toList(): List<A> = fold(::emptyList) { listOf(it) }

  infix fun <X> and(value: Option<X>): Option<X> = if (isEmpty()) {
    None
  } else {
    value
  }
}

object None : Option<Nothing>() {
  override fun isEmpty() = true

  override fun toString(): String = "None"
}

data class Some<out T>(val t: T) : Option<T>() {
  override fun isEmpty() = false

  override fun toString(): String = "Some($t)"
}

/**
 * Returns the option's value if the option is nonempty, otherwise
 * return the result of evaluating `default`.
 *
 * @param default the default expression.
 */
fun <T> Option<T>.getOrElse(default: () -> T): T = fold({ default() }, ::identity)

/**
 * Returns this option's if the option is nonempty, otherwise
 * returns another option provided lazily by `default`.
 *
 * @param alternative the default option if this is empty.
 */
inline fun <A> OptionOf<A>.orElse(alternative: () -> Option<A>): Option<A> = if (fix().isEmpty()) alternative() else fix()

infix fun <T> OptionOf<T>.or(value: Option<T>): Option<T> = if (fix().isEmpty()) {
  value
} else {
  fix()
}

fun <T> T?.toOption(): Option<T> = this?.let { Some(it) } ?: None

fun <A> Boolean.maybe(f: () -> A): Option<A> =
  if (this) {
    Some(f())
  } else {
    None
  }

fun <A> A.some(): Option<A> = Some(this)

fun <A> none(): Option<A> = None

fun <T> Iterable<T>.firstOrNone(): Option<T> = this.firstOrNull().toOption()

fun <T> Iterable<T>.firstOrNone(predicate: (T) -> Boolean): Option<T> = this.firstOrNull(predicate).toOption()

fun <T> Iterable<T>.singleOrNone(): Option<T> = this.singleOrNull().toOption()

fun <T> Iterable<T>.singleOrNone(predicate: (T) -> Boolean): Option<T> = this.singleOrNull(predicate).toOption()

fun <T> Iterable<T>.lastOrNone(): Option<T> = this.lastOrNull().toOption()

fun <T> Iterable<T>.lastOrNone(predicate: (T) -> Boolean): Option<T> = this.lastOrNull(predicate).toOption()

fun <T> Iterable<T>.elementAtOrNone(index: Int): Option<T> = this.elementAtOrNull(index).toOption()