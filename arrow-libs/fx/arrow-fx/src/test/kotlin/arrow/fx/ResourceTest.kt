package arrow.fx

import arrow.Kind
import arrow.core.Some
import arrow.core.extensions.eq
import arrow.core.extensions.list.traverse.traverse
import arrow.core.extensions.monoid
import arrow.core.test.UnitSpec
import arrow.core.test.generators.GenK
import arrow.core.test.laws.MonadLaws
import arrow.core.test.laws.MonoidLaws
import arrow.fx.extensions.io.applicative.applicative
import arrow.fx.extensions.io.bracket.bracket
import arrow.fx.extensions.resource.applicative.applicative
import arrow.fx.extensions.resource.functor.functor
import arrow.fx.extensions.resource.monad.monad
import arrow.fx.extensions.resource.monoid.monoid
import arrow.fx.extensions.resource.selective.selective
import arrow.fx.test.laws.forFew
import arrow.fx.typeclasses.seconds
import arrow.typeclasses.Eq
import arrow.typeclasses.EqK
import io.kotlintest.properties.Gen

class ResourceTest : UnitSpec() {
  init {

    val EQ: Eq<Kind<Kind<Kind<ForResource, ForIO>, Throwable>, Int>> = Resource.eqK().liftEq(Int.eq())

    testLaws(
      MonadLaws.laws(
        Resource.monad(IO.bracket()),
        Resource.functor(IO.bracket()),
        Resource.applicative(IO.bracket()),
        Resource.selective(IO.bracket()),
        Resource.genK(),
        Resource.eqK()
      ),
      MonoidLaws.laws(Resource.monoid(Int.monoid(), IO.bracket()), Gen.int().map { Resource.just(it, IO.bracket()) }, EQ)
    )

    "Resource releases resources in reverse order of acquisition" {
      forFew(5, Gen.list(Gen.string())) { l ->
        val released = mutableListOf<String>()
        l.traverse(Resource.applicative(IO.bracket())) {
          Resource({ IO { it } }, { r -> IO { released.add(r); Unit } }, IO.bracket())
        }.fix().use { IO.unit }.fix().unsafeRunSync()

        l == released.reversed()
      }
    }
  }
}

fun Resource.Companion.eqK() = object : EqK<ResourcePartialOf<ForIO, Throwable>> {
  override fun <A> Kind<ResourcePartialOf<ForIO, Throwable>, A>.eqK(other: Kind<ResourcePartialOf<ForIO, Throwable>, A>, EQ: Eq<A>): Boolean =
    (this.fix() to other.fix()).let {
      val ls = it.first.use(IO.Companion::just).fix().attempt()
      val rs = it.second.use(IO.Companion::just).fix().attempt()
      val compare = IO.applicative().mapN(ls, rs) { (l, r) -> l == r }.fix()

      compare.unsafeRunTimed(5.seconds) == Some(true)
    }
}

fun Resource.Companion.genK() = object : GenK<ResourcePartialOf<ForIO, Throwable>> {
  override fun <A> genK(gen: Gen<A>): Gen<Kind<ResourcePartialOf<ForIO, Throwable>, A>> {
    val allocate = gen.map { Resource({ IO.just(it) }, { _ -> IO.unit }, IO.bracket()) }

    return Gen.oneOf(
      // Allocate
      allocate,
      // Suspend
      allocate.map { Resource.Suspend(IO.just(it), IO.bracket()) },
      // Bind
      allocate.map { it.flatMap { a -> just(a, IO.bracket()) } }
    )
  }
}
