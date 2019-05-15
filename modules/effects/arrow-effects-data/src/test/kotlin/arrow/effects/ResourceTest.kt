package arrow.effects

import arrow.Kind
import arrow.core.extensions.monoid
import arrow.data.extensions.list.traverse.traverse
import arrow.effects.extensions.io.bracket.bracket
import arrow.effects.extensions.resource.applicative.applicative
import arrow.effects.extensions.resource.monoid.monoid
import arrow.test.UnitSpec
import arrow.test.laws.MonoidLaws
import arrow.test.laws.forFew
import arrow.typeclasses.Eq
import io.kotlintest.properties.Gen
import io.kotlintest.runner.junit4.KotlinTestRunner
import org.junit.runner.RunWith

@RunWith(KotlinTestRunner::class)
class ResourceTest : UnitSpec() {
  init {

    val EQ = Eq<Kind<ResourcePartialOf<ForIO, Throwable>, Int>> { resourceA, resourceB ->
      IO.unsafeRunBlocking(resourceA.invoke { IO.just(1) }) ==
        IO.unsafeRunBlocking(resourceB.invoke { IO.just(1) })
    }

    testLaws(
      // TODO tailRecM currently hangs see issue #
      // MonadLaws.laws(Resource.monad(IO.bracket()), EQ),
      MonoidLaws.laws(Resource.monoid(Int.monoid(), IO.bracket()), Gen.int().map { Resource.just(it, IO.bracket()) }, EQ)
    )

    "Resource releases resources in reverse order of acquisition" {
      forFew(5, Gen.list(Gen.string())) { l ->
        val released = mutableListOf<String>()
        IO.unsafeRunBlocking(
          l.traverse(Resource.applicative(IO.bracket())) {
            Resource({ IO { it } }, { r -> IO { released.add(r); Unit } }, IO.bracket())
          }.fix().invoke { IO.unit }
        )

        // This looks confusing but is correct, traverse is a rightFold => l is already "reversed"
        l == released
      }
    }
  }
}
