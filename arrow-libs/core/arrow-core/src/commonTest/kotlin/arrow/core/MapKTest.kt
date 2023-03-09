package arrow.core

import arrow.core.test.intSmall
import arrow.core.test.ior
import arrow.core.test.laws.MonoidLaws
import arrow.core.test.longSmall
import arrow.core.test.nonEmptyList
import arrow.core.test.option
import arrow.core.test.testLaws
import arrow.typeclasses.Monoid
import arrow.typeclasses.Semigroup
import io.kotest.core.spec.style.StringSpec
import io.kotest.inspectors.forAll
import io.kotest.inspectors.forAllValues
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldBeIn
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.maps.shouldContain
import io.kotest.matchers.maps.shouldContainKey
import io.kotest.matchers.maps.shouldNotContainKey
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.property.Arb
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.kotest.property.arbitrary.boolean
import io.kotest.property.arbitrary.choice
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.pair
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll

class MapKTest : StringSpec({
    testLaws(
      MonoidLaws.laws(
        Monoid.map(Semigroup.int()),
        Arb.map(Arb.longSmall(), Arb.intSmall(), maxSize = 10)
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
      // aligned keySet is union of a's and b's keys
      checkAll(Arb.map(KEY_ARB, Arb.boolean()), Arb.map(KEY_ARB, Arb.boolean())) { a, b ->
        val aligned = a.align(b)
        aligned.size shouldBe (a.keys + b.keys).size
      }

      // aligned map contains Both for all entries existing in a and b
      checkAll(Arb.map(KEY_ARB, Arb.boolean()), Arb.map(KEY_ARB, Arb.boolean())) { a, b ->
        val aligned = a.align(b)
        a.keys.intersect(b.keys).forEach {
          aligned[it]?.isBoth shouldBe true
        }
      }

      // aligned map contains Left for all entries existing only in a
      checkAll(Arb.map(KEY_ARB, Arb.boolean()), Arb.map(KEY_ARB, Arb.boolean())) { a, b ->
        val aligned = a.align(b)
        (a.keys - b.keys).forEach { key ->
          aligned[key]?.isLeft shouldBe true
        }
      }

      // aligned map contains Right for all entries existing only in b
      checkAll(Arb.map(KEY_ARB, Arb.boolean()), Arb.map(KEY_ARB, Arb.boolean())) { a, b ->
        val aligned = a.align(b)
        (b.keys - a.keys).forEach { key ->
          aligned[key]?.isRight shouldBe true
        }
      }
    }

  "zip is idempotent" {
    checkAll(
      Arb.map(KEY_ARB, Arb.intSmall())) {
        a ->
        a.zip(a) shouldBe a.mapValues { it.value to it.value }
    }
  }

  "zip is commutative" {
    checkAll(
      Arb.map(KEY_ARB, Arb.intSmall()),
      Arb.map(KEY_ARB, Arb.intSmall())
    ) {
        a,
        b ->

      a.zip(b).mapValues { it.value.second to it.value.first } shouldBe b.zip(a)
    }
  }

  "zip is associative" {
    checkAll(
      Arb.map(KEY_ARB, Arb.intSmall()),
      Arb.map(KEY_ARB, Arb.intSmall()),
      Arb.map(KEY_ARB, Arb.intSmall())
    ) {
      a,b,c ->

      fun <A, B, C> Pair<Pair<A, B>, C>.assoc(): Pair<A, Pair<B, C>> =
        this.first.first to (this.first.second to this.second)

      a.zip(b.zip(c)) shouldBe (a.zip(b)).zip(c).mapValues { it.value.assoc() }
    }
  }

  "zip with" {
    checkAll(
      Arb.map(KEY_ARB, Arb.string()),
      Arb.map(KEY_ARB, Arb.string())
    ) { a, b ->
      val fn = { k: Int, l: String, r: String -> "$k $l $r" }
      a.zip(b, fn) shouldBe a.zip(b).mapValues { fn(it.key, it.value.first, it.value.second) }
    }
  }

    "zip functoriality" {
      checkAll(
        Arb.map(KEY_ARB, Arb.string()),
        Arb.map(KEY_ARB, Arb.string())) {
        a,b ->

        val f = { e: String -> "f${e}" }
        val g = { e: String -> "g${e}" }
        fun <A,B,C,D> Pair<A,C>.bimap(f: (A) -> B, g: (C) -> D) = Pair(f(first), g(second))

        val l = a.mapValues{ f(it.value)}.zip(b.mapValues{g(it.value)})
        val r = a.zip(b).mapValues { it.value.bimap(f,g)}

        l shouldBe r
      }
    }

  "zippyness1" {
    checkAll(
      Arb.map(Arb.intSmall(), Arb.string())) {
      xs ->
        xs.zip(xs).mapValues { it.value.first } shouldBe xs
    }
  }

  "zippyness2" {
    checkAll(
      Arb.map(Arb.intSmall(), Arb.string())) {
        xs ->
      xs.zip(xs).mapValues { it.value.second } shouldBe xs
    }
  }

  "zippyness3" {
    checkAll(
      Arb.map(Arb.intSmall(), Arb.pair(Arb.string(), Arb.int()))) {
        xs ->
      xs.mapValues { it.value.first }.zip(xs.mapValues { it.value.second }) shouldBe xs
    }
  }

  "distributivity1" {
    checkAll(
      Arb.map(KEY_ARB, Arb.string()),
      Arb.map(KEY_ARB, Arb.string()),
      Arb.map(KEY_ARB, Arb.string())
    ) {x,y,z ->

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
      Arb.map(KEY_ARB, Arb.string()),
      Arb.map(KEY_ARB, Arb.string()),
      Arb.map(KEY_ARB, Arb.string())
    ) {x,y,z ->

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
      Arb.map(KEY_ARB, Arb.string()),
      Arb.map(KEY_ARB, Arb.string()),
      Arb.map(KEY_ARB, Arb.string())
    ) {x,y,z ->

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
      Arb.map(Arb.intSmall(), Arb.string())
    ) { xs ->
      val ls = xs.zip(xs).unzip()
      val rs = xs to xs

      ls shouldBe rs
    }
  }

  "zip is the inverse of unzip" {
    checkAll(
      Arb.map(Arb.intSmall(), Arb.pair(Arb.string(), Arb.int()))
    ) { xs ->
      val (a,b) = xs.unzip()
      a.zip(b) shouldBe xs
    }
  }

  "unzip with" {
    checkAll(
      Arb.map(Arb.intSmall(), Arb.pair(Arb.string(), Arb.int()))
    ) { xs ->
      xs.unzip { it.value.first to it.value.second } shouldBe xs.unzip()
    }
  }

  "unalign with" {
    checkAll(
      Arb.map(Arb.intSmall(), Arb.ior(Arb.string(), Arb.int()))
    ) { xs ->
      xs.unalign { it.value } shouldBe xs.unalign()
    }
  }

  "getOrNone" {
    checkAll(
      Arb.map(Arb.int(0 .. 1000), Arb.string())
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
      Arb.map(KEY_ARB, Arb.string()),
      Arb.map(KEY_ARB, Arb.string())
    ) { a,b ->
      a.align(b).unalign() shouldBe (a to b)
    }
  }

  "align is the inverse of unalign" {
    checkAll(
      Arb.map(Arb.intSmall(), Arb.ior(Arb.int(), Arb.string()))
    ) { xs ->
      val (a,b) = xs.unalign()

      a.align(b) shouldBe xs
    }
  }

  "padZip" {
    checkAll(
      Arb.map(KEY_ARB, Arb.string()),
      Arb.map(KEY_ARB, Arb.string())
    ) { a, b ->
      val x = a.padZip(b)

      a.forAll {
        val value: Pair<String?, String?> = x[it.key].shouldNotBeNull()

        value.first shouldBe it.value
      }

      b.forAll {
        val value: Pair<String?, String?> = x[it.key].shouldNotBeNull()

        value.second shouldBe it.value
      }
    }
  }

  "padZip with" {
    checkAll(
      Arb.map(KEY_ARB, Arb.string()),
      Arb.map(KEY_ARB, Arb.string())
    ) { a, b ->
      a.padZip(b) { a,b,c -> "$a $b $c"} shouldBe a.padZip(b).mapValues { "${it.key} ${it.value.first} ${it.value.second}"}
    }
  }

  "salign" {
    checkAll(
      Arb.map(KEY_ARB, Arb.string()),
      Arb.map(KEY_ARB, Arb.string())
    ) {
      a,b ->
      a.salign(Semigroup.string(), b) shouldBe a.align(b) {it.value.fold(::identity, ::identity) { a, b -> a + b } }
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
      Arb.map(Arb.int(), Arb.option(Arb.string()))
    ) { xs ->
      val rs = xs.filterOption()

      xs.forAll {
        val value = it.value
        if (value is Some<String>)
          rs shouldContain (it.key to value.value)
        else
          rs shouldNotContainKey it.key
      }
    }
  }

  "filterInstance" {
    checkAll(
      Arb.map(Arb.int(), Arb.choice(Arb.int(), Arb.string()))
    ) { xs ->
      val a = xs.filterIsInstance<Int, String>()
      val b = xs.filterIsInstance<Int, Int>()

      (a + b) shouldBe xs
    }
  }


    "ensure that Arb used for map keys produces a small enough set of distinct values" {

      /*
        when zipping/aligning maps we will execute different code paths depending on if a given key is present in both maps or not.
        therefor we need to make sure to use an Arb here that produces a small enough set of distint values.
        this test is to ensure that the arb in use should cause at least 50 iterations with at least 10 keys in both maps
       */

      val result = mutableListOf<Int>()

      val arb = KEY_ARB
      checkAll(
        Arb.map(arb, Arb.intSmall()),
        Arb.map(arb, Arb.intSmall())
      ) { a, b ->
         result.add((a.keys.intersect(b.keys)).size)
      }

      result.count { it > 10 } shouldBeGreaterThan 50
    }


    "zip2" {
      checkAll(
        Arb.map(KEY_ARB, Arb.intSmall()),
        Arb.map(KEY_ARB, Arb.intSmall())
      ) { a, b ->
        val result = a.zip(b) { _, aa, bb -> Pair(aa, bb) }
        val expected = a.filter { (k, _) -> b.containsKey(k) }
          .map { (k, v) -> Pair(k, Pair(v, b[k]!!)) }
          .toMap()

        result shouldBe expected
      }
    }

    "zip3" {
      checkAll(
        Arb.map(KEY_ARB, Arb.intSmall()),
        Arb.map(KEY_ARB, Arb.intSmall())
      ) { a, b ->
        val result = a.zip(b, b) { _, aa, bb, cc -> Triple(aa, bb, cc) }

        val expected = a.filter { (k, _) -> b.containsKey(k) }
          .map { (k, v) -> Pair(k, Triple(v, b[k]!!, b[k]!!)) }
          .toMap()

        result shouldBe expected
      }
    }

    "zip4" {
      checkAll(
        Arb.map(KEY_ARB, Arb.intSmall()),
        Arb.map(KEY_ARB, Arb.intSmall()),
      ) { a, b ->
        val result = a.zip(b, b, b) { _, aa, bb, cc, dd -> Tuple4(aa, bb, cc, dd) }

        val expected = a.filter { (k, _) -> b.containsKey(k) }
          .map { (k, v) -> Pair(k, Tuple4(v, b[k]!!, b[k]!!, b[k]!!)) }
          .toMap()

        result shouldBe expected
      }
    }

    "zip5" {
      checkAll(
        Arb.map(KEY_ARB, Arb.intSmall()),
        Arb.map(KEY_ARB, Arb.intSmall()),
      ) { a, b ->
        val result = a.zip(b, b, b, b) { _, aa, bb, cc, dd, ee -> Tuple5(aa, bb, cc, dd, ee) }

        val expected = a.filter { (k, _) -> b.containsKey(k) }
          .map { (k, v) -> Pair(k, Tuple5(v, b[k]!!, b[k]!!, b[k]!!, b[k]!!)) }
          .toMap()

        result shouldBe expected
      }
    }

    "zip6" {
      checkAll(
        Arb.map(KEY_ARB, Arb.intSmall()),
        Arb.map(KEY_ARB, Arb.intSmall()),
      ) { a, b ->
        val result = a.zip(b, b, b, b, b) { _, aa, bb, cc, dd, ee, ff -> Tuple6(aa, bb, cc, dd, ee, ff) }

        val expected = a.filter { (k, _) -> b.containsKey(k) }
          .map { (k, v) -> Pair(k, Tuple6(v, b[k]!!, b[k]!!, b[k]!!, b[k]!!, b[k]!!)) }
          .toMap()

        result shouldBe expected
      }
    }

    "zip7" {
      checkAll(
        Arb.map(KEY_ARB, Arb.intSmall()),
        Arb.map(KEY_ARB, Arb.intSmall())
      ) { a, b ->
        val result = a.zip(b, b, b, b, b, b) { _, aa, bb, cc, dd, ee, ff, gg -> Tuple7(aa, bb, cc, dd, ee, ff, gg) }

        val expected = a.filter { (k, _) -> b.containsKey(k) }
          .map { (k, v) -> Pair(k, Tuple7(v, b[k]!!, b[k]!!, b[k]!!, b[k]!!, b[k]!!, b[k]!!)) }
          .toMap()

        result shouldBe expected
      }
    }

    "zip8" {
      checkAll(
        Arb.map(KEY_ARB, Arb.intSmall()),
        Arb.map(KEY_ARB, Arb.intSmall())
      ) { a, b ->
        val result =
          a.zip(b, b, b, b, b, b, b) { _, aa, bb, cc, dd, ee, ff, gg, hh -> Tuple8(aa, bb, cc, dd, ee, ff, gg, hh) }

        val expected = a.filter { (k, _) -> b.containsKey(k) }
          .map { (k, v) -> Pair(k, Tuple8(v, b[k]!!, b[k]!!, b[k]!!, b[k]!!, b[k]!!, b[k]!!, b[k]!!)) }
          .toMap()

        result shouldBe expected
      }
    }

    "zip9" {
      checkAll(
        Arb.map(KEY_ARB, Arb.intSmall()),
        Arb.map(KEY_ARB, Arb.intSmall())
      ) { a, b ->
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

    "zip10" {
      checkAll(
        Arb.map(KEY_ARB, Arb.intSmall()),
        Arb.map(KEY_ARB, Arb.intSmall())
      ) { a, b ->
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

    "flatMap" {
      checkAll(
        Arb.map(Arb.string(), Arb.intSmall()),
        Arb.map(Arb.string(), Arb.string())
      ) { a, b ->
        val result: Map<String, String> = a.flatMap { b }
        val expected: Map<String, String> = a.filter { (k, _) -> b.containsKey(k) }
          .map { (k, _) -> Pair(k, b[k]!!) }
          .toMap()
        result shouldBe expected
      }
    }

})

private val KEY_ARB = Arb.int(0 .. 250)
