@file:Suppress("NAME_SHADOWING")

package arrow.core

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.property.Arb
import io.kotest.matchers.shouldBe
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.orNull
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll

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
    checkAll(
      Arb.string().orNull(0.05),
      Arb.string().orNull(0.05)
    ) { a: String?, b: String? ->
      if (a == null || b == null) Nullable.zip(a, b, { _, _ -> Unit }) shouldBe null
      else Nullable.zip(a, b, { a, b -> a + b }) shouldBe a + b
    }
  }

  "map3 only performs action when all arguments are not null" {
    checkAll(
      Arb.string().orNull(0.05),
      Arb.string().orNull(0.05),
      Arb.string().orNull(0.05)
    ) { a: String?, b: String?, c: String? ->
      if (a == null || b == null || c == null) Nullable.zip(a, b, c, { a, b, c -> a + b + c }) shouldBe null
      else Nullable.zip(a, b, c, { a, b, c -> a + b + c }) shouldBe a + b + c
    }
  }

  "map4 only performs action when all arguments are not null" {
    checkAll(
      Arb.string().orNull(0.05),
      Arb.string().orNull(0.05),
      Arb.string().orNull(0.05),
      Arb.string().orNull(0.05),
    ) { a: String?, b: String?, c: String?, d: String? ->
      if (a == null || b == null || c == null || d == null) Nullable.zip(
        a,
        b,
        c,
        d,
        { a, b, c, d -> a + b + c + d }) shouldBe null
      else Nullable.zip(a, b, c, d, { a, b, c, d -> a + b + c + d }) shouldBe a + b + c + d
    }
  }

  "map5 only performs action when all arguments are not null" {
    checkAll(
      Arb.string().orNull(0.05),
      Arb.string().orNull(0.05),
      Arb.string().orNull(0.05),
      Arb.string().orNull(0.05),
      Arb.string().orNull(0.05),
    ) { a: String?, b: String?, c: String?, d: String?, e: String? ->
      if (a == null || b == null || c == null || d == null || e == null) Nullable.zip(
        a,
        b,
        c,
        d,
        e,
        { _, _, _, _, _ -> Unit }) shouldBe null
      else Nullable.zip(a, b, c, d, e, { a, b, c, d, e -> a + b + c + d + e }) shouldBe a + b + c + d + e
    }
  }

  "map6 only performs action when all arguments are not null" {
    checkAll(
      Arb.string().orNull(0.05),
      Arb.string().orNull(0.05),
      Arb.string().orNull(0.05),
      Arb.string().orNull(0.05),
      Arb.string().orNull(0.05),
      Arb.string().orNull(0.05),
    ) { a: String?, b: String?, c: String?, d: String?, e: String?, f: String? ->
      if (a == null || b == null || c == null || d == null || e == null || f == null) Nullable.zip(
        a,
        b,
        c,
        d,
        e,
        f,
        { _, _, _, _, _, _ -> Unit }) shouldBe null
      else Nullable.zip(a, b, c, d, e, f, { a, b, c, d, e, f -> a + b + c + d + e + f }) shouldBe a + b + c + d + e + f
    }
  }

  "map7 only performs action when all arguments are not null" {
    checkAll(
      Arb.string().orNull(0.05),
      Arb.string().orNull(0.05),
      Arb.string().orNull(0.05),
      Arb.string().orNull(0.05),
      Arb.string().orNull(0.05),
      Arb.string().orNull(0.05),
      Arb.string().orNull(0.05),
    ) { a: String?, b: String?, c: String?, d: String?, e: String?, f: String?, g: String? ->
      if (a == null || b == null || c == null || d == null || e == null || f == null || g == null) Nullable.zip(
        a,
        b,
        c,
        d,
        e,
        f,
        g,
        { _, _, _, _, _, _, _ -> Unit }) shouldBe null
      else Nullable.zip(
        a,
        b,
        c,
        d,
        e,
        f,
        g,
        { a, b, c, d, e, f, g -> a + b + c + d + e + f + g }) shouldBe a + b + c + d + e + f + g
    }
  }

  "map8 only performs action when all arguments are not null" {
    checkAll(
      Arb.string().orNull(0.05),
      Arb.string().orNull(0.05),
      Arb.string().orNull(0.05),
      Arb.string().orNull(0.05),
      Arb.string().orNull(0.05),
      Arb.string().orNull(0.05),
      Arb.string().orNull(0.05),
      Arb.string().orNull(0.05),
    ) { a: String?, b: String?, c: String?, d: String?, e: String?, f: String?, g: String?, h: String? ->
      if (a == null || b == null || c == null || d == null || e == null || f == null || g == null || h == null) Nullable.zip(
        a,
        b,
        c,
        d,
        e,
        f,
        g,
        h,
        { _, _, _, _, _, _, _, _ -> Unit }) shouldBe null
      else Nullable.zip(
        a,
        b,
        c,
        d,
        e,
        f,
        g,
        h,
        { a, b, c, d, e, f, g, h -> a + b + c + d + e + f + g + h }) shouldBe a + b + c + d + e + f + g + h
    }
  }

  "map9 only performs action when all arguments are not null" {
    checkAll(
      Arb.int().orNull(0.05),
      Arb.int().orNull(0.05),
      Arb.int().orNull(0.05),
      Arb.int().orNull(0.05),
      Arb.int().orNull(0.05),
      Arb.int().orNull(0.05),
      Arb.int().orNull(0.05),
      Arb.int().orNull(0.05),
      Arb.int().orNull(0.05)
    ) { a: Int?, b: Int?, c: Int?, d: Int?, e: Int?, f: Int?, g: Int?, h: Int?, i: Int? ->
      if (a == null || b == null || c == null || d == null || e == null || f == null || g == null || h == null || i == null) {
        Nullable.zip(a, b, c, d, e, f, g, h, i, { _, _, _, _, _, _, _, _, _ -> Unit }) shouldBe null
      } else {
        Nullable.zip(
          a,
          b,
          c,
          d,
          e,
          f,
          g,
          h,
          i,
          { a, b, c, d, e, f, g, h, i -> a + b + c + d + e + f + g + h + i }) shouldBe a + b + c + d + e + f + g + h + i
      }
    }
  }
})
