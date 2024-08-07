package arrow.optics

import arrow.core.Either.Left
import arrow.core.Either.Right
import arrow.core.getOrElse
import arrow.core.identity
import arrow.core.toOption
import arrow.optics.dsl.filter
import arrow.optics.test.functionAToB
import arrow.optics.test.laws.OptionalLaws
import arrow.optics.test.laws.testLaws
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.boolean
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.orNull
import io.kotest.property.arbitrary.pair
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import kotlinx.coroutines.test.runTest
import kotlin.test.Ignore
import kotlin.test.Test

class OptionalTest {

  @Test
  fun identityLaws() =
    testLaws(
      OptionalLaws(
        optional = Optional.id(),
        aGen = Arb.int(),
        bGen = Arb.int(),
        funcGen = Arb.functionAToB(Arb.int()),
      )
    )

  @Test
  fun optionalFirstHeadLaws() =
    testLaws(
      OptionalLaws(
        optional = Optional.listHead<Int>().first(),
        aGen = Arb.pair(Arb.list(Arb.int()), Arb.boolean()),
        bGen = Arb.pair(Arb.int(), Arb.boolean()),
        funcGen = Arb.functionAToB(Arb.pair(Arb.int(), Arb.boolean())),
      )
    )

  @Test
  fun optionalSecondHeadLaws() =
    testLaws(
      OptionalLaws(
        optional = Optional.listHead<Int>().second(),
        aGen = Arb.pair(Arb.boolean(), Arb.list(Arb.int())),
        bGen = Arb.pair(Arb.boolean(), Arb.int()),
        funcGen = Arb.functionAToB(Arb.pair(Arb.boolean(), Arb.int())),
      )
    )

  @Test
  fun setAbsentOptional() = runTest {
    checkAll(Arb.incompleteUser(), Arb.token()) { user, token ->
      val updatedUser = Optional.incompleteUserToken().set(user, token)
      Optional.incompleteUserToken().getOrNull(updatedUser).shouldNotBeNull()
    }
  }

  @Test
  fun sizeOk() = runTest {
    checkAll(Arb.list(Arb.int())) { ints: List<Int> ->
      Optional.listHead<Int>().size(ints) shouldBe ints.firstOrNull().toOption().map { 1 }.getOrElse { 0 }
    }
  }

  @Test
  fun nonEmptyOk() = runTest {
    checkAll(Arb.list(Arb.int())) { ints: List<Int> ->
      Optional.listHead<Int>().isNotEmpty(ints) shouldBe ints.firstOrNull().toOption().isSome()
    }
  }

  @Test
  fun isEmptyOk() = runTest {
    checkAll(Arb.list(Arb.int())) { ints: List<Int> ->
      Optional.listHead<Int>().isEmpty(ints) shouldBe ints.firstOrNull().toOption().isNone()
    }
  }

  @Test
  fun getAllOk() = runTest {
    checkAll(Arb.list(Arb.int())) { ints: List<Int> ->
      Optional.listHead<Int>().getAll(ints) shouldBe ints.firstOrNull().toOption().toList()
    }
  }

  @Test
  fun foldOk() = runTest {
    checkAll(Arb.list(Arb.int())) { ints: List<Int> ->
      Optional.listHead<Int>().fold(0, { x, y -> x + y }, ints) shouldBe
        ints.firstOrNull().toOption().fold({ 0 }, ::identity)
    }
  }

  @Test
  fun firstOrNullOk() = runTest {
    checkAll(Arb.list(Arb.int().orNull())) { ints ->
      Optional.listHead<Int?>().firstOrNull(ints) shouldBe ints.firstOrNull()
    }
  }

  @Test
  fun lastOrNullOk() = runTest {
    checkAll(Arb.list(Arb.int().orNull())) { ints ->
      Optional.listHead<Int?>().lastOrNull(ints) shouldBe ints.firstOrNull()
    }
  }

  @Test
  fun unitAlwaysNull() = runTest {
    checkAll(Arb.string()) { string: String ->
      Optional.void<String, Int>().getOrNull(string) shouldBe null
    }
  }

  @Test
  fun unitDoesNothing() = runTest {
    checkAll(Arb.int(), Arb.string()) { int: Int, string: String ->
      Optional.void<String, Int>().set(string, int) shouldBe string
    }
  }

  @Test
  fun noTarget() = runTest {
    checkAll(Arb.list(Arb.int())) { list ->
      Optional.listHead<Int>().isNotEmpty(list) shouldBe list.isNotEmpty()
    }
  }

  @Test
  fun liftConsistentWithModify() = runTest {
    checkAll(Arb.list(Arb.int())) { list ->
      val f = { i: Int -> i + 5 }
      Optional.listHead<Int>().lift(f)(list) shouldBe Optional.listHead<Int>().modify(list, f)
    }
  }

  @Test
  fun checkTargetExists() = runTest {
    checkAll(Arb.list(Arb.int())) { list ->
      Optional.listHead<Int>().isEmpty(list) shouldBe list.isEmpty()
    }
  }

  @Test
  fun findOrNullOk() = runTest {
    checkAll(Arb.list(Arb.int()), Arb.boolean()) { list, predicate ->
      (Optional.listHead<Int>().findOrNull(list) { predicate }?.let { true }
        ?: false) shouldBe (predicate && list.isNotEmpty())
    }
  }

  @Test
  fun existsOk() = runTest {
    checkAll(Arb.list(Arb.int().orNull()), Arb.boolean()) { list, predicate ->
      Optional.listHead<Int?>().exists(list) { predicate } shouldBe (predicate && list.isNotEmpty())
    }
  }

  @Test
  fun allOk() = runTest {
    checkAll(Arb.list(Arb.int()), Arb.boolean()) { list, predicate ->
      Optional.listHead<Int>().all(list) { predicate } shouldBe if (list.isEmpty()) true else predicate
    }
  }

  @Test
  fun setNonEmptyList() = runTest {
    checkAll(Arb.list(Arb.int(), 1..200), Arb.int()) { list, value ->
      Optional.listHead<Int>().set(list, value)[0] shouldBe value
    }
  }

  @Test
  fun joinOk() = runTest {
    val joinedOptional = Optional.listHead<Int>().choice(Optional.defaultHead())

    checkAll(Arb.int()) { int ->
      joinedOptional.getOrNull(Left(listOf(int))) shouldBe joinedOptional.getOrNull(Right(int))
    }
  }

  @OptIn(DelicateOptic::class) @Test
  fun filterPrismTrue() = runTest {
    checkAll(Arb.sumType()) { sum: SumType ->
      Prism.sumType().filter { true }.getOrNull(sum) shouldBe (sum as? SumType.A)?.string
    }
  }

  @OptIn(DelicateOptic::class) @Test
  fun filterPrismFalse() = runTest {
    checkAll(Arb.sumType()) { sum: SumType ->
      Prism.sumType().filter { false }.getOrNull(sum) shouldBe null
    }
  }

  @OptIn(DelicateOptic::class) @Test
  fun filterListBasic() = runTest {
    checkAll(Arb.list(Arb.int(), range = 0 .. 20)) { lst: List<Int> ->
      Every.list<Int>().filter { true }.getAll(lst) shouldBe lst
      Every.list<Int>().filter { false }.getAll(lst) shouldBe emptyList()
    }
  }

  @OptIn(DelicateOptic::class) @Test
  fun filterPredicate() = runTest {
    checkAll(Arb.list(Arb.int())) { lst: List<Int> ->
      Every.list<Int>().filter { it > 7 }.getAll(lst) shouldBe lst.filter { it > 7 }
    }
  }

  @OptIn(DelicateOptic::class) @Test
  fun filterModify() = runTest {
    checkAll(Arb.list(Arb.pair(Arb.int(), Arb.string(maxSize = 10)))) { lst: List<Pair<Int, String>> ->
      Every.list<Pair<Int, String>>()
        .filter { (n, _) -> n % 2 == 0 }
        .compose(Lens.pairSecond())
        .modify(lst) { it.uppercase() }
        .shouldBe(lst.map { (n, s) -> Pair(n, if (n % 2 == 0) s.uppercase() else s) })
    }
  }

  @OptIn(DelicateOptic::class) @Test
  fun filterSetGetFails() = runTest {
    checkAll(Arb.int()) { n ->
      val p = Optional.filter<Int> { it % 2 == 0 }
      p.getOrNull(p.set(n, n)) shouldBe n.takeIf { it % 2 == 0 }
    }
  }

  @OptIn(DelicateOptic::class) @Test @Ignore
  // 'filter' does not satisfy the double-modification law
  fun filterSetSetFails() = runTest {
    checkAll(Arb.int(range = 0 .. 100)) { n ->
      val p = Optional.filter<Int> { it % 2 == 0 }
      p.modify(p.modify(n) { it + 1 }) { it + 1 } shouldBe p.modify(n) { it + 2 }
    }
  }
}
