package arrow.typeclasses

import arrow.Kind
import arrow.core.Tuple2
import arrow.data.*
import arrow.instances.monoid
import arrow.instances.semigroup
import arrow.instances.listk.foldable.foldable
import arrow.test.UnitSpec
import arrow.test.laws.ReducibleLaws
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldBe
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class ReducibleTests : UnitSpec() {
  init {
    val nonEmptyReducible = object : NonEmptyReducible<ForNonEmptyList, ForListK> {
      override fun FG(): Foldable<ForListK> = ListK.foldable()

      override fun <A> Kind<ForNonEmptyList, A>.split(): Tuple2<A, Kind<ForListK, A>> =
        Tuple2(fix().head, ListK(fix().tail))
    }

    testLaws(ReducibleLaws.laws(
      nonEmptyReducible,
      { n: Int -> NonEmptyList(n, listOf()) },
      Eq.any(),
      Eq.any(),
      Eq.any()))

    with(nonEmptyReducible) {
      with(Int.semigroup()) {

        "Reducible<NonEmptyList> default size implementation" {
          val nel = NonEmptyList.of(1, 2, 3)
          nel.size(Long.monoid()) shouldBe nel.size.toLong()
        }

        "Reducible<NonEmptyList>" {
          // some basic sanity checks
          val tail = (2 to 10).toList()
          val total = 1 + tail.sum()
          val nel = NonEmptyList(1, tail)
          nel.reduceLeft { a, b -> a + b } shouldBe total
          nel.reduceRight { x, ly -> ly.map { x + it } }.value() shouldBe (total)
          nel.reduce(this) shouldBe total

          // more basic checks
          val names = NonEmptyList.of("Aaron", "Betty", "Calvin", "Deirdra")
          val totalLength = names.all.map { it.length }.sum()
          names.reduceLeftTo({ it.length }, { sum, s -> s.length + sum }) shouldBe totalLength
          names.reduceMap(this) { it.length } shouldBe totalLength
        }
      }
    }
  }
}
