package arrow.effects

import arrow.Kind
import arrow.core.extensions.monoid
import arrow.data.extensions.list.traverse.traverse
import arrow.effects.extensions.io.bracket.bracket
import arrow.effects.extensions.resource.applicative.applicative
import arrow.effects.extensions.resource.monad.monad
import arrow.effects.extensions.resource.monoid.monoid
import arrow.effects.typeclasses.seconds
import arrow.test.UnitSpec
import arrow.test.laws.MonadLaws
import arrow.test.laws.MonoidLaws
import arrow.test.laws.forFew
import arrow.typeclasses.Eq
import io.kotlintest.properties.Gen
import io.kotlintest.runner.junit4.KotlinTestRunner
import org.junit.runner.RunWith

@RunWith(KotlinTestRunner::class)
class ResourceTest : UnitSpec() {
  init {

    val EQ = Eq<Kind<ResourcePartialOf<ForIO, Throwable>, Int>> { a, b ->
      a.fix().invoke { IO.just(1) }.fix().unsafeRunTimed(60.seconds) ==
        b.fix().invoke { IO.just(1) }.fix().unsafeRunTimed(60.seconds)
    }

    testLaws(
      MonadLaws.laws(Resource.monad(IO.bracket()), EQ),
      MonoidLaws.laws(Resource.monoid(Int.monoid(), IO.bracket()), Gen.int().map { Resource.just(it, IO.bracket()) }, EQ)
    )

    "Resource releases resources in reverse order of acquisition" {
      forFew(5, Gen.list(Gen.string())) { l ->
        val released = mutableListOf<String>()
        l.traverse(Resource.applicative(IO.bracket())) {
          Resource({ IO { it } }, { r -> IO { released.add(r); Unit } }, IO.bracket())
        }.fix().invoke { IO.unit }.fix().unsafeRunSync()

        // This looks confusing but is correct, traverse is a rightFold => l is already "reversed"
        l == released
      }
    }
  }
}
