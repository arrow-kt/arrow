package arrow.typeclasses

import arrow.Kind
import arrow.KindDeprecation
import arrow.core.ForOption
import arrow.core.Option
import arrow.core.Tuple2
import arrow.core.fix

@Deprecated(KindDeprecation)
/**
 * MonadLogic is a typeclass that extends a MonadPlus. It provides functions to control
 * when computations should be performed.
 */
interface MonadLogic<F> : MonadPlus<F> {

  /**
   * attempt to split the computation, giving access to the first result.
   *
   *  {: data-executable='true'}
   *
   * ```kotlin:ank
   * import arrow.core.extensions.*
   * import arrow.core.extensions.listk.monadLogic.monadLogic
   * import arrow.core.*
   *
   * fun main(args: Array<String>) {
   *   //sampleStart
   *   val result = ListK.monadLogic().run {
   *    listOf("A", "B", "C").k().splitM()
   *   }
   *   //sampleEnd
   *   println(result)
   * }
   */
  fun <A> Kind<F, A>.splitM(): Kind<F, Option<Tuple2<Kind<F, A>, A>>>

  /**
   * interleave both computations in a fair way.
   *
   *  {: data-executable='true'}
   *
   * ```kotlin:ank
   * import arrow.core.extensions.*
   * import arrow.core.extensions.sequencek.monadLogic.monadLogic
   * import arrow.core.*
   *
   * fun main(args: Array<String>) {
   *   //sampleStart
   *   val infinite = generateSequence { "#" }.k()
   *   val result = SequenceK.monadLogic().run {
   *    infinite.interleave(sequenceOf("A", "B", "C").k()).fix()
   *   }.take(3).toList()
   *   //sampleEnd
   *   println(result)
   * }
   */
  fun <A> Kind<F, A>.interleave(other: Kind<F, A>): Kind<F, A> =
    this.splitM().flatMap { option ->
      option.fold({ other }, { (fa, a) -> just(a).plusM(other.interleave(fa)) })
    }

  /**
   * Fair conjunction. Similarly to interleave
   *
   * {: data-executable='true'}
   *
   * ```kotlin:ank
   * import arrow.core.extensions.*
   * import arrow.core.extensions.listk.monadLogic.monadLogic
   * import arrow.core.*
   *
   * fun main(args: Array<String>) {
   *   //sampleStart
   *   val result = ListK.monadLogic().run {
   *    listOf(1,2,3).k().unweave { just("$it") }
   *   }
   *   //sampleEnd
   *   println(result)
   * }
   */
  fun <A, B> Kind<F, A>.unweave(ffa: (A) -> Kind<F, B>): Kind<F, B> =
    splitM().flatMap { option ->
      option.fold({ zeroM<Tuple2<Kind<F, A>, A>>() }, { just(it) })
    }.flatMap { (fa, a) ->
      ffa(a).interleave(fa.unweave(ffa))
    }

  /**
   * Logical conditional. The equivalent of Prolog's soft-cut.
   * If its first argument succeeds at all, then the results will be
   * fed into the success branch. Otherwise, the failure branch is taken.
   *
   * {: data-executable='true'}
   *
   * ```kotlin:ank
   * import arrow.core.extensions.*
   * import arrow.core.extensions.listk.monadLogic.monadLogic
   * import arrow.core.*
   *
   * fun main(args: Array<String>) {
   *   //sampleStart
   *   val result = ListK.monadLogic().run {
   *    listOf(1,2,3).k().ifThen(ListK.just("empty")) {
   *      ListK.just("$it")
   *    }
   *   }
   *   //sampleEnd
   *   println(result)
   * }
   */
  fun <A, B> Kind<F, A>.ifThen(fb: Kind<F, B>, ffa: (A) -> Kind<F, B>): Kind<F, B> =
    splitM().flatMap { option ->
      option.fold({ fb }, { (fa, a) -> ffa(a).plusM(fa.flatMap(ffa)) })
    }

  /**
   * Pruning. Selects one result out of many.
   * Useful for when multiple results of a computation will be equivalent, or should be treated as such.
   *
   * {: data-executable='true'}
   *
   * ```kotlin:ank
   * import arrow.core.extensions.*
   * import arrow.core.extensions.listk.monadLogic.monadLogic
   * import arrow.core.*
   *
   * fun main(args: Array<String>) {
   *   //sampleStart
   *   val result = ListK.monadLogic().run {
   *    listOf(1,2,3).k().once()
   *   }
   *   //sampleEnd
   *   println(result)
   * }
   */
  fun <A> Kind<F, A>.once(): Kind<F, A> =
    splitM().flatMap { option ->
      option.fold({ zeroM<A>() }, { just(it.b) })
    }

  /**
   * Inverts a logic computation. If F succeeds with at least one value, voidIfValue m fails. If F fails, then voidIfValue F succeeds the value Unit.
   *
   * {: data-executable='true'}
   *
   * ```kotlin:ank
   * import arrow.core.extensions.*
   * import arrow.core.extensions.listk.monadLogic.monadLogic
   * import arrow.core.*
   *
   * fun main(args: Array<String>) {
   *   //sampleStart
   *   val result = ListK.monadLogic().run {
   *    listOf(1,2,3).k().voidIfValue()
   *   }
   *   //sampleEnd
   *   println(result)
   * }
   */
  fun <A> Kind<F, A>.voidIfValue(): Kind<F, Unit> =
    once().ifThen(Unit.just()) { zeroM() }
  // TODO fix this when the name ambiguities of unit() are removed and not just deprecated.
}

/**
 * The inverse of splitM.
 *
 * {: data-executable='true'}
 *
 * ```kotlin:ank
 * import arrow.core.extensions.*
 * import arrow.core.extensions.listk.monadLogic.monadLogic
 * import arrow.core.*
 * import arrow.typeclasses.reflect
 *
 * fun main(args: Array<String>) {
 *  //sampleStart
 *  val result = ListK.monadLogic().run {
 *    listOf(1, 2, 3).k().splitM().flatMap {
 *      it.reflect(this)
 *    }
 *  }
 *  //sampleEnd
 *  println(result)
 * }
 */
// TODO: this should be direct part of the MonadLogic typeclass (https://github.com/arrow-kt/arrow/pull/2047#discussion_r378201777).
//  Couldn't add it due to issues with the generated code. Should be reworked when arrow-meta is available.
fun <F, A> Kind<ForOption, Tuple2<Kind<F, A>, A>>.reflect(ML: MonadLogic<F>): Kind<F, A> =
  fix().fold({ ML.zeroM() }, { ML.run { just(it.b).plusM(it.a) } })
