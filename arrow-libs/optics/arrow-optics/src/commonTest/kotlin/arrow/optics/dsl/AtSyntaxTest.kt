package arrow.optics.dsl

import arrow.core.None
import kotlin.test.Test
import kotlin.test.assertEquals

class AtSyntaxTest {

  @Test
  fun mapModify() {
    val original = mapOf("one" to 1, "two" to 2)
    val expected = Wrapper(original.mapValues { (k, i) ->
      if (k == "one") i + 1 else i
    })
    val actual = Wrapper.lens<Map<String, Int>>()
      .at("one")
      .some
      .modify(Wrapper(original), Int::inc)
    assertEquals(expected, actual)
  }

  @Test
  fun mapRemove() {
    val original = mapOf("one" to 1, "two" to 2)
    val expected = Wrapper(original - "one")
    val actual = Wrapper.lens<Map<String, Int>>()
      .at("one")
      .set(Wrapper(original), None)
    assertEquals(expected, actual)
  }

  @Test
  fun setKeep() {
    val original = setOf(1)
    val expected = Wrapper(setOf(1, 2))
    val actual = Wrapper.lens<Set<Int>>()
      .at(2)
      .set(Wrapper(original), false)
    assertEquals(expected, actual)
  }

  @Test
  fun setRemove() {
    val original = setOf(1)
    val expected = Wrapper(setOf(1, 2))
    val actual = Wrapper.lens<Set<Int>>()
      .at(2)
      .set(Wrapper(original), true)
    assertEquals(expected, actual)
  }

  @Test
  fun setFilter() {
    val original = setOf(1)
    val expected = Wrapper(emptySet<Int>())
    val actual = Wrapper.lens<Set<Int>>()
      .at(1)
      .set(Wrapper(original), false)
    assertEquals(expected, actual)
  }
}
