package arrow.recursion.typeclasses

import arrow.Kind
import arrow.core.Eval
import arrow.core.FunctionK
import arrow.core.Tuple2
import arrow.core.andThen
import arrow.core.compose
import arrow.core.extensions.tuple2.functor.functor
import arrow.core.extensions.tuple2.traverse.traverse
import arrow.core.fix
import arrow.core.toT
import arrow.free.Cofree
import arrow.recursion.Algebra
import arrow.recursion.CVAlgebra
import arrow.recursion.CVAlgebraM
import arrow.recursion.Coalgebra
import arrow.recursion.RAlgebra
import arrow.recursion.RAlgebraM
import arrow.recursion.hylo
import arrow.recursion.hyloC
import arrow.recursion.hyloM
import arrow.recursion.hyloMC
import arrow.typeclasses.Functor
import arrow.typeclasses.Monad
import arrow.typeclasses.Traverse

/**
 * ank_macro_hierarchy(arrow.recursion.typeclasses.Recursive)
 *
 * Typeclass for types that can be generically folded with algebras.
 */
interface Recursive<T, F> {

  fun FF(): Functor<F>

  /**
   * Implementation for project.
   */
  fun T.projectT(): Kind<F, T>

  /**
   * Creates a coalgebra given a functor.
   */
  fun project(): Coalgebra<F, T> = { it.projectT() }

  /**
   * Fold over any datatype using that datatypes base-functor.
   *
   * ```kotlin:ank:playground
   * import arrow.Kind
   * import arrow.core.ListK
   * import arrow.core.k
   * import arrow.recursion.Algebra
   * import arrow.recursion.extensions.listk.recursive.recursive
   * import arrow.recursion.pattern.ListF
   * import arrow.recursion.pattern.ListFPartialOf
   * import arrow.recursion.pattern.fix
   *
   * fun main() {
   *  val sumAlgebra: Algebra<ListFPartialOf<Int>, Int> = { list: Kind<ListFPartialOf<Int>, Int> ->
   *    when (val fa = list.fix()) {
   *      is ListF.NilF -> 0
   *      is ListF.ConsF -> fa.a + fa.tail
   *    }
   *  }
   *
   *  ListK.recursive<Int>().run {
   *    (0..100).toList().k().cata(sumAlgebra).also(::println)
   *  }
   * }
   * ```
   *
   * Note: Not stack-safe. Use [cataM] with a stack-safe monad, like [Eval]
   */
  fun <A> T.cata(alg: Algebra<F, A>): A = hylo(alg, project(), FF())

  /**
   * Fold monadically over any datatype using that datatypes base-functor.
   *
   * Can be used to get a stack-safe version of [cata] when the monad itself is stack-safe.
   *
   * ```kotlin:ank:playground
   * import arrow.Kind
   * import arrow.core.Eval
   * import arrow.core.ForEval
   * import arrow.core.ListK
   * import arrow.core.extensions.eval.monad.monad
   * import arrow.core.k
   * import arrow.core.value
   * import arrow.recursion.AlgebraM
   * import arrow.recursion.extensions.listf.traverse.traverse
   * import arrow.recursion.extensions.listk.recursive.recursive
   * import arrow.recursion.pattern.ListF
   * import arrow.recursion.pattern.ListFPartialOf
   * import arrow.recursion.pattern.fix
   *
   * fun main() {
   *  val sumAlgebra: AlgebraM<ListFPartialOf<Int>, ForEval, Int> = { list: Kind<ListFPartialOf<Int>, Int> ->
   *    when (val fa = list.fix()) {
   *      is ListF.NilF -> Eval.now(0)
   *      is ListF.ConsF -> Eval.now(fa.a + fa.tail)
   *    }
   *  }
   *
   *  ListK.recursive<Int>().run {
   *    (0..100).toList().k().cataM(ListF.traverse(), Eval.monad(), sumAlgebra).value().also(::println)
   *  }
   * }
   * ```
   */
  fun <M, A> T.cataM(TF: Traverse<F>, MM: Monad<M>, alg: (Kind<F, A>) -> Kind<M, A>): Kind<M, A> =
    hyloM(alg, project() andThen MM::just, TF, MM)

  /**
   * Fold over any datatype using that datatypes base-functor.
   * Also gives access to the current element and not only the fold.
   *
   * ```kotlin:ank:playground
   * import arrow.core.ForOption
   * import arrow.core.fix
   * import arrow.recursion.RAlgebra
   * import arrow.recursion.extensions.recursive
   *
   * fun main() {
   *  val printAsListAlg: RAlgebra<ForOption, Int, String> = { opt ->
   *    opt.fix().fold(ifEmpty = {
   *      "[]"
   *    }, ifSome = { (curr, fold) ->
   *      "$curr : $fold"
   *    })
   *  }
   *
   *  Int.recursive().run {
   *    10.para(printAsListAlg).also(::println)
   *  }
   * }
   * ```
   *
   * Note: Not stack-safe. Use [paraM] with a stack-safe monad, like [Eval]
   */
  fun <A> T.para(alg: RAlgebra<F, T, A>): A =
    hyloC({
      FF().run { it.map { it.fix() } }.let(alg)
    }, project() andThen { FF().run { it.map { it toT it } } },
      FF(), Tuple2.functor()
    )

  /**
   * Fold monadically over any datatype using that datatypes base-functor.
   * Also gives access to the current element and not only the fold.
   *
   * Can be used to get a stack-safe version of [para] when the monad itself is stack-safe.
   *
   * ```kotlin:ank:playground
   * import arrow.core.Eval
   * import arrow.core.ForEval
   * import arrow.core.ForOption
   * import arrow.core.Option
   * import arrow.core.extensions.eval.monad.monad
   * import arrow.core.extensions.option.traverse.traverse
   * import arrow.core.fix
   * import arrow.core.value
   * import arrow.recursion.RAlgebraM
   * import arrow.recursion.extensions.recursive
   *
   * fun main() {
   *  val printAsListAlg: RAlgebraM<ForOption, ForEval, Int, String> = { opt ->
   *    opt.fix().fold(ifEmpty = {
   *      Eval.now("[]")
   *    }, ifSome = { (curr, fold) ->
   *      Eval.now("$curr : $fold")
   *    })
   *  }
   *
   *  Int.recursive().run {
   *    10.paraM(Option.traverse(), Eval.monad(), printAsListAlg).value().also(::println)
   *  }
   * }
   * ```
   */
  fun <M, A> T.paraM(
    TF: Traverse<F>,
    MM: Monad<M>,
    alg: RAlgebraM<F, M, T, A>
  ): Kind<M, A> =
    hyloMC({
      alg(FF().run { it.map { it.fix() } })
    }, project() andThen { FF().run { it.map { it toT it } } } andThen MM::just, TF, Tuple2.traverse(), MM)

  /**
   * Fold over any datatype using that datatypes base functor.
   * Also gives access to all previous folds inside Cofree.
   *
   * You can thing of histo as sort of providing a shape-shifting cache. This is demonstrated below with a fibonacci implementation
   *  that does the minimum of work needed by using this cache.
   * Since the shape is always determined by the Functor F the cache with Option takes the form of an ordinary list, but with
   *  different functors you get different shapes (Lists will make a Tree structure for example)
   *
   * ```kotlin:ank:playground
   * import arrow.core.ForOption
   * import arrow.core.fix
   * import arrow.recursion.CVAlgebra
   * import arrow.recursion.extensions.recursive
   *
   * fun main() {
   *  // fib 0 = 0
   *  // fib 1 = 1
   *  // fib n = fib (n - 1) + fib (n - 2)
   *  val fibAlg: CVAlgebra<ForOption, Int> = { opt ->
   *    opt.fix()
   *      .fold(ifEmpty = { 0 }, ifSome = { nMinus1 ->
   *        nMinus1.tail.value().fix()
   *          .fold(ifEmpty = { 1 }, ifSome = { nMinus2 ->
   *            // we had to calculate something, so print it for showing purposes
   *            println("(${nMinus1.head} + ${nMinus2.head})")
   *            nMinus1.head + nMinus2.head
   *          })
   *      })
   *  }
   *
   *  Int.recursive().run {
   *    10.histo(fibAlg).also(::println)
   *  }
   * }
   * ```
   *
   * Note: Not stack-safe. Use [histoM] with a stack-safe monad, like [Eval]
   */
  fun <A> T.histo(alg: CVAlgebra<F, A>): A =
    hylo<F, T, Cofree<F, A>>({
      Cofree(FF(), alg(it), Eval.now(it))
    }, project(),
      FF()
    ).head

  /**
   * Fold monadically over any datatype using that datatypes base functor.
   * Also gives access to all previous folds inside Cofree.
   *
   * Can be used to get a stack-safe version of [histo] when the monad itself is stack-safe.
   *
   * The following example is shows stack-safety but since the 5000's fibonacci-number is higher than
   *  Long.MAX_VALUE the result is bullshit.
   * ```kotlin:ank:playground
   * import arrow.core.Eval
   * import arrow.core.ForEval
   * import arrow.core.ForOption
   * import arrow.core.Option
   * import arrow.core.extensions.eval.monad.monad
   * import arrow.core.extensions.option.traverse.traverse
   * import arrow.core.fix
   * import arrow.core.value
   * import arrow.recursion.CVAlgebraM
   * import arrow.recursion.extensions.recursive
   *
   * fun main() {
   *  // fib 0 = 0
   *  // fib 1 = 1
   *  // fib n = fib (n - 1) + fib (n - 2)
   *  val fibAlg: CVAlgebraM<ForOption, ForEval, Long> = { opt ->
   *    opt.fix().fold(ifEmpty = { Eval.now(0L) }, ifSome = { nMinus1 ->
   *      nMinus1.tail.flatMap { nMinus2Opt ->
   *        nMinus2Opt.fix().fold(ifEmpty = { Eval.now(1L) }, ifSome = { nMinus2 ->
   *          println("(${nMinus1.head} + ${nMinus2.head})")
   *          Eval.now(nMinus1.head + nMinus2.head)
   *        })
   *      }
   *    })
   *  }
   *
   *  Long.recursive().run {
   *    5000L.histoM(Option.traverse(), Eval.monad(), fibAlg).value().also(::println)
   *  }
   * }
   * ```
   */
  fun <M, A> T.histoM(TF: Traverse<F>, MM: Monad<M>, alg: CVAlgebraM<F, M, A>): Kind<M, A> =
    MM.run {
      hyloM<F, M, T, Cofree<F, A>>({
        alg(it).map { a -> Cofree(TF, a, Eval.now(it)) }
      }, project() andThen MM::just,
        TF, MM
      ).map { it.head }
    }

  /**
   * Fold over a structure with a normal algebra, but applies a natural transformation (FunctionK) before the fold
   */
  fun <A> T.prepro(trans: FunctionK<F, F>, alg: Algebra<F, A>): A =
    hylo(alg compose trans::invoke, project(), FF())
}
