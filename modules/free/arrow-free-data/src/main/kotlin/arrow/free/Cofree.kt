package arrow.free

import arrow.Kind
import arrow.core.Eval
import arrow.core.ForEval
import arrow.core.fix
import arrow.higherkind
import arrow.core.FunctionK
import arrow.core.extensions.eval.applicative.applicative
import arrow.typeclasses.Functor
import arrow.typeclasses.Monad
import arrow.typeclasses.Traverse

typealias CofreeEval<S, A> = Kind<S, Cofree<S, A>>

@higherkind
data class Cofree<S, A>(val FS: Functor<S>, val head: A, val tail: Eval<CofreeEval<S, A>>) : CofreeOf<S, A>, CofreeKindedJ<S, A>, Functor<S> by FS {

  fun tailForced(): CofreeEval<S, A> = tail.value()

  fun <B> transform(f: (A) -> B, g: (Cofree<S, A>) -> Cofree<S, B>): Cofree<S, B> = Cofree(FS, f(head), tail.map { it.map(g) })

  fun <B> map(f: (A) -> B): Cofree<S, B> = transform(f) { it.map(f) }

  fun mapBranchingRoot(fk: FunctionK<S, S>): Cofree<S, A> = Cofree(FS, head, tail.map { fk(it) })

  fun <T> mapBranchingS(fk: FunctionK<S, T>, FT: Functor<T>): Cofree<T, A> = Cofree(FT, head, tail.map { ce -> fk(ce.map { it.mapBranchingS(fk, FT) }) })

  fun <T> mapBranchingT(fk: FunctionK<S, T>, FT: Functor<T>): Cofree<T, A> = FT.run {
    Cofree(this, head, tail.map { ce -> fk(ce).map { it.mapBranchingT(fk, this) } })
  }

  fun <B> coflatMap(f: (Cofree<S, A>) -> B): Cofree<S, B> = Cofree(FS, f(this), tail.map { it.map { it.coflatMap(f) } })

  fun duplicate(): Cofree<S, Cofree<S, A>> = Cofree(FS, this, tail.map { it.map { it.duplicate() } })

  fun runTail(): Cofree<S, A> = Cofree(FS, head, Eval.now(tail.value()))

  fun run(): Cofree<S, A> = Cofree(FS, head, Eval.now(tail.map { it.map { it.run() } }.value()))

  fun extract(): A = head

  fun <B> cata(folder: (A, Kind<S, B>) -> Eval<B>, TF: Traverse<S>): Eval<B> {
    val ev: Eval<Kind<S, B>> = TF.run { tailForced().traverse(Eval.applicative()) { it.cata(folder, TF) }.fix() }
    return ev.flatMap { folder(extract(), it) }
  }

  companion object {
    fun <S, A> unfold(FS: Functor<S>, a: A, f: (A) -> Kind<S, A>): Cofree<S, A> = create(FS, a, f)

    fun <S, A> create(FS: Functor<S>, a: A, f: (A) -> Kind<S, A>): Cofree<S, A> = FS.run {
      Cofree(this, a, Eval.later { f(a).map { create(this, it, f) } })
    }
  }
}

fun <F, M, A, B> Cofree<F, A>.cataM(MM: Monad<M>, TF: Traverse<F>, inclusion: FunctionK<ForEval, M>, folder: (A, Kind<F, B>) -> Kind<M, B>): Kind<M, B> = MM.run {
  fun loop(ev: Cofree<F, A>): Eval<Kind<M, B>> {
    val looped: Kind<M, Kind<F, B>> = TF.run { ev.tailForced().traverse(MM) { inclusion(Eval.defer { loop(it) }).flatten() } }
    val folded: Kind<M, B> = looped.flatMap { fb -> folder(ev.head, fb) }
    return Eval.now(folded)
  }
  inclusion(loop(this@cataM)).flatten()
}
