package arrow.typeclasses

import arrow.Kind
import arrow.core.Either
import arrow.core.identity

/**
 *
 * [Decidable] is a typeclass modeling contravariant decision. [Decidable] is the contravariant version of [Alternative].
 *
 * [Decidable] basically states: Given a Kind<F, A> and a Kind<F, B> and a way to turn C into either A or B it gives you a Kind<F, C>
 *
 * For example a serializer for either a string or an int:
 *
 * ```kotlin:ank:playground
 * import arrow.Kind
 * import arrow.core.Either
 * import arrow.core.Tuple2
 * import arrow.core.identity
 * import arrow.core.right
 * import arrow.typeclasses.Decidable
 * import com.example.domain.*
 *
 * val stringSerializer = Serializer<String> { "STRING: $it" }
 * val intSerializer = Serializer<Int> { "INT: $it" }
 *
 * fun main(args: Array<String>) {
 *   //sampleStart
 *   val stringOrInt: Serializer<Either<String, Int>> = Serializer.decidable()
 *      .choose<String, Int, Either<String, Int>>(stringSerializer, intSerializer, ::identity).fix()
 *
 *   val stringOrIntEither = 1.right()
 *   val result = stringOrInt.func(stringOrIntEither)
 *   //sampleEnd
 *   println(result)
 * }
 * ```
 *
 * ank_macro_hierarchy(arrow.typeclasses.Decidable)
 */
interface Decidable<F> : Divisible<F> {

  /**
   * Choose takes two data-types of type `Kind<F, A>` and `Kind<F, B>` and produces a type of `Kind<F, C>` when given
   *  a function from `C -> Either<A, B>`
   *
   * ```kotlin:ank:playground
   * import arrow.Kind
   * import arrow.core.Either
   * import arrow.core.Tuple2
   * import arrow.core.identity
   * import arrow.core.right
   * import arrow.typeclasses.Decidable
   * import com.example.domain.*
   *
   * val stringSerializer = Serializer<String> { "STRING: $it" }
   * val intSerializer = Serializer<Int> { "INT: $it" }
   *
   * fun main(args: Array<String>) {
   *   //sampleStart
   *   val stringOrInt: Serializer<Either<String, Int>> = Serializer.decidable()
   *      .choose<String, Int, Either<String, Int>>(stringSerializer, intSerializer, ::identity).fix()
   *
   *   val stringOrIntEither = 1.right()
   *   val result = stringOrInt.func(stringOrIntEither)
   *   //sampleEnd
   *   println(result)
   * }
   * ```
   */
  fun <A, B, Z> choose(fa: Kind<F, A>, fb: Kind<F, B>, f: (Z) -> Either<A, B>): Kind<F, Z>

  fun <A, B, C, Z> choose(
    fa: Kind<F, A>,
    fb: Kind<F, B>,
    fc: Kind<F, C>,
    f: (Z) -> Either<A, Either<B, C>>
  ): Kind<F, Z> =
    choose(
      fa,
      choose(fb, fc, ::identity),
      f
    )

  fun <A, B, C, D, Z> choose(
    fa: Kind<F, A>,
    fb: Kind<F, B>,
    fc: Kind<F, C>,
    fd: Kind<F, D>,
    f: (Z) -> Either<A, Either<B, Either<C, D>>>
  ): Kind<F, Z> =
    choose(
      fa,
      choose(
        fb,
        choose<C, D, Either<C, D>>(fc, fd, ::identity),
        ::identity
      ),
      f
    )

  fun <A, B, C, D, E, Z> choose(
    fa: Kind<F, A>,
    fb: Kind<F, B>,
    fc: Kind<F, C>,
    fd: Kind<F, D>,
    fe: Kind<F, E>,
    f: (Z) -> Either<A, Either<B, Either<C, Either<D, E>>>>
  ): Kind<F, Z> =
    choose(
      fa,
      choose(
        fb,
        choose<C, Either<D, E>, Either<C, Either<D, E>>>(
          fc,
          choose(fd, fe, ::identity),
          ::identity
        ),
        ::identity
      ),
      f
    )

  fun <A, B, C, D, E, FF, Z> choose(
    fa: Kind<F, A>,
    fb: Kind<F, B>,
    fc: Kind<F, C>,
    fd: Kind<F, D>,
    fe: Kind<F, E>,
    ff: Kind<F, FF>,
    f: (Z) -> Either<A, Either<B, Either<C, Either<D, Either<E, FF>>>>>
  ): Kind<F, Z> =
    choose(
      fa,
      choose(
        fb,
        choose<C, Either<D, Either<E, FF>>, Either<C, Either<D, Either<E, FF>>>>(
          fc,
          choose<D, Either<E, FF>, Either<D, Either<E, FF>>>(
            fd,
            choose(fe, ff, ::identity),
            ::identity
          ),
          ::identity
        ),
        ::identity
      ),
      f
    )

  fun <A, B, C, D, E, FF, G, Z> choose(
    fa: Kind<F, A>,
    fb: Kind<F, B>,
    fc: Kind<F, C>,
    fd: Kind<F, D>,
    fe: Kind<F, E>,
    ff: Kind<F, FF>,
    fg: Kind<F, G>,
    f: (Z) -> Either<A, Either<B, Either<C, Either<D, Either<E, Either<FF, G>>>>>>
  ): Kind<F, Z> =
    choose(
      fa,
      choose(
        fb,
        choose<C, Either<D, Either<E, Either<FF, G>>>, Either<C, Either<D, Either<E, Either<FF, G>>>>>(
          fc,
          choose(
            fd,
            choose<E, Either<FF, G>, Either<E, Either<FF, G>>>(
              fe,
              choose(ff, fg, ::identity),
              ::identity
            ),
            ::identity
          ),
          ::identity
        ),
        ::identity
      ),
      f
    )

  fun <A, B, C, D, E, FF, G, H, Z> choose(
    fa: Kind<F, A>,
    fb: Kind<F, B>,
    fc: Kind<F, C>,
    fd: Kind<F, D>,
    fe: Kind<F, E>,
    ff: Kind<F, FF>,
    fg: Kind<F, G>,
    fh: Kind<F, H>,
    f: (Z) -> Either<A, Either<B, Either<C, Either<D, Either<E, Either<FF, Either<G, H>>>>>>>
  ): Kind<F, Z> =
    choose(
      fa,
      choose(
        fb,
        choose<C, Either<D, Either<E, Either<FF, Either<G, H>>>>, Either<C, Either<D, Either<E, Either<FF, Either<G, H>>>>>>(
          fc,
          choose(
            fd,
            choose<E, Either<FF, Either<G, H>>, Either<E, Either<FF, Either<G, H>>>>(
              fe,
              choose<FF, Either<G, H>, Either<FF, Either<G, H>>>(
                ff,
                choose(fg, fh, ::identity),
                ::identity
              ),
              ::identity
            ),
            ::identity
          ),
          ::identity
        ),
        ::identity
      ),
      f
    )

  fun <A, B, C, D, E, FF, G, H, I, Z> choose(
    fa: Kind<F, A>,
    fb: Kind<F, B>,
    fc: Kind<F, C>,
    fd: Kind<F, D>,
    fe: Kind<F, E>,
    ff: Kind<F, FF>,
    fg: Kind<F, G>,
    fh: Kind<F, H>,
    fi: Kind<F, I>,
    f: (Z) -> Either<A, Either<B, Either<C, Either<D, Either<E, Either<FF, Either<G, Either<H, I>>>>>>>>
  ): Kind<F, Z> =
    choose(
      fa,
      choose(
        fb,
        choose<C, Either<D, Either<E, Either<FF, Either<G, Either<H, I>>>>>, Either<C, Either<D, Either<E, Either<FF, Either<G, Either<H, I>>>>>>>(
          fc,
          choose(
            fd,
            choose<E, Either<FF, Either<G, Either<H, I>>>, Either<E, Either<FF, Either<G, Either<H, I>>>>>(
              fe,
              choose(
                ff,
                choose<G, Either<H, I>, Either<G, Either<H, I>>>(
                  fg,
                  choose(fh, fi, ::identity),
                  ::identity
                ),
                ::identity
              ),
              ::identity
            ),
            ::identity
          ),
          ::identity
        ),
        ::identity
      ),
      f
    )

  fun <A, B, C, D, E, FF, G, H, I, J, Z> choose(
    fa: Kind<F, A>,
    fb: Kind<F, B>,
    fc: Kind<F, C>,
    fd: Kind<F, D>,
    fe: Kind<F, E>,
    ff: Kind<F, FF>,
    fg: Kind<F, G>,
    fh: Kind<F, H>,
    fi: Kind<F, I>,
    fj: Kind<F, J>,
    f: (Z) -> Either<A, Either<B, Either<C, Either<D, Either<E, Either<FF, Either<G, Either<H, Either<I, J>>>>>>>>>
  ): Kind<F, Z> =
    choose(
      fa,
      choose(
        fb,
        choose<C, Either<D, Either<E, Either<FF, Either<G, Either<H, Either<I, J>>>>>>, Either<C, Either<D, Either<E, Either<FF, Either<G, Either<H, Either<I, J>>>>>>>>(
          fc,
          choose(
            fd,
            choose<E, Either<FF, Either<G, Either<H, Either<I, J>>>>, Either<E, Either<FF, Either<G, Either<H, Either<I, J>>>>>>(
              fe,
              choose(
                ff,
                choose<G, Either<H, Either<I, J>>, Either<G, Either<H, Either<I, J>>>>(
                  fg,
                  choose<H, Either<I, J>, Either<H, Either<I, J>>>(
                    fh,
                    choose(fi, fj, ::identity),
                    ::identity
                  ),
                  ::identity
                ),
                ::identity
              ),
              ::identity
            ),
            ::identity
          ),
          ::identity
        ),
        ::identity
      ),
      f
    )
}
