package arrow.typeclasses

import arrow.Kind
import arrow.core.NonFatal
import arrow.documented
import kotlin.coroutines.startCoroutine

/**
 * ank_macro_hierarchy(arrow.typeclasses.MonadError)
 */
interface MonadError<F, E> : ApplicativeError<F, E>, Monad<F> {

  fun <A> Kind<F, A>.ensure(error: () -> E, predicate: (A) -> Boolean): Kind<F, A> =
    this.flatMap {
      if (predicate(it)) just(it)
      else raiseError(error())
    }

}

/**
 * ank_macro_hierarchy(arrow.typeclasses.MonadThrow)
 *
 * A MonadError with the error type fixed to Throwable. It provides [bindingCatch] for automatically catching throwable
 * errors in the context of a binding, short-circuiting the complete computation and returning the error raised to the
 * same computational context (through [raiseError]).
 *
 * ```kotlin:ank:playground:extension
 * _imports_
 *
 * fun main(args: Array<String>) {
 *   val result =
 *   //sampleStart
 *   _extensionFactory_
 *   //sampleEnd
 *   println(result)
 * }
 * ```
 *
 * ### Example
 *
 * Oftentimes we find ourselves in situations where we need to sequence some computations that could potentially fail.
 * [bindingCatch] allows us to safely compute those by automatically catching any exceptions thrown during the process.
 *
 * ```kotlin:ank:playground:extension
 * _imports_
 * import arrow.Kind
 * import arrow.typeclasses.MonadThrow
 *
 * typealias Impacted = Boolean
 *
 * object Nuke
 * object Target
 * class MissedByMeters(private val meters: Int) : Throwable("Missed by $meters meters")
 *
 * fun <F> MonadThrow<F>.arm(): Kind<F, Nuke> = just(Nuke)
 * fun <F> MonadThrow<F>.aim(): Kind<F, Target> = just(Target)
 * fun <F> MonadThrow<F>.launchImpure(target: Target, nuke: Nuke): Impacted {
 *   throw MissedByMeters(5)
 * }
 *
 * fun main(args: Array<String>) {
 *    //sampleStart
 *    fun <F> MonadThrow<F>.attack(): Kind<F, Impacted> =
 *      bindingCatch {
 *        val nuke = arm().bind()
 *        val target = aim().bind()
 *        val impact = launchImpure(target, nuke) // this throws!
 *        impact
 *      }
 *
 *    val result = _extensionFactory_.attack()
 *    //sampleEnd
 *    println(result)
 * }
 * ```
 */
@documented
interface MonadThrow<F> : MonadError<F, Throwable> {

  /**
   * Entry point for monad bindings which enables for comprehensions. The underlying implementation is based on
   * coroutines. A coroutine is initiated and suspended inside [MonadErrorContinuation] yielding to [Monad.flatMap].
   * Once all the flatMap binds are completed, the underlying monad is returned from the act of executing the coroutine.
   *
   * This one operates over [MonadError] instances that can support [Throwable] in their error type automatically
   * lifting errors as failed computations in their monadic context and not letting exceptions thrown as the regular
   * monad binding does.
   *
   * ### Example
   *
   * Oftentimes we find ourselves in situations where we need to sequence some computations that could potentially fail.
   * [bindingCatch] allows us to safely compute those by automatically catching any exceptions thrown during the process.
   *
   * ```kotlin:ank:playground:extension
   * _imports_
   * import arrow.Kind
   * import arrow.typeclasses.MonadThrow
   *
   * typealias Impacted = Boolean
   *
   * object Nuke
   * object Target
   * class MissedByMeters(private val meters: Int) : Throwable("Missed by $meters meters")
   *
   * fun <F> MonadThrow<F>.arm(): Kind<F, Nuke> = just(Nuke)
   * fun <F> MonadThrow<F>.aim(): Kind<F, Target> = just(Target)
   * fun <F> MonadThrow<F>.launchImpure(target: Target, nuke: Nuke): Impacted {
   *   throw MissedByMeters(5)
   * }
   *
   * fun main(args: Array<String>) {
   *    //sampleStart
   *    fun <F> MonadThrow<F>.attack(): Kind<F, Impacted> =
   *      bindingCatch {
   *        val nuke = arm().bind()
   *        val target = aim().bind()
   *        val impact = launchImpure(target, nuke) // this throws!
   *        impact
   *      }
   *
   *    val result = _extensionFactory_.attack()
   *    //sampleEnd
   *    println(result)
   * }
   * ```
   *
   */
  @Deprecated(
    "`bindingCatch` is getting renamed to `fx` for consistency with the Arrow Fx system. Use the Fx extensions for comprehensions",
    ReplaceWith("fx")
  )
  fun <B> bindingCatch(c: suspend MonadErrorContinuation<F, *>.() -> B): Kind<F, B> {
    val continuation = MonadErrorContinuation<F, B>(this)
    val wrapReturn: suspend MonadErrorContinuation<F, *>.() -> Kind<F, B> = { just(c()) }
    wrapReturn.startCoroutine(continuation, continuation)
    return continuation.returnedMonad()
  }

  override fun <B> binding(c: suspend MonadContinuation<F, *>.() -> B): Kind<F, B> =
    bindingCatch { c() }

  fun <A> Throwable.raiseNonFatal(): Kind<F, A> =
    if (NonFatal(this)) raiseError(this) else throw this
}
