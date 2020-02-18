package arrow.mtl

import arrow.Kind
import arrow.core.Either
import arrow.core.extensions.either.eq.eq
import arrow.fx.ForIO
import arrow.fx.IO
import arrow.fx.extensions.io.applicative.applicative
import arrow.fx.extensions.io.concurrent.waitFor
import arrow.fx.fix
import arrow.fx.typeclasses.Duration
import arrow.fx.typeclasses.seconds
import arrow.test.generators.GenK
import arrow.typeclasses.Eq
import arrow.typeclasses.EqK
import io.kotlintest.properties.Gen

fun IO.Companion.eqK(timeout: Duration = 60.seconds) = object : EqK<ForIO> {
  override fun <A> Kind<ForIO, A>.eqK(other: Kind<ForIO, A>, EQ: Eq<A>): Boolean =
    Either.eq(Eq.any(), EQ).run {
      IO.applicative().mapN(fix().attempt(), other.fix().attempt()) { (a, b) -> a.eqv(b) }
        .waitFor(timeout)
        .unsafeRunSync()
    }
}

fun IO.Companion.genK() = object : GenK<ForIO> {
  override fun <A> genK(gen: Gen<A>): Gen<Kind<ForIO, A>> =
    gen.map {
      IO.just(it)
    }
}
