package arrow.effects

import arrow.Kind
import arrow.core.extensions.monoid
import arrow.data.extensions.list.traverse.traverse
import arrow.effects.extensions.fx.bracket.bracket
import arrow.effects.extensions.resource.applicative.applicative
import arrow.effects.extensions.resource.monoid.monoid
import arrow.effects.suspended.fx.ForFx
import arrow.effects.suspended.fx.Fx
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

    val EQ = Eq<Kind<ResourcePartialOf<ForFx, Throwable>, Int>> { resourceA, resourceB ->
      Fx.unsafeRunBlocking(resourceA.invoke { Fx.just(1) }) ==
        Fx.unsafeRunBlocking(resourceB.invoke { Fx.just(1) })
    }

    testLaws(
      // TODO tailRecM currently hangs see issue #
      // MonadLaws.laws(Resource.monad(Fx.bracket()), EQ),
      MonoidLaws.laws(Resource.monoid(Int.monoid(), Fx.bracket()), Gen.int().map { Resource.just(it, Fx.bracket()) }, EQ)
    )

    "Resource releases resources in reverse order of acquisition" {
      forFew(5, Gen.list(Gen.string())) { l ->
        val released = mutableListOf<String>()
        Fx.unsafeRunBlocking(
          l.traverse(Resource.applicative(Fx.bracket())) {
            Resource({ Fx { it } }, { r -> Fx { released.add(r); Unit } }, Fx.bracket())
          }.fix().invoke { Fx.unit }
        )

        // This looks confusing but is correct, traverse is a rightFold => l is already "reversed"
        l == released
      }
    }
  }
}
