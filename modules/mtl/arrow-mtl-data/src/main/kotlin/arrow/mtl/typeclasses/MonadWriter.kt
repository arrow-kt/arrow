package arrow.mtl.typeclasses

import arrow.Kind
import arrow.core.Tuple2
import arrow.typeclasses.Monad

/**
 * ank_macro_hierarchy(arrow.mtl.typeclasses.MonadWriter)
 * A monad that support monoidal accumulation (e.g. logging List of String)
 */
interface MonadWriter<F, W> : Monad<F> {

  /** Lift a writer action into the effect */
  fun <A> writer(aw: Tuple2<W, A>): Kind<F, A>

  /** Run the effect and pair the accumulator with the result */
  fun <A> Kind<F, A>.listen(): Kind<F, Tuple2<W, A>>

  /** Apply the effectful function to the accumulator */
  fun <A> Kind<F, Tuple2<(W) -> W, A>>.pass(): Kind<F, A>

  /** Lift the log into the effect */
  fun tell(w: W): Kind<F, Unit> = writer(Tuple2(w, Unit))

  /** Pair the value with an inspection of the accumulator */
  fun <A, B> Kind<F, A>.listens(f: (W) -> B): Kind<F, Tuple2<B, A>> = listen().map() { Tuple2(f(it.a), it.b) }

  /** Modify the accumulator */
  fun <A> Kind<F, A>.censor(f: (W) -> W): Kind<F, A> = listen().flatMap() { writer(Tuple2(f(it.a), it.b)) }
}
