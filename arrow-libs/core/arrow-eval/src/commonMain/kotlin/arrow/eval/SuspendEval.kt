package arrow.eval

import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.getOrElse
import arrow.eval.Eval.Now
import kotlin.jvm.JvmStatic

public sealed interface SuspendEval<out A> {
  public companion object {
    @JvmStatic
    public fun <A> now(a: A): SuspendEval<A> = Eval.now(a)

    @JvmStatic
    public inline fun <A> later(crossinline f: suspend () -> A): Later<A> =
      Later { f() }

    @JvmStatic
    public inline fun <A> always(crossinline f: suspend () -> A): Always<A> =
      Always { f() }

    @JvmStatic
    public inline fun <A> defer(crossinline f: suspend () -> SuspendEval<A>): SuspendEval<A> =
      Defer { f() }

    @JvmStatic
    public fun raise(t: Throwable): SuspendEval<Nothing> =
      defer { throw t }

    private tailrec suspend fun <A> collapse(fa: SuspendEval<A>): SuspendEval<A> =
      when (fa) {
        is AbstractDefer -> collapse(fa.thunk())
        is AbstractFlatMap ->
          object : FlatMap<A>() {
            override suspend fun <S> startSuspend(): SuspendEval<S> = fa.startSuspend()
            override suspend fun <S> runSuspend(s: S): SuspendEval<A> = collapse1(fa.runSuspend(s))
          }
        else -> fa
      }

    // Enforce tailrec call to collapse inside compute loop
    private suspend fun <A> collapse1(fa: SuspendEval<A>): SuspendEval<A> = collapse(fa)

    @Suppress("UNCHECKED_CAST")
    private suspend fun <A> evaluate(e: SuspendEval<A>): A = run {
      var curr: SuspendEval<Any?> = e
      val fs: MutableList<suspend (Any?) -> SuspendEval<Any?>> = mutableListOf()

      fun addToMemo(m: AbstractMemoize<Any?>): (Any?) -> SuspendEval<Any?> = {
        m.result = Some(it)
        now(it)
      }

      loop@ while (true) {
        when (curr) {
          is AbstractFlatMap -> {
            val currComp = curr as AbstractFlatMap<A>
            currComp.startSuspend<A>().let { cc ->
              when (cc) {
                is AbstractFlatMap -> {
                  curr = cc.startSuspend<A>()
                  fs.add(0, currComp::runSuspend)
                  fs.add(0, cc::runSuspend)
                }

                is AbstractMemoize -> {
                  cc.result.fold(
                    {
                      curr = cc.eval
                      fs.add(0, currComp::runSuspend)
                      fs.add(0, addToMemo(cc as AbstractMemoize<Any?>))
                    },
                    {
                      curr = Now(it)
                      fs.add(0, currComp::runSuspend)
                    }
                  )
                }

                else -> {
                  curr = currComp.runSuspend(cc.run())
                }
              }
            }
          }

          is AbstractMemoize -> {
            val currComp = curr as AbstractMemoize<Any?>
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
              curr = fs[0](curr.run())
              fs.removeAt(0)
            } else {
              break@loop
            }
        }
      }

      return curr.run() as A
    }
  }

  public suspend fun run(): A

  public fun memoize(): SuspendEval<A>

  public fun <B> mapSuspend(f: suspend (A) -> B): SuspendEval<B> =
    flatMapSuspend { a -> Now(f(a)) }

  @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE", "UNCHECKED_CAST")
  public fun <B> flatMapSuspend(f: suspend (A) -> SuspendEval<B>): SuspendEval<B> =
    when (this) {
      is AbstractFlatMap<A> -> object : FlatMap<B>() {
        override suspend fun <S> startSuspend(): SuspendEval<S> = (this@SuspendEval).startSuspend()

        // @IgnoreJRERequirement
        override suspend fun <S> runSuspend(s: S): SuspendEval<B> =
          object : FlatMap<B>() {
            override suspend fun <S1> startSuspend(): SuspendEval<S1> = (this@SuspendEval).runSuspend(s) as SuspendEval<S1>
            override suspend fun <S1> runSuspend(s1: S1): SuspendEval<B> = f(s1 as A)
          }
      }
      is AbstractDefer<A> -> object : FlatMap<B>() {
        override suspend fun <S> startSuspend(): SuspendEval<S> = this@SuspendEval.thunk() as SuspendEval<S>
        override suspend fun <S> runSuspend(s: S): SuspendEval<B> = f(s as A)
      }
      else -> object : FlatMap<B>() {
        override suspend fun <S> startSuspend(): Eval<S> = this@SuspendEval as Eval<S>
        override suspend fun <S> runSuspend(s: S): SuspendEval<B> = f(s as A)
      }
    }

  public data class Later<out A>(private val f: suspend () -> A) : SuspendEval<A> {
    var result: Option<@UnsafeVariance A> = None

    override suspend fun run(): A = result.getOrElse {
      f().also { result = Some(it) }
    }

    override fun memoize(): SuspendEval<A> = this

    override fun toString(): String =
      "SuspendEval.Later(f)"
  }

  public data class Always<out A>(private val f: suspend () -> A) : SuspendEval<A> {
    override suspend fun run(): A = f()
    override fun memoize(): SuspendEval<A> = Later(f)

    override fun toString(): String =
      "SuspendEval.Always(f)"
  }

  public interface AbstractDefer<out A>: SuspendEval<A> {
    public val thunk: suspend () -> SuspendEval<A>
  }

  public data class Defer<out A>(override val thunk: suspend () -> SuspendEval<A>) : AbstractDefer<A> {
    override fun memoize(): SuspendEval<A> = Memoize(this)
    override suspend fun run(): A = collapse(this).run()

    override fun toString(): String =
      "SuspendEval.Defer(thunk)"
  }

  public interface AbstractFlatMap<out A> : SuspendEval<A> {
    public suspend fun <S> startSuspend(): SuspendEval<S>
    public suspend fun <S> runSuspend(s: S): SuspendEval<A>
  }

  public abstract class FlatMap<out A> : AbstractFlatMap<A> {
    public abstract override suspend fun <S> startSuspend(): SuspendEval<S>
    public abstract override suspend fun <S> runSuspend(s: S): SuspendEval<A>
    override fun memoize(): SuspendEval<A> = Memoize(this)
    override suspend fun run(): A = evaluate(this)

    override fun toString(): String =
      "Eval.FlatMap(..)"
  }

  public interface AbstractMemoize<A> : SuspendEval<A> {
    public val eval: SuspendEval<A>
    public var result: Option<A>
  }

  public data class Memoize<A>(override val eval: SuspendEval<A>) : AbstractMemoize<A> {
    override var result: Option<A> = None
    override fun memoize(): SuspendEval<A> = this
    override suspend fun run(): A = result.getOrElse {
      evaluate(eval).also { result = Some(it) }
    }

    override fun toString(): String =
      "Eval.Memoize($eval)"
  }
}
