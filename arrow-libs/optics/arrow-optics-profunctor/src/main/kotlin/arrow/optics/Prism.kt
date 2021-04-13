package arrow.optics

import arrow.core.Either
import arrow.core.identity
import arrow.optics.internal.Choice
import arrow.optics.internal.Pro
import arrow.optics.internal.Profunctor

typealias Prism<S, A> = PPrism<S, S, A, A>
typealias PPrism<S, T, A, B> = Optic<PrismK, S, T, A, B>

fun <S, T, A ,B> Optic.Companion.prism(
  match: (S) -> Either<T, A>,
  re: (B) -> T
): PPrism<S, T, A, B> =
  object : PPrism<S, T, A, B> {
    override fun <P> Profunctor<P>.transform(focus: Pro<P, A, B>): Pro<P, S, T> =
      (this as Choice<P>).run {
        focus.right<A, B, T>().dimap({ s ->
          match(s)
        }, { e ->
          e.fold(::identity) { b -> re(b) }
        })
      }
  }

fun <S, A> Optic.Companion.simplePrism(
  match: (S) -> A?,
  update: (A) -> S
): Prism<S, A> =
  prism({ s ->
    match(s)?.let { a -> Either.Right(a) } ?: Either.Left(s)
  }, { b -> update(b) })

