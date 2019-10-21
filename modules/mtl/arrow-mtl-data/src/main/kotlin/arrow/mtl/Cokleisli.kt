package arrow.mtl

import arrow.Kind
import arrow.higherkind
import arrow.typeclasses.Comonad

typealias CokleisliFun<F, A, B> = (Kind<F, A>) -> B

typealias CoreaderT<F, A, B> = Cokleisli<F, A, B>

@higherkind
data class Cokleisli<F, A, B>(val MM: Comonad<F>, val run: CokleisliFun<F, A, B>) : CokleisliOf<F, A, B>, CokleisliKindedJ<F, A, B>, Comonad<F> by MM {

  fun <C, D> bimap(g: (D) -> A, f: (B) -> C): Cokleisli<F, D, C> = Cokleisli(MM) { f(run(it.map(g))) }

  fun <D> lmap(g: (D) -> A): Cokleisli<F, D, B> = Cokleisli(MM) { run(it.map(g)) }

  fun <C> map(f: (B) -> C): Cokleisli<F, A, C> = Cokleisli(MM) { f(run(it)) }

  fun <C> contramapValue(f: (Kind<F, C>) -> Kind<F, A>): Cokleisli<F, C, B> = Cokleisli(MM) { run(f(it)) }

  fun <D> compose(a: Cokleisli<F, D, A>): Cokleisli<F, D, B> = Cokleisli(MM) { run(it.coflatMap(a.run)) }

  @JvmName("andThenK")
  fun <C> andThen(a: Kind<F, C>): Cokleisli<F, A, C> = Cokleisli(MM) { run { a.extract() } }

  fun <C> andThen(a: Cokleisli<F, B, C>): Cokleisli<F, A, C> = a.compose(this)

  fun <C> flatMap(f: (B) -> Cokleisli<F, A, C>): Cokleisli<F, A, C> = Cokleisli(MM) { f(run(it)).run(it) }

  companion object {
    operator fun <F, A, B> invoke(MF: Comonad<F>, run: (Kind<F, A>) -> B): Cokleisli<F, A, B> = Cokleisli(MF, run)

    fun <F, A, B> just(MF: Comonad<F>, b: B): Cokleisli<F, A, B> = Cokleisli(MF) { b }

    fun <F, B> ask(MF: Comonad<F>): Cokleisli<F, B, B> = Cokleisli(MF) { MF.run { it.extract() } }
  }
}
