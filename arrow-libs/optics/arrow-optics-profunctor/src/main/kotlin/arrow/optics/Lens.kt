package arrow.optics

import arrow.optics.internal.Pro
import arrow.optics.internal.Profunctor
import arrow.optics.internal.Strong

typealias Lens<S, A> = PLens<S, S, A, A>
typealias PLens<S, T, A, B> = Optic<LensK, S, T, A, B>

fun <S, T, A, B> Optic.Companion.lens(
  get: (S) -> A,
  set: (S, B) -> T
): PLens<S, T, A, B> =
  object : PLens<S, T, A, B> {
    override fun <P> Profunctor<P>.transform(focus: Pro<P, A, B>): Pro<P, S, T> =
      (this as Strong<P>).run {
        focus.first<A, B, S>().dimap({ s ->
          get(s) to s
        }, { (b, s) ->
          set(s, b)
        })
      }
  }
