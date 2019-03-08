package arrow.data

import arrow.Kind
import arrow.core.*
import arrow.higherkind
import arrow.typeclasses.*

@higherkind
data class Coproduct<F, G, A>(val run: Either<Kind<F, A>, Kind<G, A>>) : CoproductOf<F, G, A>, CoproductKindedJ<F, G, A> {

  fun <B> map(CF: Functor<F>, CG: Functor<G>, f: (A) -> B): Coproduct<F, G, B> =
    Coproduct(run.bimap(CF.lift(f), CG.lift(f)))

  fun <B> contramap(CF: Contravariant<F>, CG: Contravariant<G>, f: (B) -> A): Coproduct<F, G, B> =
    Coproduct(run.bimap(CF.lift(f), CG.lift(f)))

  fun <B> coflatMap(CF: Comonad<F>, CG: Comonad<G>, f: (Coproduct<F, G, A>) -> B): Coproduct<F, G, B> =
    Coproduct(run.bimap(
      { CF.run { it.coflatMap { f(Coproduct(Left(it))) } } },
      { CG.run { it.coflatMap { f(Coproduct(Right(it))) } } }
    ))

  fun extract(CF: Comonad<F>, CG: Comonad<G>): A =
    run.fold({ CF.run { it.extract() } }, { CG.run { it.extract() } })

  fun <H> fold(f: FunctionK<F, H>, g: FunctionK<G, H>): Kind<H, A> =
    run.fold({ f(it) }, { g(it) })

  fun <B> foldLeft(b: B, f: (B, A) -> B, FF: Foldable<F>, FG: Foldable<G>): B =
    run.fold({ FF.run { it.foldLeft(b, f) } }, { FG.run { it.foldLeft(b, f) } })

  fun <B> foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>, FF: Foldable<F>, FG: Foldable<G>): Eval<B> =
    run.fold({ FF.run { it.foldRight(lb, f) } }, { FG.run { it.foldRight(lb, f) } })

  fun <H, B> traverse(GA: Applicative<H>, FT: Traverse<F>, GT: Traverse<G>, f: (A) -> Kind<H, B>): Kind<H, Coproduct<F, G, B>> = GA.run {
    run.fold({
      FT.run { it.traverse(GA, f) }.map { Coproduct<F, G, B>(Left(it)) }
    }, {
      GT.run { it.traverse(GA, f) }.map { Coproduct<F, G, B>(Right(it)) }
    })
  }

  companion object {
    operator fun <F, G, A> invoke(run: Either<Kind<F, A>, Kind<G, A>>): Coproduct<F, G, A> =
      Coproduct(run)
  }
}

fun <F, G, A, H> CoproductOf<F, G, Kind<H, A>>.sequence(HA: Applicative<H>, FT: Traverse<F>, GT: Traverse<G>): Kind<H, Coproduct<F, G, A>> =
  fix().traverse(HA, FT, GT, ::identity)

fun <F, G, A> EitherOf<Kind<F, A>, Kind<G, A>>.coproduct(): Coproduct<F, G, A> =
  Coproduct(fix())

fun <F, G, H> FunctionK<F, G>.or(h: FunctionK<H, G>): FunctionK<CoproductPartialOf<F, H>, G> =
  object : FunctionK<CoproductPartialOf<F, H>, G> {
    override fun <A> invoke(fa: CoproductOf<F, H, A>): Kind<G, A> {
      return fa.fix().fold(this@or, h)
    }
  }
