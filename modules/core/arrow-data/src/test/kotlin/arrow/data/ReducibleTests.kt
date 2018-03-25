package arrow.data

import arrow.Kind
import arrow.core.Tuple2
import arrow.instances.IntSemigroupInstance
import arrow.instances.LongMonoidInstance
import arrow.test.UnitSpec
import arrow.test.laws.ReducibleLaws
import arrow.typeclasses.Eq
import arrow.typeclasses.Foldable
import arrow.typeclasses.NonEmptyReducible
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldBe
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class ReducibleTests : UnitSpec() {
    init {
        val nonEmptyReducible = object : NonEmptyReducible<ForNonEmptyList, ForListK> {
            override fun FG(): Foldable<ForListK> = ListK.foldable()

            override fun <A> split(fa: Kind<ForNonEmptyList, A>): Tuple2<A, Kind<ForListK, A>> = Tuple2(fa.fix().head, ListK(fa.fix().tail))
        }

        testLaws(ReducibleLaws.laws(
                nonEmptyReducible,
                { n: Int -> NonEmptyList(n, listOf()) },
                Eq.any(),
                Eq.any(),
                Eq.any()))

        with(nonEmptyReducible) {
            with(IntSemigroupInstance) {

                "Reducible<NonEmptyList> default size implementation" {
                    val nel = NonEmptyList.of(1, 2, 3)
                    LongMonoidInstance.size(nel) shouldBe nel.size.toLong()
                }

                "Reducible<NonEmptyList>" {
                    // some basic sanity checks
                    val tail = (2 to 10).toList()
                    val total = 1 + tail.sum()
                    val nel = NonEmptyList(1, tail)
                    nel.reduceLeft({ a, b -> a + b }) shouldBe total
                    nel.reduceRight({ x, ly -> ly.map({ x + it }) }).value() shouldBe (total)
                    nel.reduce(this) shouldBe total

                    // more basic checks
                    val names = NonEmptyList.of("Aaron", "Betty", "Calvin", "Deirdra")
                    val totalLength = names.all.map({ it.length }).sum()
                    names.reduceLeftTo({ it.length }, { sum, s -> s.length + sum }) shouldBe totalLength
                    names.reduceMap(this, { it.length }) shouldBe totalLength
                }
            }
        }
    }
}
