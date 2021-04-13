package arrow.core

import arrow.typeclasses.Monoid
import org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement

/**
 * Eval is a monad which controls evaluation of a value or a computation that produces a value.
 *
 * Three basic evaluation strategies:
 *
 *  - Now:    evaluated immediately
 *  - Later:  evaluated once when value is needed
 *  - Always: evaluated every time value is needed
 *
 * The Later and Always are both lazy strategies while Now is eager.
 * Later and Always are distinguished from each other only by
 * memoization: once evaluated Later will save the value to be returned
 * immediately if it is needed again. Always will run its computation
 * every time.
 *
 * methods, which use an internal trampoline to avoid stack overflows.
 * Computation done within .map and .flatMap is always done lazily,
 * even when applied to a Now instance.
 *
 * It is not generally good style to pattern-match on Eval instances.
 * Rather, use .map and .flatMap to chain computation, and use .value
 * to get the result when needed. It is also not good style to create
 * Eval instances whose computation involves calling .value on another
 * Eval instance -- this can defeat the trampolining and lead to stack
 * overflows.
 *
 * Example of stack safety:
 *
 * ```kotlin:ank:playground
 * import arrow.core.Eval
 *
 * //sampleStart
 * fun even(n: Int): Eval<Boolean> =
 *   Eval.always { n == 0 }.flatMap {
 *     if(it == true) Eval.now(true)
 *     else odd(n - 1)
 *   }
 *
 * fun odd(n: Int): Eval<Boolean> =
 *   Eval.always { n == 0 }.flatMap {
 *     if(it == true) Eval.now(false)
 *     else even(n - 1)
 *   }
 *
 * // if not wrapped in eval this type of computation would blow the stack and result in a StackOverflowError
 * fun main() {
 *   println(odd(100000).value())
 * }
 * //sampleEnd
 * ```
 *
 */
sealed class Eval<out A> {

  companion object {

    /**
     * Creates an Eval instance from an already constructed value but still defers evaluation when chaining expressions with `map` and `flatMap`
     *
     * @param a is an already computed value of type [A]
     *
     * ```kotlin:ank:playground
     * import arrow.core.*
     *
     * fun main() {
     * //sampleStart
     *   val eager = Eval.now(1).map { it + 1 }
     *   println(eager.value())
     * //sampleEnd
     * }
     * ```
     *
     * It will return 2.
     */
    @JvmStatic
    fun <A> now(a: A): Eval<A> =
      Now(a)

    /**
     * Creates an Eval instance from a function deferring it's evaluation until `.value()` is invoked memoizing the computed value.
     *
     * @param f is a function or computation that will be called only once when `.value()` is invoked for the first time.
     *
     * ```kotlin:ank:playground
     * import arrow.core.*
     *
     * fun main() {
     * //sampleStart
     *   val lazyEvaled = Eval.later { "expensive computation" }
     *   println(lazyEvaled.value())
     * //sampleEnd
     * }
     * ```
     *
     * "expensive computation" is only computed once since the results are memoized and multiple calls to `value()` will just return the cached value.
     */
    @JvmStatic
    inline fun <A> later(crossinline f: () -> A): Later<A> =
      Later { f() }

    /**
     * Creates an Eval instance from a function deferring it's evaluation until `.value()` is invoked recomputing each time `.value()` is invoked.
     *
     * @param f is a function or computation that will be called every time `.value()` is invoked.
     *
     * ```kotlin:ank:playground
     * import arrow.core.*
     *
     * fun main() {
     * //sampleStart
     *   val alwaysEvaled = Eval.always { "expensive computation" }
     *   println(alwaysEvaled.value())
     * //sampleEnd
     * }
     * ```
     *
     * "expensive computation" is computed every time `value()` is invoked.
     */
    @JvmStatic
    inline fun <A> always(crossinline f: () -> A) =
      Always { f() }

    @JvmStatic
    inline fun <A> defer(crossinline f: () -> Eval<A>): Eval<A> =
      Defer { f() }

    @JvmStatic
    fun raise(t: Throwable): Eval<Nothing> =
      defer { throw t }

    /**
     * Collapse the call stack for eager evaluations.
     */
    private tailrec fun <A> collapse(fa: Eval<A>): Eval<A> =
      when (fa) {
        is Defer -> collapse(fa.thunk())
        is FlatMap ->
          object : FlatMap<A>() {
            override fun <S> start(): Eval<S> = fa.start()
            override fun <S> run(s: S): Eval<A> = collapse1(fa.run(s))
          }
        else -> fa
      }

    // Enforce tailrec call to collapse inside compute loop
    private fun <A> collapse1(fa: Eval<A>): Eval<A> = collapse(fa)

    @Suppress("UNCHECKED_CAST")
    private fun <A> evaluate(e: Eval<A>): A = run {
      var curr: Eval<Any?> = e
      val fs: MutableList<(Any?) -> Eval<Any?>> = mutableListOf()

      fun addToMemo(m: Memoize<Any?>): (Any?) -> Eval<Any?> = {
        m.result = Some(it)
        now(it)
      }

      loop@ while (true) {
        when (curr) {
          is FlatMap -> {
            val currComp = curr as FlatMap<A>
            currComp.start<A>().let { cc ->
              when (cc) {
                is FlatMap -> {
                  curr = cc.start<A>()
                  fs.add(0, currComp::run)
                  fs.add(0, cc::run)
                }
                is Memoize -> {
                  cc.result.fold(
                    {
                      curr = cc.eval
                      fs.add(0, currComp::run)
                      fs.add(0, addToMemo(cc as Memoize<Any?>))
                    },
                    {
                      curr = Now(it)
                      fs.add(0, currComp::run)
                    }
                  )
                }
                else -> {
                  curr = currComp.run(cc.value())
                }
              }
            }
          }
          is Memoize -> {
            val currComp = curr as Memoize<Any?>
            val eval = currComp.eval
            currComp.result.fold(
              {
                curr = eval
                fs.add(0, addToMemo(currComp))
              },
              {
                if (fs.isNotEmpty()) {
                  curr = fs[0](it)
                  fs.removeAt(0)
                }
              }
            )
          }
          else ->
            if (fs.isNotEmpty()) {
              curr = fs[0](curr.value())
              fs.removeAt(0)
            } else {
              break@loop
            }
        }
      }

      return curr.value() as A
    }
  }

  abstract fun value(): A

  abstract fun memoize(): Eval<A>

  inline fun <B> map(crossinline f: (A) -> B): Eval<B> =
    flatMap { a -> Now(f(a)) }

  @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE", "UNCHECKED_CAST")
  fun <B> flatMap(f: (A) -> Eval<B>): Eval<B> =
    when (this) {
      is FlatMap<A> -> object : FlatMap<B>() {
        override fun <S> start(): Eval<S> = (this@Eval).start()

        @IgnoreJRERequirement
        override fun <S> run(s: S): Eval<B> =
          object : FlatMap<B>() {
            override fun <S1> start(): Eval<S1> = (this@Eval).run(s) as Eval<S1>
            override fun <S1> run(s1: S1): Eval<B> = f(s1 as A)
          }
      }
      is Defer<A> -> object : FlatMap<B>() {
        override fun <S> start(): Eval<S> = this@Eval.thunk() as Eval<S>
        override fun <S> run(s: S): Eval<B> = f(s as A)
      }
      else -> object : FlatMap<B>() {
        override fun <S> start(): Eval<S> = this@Eval as Eval<S>
        override fun <S> run(s: S): Eval<B> = f(s as A)
      }
    }

  inline fun <B> coflatMap(crossinline f: (Eval<A>) -> B): Eval<B> =
    Later { f(this) }

  /**
   * Construct an eager Eval<A> instance. In some sense it is equivalent to using a val.
   *
   * This type should be used when an A value is already in hand, or when the computation to produce an A value is
   * pure and very fast.
   */
  data class Now<out A>(val value: A) : Eval<A>() {
    override fun value(): A = value
    override fun memoize(): Eval<A> = this

    override fun toString(): String =
      "Eval.Now($value)"

    companion object {
      @PublishedApi
      internal val unit: Eval<Unit> = Now(Unit)
    }
  }

  /**
   * Construct a lazy Eval<A> instance.
   *
   * This type should be used for most "lazy" values. In some sense it is equivalent to using a lazy val.
   *
   * When caching is not required or desired (e.g. if the value produced may be large) prefer Always. When there
   * is no computation necessary, prefer Now.
   *
   * Once Later has been evaluated, the closure (and any values captured by the closure) will not be retained, and
   * will be available for garbage collection.
   */
  data class Later<out A>(private val f: () -> A) : Eval<A>() {
    val value: A by lazy(f)

    override fun value(): A = value
    override fun memoize(): Eval<A> = this

    override fun toString(): String =
      "Eval.Later(f)"
  }

  /**
   * Construct a lazy Eval<A> instance.
   *
   * This type can be used for "lazy" values. In some sense it is equivalent to using a Function0 value.
   *
   * This type will evaluate the computation every time the value is required. It should be avoided except when
   * laziness is required and caching must be avoided. Generally, prefer Later.
   */
  data class Always<out A>(private val f: () -> A) : Eval<A>() {
    override fun value(): A = f()
    override fun memoize(): Eval<A> = Later(f)

    override fun toString(): String =
      "Eval.Always(f)"
  }

  /**
   * Defer is a type of Eval<A> that is used to defer computations which produce Eval<A>.
   *
   * Users should not instantiate Defer instances themselves. Instead, they will be automatically created when needed.
   */
  data class Defer<out A>(val thunk: () -> Eval<A>) : Eval<A>() {
    override fun memoize(): Eval<A> = Memoize(this)
    override fun value(): A = collapse(this).value()

    override fun toString(): String =
      "Eval.Defer(thunk)"
  }

  /**
   * FlatMap is a type of Eval<A> that is used to chain computations involving .map and .flatMap. Along with
   * Eval#flatMap. It implements the trampoline that guarantees stack-safety.
   *
   * Users should not instantiate FlatMap instances themselves. Instead, they will be automatically created when
   * needed.
   *
   * Unlike a traditional trampoline, the internal workings of the trampoline are not exposed. This allows a slightly
   * more efficient implementation of the .value method.
   */
  abstract class FlatMap<out A> : Eval<A>() {
    abstract fun <S> start(): Eval<S>
    abstract fun <S> run(s: S): Eval<A>
    override fun memoize(): Eval<A> = Memoize(this)
    override fun value(): A = evaluate(this)

    override fun toString(): String =
      "Eval.FlatMap(..)"
  }

  /**
   * Memoize is a type of Eval<A> that is used to memoize an eval value. Unlike Later, Memoize exposes its cache,
   * allowing Eval's internal trampoline to compute it when needed.
   *
   * Users should not instantiate Memoize instances themselves. Instead, they will be automatically created when
   * needed.
   */
  internal data class Memoize<A>(val eval: Eval<A>) : Eval<A>() {
    var result: Option<A> = None
    override fun memoize() = this
    override fun value(): A = result.getOrElse {
      evaluate(eval).also { result = Some(it) }
    }

    override fun toString(): String =
      "Eval.Memoize($eval)"
  }

  override fun toString(): String =
    "Eval(...)"
}

fun <A, B> Iterator<A>.iterateRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> {
  fun loop(): Eval<B> =
    Eval.defer { if (this.hasNext()) f(this.next(), loop()) else lb }
  return loop()
}

fun <A, B, Z> Eval<A>.zip(b: Eval<B>, map: (A, B) -> Z): Eval<Z> =
  flatMap { a: A -> b.map { bb: B -> map(a, bb) } }

fun <A, B> Eval<A>.zip(b: Eval<B>): Eval<Pair<A, B>> =
  flatMap { a: A -> b.map { bb: B -> Pair(a, bb) } }

fun <A, B, C, D> Eval<A>.zip(
  b: Eval<B>,
  c: Eval<C>,
  map: (A, B, C) -> D
): Eval<D> =
  zip(b, c, Eval.Now.unit, Eval.Now.unit, Eval.Now.unit, Eval.Now.unit, Eval.Now.unit, Eval.Now.unit, Eval.Now.unit) { aa, bb, cc, _, _, _, _, _, _, _ -> map(aa, bb, cc) }

fun <A, B, C, D, E> Eval<A>.zip(
  b: Eval<B>,
  c: Eval<C>,
  d: Eval<D>,
  map: (A, B, C, D) -> E
): Eval<E> =
  zip(b, c, d, Eval.Now.unit, Eval.Now.unit, Eval.Now.unit, Eval.Now.unit, Eval.Now.unit, Eval.Now.unit) { aa, bb, cc, dd, _, _, _, _, _, _ -> map(aa, bb, cc, dd) }

fun <A, B, C, D, E, F> Eval<A>.zip(
  b: Eval<B>,
  c: Eval<C>,
  d: Eval<D>,
  e: Eval<E>,
  map: (A, B, C, D, E) -> F
): Eval<F> =
  zip(b, c, d, e, Eval.Now.unit, Eval.Now.unit, Eval.Now.unit, Eval.Now.unit, Eval.Now.unit) { aa, bb, cc, dd, ee, _, _, _, _, _ -> map(aa, bb, cc, dd, ee) }

fun <A, B, C, D, E, F, G> Eval<A>.zip(
  b: Eval<B>,
  c: Eval<C>,
  d: Eval<D>,
  e: Eval<E>,
  f: Eval<F>,
  map: (A, B, C, D, E, F) -> G
): Eval<G> =
  zip(b, c, d, e, f, Eval.Now.unit, Eval.Now.unit, Eval.Now.unit, Eval.Now.unit) { aa, bb, cc, dd, ee, ff, _, _, _, _ -> map(aa, bb, cc, dd, ee, ff) }

fun <A, B, C, D, E, F, G, H> Eval<A>.zip(
  b: Eval<B>,
  c: Eval<C>,
  d: Eval<D>,
  e: Eval<E>,
  f: Eval<F>,
  g: Eval<G>,
  map: (A, B, C, D, E, F, G) -> H
): Eval<H> =
  zip(b, c, d, e, f, g, Eval.Now.unit, Eval.Now.unit, Eval.Now.unit) { aa, bb, cc, dd, ee, ff, gg, _, _, _ -> map(aa, bb, cc, dd, ee, ff, gg) }

fun <A, B, C, D, E, F, G, H, I> Eval<A>.zip(
  b: Eval<B>,
  c: Eval<C>,
  d: Eval<D>,
  e: Eval<E>,
  f: Eval<F>,
  g: Eval<G>,
  h: Eval<H>,
  map: (A, B, C, D, E, F, G, H) -> I
): Eval<I> =
  zip(b, c, d, e, f, g, h, Eval.Now.unit, Eval.Now.unit) { aa, bb, cc, dd, ee, ff, gg, hh, _, _ -> map(aa, bb, cc, dd, ee, ff, gg, hh) }

fun <A, B, C, D, E, F, G, H, I, J> Eval<A>.zip(
  b: Eval<B>,
  c: Eval<C>,
  d: Eval<D>,
  e: Eval<E>,
  f: Eval<F>,
  g: Eval<G>,
  h: Eval<H>,
  i: Eval<I>,
  map: (A, B, C, D, E, F, G, H, I) -> J
): Eval<J> =
  zip(b, c, d, e, f, g, h, i, Eval.Now.unit) { aa, bb, cc, dd, ee, ff, gg, hh, ii, _ -> map(aa, bb, cc, dd, ee, ff, gg, hh, ii) }

fun <A, B, C, D, E, F, G, H, I, J, K> Eval<A>.zip(
  b: Eval<B>,
  c: Eval<C>,
  d: Eval<D>,
  e: Eval<E>,
  f: Eval<F>,
  g: Eval<G>,
  h: Eval<H>,
  i: Eval<I>,
  j: Eval<J>,
  map: (A, B, C, D, E, F, G, H, I, J) -> K
): Eval<K> =
  flatMap { aa ->
    b.flatMap { bb ->
      c.flatMap { cc ->
        d.flatMap { dd ->
          e.flatMap { ee ->
            f.flatMap { ff ->
              g.flatMap { gg ->
                h.flatMap { hh ->
                  i.flatMap { ii ->
                    j.map { jj ->
                      map(aa, bb, cc, dd, ee, ff, gg, hh, ii, jj)
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }

fun <A> Eval<A>.replicate(n: Int): Eval<List<A>> =
  if (n <= 0) Eval.now(emptyList())
  else this.zip(replicate(n - 1)) { a: A, xs: List<A> -> listOf(a) + xs }

fun <A> Eval<A>.replicate(n: Int, MA: Monoid<A>): Eval<A> = MA.run {
  if (n <= 0) Eval.now(MA.empty())
  else this@replicate.zip(replicate(n - 1, MA)) { a: A, xs: A -> MA.run { a + xs } }
}
