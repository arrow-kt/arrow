package arrow.aql

import arrow.core.Tuple2
import arrow.core.Tuple3
import arrow.core.toT
import arrow.typeclasses.Applicative

interface From<F> {

  fun applicative(): Applicative<F>

  infix fun <A, B> Source<F, A>.join(fb: Source<F, B>): Source<F, Tuple2<A, B>> =
    applicative().run { this@join.product(fb) }

  fun <A, B, C> Source<F, Tuple2<A, B>>.join(fc: Source<F, C>, dummy: Unit = Unit): Source<F, Tuple3<A, B, C>> =
    applicative().run { this@join.product(fc) }

  infix fun <A, B, Z, X> Query<F, A, Z>.join(query: Query<F, B, X>): Query<F, Tuple2<A, B>, Tuple2<Z, X>> =
    applicative().run {
      Query(
        select = { select(a) toT query.select(b) },
        from = from.product(query.from)
      )
    }
}
