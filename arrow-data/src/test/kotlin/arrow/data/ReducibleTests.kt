package arrow.data

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldBe
import arrow.*
import arrow.core.Tuple2
import arrow.instances.LongMonoid
import arrow.test.laws.ReducibleLaws
import org.junit.runner.RunWith
import arrow.test.UnitSpec

@RunWith(KTestJUnitRunner::class)
class ReducibleTests : UnitSpec() {
    init {
        val nonEmptyReducible = object : NonEmptyReducible<NonEmptyListHK, ListKWHK>() {
            override fun FG(): Foldable<ListKWHK> = ListKW.foldable()

            override fun <A> split(fa: HK<NonEmptyListHK, A>): Tuple2<A, HK<ListKWHK, A>> = Tuple2(fa.ev().head, ListKW(fa.ev().tail))
        }

        testLaws(ReducibleLaws.laws(
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
        }
    }
}
