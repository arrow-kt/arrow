package arrow.effects.typeclasses

import arrow.Kind
import arrow.core.Tuple2
import arrow.core.Tuple3
import kotlin.coroutines.CoroutineContext

interface ParMapN<F> {

  /**
   * Map two tasks in parallel within a new [F] on [this@parMapN].
   *
   * ```kotlin:ank:playground
   * import arrow.effects.extensions.io.concurrent.parMapN
   * import arrow.effects.extensions.io.monadDefer.delay
   * import kotlinx.coroutines.Dispatchers
   *
   * fun main(args: Array<String>) {
   *   //sampleStart
   *   val result = Dispatchers.Default.parMapN(
   *     delay { "First one is on ${Thread.currentThread().name}" },
   *     delay { "Second one is on ${Thread.currentThread().name}" }
   *   ) { a, b ->
   *     "$a\n$b"
   *   }
   *   //sampleEnd
   *   println(result.unsafeRunSync())
   * }
   * ```
   *
   * @param this@parMapN [CoroutineContext] to execute the source [F] on.
   * @param fa value to parallel map
   * @param fb value to parallel map
   * @param f function to map/combine value [A] and [B]
   * @return [F] with the result of function [f].
   *
   * @see racePair for a version that does not await all results to be finished.
   */
  fun <A, B, C> CoroutineContext.parMapN(fa: Kind<F, A>, fb: Kind<F, B>, f: (A, B) -> C): Kind<F, C>

  /**
   * @see parMapN
   */
  fun <A, B, C, D> CoroutineContext.parMapN(fa: Kind<F, A>, fb: Kind<F, B>, fc: Kind<F, C>, f: (A, B, C) -> D): Kind<F, D>

  /**
   * @see parMapN
   */
  fun <A, B, C, D, E> CoroutineContext.parMapN(
    fa: Kind<F, A>,
    fb: Kind<F, B>,
    fc: Kind<F, C>,
    fd: Kind<F, D>,
    f: (A, B, C, D) -> E
  ): Kind<F, E> =
    parMapN(
      parMapN(fa, fb, ::Tuple2),
      parMapN(fc, fd, ::Tuple2)
    ) { (a, b), (c, d) ->
      f(a, b, c, d)
    }

  /**
   * @see parMapN
   */
  fun <A, B, C, D, E, G> CoroutineContext.parMapN(
    fa: Kind<F, A>,
    fb: Kind<F, B>,
    fc: Kind<F, C>,
    fd: Kind<F, D>,
    fe: Kind<F, E>,
    f: (A, B, C, D, E) -> G
  ): Kind<F, G> =
    parMapN(parMapN(fa, fb, fc, ::Tuple3),
      parMapN(fd, fe, ::Tuple2)
    ) { (a, b, c), (d, e) ->
      f(a, b, c, d, e)
    }

  /**
   * @see parMapN
   */
  fun <A, B, C, D, E, G, H> CoroutineContext.parMapN(
    fa: Kind<F, A>,
    fb: Kind<F, B>,
    fc: Kind<F, C>,
    fd: Kind<F, D>,
    fe: Kind<F, E>,
    fg: Kind<F, G>,
    f: (A, B, C, D, E, G) -> H
  ): Kind<F, H> =
    parMapN(parMapN(fa, fb, fc, ::Tuple3),
      parMapN(fd, fe, fg, ::Tuple3)
    ) { (a, b, c), (d, e, g) ->
      f(a, b, c, d, e, g)
    }

  /**
   * @see parMapN
   */
  fun <A, B, C, D, E, G, H, I> CoroutineContext.parMapN(
    fa: Kind<F, A>,
    fb: Kind<F, B>,
    fc: Kind<F, C>,
    fd: Kind<F, D>,
    fe: Kind<F, E>,
    fg: Kind<F, G>,
    fh: Kind<F, H>,
    f: (A, B, C, D, E, G, H) -> I
  ): Kind<F, I> =
    parMapN(parMapN(fa, fb, fc, ::Tuple3),
      parMapN(fd, fe, ::Tuple2),
      parMapN(fg, fh, ::Tuple2)) { (a, b, c), (d, e), (g, h) ->
      f(a, b, c, d, e, g, h)
    }

  /**
   * @see parMapN
   */
  fun <A, B, C, D, E, G, H, I, J> CoroutineContext.parMapN(
    fa: Kind<F, A>,
    fb: Kind<F, B>,
    fc: Kind<F, C>,
    fd: Kind<F, D>,
    fe: Kind<F, E>,
    fg: Kind<F, G>,
    fh: Kind<F, H>,
    fi: Kind<F, I>,
    f: (A, B, C, D, E, G, H, I) -> J
  ): Kind<F, J> =
    parMapN(parMapN(fa, fb, fc, ::Tuple3),
      parMapN(fd, fe, fg, ::Tuple3),
      parMapN(fh, fi, ::Tuple2)) { (a, b, c), (d, e, g), (h, i) ->
      f(a, b, c, d, e, g, h, i)
    }

  /**
   * @see parMapN
   */
  fun <A, B, C, D, E, G, H, I, J, K> CoroutineContext.parMapN(
    fa: Kind<F, A>,
    fb: Kind<F, B>,
    fc: Kind<F, C>,
    fd: Kind<F, D>,
    fe: Kind<F, E>,
    fg: Kind<F, G>,
    fh: Kind<F, H>,
    fi: Kind<F, I>,
    fj: Kind<F, J>,
    f: (A, B, C, D, E, G, H, I, J) -> K
  ): Kind<F, K> =
    parMapN(parMapN(fa, fb, fc, ::Tuple3),
      parMapN(fd, fe, fg, ::Tuple3),
      parMapN(fh, fi, fj, ::Tuple3)) { (a, b, c), (d, e, g), (h, i, j) ->
      f(a, b, c, d, e, g, h, i, j)
    }

}
