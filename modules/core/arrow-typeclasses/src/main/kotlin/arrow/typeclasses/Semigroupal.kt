package arrow.typeclasses

import arrow.Kind
import arrow.core.*
import arrow.documented

/**
 * ank_macro_hierarchy(arrow.typeclasses.Semigroupal)
 *
 * The [Semigroupal] type class for a given type `F` can be seen as an abstraction over the [cartesian product](https://en.wikipedia.org/wiki/Cartesian_product).
 * It defines the function [product].
 *
 * The [product] function for a given type `F`, `A` and `B` combines a `Kind<F, A>` and a `Kind<F, B>` into a `Kind<F, Tuple2<A, B>>`.
 * This function guarantees compliance with the following laws:
 *
 * [Semigroupal]s are associative under the bijection `f = (a,(b,c)) -> ((a,b),c)` or `f = ((a,b),c) -> (a,(b,c))`.
 * Therefore, the following laws also apply:
 *
 * ```kotlin
 * f((a.product(b)).product(c)) == a.product(b.product(c))
 * ```
 *
 * ```kotlin
 * f(a.product(b.product(c))) == (a.product(b)).product(c)
 * ```
 *
 * Currently, [Semigroupal] instances are defined for [Option], [ListK], [SequenceK] and [SetK].
 *
 * ```kotlin:ank:playground:extension
 * _imports_
 *
 * fun main(args: Array<String>) {
 *   val result =
 *   //sampleStart
 *   _extensionFactory_
 *   //sampleEnd
 *   println(result)
 * }
 * ```
 *
 * ### Examples
 *
 * Here a some examples:
 *
 * ```kotlin:ank:playground
 * import arrow.core.Option
 * import arrow.core.extensions.option.semigroupal.semigroupal
 *
 * fun main(args: Array<String>) {
 *   val result =
 *   //sampleStart
 *   Option.semigroupal().run {
 *       Option.just(1).product(Option.just(1))
 *   }
 *   //sampleEnd
 *   println(result)
 * }
 * ```
 *
 * [Semigroupal] also has support of the `*` syntax:
 *
 * ```kotlin:ank:playground
 * import arrow.core.Option
 * import arrow.core.extensions.option.semigroupal.semigroupal
 *
 * fun main(args: Array<String>) {
 *   val result =
 *   //sampleStart
 *   Option.semigroupal().run {
 *       Option.just(1) * Option.just(2)
 *   }
 *   //sampleEnd
 *   println(result)
 * }
 * ```
 * The same applies to [ListK], [SequenceK] and [SetK] instances:
 *
 * ```kotlin:ank:playground
 * import arrow.data.ListK
 * import arrow.data.extensions.listk.semigroupal.semigroupal
 * import arrow.data.k
 *
 * fun main(args: Array<String>) {
 *   val result =
 *   //sampleStart
 *   ListK.semigroupal().run {
 *       listOf(1,2,3).k() * listOf('a','b','c').k()
 *   }
 *   //sampleEnd
 *   println(result)
 * }
 * ```
 */
@documented
interface Semigroupal<F> {

    /**
     * Multiplicatively combine F<A> and F<B> into F<Tuple2<A, B>>
     */
    fun <A, B> Kind<F, A>.product(fb: Kind<F, B>): Kind<F, Tuple2<A, B>>

    /**
     * Add support for the * syntax
     */
    operator fun <A, B> Kind<F, A>.times(fb: Kind<F, B>): Kind<F, Tuple2<A, B>> =
            this.product(fb)
}
