package arrow.optics.plugin

import kotlin.test.Test

class PrismTests {

  @Test
  fun `Prism will be generated for sealed class`() {
    """
      |$`package`
      |$imports
      |@optics
      |sealed class PrismSealed(val field: String, val nullable: String?) {
      | data class PrismSealed1(private val a: String?) : PrismSealed("", a)
      | data class PrismSealed2(private val b: String?) : PrismSealed("", b)
      | companion object
      |}
      |val i: Prism<PrismSealed, PrismSealed.PrismSealed1> = PrismSealed.prismSealed1
      """.compilationSucceeds(allWarningsAsErrors = true)
  }

  @Test
  fun `Prism will be generated for generic sealed class`() {
    """
      |$`package`
      |$imports
      |@optics
      |sealed class PrismSealed<A,B>(val field: A, val nullable: B?) {
      | data class PrismSealed1(private val a: String?) : PrismSealed<String, String>("", a)
      | data class PrismSealed2<C>(private val b: C?) : PrismSealed<String, C>("", b)
      | companion object
      |}
      |val i: Prism<PrismSealed<String, String>, PrismSealed.PrismSealed1> = PrismSealed.prismSealed1()
      """.compilationSucceeds(allWarningsAsErrors = true)
  }

  @Test
  fun `Prism will be generated for sealed class and subclasses having keywords as names`() {
    """
      |$`package`
      |$imports
      |@optics
      |sealed class PrismSealed(val field: String, val nullable: String?) {
      | data class In(private val a: String?) : PrismSealed("", a)
      | data class PrismSealed2(private val b: String?) : PrismSealed("", b)
      | companion object
      |}
      |val i: Prism<PrismSealed, PrismSealed.In> = PrismSealed.`in`
      """.compilationSucceeds(allWarningsAsErrors = true)
  }

  @Test
  fun `Prism will be generated for generic sealed class and subclasses having keywords as names`() {
    """
      |$`package`
      |$imports
      |@optics
      |sealed class PrismSealed<A,B>(val field: A, val nullable: B?) {
      | data class In(private val a: String?) : PrismSealed<String, String>("", a)
      | data class PrismSealed2<C>(private val b: C?) : PrismSealed<String, C>("", b)
      | companion object
      |}
      |val i: Prism<PrismSealed<String, String>, PrismSealed.In> = PrismSealed.`in`()
      """.compilationSucceeds(allWarningsAsErrors = true)
  }

  @Test
  fun `Prism will be generated without warning for sealed class with only one subclass`() {
    """
      |$`package`
      |$imports
      |@optics
      |sealed class PrismSealed(val field: String, val nullable: String?) {
      | data class PrismSealed1(private val a: String?) : PrismSealed("", a)
      | companion object
      |}
      |val i: Prism<PrismSealed, PrismSealed.PrismSealed1> = PrismSealed.prismSealed1
      """.compilationSucceeds(allWarningsAsErrors = true)
  }

  @Test
  fun `Prism will not be generated for sealed class if DSL Target is specified`() {
    """
      |$`package`
      |$imports
      |@optics([OpticsTarget.DSL])
      |sealed class PrismSealed(val field: String, val nullable: String?) {
      | data class PrismSealed1(private val a: String?) : PrismSealed("", a)
      | data class PrismSealed2(private val b: String?) : PrismSealed("", b)
      | companion object
      |}
      |val i: Prism<PrismSealed, PrismSealed.PrismSealed1> = PrismSealed.prismSealed1
      |val r = i != null
      """.compilationFails()
  }
}
