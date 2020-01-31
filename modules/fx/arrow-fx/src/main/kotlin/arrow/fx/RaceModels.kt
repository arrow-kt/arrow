package arrow.fx

import arrow.core.Either
import arrow.fx.typeclasses.Fiber

/** Alias for `Either` structure to provide consistent signature for race methods. */
sealed class RacePair<F, A, B> {
  data class First<F, A, B>(val winner: A, val fiberB: Fiber<F, B>) : RacePair<F, A, B>()
  data class Second<F, A, B>(val fiberA: Fiber<F, A>, val winner: B) : RacePair<F, A, B>()

  inline fun <C> fold(
    ifA: (A, Fiber<F, B>) -> C,
    ifB: (Fiber<F, A>, B) -> C
  ): C = when (this) {
    is First -> ifA(winner, fiberB)
    is Second -> ifB(fiberA, winner)
  }
}

sealed class RaceTriple<F, A, B, C> {
  data class First<F, A, B, C>(val winner: A, val fiberB: Fiber<F, B>, val fiberC: Fiber<F, C>) : RaceTriple<F, A, B, C>()
  data class Second<F, A, B, C>(val fiberA: Fiber<F, A>, val winner: B, val fiberC: Fiber<F, C>) : RaceTriple<F, A, B, C>()
  data class Third<F, A, B, C>(val fiberA: Fiber<F, A>, val fiberB: Fiber<F, B>, val winner: C) : RaceTriple<F, A, B, C>()

  inline fun <D> fold(
    ifA: (A, Fiber<F, B>, Fiber<F, C>) -> D,
    ifB: (Fiber<F, A>, B, Fiber<F, C>) -> D,
    ifC: (Fiber<F, A>, Fiber<F, B>, C) -> D
  ): D = when (this) {
    is First -> ifA(winner, fiberB, fiberC)
    is Second -> ifB(fiberA, winner, fiberC)
    is Third -> ifC(fiberA, fiberB, winner)
  }
}

/** Alias for `Either` structure to provide consistent signature for race methods. */
typealias Race2<A, B> = Either<A, B>

sealed class Race3 <out A, out B, out C> {
  data class First<A>(val winner: A) : Race3<A, Nothing, Nothing>()
  data class Second<B>(val winner: B) : Race3<Nothing, B, Nothing>()
  data class Third<C>(val winner: C) : Race3<Nothing, Nothing, C>()

  inline fun <D> fold(
    ifA: (A) -> D,
    ifB: (B) -> D,
    ifC: (C) -> D
  ): D = when (this) {
    is First -> ifA(winner)
    is Second -> ifB(winner)
    is Third -> ifC(winner)
  }
}

sealed class Race4 <out A, out B, out C, out D> {
  data class First<A>(val winner: A) : Race4<A, Nothing, Nothing, Nothing>()
  data class Second<B>(val winner: B) : Race4<Nothing, B, Nothing, Nothing>()
  data class Third<C>(val winner: C) : Race4<Nothing, Nothing, C, Nothing>()
  data class Fourth<D>(val winner: D) : Race4<Nothing, Nothing, Nothing, D>()

  inline fun <E> fold(
    ifA: (A) -> E,
    ifB: (B) -> E,
    ifC: (C) -> E,
    ifD: (D) -> E
  ): E = when (this) {
    is First -> ifA(winner)
    is Second -> ifB(winner)
    is Third -> ifC(winner)
    is Fourth -> ifD(winner)
  }
}

sealed class Race5 <out A, out B, out C, out D, out E> {
  data class First<A>(val winner: A) : Race5<A, Nothing, Nothing, Nothing, Nothing>()
  data class Second<B>(val winner: B) : Race5<Nothing, B, Nothing, Nothing, Nothing>()
  data class Third<C>(val winner: C) : Race5<Nothing, Nothing, C, Nothing, Nothing>()
  data class Fourth<D>(val winner: D) : Race5<Nothing, Nothing, Nothing, D, Nothing>()
  data class Fifth<E>(val winner: E) : Race5<Nothing, Nothing, Nothing, Nothing, E>()

  inline fun <F> fold(
    ifA: (A) -> F,
    ifB: (B) -> F,
    ifC: (C) -> F,
    ifD: (D) -> F,
    ifE: (E) -> F
  ): F = when (this) {
    is First -> ifA(winner)
    is Second -> ifB(winner)
    is Third -> ifC(winner)
    is Fourth -> ifD(winner)
    is Fifth -> ifE(winner)
  }
}

sealed class Race6 <out A, out B, out C, out D, out E, out F> {
  data class First<A>(val winner: A) : Race6<A, Nothing, Nothing, Nothing, Nothing, Nothing>()
  data class Second<B>(val winner: B) : Race6<Nothing, B, Nothing, Nothing, Nothing, Nothing>()
  data class Third<C>(val winner: C) : Race6<Nothing, Nothing, C, Nothing, Nothing, Nothing>()
  data class Fourth<D>(val winner: D) : Race6<Nothing, Nothing, Nothing, D, Nothing, Nothing>()
  data class Fifth<E>(val winner: E) : Race6<Nothing, Nothing, Nothing, Nothing, E, Nothing>()
  data class Sixth<F>(val winner: F) : Race6<Nothing, Nothing, Nothing, Nothing, Nothing, F>()

  inline fun <G> fold(
    ifA: (A) -> G,
    ifB: (B) -> G,
    ifC: (C) -> G,
    ifD: (D) -> G,
    ifE: (E) -> G,
    ifF: (F) -> G
  ): G = when (this) {
    is First -> ifA(winner)
    is Second -> ifB(winner)
    is Third -> ifC(winner)
    is Fourth -> ifD(winner)
    is Fifth -> ifE(winner)
    is Sixth -> ifF(winner)
  }
}

sealed class Race7 <out A, out B, out C, out D, out E, out F, out G> {
  data class First<A>(val winner: A) : Race7<A, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing>()
  data class Second<B>(val winner: B) : Race7<Nothing, B, Nothing, Nothing, Nothing, Nothing, Nothing>()
  data class Third<C>(val winner: C) : Race7<Nothing, Nothing, C, Nothing, Nothing, Nothing, Nothing>()
  data class Fourth<D>(val winner: D) : Race7<Nothing, Nothing, Nothing, D, Nothing, Nothing, Nothing>()
  data class Fifth<E>(val winner: E) : Race7<Nothing, Nothing, Nothing, Nothing, E, Nothing, Nothing>()
  data class Sixth<F>(val winner: F) : Race7<Nothing, Nothing, Nothing, Nothing, Nothing, F, Nothing>()
  data class Seventh<G>(val winner: G) : Race7<Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, G>()

  inline fun <H> fold(
    ifA: (A) -> H,
    ifB: (B) -> H,
    ifC: (C) -> H,
    ifD: (D) -> H,
    ifE: (E) -> H,
    ifF: (F) -> H,
    ifG: (G) -> H
  ): H = when (this) {
    is First -> ifA(winner)
    is Second -> ifB(winner)
    is Third -> ifC(winner)
    is Fourth -> ifD(winner)
    is Fifth -> ifE(winner)
    is Sixth -> ifF(winner)
    is Seventh -> ifG(winner)
  }
}

sealed class Race8 <out A, out B, out C, out D, out E, out F, out G, out H> {
  data class First<A>(val winner: A) : Race8<A, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing>()
  data class Second<B>(val winner: B) : Race8<Nothing, B, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing>()
  data class Third<C>(val winner: C) : Race8<Nothing, Nothing, C, Nothing, Nothing, Nothing, Nothing, Nothing>()
  data class Fourth<D>(val winner: D) : Race8<Nothing, Nothing, Nothing, D, Nothing, Nothing, Nothing, Nothing>()
  data class Fifth<E>(val winner: E) : Race8<Nothing, Nothing, Nothing, Nothing, E, Nothing, Nothing, Nothing>()
  data class Sixth<F>(val winner: F) : Race8<Nothing, Nothing, Nothing, Nothing, Nothing, F, Nothing, Nothing>()
  data class Seventh<G>(val winner: G) : Race8<Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, G, Nothing>()
  data class Eighth<H>(val winner: H) : Race8<Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, H>()

  inline fun <I> fold(
    ifA: (A) -> I,
    ifB: (B) -> I,
    ifC: (C) -> I,
    ifD: (D) -> I,
    ifE: (E) -> I,
    ifF: (F) -> I,
    ifG: (G) -> I,
    ifH: (H) -> I
  ): I = when (this) {
    is First -> ifA(winner)
    is Second -> ifB(winner)
    is Third -> ifC(winner)
    is Fourth -> ifD(winner)
    is Fifth -> ifE(winner)
    is Sixth -> ifF(winner)
    is Seventh -> ifG(winner)
    is Eighth -> ifH(winner)
  }
}

sealed class Race9 <out A, out B, out C, out D, out E, out F, out G, out H, out I> {
  data class First<A>(val winner: A) : Race9<A, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing>()
  data class Second<B>(val winner: B) : Race9<Nothing, B, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing>()
  data class Third<C>(val winner: C) : Race9<Nothing, Nothing, C, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing>()
  data class Fourth<D>(val winner: D) : Race9<Nothing, Nothing, Nothing, D, Nothing, Nothing, Nothing, Nothing, Nothing>()
  data class Fifth<E>(val winner: E) : Race9<Nothing, Nothing, Nothing, Nothing, E, Nothing, Nothing, Nothing, Nothing>()
  data class Sixth<F>(val winner: F) : Race9<Nothing, Nothing, Nothing, Nothing, Nothing, F, Nothing, Nothing, Nothing>()
  data class Seventh<G>(val winner: G) : Race9<Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, G, Nothing, Nothing>()
  data class Eighth<H>(val winner: H) : Race9<Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, H, Nothing>()
  data class Ninth<I>(val winner: I) : Race9<Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, I>()

  inline fun <J> fold(
    ifA: (A) -> J,
    ifB: (B) -> J,
    ifC: (C) -> J,
    ifD: (D) -> J,
    ifE: (E) -> J,
    ifF: (F) -> J,
    ifG: (G) -> J,
    ifH: (H) -> J,
    ifI: (I) -> J
  ): J = when (this) {
    is First -> ifA(winner)
    is Second -> ifB(winner)
    is Third -> ifC(winner)
    is Fourth -> ifD(winner)
    is Fifth -> ifE(winner)
    is Sixth -> ifF(winner)
    is Seventh -> ifG(winner)
    is Eighth -> ifH(winner)
    is Ninth -> ifI(winner)
  }
}
