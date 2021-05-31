@file:Suppress("NAME_SHADOWING")

package arrow.core

import arrow.core.test.generators.intSmall
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.property.Arb
import io.kotest.property.checkAll
import io.kotest.matchers.shouldBe
import io.kotest.property.arbitrary.of

class NullableTest : StringSpec({
  "map1 short circuits if any arg is null" {
    Nullable.zip(null) { Unit }.shouldBeNull()
  }

  "map1 performs action when arg is not null" {
    checkAll(Arb.intSmall()) { a ->
      Nullable.zip(a) { it + 1 } shouldBe a + 1
    }
  }

  "map2 only performs action when all arguments are not null" {
    checkAll(combArb("a", null, 2)) { (a: String?, b: String?) ->
      if (listOf(a, b).all { it != null }) {
        Nullable.zip(a, b, { a, b -> a + b }).let {
          it shouldBe a!! + b!!
        }
      } else {
        Nullable.zip(a, b, { _, _ -> Unit }) shouldBe null
      }
    }
  }

  "map3 only performs action when all arguments are not null" {
    checkAll(combArb("a", null, 3)) { (a: String?, b: String?, c: String?) ->
      if (listOf(a, b, c).all { it != null }) {
        Nullable.zip(a, b, c, { a, b, c -> a + b + c }).let {
          it shouldBe a!! + b!! + c!!
        }
      } else {
        Nullable.zip(a, b, c, { _, _, _ -> Unit }) shouldBe null
      }
    }
  }

  "map4 only performs action when all arguments are not null" {
    checkAll(combArb(1, null, 4)) { (a: Int?, b: Int?, c: Int?, d: Int?) ->
      if (listOf(a, b, c, d).all { it != null }) {
        Nullable.zip(a, b, c, d, { a, b, c, d -> a + b + c + d }).let {
          it shouldBe a!! + b!! + c!! + d!!
        }
      } else {
        Nullable.zip(a, b, c, d, { _, _, _, _ -> Unit }) shouldBe null
      }
    }
  }

  "map5 only performs action when all arguments are not null" {
    checkAll(combArb(1, null, 5)) { (a: Int?, b: Int?, c: Int?, d: Int?, e: Int?) ->
      if (listOf(a, b, c, d, e).all { it != null }) {
        Nullable.zip(a, b, c, d, e, { a, b, c, d, e -> a + b + c + d + e }).let {
          it shouldBe a!! + b!! + c!! + d!! + e!!
        }
      } else {
        Nullable.zip(a, b, c, d, e, { _, _, _, _, _ -> Unit }) shouldBe null
      }
    }
  }

  "map6 only performs action when all arguments are not null" {
    checkAll(combArb(1, null, 6)) { (a: Int?, b: Int?, c: Int?, d: Int?, e: Int?, f: Int?) ->
      if (listOf(a, b, c, d, e, f).all { it != null }) {
        Nullable.zip(a, b, c, d, e, f, { a, b, c, d, e, f -> a + b + c + d + e + f }).let {
          it shouldBe a!! + b!! + c!! + d!! + e!! + f!!
        }
      } else {
        Nullable.zip(a, b, c, d, e, f, { _, _, _, _, _, _ -> Unit }) shouldBe null
      }
    }
  }

  "map7 only performs action when all arguments are not null" {
    checkAll(combArb(1, null, 7)) { (a: Int?, b: Int?, c: Int?, d: Int?, e: Int?, f: Int?, g: Int?) ->
      if (listOf(a, b, c, d, e, f, g).all { it != null }) {
        Nullable.zip(a, b, c, d, e, f, g, { a, b, c, d, e, f, g -> a + b + c + d + e + f + g }).let {
          it shouldBe a!! + b!! + c!! + d!! + e!! + f!! + g!!
        }
      } else {
        Nullable.zip(a, b, c, d, e, f, g, { _, _, _, _, _, _, _ -> Unit }) shouldBe null
      }
    }
  }

  "map8 only performs action when all arguments are not null" {
    checkAll(combArb(1, null, 8)) { (a: Int?, b: Int?, c: Int?, d: Int?, e: Int?, f: Int?, g: Int?, h: Int?) ->
      if (listOf(a, b, c, d, e, f, g, h).all { it != null }) {
        Nullable.zip(a, b, c, d, e, f, g, h, { a, b, c, d, e, f, g, h -> a + b + c + d + e + f + g + h }).let {
          it shouldBe a!! + b!! + c!! + d!! + e!! + f!! + g!! + h!!
        }
      } else {
        Nullable.zip(a, b, c, d, e, f, g, h, { _, _, _, _, _, _, _, _ -> Unit }) shouldBe null
      }
    }
  }

  "map9 only performs action when all arguments are not null" {
    checkAll(combArb(1, null, 9)) { (a: Int?, b: Int?, c: Int?, d: Int?, e: Int?, f: Int?, g: Int?, h: Int?, i: Int?) ->
      if (listOf(a, b, c, d, e, f, g, h, i).all { it != null }) {
        Nullable.zip(a, b, c, d, e, f, g, h, i, { a, b, c, d, e, f, g, h, i -> a + b + c + d + e + f + g + h + i })
          .let {
            it shouldBe a!! + b!! + c!! + d!! + e!! + f!! + g!! + h!! + i!!
          }
      } else {
        Nullable.zip(a, b, c, d, e, f, g, h, i, { _, _, _, _, _, _, _, _, _ -> Unit }) shouldBe null
      }
    }
  }
})

private fun <A> List<A>.forkPath(choice1: A, choice2: A): Pair<List<A>, List<A>> =
  Pair(this + choice1, this + choice2)

private fun <A> List<List<A>>.forkPaths(choice1: A, choice2: A): List<List<A>> =
  this.fold(emptyList()) { acc: List<List<A>>, path: List<A> ->
    val paths: Pair<List<A>, List<A>> = path.forkPath(choice1, choice2)
    acc.plusElement(paths.first).plusElement(paths.second)
  }

private fun <A> generateAllPathsForNForks(choice1: A, choice2: A, n: Int): List<List<A>> =
  IntRange(1, n).fold(listOf(emptyList())) { acc: List<List<A>>, _ ->
    acc.forkPaths(choice1, choice2)
  }

private fun <A> combArb(choice1: A, choice2: A, n: Int): Arb<List<A>> =
  Arb.of(generateAllPathsForNForks(choice1, choice2, n))

class ArberateAllPathsForNForksTest : StringSpec({
  "for 0 forks" {
    generateAllPathsForNForks("a", "b", 0) shouldContainAll emptyList()
  }

  "for 1 fork" {
    generateAllPathsForNForks("a", "b", 1) shouldBe listOf(
      listOf("a"),
      listOf("b")
    )
  }

  "for 2 forks" {
    generateAllPathsForNForks(0, 1, 2) shouldBe listOf(
      listOf(0, 0),
      listOf(0, 1),
      listOf(1, 0),
      listOf(1, 1)
    )
  }
})

private operator fun <E> List<E>.component6(): E = this[5]
private operator fun <E> List<E>.component7(): E = this[6]
private operator fun <E> List<E>.component8(): E = this[7]
private operator fun <E> List<E>.component9(): E = this[8]
