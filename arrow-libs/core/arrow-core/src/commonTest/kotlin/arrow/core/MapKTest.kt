package arrow.core

import arrow.core.test.functionABCToD
import arrow.core.test.functionAToB
import arrow.core.test.intSmall
import arrow.core.test.ior
import arrow.core.test.laws.MonoidLaws
import arrow.core.test.map2
import arrow.core.test.map3
import arrow.core.test.option
import arrow.core.test.testLaws
import arrow.platform.test.FlakyOnJs
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.maps.shouldBeEmpty
import io.kotest.matchers.maps.shouldContain
import io.kotest.matchers.maps.shouldContainKey
import io.kotest.matchers.maps.shouldNotContainKey
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.boolean
import io.kotest.property.arbitrary.choice
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.orNull
import io.kotest.property.arbitrary.pair
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class MapKTest {
  @Test fun monoidLaws() =
    testLaws(
      MonoidLaws(
        "Map",
        emptyMap(),
        { a, b -> a.combine(b, Int::plus) },
        Arb.map(Arb.int(), Arb.intSmall(), maxSize = 10)
      )
    )

  // In addition to verifying monoid laws, we must test the property of order-preservation
  // when delegating to the nested monoid operation. Since a monoid's associative operation
  // is not necessarily commutative, preserving the order of parameters is essential.
  @Test fun orderPreservationInNestedCombine() = runTest {
    checkAll(
      // The range of keys and map sizes are chosen to allow for varying maps sizes, while ensuring key conflicts.
      // Consider that minSize is inclusive while maxSize is exclusive.
      Arb.map(Arb.int(1, 2), Arb.string(), minSize = 1, maxSize = 3),
      Arb.map(Arb.int(1, 2), Arb.string(), minSize = 1, maxSize = 3),
    ) { map1, map2 ->

      // Notice that string concatenation is non-commutative.
      val result = map1.combine(map2, String::plus)
      map1.keys.intersect(map2.keys).forEach { key ->
        val expectedValue = map1[key].plus(map2[key])
        result[key] shouldBe expectedValue
      }
    }
  }


  @Test fun alignMaps() = runTest {
      checkAll(
        Arb.map2(Arb.int(), Arb.int(), Arb.int())
      ) { (a, b) ->
        val aligned = a.align(b)
        // aligned keySet is union of a's and b's keys
        aligned.size shouldBe (a.keys + b.keys).size
        // aligned map contains Both for all entries existing in a and b
        a.keys.intersect(b.keys).forEach {
          aligned[it]?.isBoth() shouldBe true
        }
        // aligned map contains Left for all entries existing only in a
        (a.keys - b.keys).forEach { key ->
          aligned[key]?.isLeft() shouldBe true
        }
        // aligned map contains Right for all entries existing only in b
        (b.keys - a.keys).forEach { key ->
          aligned[key]?.isRight() shouldBe true
        }
      }
    }

  @Test fun zipIsIdempotent() = runTest {
      checkAll(
        Arb.map(Arb.int(), Arb.intSmall())) {
          a ->
          a.zip(a) shouldBe a.mapValues { it.value to it.value }
      }
    }

  @Test fun alignIsIdempotent() = runTest {
      checkAll(
        Arb.map(Arb.int(), Arb.intSmall())) {
          a ->
        a.align(a) shouldBe a.mapValues { Ior.Both(it.value, it.value) }
      }
    }

  @Test fun zipIsCommutative() = runTest {
      checkAll(
        Arb.map2(Arb.int(), Arb.int(), Arb.int())
      ) { (a, b) ->

        a.zip(b) shouldBe b.zip(a).mapValues { it.value.second to it.value.first }
      }
    }

  @Test fun alignIsCommutative() = runTest {
      checkAll(
        Arb.map2(Arb.int(), Arb.int(), Arb.int())
      ) { (a, b) ->

        a.align(b) shouldBe b.align(a).mapValues { it.value.swap() }
      }
    }

  @Test fun zipIsAssociative() = runTest {
      checkAll(
        Arb.map3(Arb.int(), Arb.int(), Arb.int(), Arb.int())
      ) { (a, b, c)  ->

        fun <A, B, C> Pair<Pair<A, B>, C>.assoc(): Pair<A, Pair<B, C>> =
          this.first.first to (this.first.second to this.second)

        a.zip(b.zip(c)) shouldBe (a.zip(b)).zip(c).mapValues { it.value.assoc() }
      }
    }

  @Test fun alignIsAssociative() = runTest {
      checkAll(
        Arb.map3(Arb.int(), Arb.int(), Arb.int(), Arb.int())
      ) { (a, b, c)  ->

        fun <A, B, C> Ior<Ior<A, B>, C>.assoc(): Ior<A, Ior<B, C>> =
          when (this) {
            is Ior.Left -> when (val inner = this.value) {
              is Ior.Left -> Ior.Left(inner.value)
              is Ior.Right -> Ior.Right(Ior.Left(inner.value))
              is Ior.Both -> Ior.Both(inner.leftValue, Ior.Left(inner.rightValue))
            }
            is Ior.Right -> Ior.Right(Ior.Right(this.value))
            is Ior.Both -> when (val inner = this.leftValue) {
              is Ior.Left -> Ior.Both(inner.value, Ior.Right(this.rightValue))
              is Ior.Right -> Ior.Right(Ior.Both(inner.value, this.rightValue))
              is Ior.Both -> Ior.Both(inner.leftValue, Ior.Both(inner.rightValue, this.rightValue))
            }
          }

        a.align(b.align(c)) shouldBe (a.align(b)).align(c).mapValues { it.value.assoc() }
      }
    }

  @Test fun zipWith() = runTest {
      checkAll(
        Arb.map2(Arb.int(), Arb.int(), Arb.int()),
        Arb.functionABCToD<Int, Int, Int, Int>(Arb.int())
      ) { (a, b), fn ->
        a.zip(b, fn) shouldBe a.zip(b).mapValues { fn(it.key, it.value.first, it.value.second) }
      }
    }

  @Test fun alignWith() = runTest {
      checkAll(
        Arb.map2(Arb.int(), Arb.int(), Arb.int()),
        Arb.functionAToB<Map.Entry<Int, Ior<Int, Int>>, Int>(Arb.int())
      ) { (a, b), fn ->
        a.align(b, fn) shouldBe a.align(b).mapValues { fn(it) }
      }
    }

  @Test fun zipFunctor() = runTest {
      checkAll(
        Arb.map2(Arb.int(), Arb.int(), Arb.int()),
        Arb.functionAToB<Int, Int>(Arb.int()),
        Arb.functionAToB<Int, Int>(Arb.int())
      ) {
          (a,b),f,g ->

        fun <A,B,C,D> Pair<A,C>.bimap(f: (A) -> B, g: (C) -> D) = Pair(f(first), g(second))

        val l = a.mapValues{ f(it.value)}.zip(b.mapValues{g(it.value)})
        val r = a.zip(b).mapValues { it.value.bimap(f,g)}

        l shouldBe r
      }
    }

  @Test fun alignFunctor() = runTest {
      checkAll(
        Arb.map2(Arb.int(), Arb.int(), Arb.int()),
        Arb.functionAToB<Int, Int>(Arb.int()),
        Arb.functionAToB<Int, Int>(Arb.int())
      ) {
          (a,b),f,g ->

        val l = a.mapValues{ f(it.value) }.align(b.mapValues{ g(it.value) })
        val r = a.align(b).mapValues { it.value.map(g).mapLeft(f) }

        l shouldBe r
      }
    }

  @Test fun alignedness() = runTest {
      checkAll(
        Arb.map2(Arb.int(), Arb.int(), Arb.int())
      ) { (a, b) ->

        fun <K, V> toList(es: Map<K, V>): List<V> =
          es.fold(emptyList()) { acc, e ->
            acc + e.value
          }

        val left = toList(a)

        fun <A, B> Ior<A, B>.toLeftOption() =
          fold({ it }, { null }, { a, _ -> a })

        // toListOf (folded . here) (align x y)
        val middle = toList(a.align(b).mapValues { it.value.toLeftOption() }).filterNotNull()

        // mapMaybe justHere (toList (align x y))
        val right = toList(a.align(b)).mapNotNull { it.toLeftOption() }

        left shouldBe right
        left shouldBe middle
      }
    }

  @Test fun zippyness1() = runTest {
      checkAll(
        Arb.map(Arb.int(), Arb.int())) {
        xs ->
          xs.zip(xs).mapValues { it.value.first } shouldBe xs
      }
    }

  @Test fun zipyness2() = runTest {
      checkAll(
        Arb.map(Arb.int(), Arb.int(), maxSize = 30)) {
          xs ->
        xs.zip(xs).mapValues { it.value.second } shouldBe xs
      }
    }

  @Test fun zipyness3() = runTest {
      checkAll(
        Arb.map(Arb.int(), Arb.pair(Arb.int(), Arb.int()), maxSize = 30)) {
          xs ->
        xs.mapValues { it.value.first }.zip(xs.mapValues { it.value.second }) shouldBe xs
      }
    }

  @Test fun distributivity1() = runTest {
      checkAll(
        Arb.map3(Arb.int(), Arb.int(), Arb.int(), Arb.int())
      ) {(x,y,z) ->

        fun <A, B, C> Pair<Ior<A, C>, Ior<B, C>>.undistrThesePair(): Ior<Pair<A, B>, C> =
          when (val l = this.first) {
            is Ior.Left -> {
              when (val r = this.second) {
                is Ior.Left -> Ior.Left(l.value to r.value)
                is Ior.Both -> Ior.Both(l.value to r.leftValue, r.rightValue)
                is Ior.Right -> Ior.Right(r.value)
              }
            }
            is Ior.Both -> when (val r = this.second) {
              is Ior.Left -> Ior.Both(l.leftValue to r.value, l.rightValue)
              is Ior.Both -> Ior.Both(l.leftValue to r.leftValue, l.rightValue)
              is Ior.Right -> Ior.Right(l.rightValue)
            }
            is Ior.Right -> Ior.Right(l.value)
          }

        val ls = x.zip(y).align(z)
        val rs = x.align(z).zip(y.align(z)).mapValues { it.value.undistrThesePair() }

        ls shouldBe rs
      }
    }

  @Test fun distributivity2() = runTest {
      checkAll(
        Arb.map3(Arb.int(), Arb.int(), Arb.int(), Arb.int())
      ) {(x,y,z) ->

        fun <A, B, C> Pair<Ior<A, B>, C>.distrPairThese(): Ior<Pair<A, C>, Pair<B, C>> =
          when (val l = this.first) {
            is Ior.Left -> Ior.Left(l.value to this.second)
            is Ior.Right -> Ior.Right(l.value to this.second)
            is Ior.Both -> Ior.Both(l.leftValue to this.second, l.rightValue to this.second)
          }

        val ls = x.align(y).zip(z).mapValues { it.value.distrPairThese() }
        val rs = x.zip(z).align(y.zip(z))

        ls shouldBe rs
      }
    }

  @Test fun distributivity3() = runTest {
      checkAll(
        Arb.map3(Arb.int(), Arb.int(), Arb.int(), Arb.int())
      ) {(x,y,z) ->

        fun <A, B, C> Ior<Pair<A, C>, Pair<B, C>>.undistrPairThese(): Pair<Ior<A, B>, C> =
          when (val e = this) {
            is Ior.Left -> Ior.Left(e.value.first) to e.value.second
            is Ior.Both -> Ior.Both(e.leftValue.first, e.rightValue.first) to e.leftValue.second
            is Ior.Right -> Ior.Right(e.value.first) to e.value.second
          }

        val ls = x.align(y).zip(z)
        val rs = x.zip(z).align(y.zip(z)).mapValues { it.value.undistrPairThese() }

        ls shouldBe rs
      }
    }

  @Test fun unzipInverseOfZip() = runTest {
      checkAll(
        Arb.map(Arb.int(), Arb.int(), maxSize = 30)
      ) { xs ->
        val ls = xs.zip(xs).unzip()
        val rs = xs to xs

        ls shouldBe rs
      }
    }

  @Test fun zipInverseOfUnzip() = runTest {
      checkAll(
        Arb.map(Arb.int(), Arb.pair(Arb.int(), Arb.int()), maxSize = 30)
      ) { xs ->
        val (a,b) = xs.unzip()
        a.zip(b) shouldBe xs
      }
    }

  @Test fun unzipWith() = runTest {
      checkAll(
        Arb.map(Arb.int(), Arb.pair(Arb.int(), Arb.int()), maxSize = 30)
      ) { xs ->
        xs.unzip { it.value.first to it.value.second } shouldBe xs.unzip()
      }
    }

  @Test fun unalignWith() = runTest {
      checkAll(
        Arb.map(Arb.int(), Arb.ior(Arb.int(), Arb.int()), maxSize = 30)
      ) { xs ->
        xs.unalign { it.value } shouldBe xs.unalign()
      }
    }

  @Test fun getOrNoneOk() = runTest {
      checkAll(
        Arb.map(Arb.int(0 .. 1000), Arb.int(), maxSize = 30)
      ) { xs ->
        val (found, notFound) = (0 .. 1000).partition { xs.containsKey(it) }

        found.forEach {
          xs.getOrNone(it)
            .shouldBeInstanceOf<Some<String>>()
            .value.shouldBe(xs[it])
        }

        notFound.forEach {
          xs.getOrNone(it)
            .shouldBeInstanceOf<None>()
        }
      }
    }

  @Test fun unalignInverseOfAlign() = runTest {
      checkAll(
        Arb.map2(Arb.int(), Arb.int(), Arb.int())
      ) { (a, b) ->
        a.align(b).unalign() shouldBe (a to b)
      }
    }

  @Test fun alignInverseOfUnalign() = runTest {
      checkAll(
        Arb.map(Arb.int(), Arb.ior(Arb.int(), Arb.int()), maxSize = 30)
      ) { xs ->
        val (a,b) = xs.unalign()

        a.align(b) shouldBe xs
      }
    }

  @Test fun padZipOk() = runTest {
      checkAll(
        Arb.map2(Arb.int(), Arb.int(), Arb.int())
      ) { (a, b) ->
        val x = a.padZip(b)

        a.forEach {
          val value = x[it.key].shouldNotBeNull()

          value.first shouldBe it.value
        }

        b.forEach {
          val value = x[it.key].shouldNotBeNull()

          value.second shouldBe it.value
        }
      }
    }

  @Test fun padZipWith() = runTest {
      checkAll(
        Arb.map2(Arb.int(), Arb.int(), Arb.int()),
        Arb.functionABCToD<Int, Int?, Int?, Int>(Arb.int())
      ) { (a, b), fn ->
        a.padZip(b, fn) shouldBe a.padZip(b).mapValues { fn(it.key, it.value.first, it.value.second) }
      }
    }

  @Test fun salignOk() = runTest {
      checkAll(
        Arb.map2(Arb.int(), Arb.intSmall(), Arb.intSmall())
      ) { (a, b) ->
        a.salign(b, Int::plus) shouldBe a.align(b) {it.value.fold(::identity, ::identity) { a, b -> a + b } }
      }
    }

  @Test fun mapValuesNotNullOk() = runTest {
      checkAll(
        Arb.map(Arb.int(), Arb.boolean(), maxSize = 30)
      ) { xs ->
        val rs = xs.mapValuesNotNull { (_, pred) -> if(pred) true else null }

        xs.forEach {
          if (it.value)
            rs shouldContainKey it.key
          else
            rs shouldNotContainKey it.key
        }
      }
    }

  @Test fun filterOptionOk() = runTest {
      checkAll(
        Arb.map(Arb.int(), Arb.option(Arb.int()), maxSize = 30)
      ) { xs ->
        val rs = xs.filterOption()

        xs.forEach {
          val value = it.value
          if (value is Some<Int>)
            rs shouldContain (it.key to value.value)
          else
            rs shouldNotContainKey it.key
        }
      }
    }

  @Test fun filterInstanceOk() = runTest {
      checkAll(
        Arb.map(Arb.int(), Arb.choice(Arb.int(), Arb.int()), maxSize = 30)
      ) { xs ->
        val a = xs.filterIsInstance<Int, String>()
        val b = xs.filterIsInstance<Int, Int>()

        (a + b) shouldBe xs
      }
    }

  @Test fun filterInstanceIdentity() = runTest {
      checkAll(Arb.map(Arb.int(), Arb.int(), maxSize = 30)) { xs ->
        xs.filterIsInstance<Int, Int>() shouldBe xs
      }
    }

  @Test fun filterInstanceIdentityNull() = runTest {
      checkAll(Arb.map(Arb.int(), Arb.int().orNull(), maxSize = 30)) { xs ->
        xs.filterIsInstance<Int, Int?>() shouldBe xs
      }
    }

  @Test fun zip2Ok() = runTest {
      checkAll(
        Arb.map2(Arb.int(), Arb.int(), Arb.int())
      ) { (a, b) ->
        val result = a.zip(b) { _, aa, bb -> Pair(aa, bb) }
        val expected = a.filter { (k, _) -> b.containsKey(k) }
          .map { (k, v) -> Pair(k, Pair(v, b[k]!!)) }
          .toMap()

        result shouldBe expected
      }
    }

  @Test fun zip2Null() = runTest {
      checkAll(
        Arb.map2(Arb.int(), Arb.int(), Arb.int().orNull())
      ) { (mapA, mapB) ->
        val result = mapA.zip(mapB) { _, aa, bb -> Pair(aa, bb) }
        val expected = mapA.filter { (k, _) -> mapB.containsKey(k) }
          .map { (k, v) -> Pair(k, Pair(v, mapB[k])) }
          .toMap()

        result shouldBe expected
      }
    }

  @Test fun zip3Ok() = runTest {
      checkAll(
        Arb.map2(Arb.int(), Arb.int(), Arb.int())
      ) { (a, b) ->
        val result = a.zip(b, b) { _, aa, bb, cc -> Triple(aa, bb, cc) }

        val expected = a.filter { (k, _) -> b.containsKey(k) }
          .map { (k, v) -> Pair(k, Triple(v, b[k]!!, b[k]!!)) }
          .toMap()

        result shouldBe expected
      }
    }

  @Test fun zip3Null() = runTest {
      checkAll(
        Arb.map2(Arb.int(), Arb.int(), Arb.int().orNull())
      ) { (mapA, mapB) ->
        val result = mapA.zip(mapB, mapB) { _, aa, bb, cc -> Triple(aa, bb, cc) }
        val expected = mapA.filter { (k, _) -> mapB.containsKey(k) }
          .map { (k, v) -> Pair(k, Triple(v, mapB[k], mapB[k])) }
          .toMap()

        result shouldBe expected
      }
    }

  @Test fun zip4Ok() = runTest {
      checkAll(
        Arb.map2(Arb.int(), Arb.int(), Arb.int())
      ) { (a, b) ->
        val result = a.zip(b, b, b) { _, aa, bb, cc, dd -> Tuple4(aa, bb, cc, dd) }

        val expected = a.filter { (k, _) -> b.containsKey(k) }
          .map { (k, v) -> Pair(k, Tuple4(v, b[k]!!, b[k]!!, b[k]!!)) }
          .toMap()

        result shouldBe expected
      }
    }

  @Test fun zip4Null() = runTest {
      checkAll(
        Arb.map2(Arb.int(), Arb.int(), Arb.int().orNull())
      ) { (mapA, mapB) ->
        val result = mapA.zip(mapB, mapB, mapB) { _, aa, bb, cc, dd -> Tuple4(aa, bb, cc, dd) }
        val expected = mapA.filter { (k, _) -> mapB.containsKey(k) }
          .map { (k, v) -> Pair(k, Tuple4(v, mapB[k], mapB[k], mapB[k])) }
          .toMap()

        result shouldBe expected
      }
    }

  @Test fun zip5Ok() = runTest {
      checkAll(
        Arb.map2(Arb.int(), Arb.int(), Arb.int())
      ) { (a, b) ->
        val result = a.zip(b, b, b, b) { _, aa, bb, cc, dd, ee -> Tuple5(aa, bb, cc, dd, ee) }

        val expected = a.filter { (k, _) -> b.containsKey(k) }
          .map { (k, v) -> Pair(k, Tuple5(v, b[k]!!, b[k]!!, b[k]!!, b[k]!!)) }
          .toMap()

        result shouldBe expected
      }
    }

  @Test fun zip5Null() = runTest {
      checkAll(
        Arb.map2(Arb.int(), Arb.int(), Arb.int().orNull())
      ) { (mapA, mapB) ->
        val result = mapA.zip(mapB, mapB, mapB, mapB) { _, aa, bb, cc, dd, ee -> Tuple5(aa, bb, cc, dd, ee) }
        val expected = mapA.filter { (k, _) -> mapB.containsKey(k) }
          .map { (k, v) -> Pair(k, Tuple5(v, mapB[k], mapB[k], mapB[k], mapB[k])) }
          .toMap()

        result shouldBe expected
      }
    }

  @Test fun zip6Ok() = runTest {
      checkAll(
        Arb.map2(Arb.int(), Arb.int(), Arb.int())
      ) { (a, b) ->
        val result = a.zip(b, b, b, b, b) { _, aa, bb, cc, dd, ee, ff -> Tuple6(aa, bb, cc, dd, ee, ff) }

        val expected = a.filter { (k, _) -> b.containsKey(k) }
          .map { (k, v) -> Pair(k, Tuple6(v, b[k]!!, b[k]!!, b[k]!!, b[k]!!, b[k]!!)) }
          .toMap()

        result shouldBe expected
      }
    }

  @Test fun zip6Null() = runTest {
      checkAll(
        Arb.map2(Arb.int(), Arb.int(), Arb.int().orNull())
      ) { (mapA, mapB) ->
        val result = mapA.zip(mapB, mapB, mapB, mapB, mapB) { _, aa, bb, cc, dd, ee, ff -> Tuple6(aa, bb, cc, dd, ee, ff) }
        val expected = mapA.filter { (k, _) -> mapB.containsKey(k) }
          .map { (k, v) -> Pair(k, Tuple6(v, mapB[k], mapB[k], mapB[k], mapB[k], mapB[k])) }
          .toMap()

        result shouldBe expected
      }
    }

  @Test fun zip7Ok() = runTest {
      checkAll(
        Arb.map2(Arb.int(), Arb.int(), Arb.int())
      ) { (a, b) ->
        val result = a.zip(b, b, b, b, b, b) { _, aa, bb, cc, dd, ee, ff, gg -> Tuple7(aa, bb, cc, dd, ee, ff, gg) }

        val expected = a.filter { (k, _) -> b.containsKey(k) }
          .map { (k, v) -> Pair(k, Tuple7(v, b[k]!!, b[k]!!, b[k]!!, b[k]!!, b[k]!!, b[k]!!)) }
          .toMap()

        result shouldBe expected
      }
    }

  @Test fun zip7Null() = runTest {
      checkAll(
        Arb.map2(Arb.int(), Arb.int(), Arb.int().orNull())
      ) { (mapA, mapB) ->
        val result = mapA.zip(mapB, mapB, mapB, mapB, mapB, mapB) { _, aa, bb, cc, dd, ee, ff, gg -> Tuple7(aa, bb, cc, dd, ee, ff, gg) }
        val expected = mapA.filter { (k, _) -> mapB.containsKey(k) }
          .map { (k, v) -> Pair(k, Tuple7(v, mapB[k], mapB[k], mapB[k], mapB[k], mapB[k], mapB[k])) }
          .toMap()

        result shouldBe expected
      }
    }

  @Test fun zip8Ok() = runTest {
      checkAll(
        Arb.map2(Arb.int(), Arb.int(), Arb.int())
      ) { (a, b) ->
        val result =
          a.zip(b, b, b, b, b, b, b) { _, aa, bb, cc, dd, ee, ff, gg, hh -> Tuple8(aa, bb, cc, dd, ee, ff, gg, hh) }

        val expected = a.filter { (k, _) -> b.containsKey(k) }
          .map { (k, v) -> Pair(k, Tuple8(v, b[k]!!, b[k]!!, b[k]!!, b[k]!!, b[k]!!, b[k]!!, b[k]!!)) }
          .toMap()

        result shouldBe expected
      }
    }

  @Test fun zip8Null() = runTest {
      checkAll(
        Arb.map2(Arb.int(), Arb.int(), Arb.int().orNull())
      ) { (mapA, mapB) ->
        val result = mapA.zip(mapB, mapB, mapB, mapB, mapB, mapB, mapB) { _, aa, bb, cc, dd, ee, ff, gg, hh -> Tuple8(aa, bb, cc, dd, ee, ff, gg, hh) }
        val expected = mapA.filter { (k, _) -> mapB.containsKey(k) }
          .map { (k, v) -> Pair(k, Tuple8(v, mapB[k], mapB[k], mapB[k], mapB[k], mapB[k], mapB[k], mapB[k])) }
          .toMap()

        result shouldBe expected
      }
    }

  @Test fun zip9Ok() = runTest {
      checkAll(
        Arb.map2(Arb.int(), Arb.int(), Arb.int())
      ) { (a, b) ->
        val result = a.zip(b, b, b, b, b, b, b, b) { _, aa, bb, cc, dd, ee, ff, gg, hh, ii ->
          Tuple9(
            aa,
            bb,
            cc,
            dd,
            ee,
            ff,
            gg,
            hh,
            ii
          )
        }

        val expected = a.filter { (k, _) -> b.containsKey(k) }
          .map { (k, v) -> Pair(k, Tuple9(v, b[k]!!, b[k]!!, b[k]!!, b[k]!!, b[k]!!, b[k]!!, b[k]!!, b[k]!!)) }
          .toMap()

        result shouldBe expected
      }
    }

  @Test fun flatMapValuesOk() = runTest {
      checkAll(
        Arb.map2(Arb.int(), Arb.int(), Arb.int())
      ) { (a, b) ->
        val result = a.flatMapValues { b }
        val expected = a.filter { (k, _) -> b.containsKey(k) }
          .map { (k, _) -> Pair(k, b[k]!!) }
          .toMap()
        result shouldBe expected
      }
    }

  @Test fun mapValuesOrAccumulateEmpty() = runTest {
      val result: Either<NonEmptyList<String>, Map<Int, String>> = emptyMap<Int, Int>().mapValuesOrAccumulate {
        it.value.toString()
      }

    result.shouldBeInstanceOf<Either.Right<Map<Int, String>>>()
      .value.shouldBeEmpty()
  }

  @Test fun mapValuesOrAccumulateMaps() = runTest {
    checkAll(
      Arb.map(Arb.int(), Arb.int(), maxSize = 30)
    ) { xs ->

      val result: Either<NonEmptyList<String>, Map<Int, String>> = xs.mapValuesOrAccumulate {
        it.value.toString()
      }

      result.shouldBeInstanceOf<Either.Right<Map<Int, String>>>()

      result.value shouldBe xs.mapValues { it.value.toString() }
    }
  }

  @Test @FlakyOnJs fun mapValuesOrAccumulateAccumulates() = runTest {
    checkAll(
      Arb.map(Arb.int(), Arb.int(), minSize = 1, maxSize = 30)
    ) { xs ->
       xs.mapValuesOrAccumulate {
         raise(it.value)
       }.shouldBeInstanceOf<Either.Left<NonEmptyList<Int>>>()
         .value.all.shouldContainAll(xs.values)
    }
  }

  @Test fun flatMapValuesNull() = runTest {
    checkAll(
      Arb.map2(Arb.int(), Arb.int(), Arb.int().orNull())
    ) { (mapA, mapB) ->
      val result = mapA.flatMapValues { mapB }
      val expected = mapA.filter { (k, _) -> mapB.containsKey(k) }
        .map { (k, _) -> Pair(k, mapB[k]) }
        .toMap()
      result shouldBe expected
    }
  }
}
