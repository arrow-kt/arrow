package arrow.optics.dsl

import arrow.core.*
import arrow.optics.Lens
import kotlin.jvm.JvmInline
import kotlin.test.Test
import kotlin.test.assertEquals

class EverySyntaxTest {

  @Test
  fun list() {
    val original = listOf(1, 2)
    val expected = Wrapper(original.map(Int::inc))
    val actual = Wrapper.lens<List<Int>>()
      .every
      .modify(Wrapper(original), Int::inc)
    assertEquals(expected, actual)
  }

  @Test
  fun either_right() {
    val original: Either<String, Int> = 1.right()
    val expected = Wrapper(original.map(Int::inc))
    val actual = Wrapper.lens<Either<String, Int>>()
      .every
      .modify(Wrapper(original), Int::inc)
    assertEquals(expected, actual)
  }

  @Test
  fun either_left() {
    val original: Either<String, Int> = "one".left()
    val expected = Wrapper(original)
    val actual = Wrapper.lens<Either<String, Int>>()
      .every
      .modify(Wrapper(original), Int::inc)
    assertEquals(expected, actual)
  }

  @Test
  fun map() {
    val original = mapOf("one" to 1, "two" to 2)
    val expected = Wrapper(original.mapValues { (_, i) -> i + 1 })
    val actual = Wrapper.lens<Map<String, Int>>()
      .every
      .modify(Wrapper(original), Int::inc)
    assertEquals(expected, actual)
  }

  @Test
  fun nonEmptyList() {
    val original = nonEmptyListOf(1, 2)
    val expected = Wrapper(original.map(Int::inc))
    val actual = Wrapper.lens<NonEmptyList<Int>>()
      .every
      .modify(Wrapper(original), Int::inc)
    assertEquals(expected, actual)
  }

  @Test
  fun some() {
    val original = 1.some()
    val expected = Wrapper(original.map(Int::inc))
    val actual = Wrapper.lens<Option<Int>>()
      .every
      .modify(Wrapper(original), Int::inc)
    assertEquals(expected, actual)
  }

  @Test
  fun none() {
    val original = none<Int>()
    val expected = Wrapper(original.map(Int::inc))
    val actual = Wrapper.lens<Option<Int>>()
      .every
      .modify(Wrapper(original), Int::inc)
    assertEquals(expected, actual)
  }

  @Test
  fun sequence() {
    val original = sequenceOf(1, 2)
    val expected = Wrapper(original.map(Int::inc))
    val actual = Wrapper.lens<Sequence<Int>>()
      .every
      .modify(Wrapper(original), Int::inc)
    assertEquals(expected.value.toList(), actual.value.toList())
  }

  private fun String.mapEach(block: (Char) -> Char): String =
    map(block).joinToString(separator = "")

  @Test
  fun string() {
    val original = "abc"
    val expected = Wrapper(original.mapEach(Char::titlecaseChar))
    val actual = Wrapper.lens<String>()
      .every
      .modify(Wrapper(original), Char::titlecaseChar)
    assertEquals(expected, actual)
  }
}
