package arrow.typeclasses

import arrow.Kind
import arrow.core.Tuple2

interface Functor<F> : Invariant<F> {

  fun <A, B> Kind<F, A>.map(f: (A) -> B): Kind<F, B>

  override fun <A, B> Kind<F, A>.imap(f: (A) -> B, g: (B) -> A): Kind<F, B> =
    map(f)

  fun <A, B> lift(f: (A) -> B): (Kind<F, A>) -> Kind<F, B> =
    { fa: Kind<F, A> ->
      fa.map(f)
    }

  fun <A> Kind<F, A>.void(): Kind<F, Unit> = map { _ -> Unit }

  fun <A, B> Kind<F, A>.fproduct(f: (A) -> B): Kind<F, Tuple2<A, B>> = map { a -> Tuple2(a, f(a)) }

  fun <A, B> Kind<F, A>.`as`(b: B): Kind<F, B> = map { _ -> b }

  fun <A, B> Kind<F, A>.tupleLeft(b: B): Kind<F, Tuple2<B, A>> = map { a -> Tuple2(b, a) }

  fun <A, B> Kind<F, A>.tupleRight(b: B): Kind<F, Tuple2<A, B>> = map { a -> Tuple2(a, b) }

  fun <B, A : B> Kind<F, A>.widen(): Kind<F, B> = this
}
