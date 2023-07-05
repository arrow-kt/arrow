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
import arrow.typeclasses.Semigroup
import io.kotest.core.spec.style.StringSpec
import io.kotest.inspectors.forAll
import io.kotest.inspectors.forAllValues
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldBeIn
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.maps.shouldBeEmpty
import io.kotest.matchers.maps.shouldContain
import io.kotest.matchers.maps.shouldContainKey
import io.kotest.matchers.maps.shouldNotContainKey
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.kotest.property.Arb
import io.kotest.property.arbitrary.boolean
import io.kotest.property.arbitrary.choice
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.orNull
import io.kotest.property.arbitrary.pair
import io.kotest.property.checkAll

class MapKTest : StringSpec({
    testLaws(
      MonoidLaws(
        "Map",
        emptyMap(),
        { a, b -> a.combine(b, Int::plus) },
        Arb.map(Arb.int(), Arb.intSmall(), maxSize = 10)
      )
    )

    "traverseEither is stacksafe" {
      val acc = mutableListOf<Int>()
      val res = (0..20_000).associateWith { it }.traverse { v ->
        acc.add(v)
        Either.Right(v)
      }
      res shouldBe acc.associateWith { it }.right()
      res shouldBe (0..20_000).associateWith { it }.right()
    }

    "traverseEither short-circuit" {
      checkAll(Arb.map(Arb.int(), Arb.int())) { ints ->
        val acc = mutableListOf<Int>()
        val evens = ints.traverse {
          if (it % 2 == 0) {
            acc.add(it)
            Either.Right(it)
          } else Either.Left(it)
        }
        acc shouldBe ints.values.takeWhile { it % 2 == 0 }
        when (evens) {
          is Either.Right -> evens.value shouldBe ints
          is Either.Left -> evens.value shouldBe ints.values.first { it % 2 != 0 }
        }
      }
    }

    "traverseOption is stack-safe" {
      // also verifies result order and execution order (l to r)
      val acc = mutableListOf<Int>()
      val res = (0..20_000).associateWith { it }.traverse { a ->
        acc.add(a)
        Some(a)
      }
      res shouldBe Some(acc.associateWith { it })
      res shouldBe Some((0..20_000).associateWith { it })
    }

    "traverseOption short-circuits" {
      checkAll(Arb.map(Arb.int(), Arb.int())) { ints ->
        var shortCircuited = 0
        val result = ints.traverse {
          if (it % 2 == 0) {
            Some(it)
          } else {
            shortCircuited++
            None
          }
        }
        shortCircuited.shouldBeIn(0, 1)

        if (shortCircuited == 0) {
          result.isSome().shouldBeTrue()
        } else if (shortCircuited == 1) {
          result.isNone().shouldBeTrue()
        }
      }
    }

    "sequenceOption yields some when all entries in the list are some" {
      checkAll(Arb.list(Arb.int())) { ints ->
        val evens = ints.map { (it % 2 == 0).maybe { it } }.sequence()
        evens.fold({ Unit }) { it shouldBe ints }
      }
    }

    "traverseValidated is stacksafe" {
      val acc = mutableListOf<Int>()
      val res = (0..20_000).associateWith { it }.traverse(Semigroup.string()) { v ->
        acc.add(v)
        Validated.Valid(v)
      }
      res shouldBe acc.associateWith { it }.valid()
      res shouldBe (0..20_000).associateWith { it }.valid()
    }

    "traverseValidated acummulates" {
      checkAll(Arb.map(Arb.int(), Arb.int())) { ints ->
        val res: ValidatedNel<Int, Map<Int, Int>> =
          ints.traverse(Semigroup.nonEmptyList()) { i -> if (i % 2 == 0) i.validNel() else i.invalidNel() }

        val expected: ValidatedNel<Int, Map<Int, Int>> =
          Option.fromNullable(ints.values.filterNot { it % 2 == 0 }.toNonEmptyListOrNull())
            .fold(
              { ints.entries.filter { (_, v) -> v % 2 == 0 }.associate { (k, v) -> k to v }.validNel() },
              { it.invalid() })

        res shouldBe expected
      }
    }

    "can align maps" {
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

    "zip is idempotent" {
      checkAll(
        Arb.map(Arb.int(), Arb.intSmall())) {
          a ->
          a.zip(a) shouldBe a.mapValues { it.value to it.value }
      }
    }

    "align is idempotent" {
      checkAll(
        Arb.map(Arb.int(), Arb.intSmall())) {
          a ->
        a.align(a) shouldBe a.mapValues { Ior.Both(it.value, it.value) }
      }
    }

    "zip is commutative" {
      checkAll(
        Arb.map2(Arb.int(), Arb.int(), Arb.int())
      ) { (a, b) ->

        a.zip(b) shouldBe b.zip(a).mapValues { it.value.second to it.value.first }
      }
    }

    "align is commutative" {
      checkAll(
        Arb.map2(Arb.int(), Arb.int(), Arb.int())
      ) { (a, b) ->

        a.align(b) shouldBe b.align(a).mapValues { it.value.swap() }
      }
    }

    "zip is associative" {
      checkAll(
        Arb.map3(Arb.int(), Arb.int(), Arb.int(), Arb.int())
      ) { (a, b, c)  ->

        fun <A, B, C> Pair<Pair<A, B>, C>.assoc(): Pair<A, Pair<B, C>> =
          this.first.first to (this.first.second to this.second)

        a.zip(b.zip(c)) shouldBe (a.zip(b)).zip(c).mapValues { it.value.assoc() }
      }
    }

    "align is associative" {
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

    "zip with" {
      checkAll(
        Arb.map2(Arb.int(), Arb.int(), Arb.int()),
        Arb.functionABCToD<Int, Int, Int, Int>(Arb.int())
      ) { (a, b), fn ->
        a.zip(b, fn) shouldBe a.zip(b).mapValues { fn(it.key, it.value.first, it.value.second) }
      }
    }

    "align with" {
      checkAll(
        Arb.map2(Arb.int(), Arb.int(), Arb.int()),
        Arb.functionAToB<Map.Entry<Int, Ior<Int, Int>>, Int>(Arb.int())
      ) { (a, b), fn ->
        a.align(b, fn) shouldBe a.align(b).mapValues { fn(it) }
      }
    }

    "zip functoriality" {
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

    "align functoriality" {
      checkAll(
        Arb.map2(Arb.int(), Arb.int(), Arb.int()),
        Arb.functionAToB<Int, Int>(Arb.int()),
        Arb.functionAToB<Int, Int>(Arb.int())
      ) {
          (a,b),f,g ->

        val l = a.mapValues{ f(it.value)}.align(b.mapValues{g(it.value)})
        val r = a.align(b).mapValues { it.value.bimap(f,g)}

        l shouldBe r
      }
    }

    "alignedness" {
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

    "zippyness1" {
      checkAll(
        Arb.map(Arb.int(), Arb.int())) {
        xs ->
          xs.zip(xs).mapValues { it.value.first } shouldBe xs
      }
    }

    "zippyness2" {
      checkAll(
        Arb.map(Arb.int(), Arb.int())) {
          xs ->
        xs.zip(xs).mapValues { it.value.second } shouldBe xs
      }
    }

    "zippyness3" {
      checkAll(
        Arb.map(Arb.int(), Arb.pair(Arb.int(), Arb.int()))) {
          xs ->
        xs.mapValues { it.value.first }.zip(xs.mapValues { it.value.second }) shouldBe xs
      }
    }

    "distributivity1" {
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

    "distributivity2" {
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

    "distributivity3" {
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

    "unzip is the inverse of zip" {
      checkAll(
        Arb.map(Arb.int(), Arb.int())
      ) { xs ->
        val ls = xs.zip(xs).unzip()
        val rs = xs to xs

        ls shouldBe rs
      }
    }

    "zip is the inverse of unzip" {
      checkAll(
        Arb.map(Arb.int(), Arb.pair(Arb.int(), Arb.int()))
      ) { xs ->
        val (a,b) = xs.unzip()
        a.zip(b) shouldBe xs
      }
    }

    "unzip with" {
      checkAll(
        Arb.map(Arb.int(), Arb.pair(Arb.int(), Arb.int()))
      ) { xs ->
        xs.unzip { it.value.first to it.value.second } shouldBe xs.unzip()
      }
    }

    "unalign with" {
      checkAll(
        Arb.map(Arb.int(), Arb.ior(Arb.int(), Arb.int()))
      ) { xs ->
        xs.unalign { it.value } shouldBe xs.unalign()
      }
    }

    "getOrNone" {
      checkAll(
        Arb.map(Arb.int(0 .. 1000), Arb.int())
      ) { xs ->
        val (found, notFound) = (0 .. 1000).partition { xs.containsKey(it) }

        found.forAll {
          xs.getOrNone(it)
            .shouldBeInstanceOf<Some<String>>()
            .value.shouldBe(xs[it])
        }

        notFound.forAll {
          xs.getOrNone(it)
            .shouldBeInstanceOf<None>()
        }
      }
    }

    "unalign is the inverse of align" {
      checkAll(
        Arb.map2(Arb.int(), Arb.int(), Arb.int())
      ) { (a, b) ->
        a.align(b).unalign() shouldBe (a to b)
      }
    }

    "align is the inverse of unalign" {
      checkAll(
        Arb.map(Arb.int(), Arb.ior(Arb.int(), Arb.int()))
      ) { xs ->
        val (a,b) = xs.unalign()

        a.align(b) shouldBe xs
      }
    }

    "padZip" {
      checkAll(
        Arb.map2(Arb.int(), Arb.int(), Arb.int())
      ) { (a, b) ->
        val x = a.padZip(b)

        a.forAll {
          val value = x[it.key].shouldNotBeNull()

          value.first shouldBe it.value
        }

        b.forAll {
          val value = x[it.key].shouldNotBeNull()

          value.second shouldBe it.value
        }
      }
    }

    "padZip with" {
      checkAll(
        Arb.map2(Arb.int(), Arb.int(), Arb.int()),
        Arb.functionABCToD<Int, Int?, Int?, Int>(Arb.int())
      ) { (a, b), fn ->
        a.padZip(b, fn) shouldBe a.padZip(b).mapValues { fn(it.key, it.value.first, it.value.second) }
      }
    }

    "salign" {
      checkAll(
        Arb.map2(Arb.int(), Arb.intSmall(), Arb.intSmall())
      ) { (a, b) ->
        a.salign(Semigroup.int(), b) shouldBe a.align(b) {it.value.fold(::identity, ::identity) { a, b -> a + b } }
      }
    }

    "void" {
      checkAll(
        Arb.map(Arb.intSmall(), Arb.intSmall())
      ) { a ->
        val result = a.void()

        result.keys shouldBe a.keys
        result.forAllValues { it shouldBe Unit }
      }
    }

    "filterMap" {
      checkAll(
        Arb.map(Arb.int(), Arb.boolean())
      ) { xs ->
        val rs = xs.filterMap { if(it) true else null }

        xs.forAll {
          if (it.value)
            rs shouldContainKey it.key
          else
            rs shouldNotContainKey it.key
        }
      }
    }

    "filterOption" {
      checkAll(
        Arb.map(Arb.int(), Arb.option(Arb.int()))
      ) { xs ->
        val rs = xs.filterOption()

        xs.forAll {
          val value = it.value
          if (value is Some<Int>)
            rs shouldContain (it.key to value.value)
          else
            rs shouldNotContainKey it.key
        }
      }
    }

    "filterInstance" {
      checkAll(
        Arb.map(Arb.int(), Arb.choice(Arb.int(), Arb.int()))
      ) { xs ->
        val a = xs.filterIsInstance<Int, String>()
        val b = xs.filterIsInstance<Int, Int>()

        (a + b) shouldBe xs
      }
    }

    "filterInstance: identity" {
      checkAll(Arb.map(Arb.int(), Arb.int())) { xs ->
        xs.filterIsInstance<Int, Int>() shouldBe xs
      }
    }

    "filterInstance: identity with null" {
      checkAll(Arb.map(Arb.int(), Arb.int().orNull())) { xs ->
        xs.filterIsInstance<Int, Int?>() shouldBe xs
      }
    }

    "zip2" {
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

    "zip2 with nullables" {
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

    "zip3" {
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

    "zip3 with nullables" {
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

    "zip4" {
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

    "zip4 with nullables" {
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

    "zip5" {
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

    "zip5 with nullables" {
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

    "zip6" {
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

    "zip6 with nullables" {
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

    "zip7" {
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

    "zip7 with nullables" {
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

    "zip8" {
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

    "zip8 with nullables" {
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

    "zip9" {
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

    "zip9 with nullables" {
      checkAll(
        Arb.map2(Arb.int(), Arb.int(), Arb.int().orNull())
      ) { (mapA, mapB) ->
        val result = mapA.zip(mapB, mapB, mapB, mapB, mapB, mapB, mapB, mapB) { _, aa, bb, cc, dd, ee, ff, gg, hh, ii ->
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
        val expected = mapA.filter { (k, _) -> mapB.containsKey(k) }
          .map { (k, v) -> Pair(k, Tuple9(v, mapB[k], mapB[k], mapB[k], mapB[k], mapB[k], mapB[k], mapB[k], mapB[k])) }
          .toMap()

        result shouldBe expected
      }
    }

    "zip10" {
      checkAll(
        Arb.map2(Arb.int(), Arb.int(), Arb.int())
      ) { (a, b) ->
        val result = a.zip(b, b, b, b, b, b, b, b, b) { _, aa, bb, cc, dd, ee, ff, gg, hh, ii, jj ->
          Tuple10(
            aa,
            bb,
            cc,
            dd,
            ee,
            ff,
            gg,
            hh,
            ii,
            jj
          )
        }

        val expected = a.filter { (k, _) -> b.containsKey(k) }
          .map { (k, v) -> Pair(k, Tuple10(v, b[k]!!, b[k]!!, b[k]!!, b[k]!!, b[k]!!, b[k]!!, b[k]!!, b[k]!!, b[k]!!)) }
          .toMap()

        result shouldBe expected
      }
    }

    "zip10 with nullables" {
      checkAll(
        Arb.map2(Arb.int(), Arb.int(), Arb.int().orNull())
      ) { (mapA, mapB) ->
        val result = mapA.zip(mapB, mapB, mapB, mapB, mapB, mapB, mapB, mapB, mapB) { _, aa, bb, cc, dd, ee, ff, gg, hh, ii, jj ->
          Tuple10(
            aa,
            bb,
            cc,
            dd,
            ee,
            ff,
            gg,
            hh,
            ii,
            jj
          )
        }
        val expected = mapA.filter { (k, _) -> mapB.containsKey(k) }
          .map { (k, v) -> Pair(k, Tuple10(v, mapB[k], mapB[k], mapB[k], mapB[k], mapB[k], mapB[k], mapB[k], mapB[k], mapB[k])) }
          .toMap()

        result shouldBe expected
      }
    }

    "flatMap" {
      checkAll(
        Arb.map2(Arb.int(), Arb.int(), Arb.int())
      ) { (a, b) ->
        val result = a.flatMap { b }
        val expected = a.filter { (k, _) -> b.containsKey(k) }
          .map { (k, _) -> Pair(k, b[k]!!) }
          .toMap()
        result shouldBe expected
      }
    }

  "mapOrAccumulate of empty should be empty" {
      val result: Either<NonEmptyList<String>, Map<Int, String>> = emptyMap<Int, Int>().mapOrAccumulate {
        it.value.toString()
      }

    result.shouldBeInstanceOf<Either.Right<Map<Int, String>>>()
      .value.shouldBeEmpty()
  }

  "mapOrAccumulate can map" {
    checkAll(
      Arb.map(Arb.int(), Arb.int())
    ) { xs ->

      val result: Either<NonEmptyList<String>, Map<Int, String>> = xs.mapOrAccumulate {
        it.value.toString()
      }

      result.shouldBeInstanceOf<Either.Right<Map<Int, String>>>()

      result.value shouldBe xs.mapValues { it.value.toString() }
    }
  }

  "mapOrAccumulate accumulates errors" {
    checkAll(
      Arb.map(Arb.int(), Arb.int(), minSize = 1)
    ) { xs ->
       xs.mapOrAccumulate {
          raise(it.value)
      }.shouldBeInstanceOf<Either.Left<NonEmptyList<Int>>>()
         .value.all.shouldContainAll(xs.values)
    }
  }

  "flatMap with nullables" {
    checkAll(
      Arb.map2(Arb.int(), Arb.int(), Arb.int().orNull())
    ) { (mapA, mapB) ->
      val result = mapA.flatMap { mapB }
      val expected = mapA.filter { (k, _) -> mapB.containsKey(k) }
        .map { (k, _) -> Pair(k, mapB[k]) }
        .toMap()
      result shouldBe expected
    }
  }
})
