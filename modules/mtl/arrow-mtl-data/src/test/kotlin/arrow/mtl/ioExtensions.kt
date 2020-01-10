package arrow.mtl

import arrow.Kind
import arrow.core.Either
import arrow.core.Option
import arrow.core.extensions.either.eq.eq
import arrow.core.extensions.option.eq.eq
import arrow.fx.IO
import arrow.fx.IOPartialOf
import arrow.fx.fix
import arrow.fx.typeclasses.Duration
import arrow.fx.typeclasses.seconds
import arrow.test.generators.GenK
import arrow.typeclasses.Eq
import arrow.typeclasses.EqK
import io.kotlintest.properties.Gen

fun IO.Companion.eqK(timeout: Duration = 60.seconds) = object : EqK<IOPartialOf<Nothing>> {
  override fun <A> Kind<IOPartialOf<Nothing>, A>.eqK(other: Kind<IOPartialOf<Nothing>, A>, EQ: Eq<A>): Boolean =
    (this.fix() to other.fix()).let {
      val ls = it.first.attempt().unsafeRunTimed(timeout)
      val rs = it.second.attempt().unsafeRunTimed(timeout)

      Option.eq(Either.eq(Eq.any(), Either.eq(Eq.any(), EQ))).run {
        ls.eqv(rs)
      }
    }
}

fun IO.Companion.genK() = object : GenK<IOPartialOf<Nothing>> {
  override fun <A> genK(gen: Gen<A>): Gen<Kind<IOPartialOf<Nothing>, A>> =
    gen.map {
      IO.just(it)
    }
}
