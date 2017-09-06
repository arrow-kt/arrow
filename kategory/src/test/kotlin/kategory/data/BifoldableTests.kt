package kategory.data

import io.kotlintest.KTestJUnitRunner
import kategory.*
import kategory.laws.BifoldableLaws
import kategory.typeclasses.Bifoldable
import org.junit.runner.RunWith

typealias EitherEither<A, B> = Either<Either<A, B>, Either<A, B>>

@RunWith(KTestJUnitRunner::class)
class BifoldableTests : UnitSpec() {
    init {

        val eitherBifoldable: Bifoldable<EitherHK> = object : Bifoldable<EitherHK> {
            override fun <A, B, C> bifoldLeft(fab: HK2<EitherHK, A, B>, c: C, f: (C, A) -> C, g: (C, B) -> C): C =
                    when (fab) {
                        is Either.Left -> f(c, fab.a)
                        else -> g(c, (fab as Either.Right).b)
                    }

            override fun <A, B, C> bifoldRight(fab: HK2<EitherHK, A, B>, c: Eval<C>, f: (A, Eval<C>) -> Eval<C>, g: (B, Eval<C>) -> Eval<C>): Eval<C> =
                    when (fab) {
                        is Either.Left -> f(fab.a, c)
                        else -> g((fab as Either.Right).b, c)
                    }
        }

        val eitherComposeEither = eitherBifoldable.compose(eitherBifoldable)

        testLaws(BifoldableLaws.laws(eitherComposeEither, { cf: Int ->  }, Eq.any()))

        /*testLaws(ReducibleLaws.laws(
                nonEmptyReducible,
                { n: Int -> NonEmptyList(n, listOf()) },
                Eq.any(),
                Eq.any(),
                Eq.any()))

        "Reducible<NonEmptyList> default size implementation" {
            val nel = NonEmptyList.of(1, 2, 3)
            nonEmptyReducible.size(LongMonoid, nel) shouldBe nel.size.toLong()
        }

        "Reducible<NonEmptyList>" {
            // some basic sanity checks
            val tail = (2 to 10).toList()
            val total = 1 + tail.sum()
            val nel = NonEmptyList(1, tail)
            nonEmptyReducible.reduceLeft(nel, { a, b -> a + b }) shouldBe total
            nonEmptyReducible.reduceRight(nel, { x, ly -> ly.map({ x + it }) }).value() shouldBe (total)
            nonEmptyReducible.reduce(nel) shouldBe total

            // more basic checks
            val names = NonEmptyList.of("Aaron", "Betty", "Calvin", "Deirdra")
            val totalLength = names.all.map({ it.length }).sum()
            nonEmptyReducible.reduceLeftTo(names, { it.length }, { sum, s -> s.length + sum }) shouldBe totalLength
            nonEmptyReducible.reduceMap(names, { it.length }) shouldBe totalLength
        }*/
    }
}
