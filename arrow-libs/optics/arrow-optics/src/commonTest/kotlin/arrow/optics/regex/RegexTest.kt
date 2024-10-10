package arrow.optics.regex

import arrow.optics.Lens
import arrow.optics.Traversal
import arrow.optics.dsl.every
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class RegexTest {
  data class NumbersAndMoreNumbers(val number: Int, val moreNumbers: List<NumbersAndMoreNumbers>) {
    companion object {
      val number: Lens<NumbersAndMoreNumbers, Int> =
        Lens(NumbersAndMoreNumbers::number) { n, s -> n.copy(number = s) }
      val moreNumbers: Lens<NumbersAndMoreNumbers, List<NumbersAndMoreNumbers>> =
        Lens(NumbersAndMoreNumbers::moreNumbers) { n, s -> n.copy(moreNumbers = s) }
    }
  }

  val <S> Traversal<S, NumbersAndMoreNumbers>.number
    get() = this compose NumbersAndMoreNumbers.number

  val example = NumbersAndMoreNumbers(
    number = 1,
    moreNumbers = listOf(
      NumbersAndMoreNumbers(
        number = 2,
        moreNumbers = emptyList()
      ),
      NumbersAndMoreNumbers(
        number = 3,
        moreNumbers = listOf(
          NumbersAndMoreNumbers(
            number = 4,
            moreNumbers = emptyList()
          ),
          NumbersAndMoreNumbers(
            number = 5,
            moreNumbers = emptyList()
          ),
        )
      ),
    )
  )

  @Test
  fun testAnd() {
    val optic = NumbersAndMoreNumbers.number * (NumbersAndMoreNumbers.moreNumbers.every + NumbersAndMoreNumbers.number)
    optic.getAll(example) shouldBe listOf(1, 2, 3)
  }

  @Test
  fun testZeroOrMore() {
    val optic = zeroOrMore(NumbersAndMoreNumbers.moreNumbers.every).number
    optic.getAll(example) shouldBe listOf(1, 2, 3, 4, 5)
  }

  @Test
  fun testOnceOrMore() {
    val optic = onceOrMore(NumbersAndMoreNumbers.moreNumbers.every).number
    optic.getAll(example) shouldBe listOf(2, 3, 4, 5)
  }
}
