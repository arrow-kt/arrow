package arrow.aql

import arrow.Kind
import arrow.core.Tuple2
import arrow.core.Tuple3
import arrow.typeclasses.Applicative

interface From<F> {

  fun applicative(): Applicative<F>

  fun <A, B> Source<F, A>.join(fb: Kind<F, B>): Source<F, Tuple2<A, B>> =
    applicative().run { this@join.product(fb) }

  fun <A, B, C> Source<F, Tuple2<A, B>>.join(fc: Source<F, C>, dummy: Unit = Unit): Source<F, Tuple3<A, B, C>> =
    applicative().run { this@join.product(fc) }

}