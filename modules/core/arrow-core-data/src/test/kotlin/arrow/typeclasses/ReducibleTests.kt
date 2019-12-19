package arrow.typeclasses

import arrow.Kind
import arrow.core.ForListK
import arrow.core.ForNonEmptyList
import arrow.core.ListK
import arrow.core.NonEmptyList
import arrow.core.Tuple2
import arrow.core.extensions.listk.foldable.foldable
import arrow.core.extensions.monoid
import arrow.core.extensions.semigroup
import arrow.core.fix
import arrow.test.UnitSpec
import arrow.test.generators.genK
import arrow.test.laws.ReducibleLaws
import io.kotlintest.shouldBe

class ReducibleTests : UnitSpec() {
  init {
    val nonEmptyReducible = object : NonEmptyReducible<ForNonEmptyList, ForListK> {
      override fun FG(): Foldable<ForListK> = ListK.foldable()

      override fun <A> Kind<ForNonEmptyList, A>.split(): Tuple2<A, Kind<ForListK, A>> =
        Tuple2(fix().head, ListK(fix().tail))
    }

    testLaws(ReducibleLaws.laws(
      nonEmptyReducible,
      NonEmptyList.genK()
    ))

    with(nonEmptyReducible) {
      with(Int.semigroup()) {
        val SG = this

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
          nel.reduce(SG) shouldBe total

          // more basic checks
          val names = NonEmptyList.of("Aaron", "Betty", "Calvin", "Deirdra")
          val totalLength = names.all.map { it.length }.sum()
          names.reduceLeftTo({ it.length }, { sum, s -> s.length + sum }) shouldBe totalLength
          names.reduceMap(SG) { it.length } shouldBe totalLength
        }
      }
    }
  }
}
