package arrow.core

import arrow.higherkind

fun <A> EvalOf<A>.value(): A = this.fix().value()

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
 */
@higherkind
sealed class Eval<out A> : EvalOf<A> {

  companion object {

    fun <A, B> tailRecM(a: A, f: (A) -> EvalOf<Either<A, B>>): Eval<B> =
      f(a).fix().flatMap { eval: Either<A, B> ->
        when (eval) {
          is Either.Left -> tailRecM(eval.a, f)
          is Either.Right -> just(eval.b)
        }
      }

    fun <A> just(a: A): Eval<A> = now(a)

    fun <A> now(a: A) = Now(a)

    fun <A> later(f: () -> A) = Later(f)

    fun <A> always(f: () -> A) = Always(f)

    fun <A> defer(f: () -> Eval<A>): Eval<A> = Defer(f)

    fun raise(t: Throwable): Eval<Nothing> = defer { throw t }

    val Unit: Eval<Unit> = Now(kotlin.Unit)

    val True: Eval<Boolean> = Now(true)

    val False: Eval<Boolean> = Now(false)

    val Zero: Eval<Int> = Now(0)

    val One: Eval<Int> = Now(1)

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

    //Enforce tailrec call to collapse inside compute loop
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
                  val inStartFun: (Any?) -> Eval<A> = { cc.run(it) }
                  val outStartFun: (Any?) -> Eval<A> = { currComp.run(it) }
                  curr = cc.start<A>()
                  fs.add(0, outStartFun)
                  fs.add(0, inStartFun)
                }
                is Memoize -> {
                  cc.result.fold(
                    {
                      curr = cc.eval
                      fs.add(0) { currComp.run(it) }
                      fs.add(0, addToMemo(cc as Memoize<Any?>))
                    },
                    {
                      curr = Now(it)
                      fs.add(0) { currComp.run(it) }
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
            val currComp = curr as Memoize<A>
            val eval = currComp.eval
            currComp.result.fold(
              {
                curr = eval
                fs.add(0, addToMemo(currComp as Memoize<Any?>))
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

  fun <B> map(f: (A) -> B): Eval<B> = flatMap { a -> Now(f(a)) }

  fun <B> ap(ff: EvalOf<(A) -> B>): Eval<B> = ff.fix().flatMap { f -> map(f) }.fix()

  @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE", "UNCHECKED_CAST")
  fun <B> flatMap(f: (A) -> EvalOf<B>): Eval<B> =
    when (this) {
      is FlatMap<A> -> object : FlatMap<B>() {
        override fun <S> start(): Eval<S> = (this@Eval).start()
        override fun <S> run(s: S): Eval<B> =
          object : FlatMap<B>() {
            override fun <S1> start(): Eval<S1> = (this@Eval).run(s) as Eval<S1>
            override fun <S1> run(s1: S1): Eval<B> = f(s1 as A).fix()
          }
      }
      is Defer<A> -> object : FlatMap<B>() {
        override fun <S> start(): Eval<S> = this@Eval.thunk() as Eval<S>
        override fun <S> run(s: S): Eval<B> = f(s as A).fix()
      }
      else -> object : FlatMap<B>() {
        override fun <S> start(): Eval<S> = this@Eval as Eval<S>
        override fun <S> run(s: S): Eval<B> = f(s as A).fix()
      }
    }

  fun <B> coflatMap(f: (EvalOf<A>) -> B): Eval<B> = Later { f(this) }

  fun extract(): A = value()

  /**
   * Construct an eager Eval<A> instance. In some sense it is equivalent to using a val.
   *
   * This type should be used when an A value is already in hand, or when the computation to produce an A value is
   * pure and very fast.
   */
  data class Now<out A>(val value: A) : Eval<A>() {
    override fun value(): A = value
    override fun memoize(): Eval<A> = this
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
  }

  /**
   * Defer is a type of Eval<A> that is used to defer computations which produce Eval<A>.
   *
   * Users should not instantiate Defer instances themselves. Instead, they will be automatically created when needed.
   */
  data class Defer<out A>(val thunk: () -> Eval<A>) : Eval<A>() {
    override fun memoize(): Eval<A> = Memoize(this)
    override fun value(): A = collapse(this).value()
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
  internal abstract class FlatMap<out A> : Eval<A>() {
    abstract fun <S> start(): Eval<S>
    abstract fun <S> run(s: S): Eval<A>
    override fun memoize(): Eval<A> = Memoize(this)
    override fun value(): A = evaluate(this)
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
  }
}
