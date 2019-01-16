package arrow.aql

import arrow.typeclasses.Functor

/**
 * `select` allows obtaining and transforming data from any data source containing
 * `A` given a function `(A) -> B` where `A` denotes the input type and `B` the
 * transformed type.
 *
 * Select represents a selection of data from a given data source.
 * The underlying implementation delegates directly to the [functor] instance
 * and continues the fluid builder or infix style expression
 */
interface Select<F> {

  fun functor(): Functor<F>

  infix fun <A, Z> Source<F, A>.query(f: Source<F, A>.() -> Z): Z =
    f(this)

  /**
   * Commented method or class
   *
   * {: data-executable='true'}
   *
   * ```kotlin:ank
   * import arrow.aql.extensions.list.select.*
   * import arrow.aql.extensions.listk.select.select
   *
   * fun main(args: Array<String>) {
   *   //sampleStart
   *   val result: List<Int> =
   *     listOf(1, 2, 3).query {
   *       select { this + 1 }
   *     }.value()
   *  //sampleEnd
   *  println(result)
   *  }
   *  ```
   */
  infix fun <A, Z> Source<F, A>.select(f: Selection<A, Z>): Query<F, A, Z> =
    Query(f, this)

  fun <A> Source<F, A>.selectAll(): Query<F, A, A> =
    Query({ this }, this)

  fun <A, Z> Query<F, A, Z>.value(): Source<F, Z> =
    functor().run { from.map(select) }

}