package arrow.aql

import arrow.typeclasses.Functor

/**
 * SELECT * FROM Student
 *
 *  listOf(1, 2, 3)
 *    .select { it * 10 }
 *    .value()
 *    //listOf(10, 20, 30)
 */
interface Select<F> {

  fun functor(): Functor<F>

  infix fun <A, Z> Source<F, A>.query(f: Source<F, A>.() -> Z): Z =
    f(this)

  infix fun <A, Z> Source<F, A>.select(f: Selection<A, Z>): Query<F, A, Z> =
    Query(f, this)

  fun <A> Source<F, A>.selectAll(): Query<F, A, A> =
    Query({ this }, this)

  fun <A, Z> Query<F, A, Z>.value(): Source<F, Z> =
    functor().run { from.map(select) }

}