package arrow.fx.typeclasses

import arrow.Kind
import arrow.core.Either
import arrow.fx.Ref
import arrow.typeclasses.MonadThrow

/**
 * ank_macro_hierarchy(arrow.fx.typeclasses.MonadDefer)
 *
 * The context required to defer evaluating a safe computation.
 **/
interface MonadDefer<F> : MonadThrow<F>, Bracket<F, Throwable> {

  fun <A> defer(fa: () -> Kind<F, A>): Kind<F, A>

  fun <A> later(f: () -> A): Kind<F, A> =
    defer {
      try {
        just(f())
      } catch (t: Throwable) {
        t.raiseNonFatal<A>()
      }
    }

  fun <A> later(fa: Kind<F, A>): Kind<F, A> = defer { fa }

  fun lazy(): Kind<F, Unit> = later { }

  fun <A> laterOrRaise(f: () -> Either<Throwable, A>): Kind<F, A> =
    defer { f().fold({ raiseError<A>(it) }, { just(it) }) }

  /**
   * Create a [Ref] a pure atomic reference to safely manage mutable state.
   * It's a pure version of [java.util.concurrent.atomic.AtomicReference] that works in context [F].
   *
   * It's always initialized to a value, and allows for concurrent updates.
   *
   * ```kotlin:ank:playground
   * import arrow.Kind
   * import arrow.core.Tuple2
   * import arrow.fx.*
   * import arrow.fx.extensions.io.monadDefer.monadDefer
   * import arrow.fx.typeclasses.MonadDefer
   *
   * fun main(args: Array<String>) {
   *   fun <F> MonadDefer<F>.refExample(): Kind<F, Tuple2<Int, Int>> =
   *     //sampleStart
   *     fx.monad {
   *       val ref = !Ref(1)
   *       val initial = !ref.getAndUpdate(Int::inc)
   *       val updated = !ref.get()
   *       Tuple2(initial, updated)
   *     }
   *
   *   //sampleEnd
   *   IO.monadDefer().refExample()
   *     .fix().unsafeRunSync().let(::println)
   * }
   * ```
   */
  fun <A> Ref(a: A): Kind<F, Ref<F, A>> = Ref(this, a)
}
