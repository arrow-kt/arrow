package arrow.optics.plugin

import kotlin.test.Test

class LensTests {

  @Test
  fun `Lenses will be generated for data class`() {
    """
      |$`package`
      |$imports
      |@optics
      |data class LensData(
      |  val field1: String
      |) { companion object }
      |
      |val i: Lens<LensData, String> = LensData.field1
      |val r = i != null
      """.evals("r" to true)
  }

  @Test
  fun `Lenses will be generated for data class with parameters having keywords as names`() {
    """
      |$`package`
      |$imports
      |@optics
      |data class LensData(
      |  val `in`: String
      |) { companion object }
      """.compilationSucceeds()
  }

  @Test
  fun `Lenses will be generated for generic data class with parameters having keywords as names`() {
    """
      |$`package`
      |$imports
      |@optics
      |data class LensData<T>(
      |  val `in`: T
      |) { companion object }
      """.compilationSucceeds()
  }

  @Test
  fun `Lenses will be generated for data class with secondary constructors`() {
    """
      |$`package`
      |$imports
      |@optics
      |data class LensesSecondaryConstructor(val fieldNumber: Int, val fieldString: String) {
      |  constructor(number: Int) : this(number, number.toString())
      |  companion object
      |}
      |
      |val i: Lens<LensesSecondaryConstructor, String> = LensesSecondaryConstructor.fieldString
      |val r = i != null
      """.evals("r" to true)
  }

  @Test
  fun `Lenses which mentions imported elements`() {
    """
      |$`package`
      |$imports
      |
      |@optics
      |data class OpticsTest(val time: kotlin.time.Duration) {
      |  companion object
      |}
      |
      |val i: Lens<OpticsTest, kotlin.time.Duration> = OpticsTest.time
      |val r = i != null
      """.evals("r" to true)
  }

  @Test
  fun `Lenses which mentions type arguments`() {
    """
      |$`package`
      |$imports
      |@optics
      |data class OpticsTest<A>(val field: A) {
      |  companion object
      |}
      |
      |val i: Lens<OpticsTest<Int>, Int> = OpticsTest.field()
      |val r = i != null
      """.evals("r" to true)
  }

  @Test
  fun `Lenses for nested classes`() {
    """
      |$`package`
      |$imports
      |@optics
      |data class LensData(val field1: String) {
      |  @optics
      |  data class InnerLensData(val field2: String) {
      |    companion object
      |  }
      |  companion object 
      |}
      |
      |val i: Lens<LensData.InnerLensData, String> = LensData.InnerLensData.field2
      |val r = i != null
      """.evals("r" to true)
  }

  @Test
  fun `Lenses for nested classes with repeated names (#2718)`() {
    """
      |$`package`
      |$imports
      |@optics
      |data class LensData(val field1: String) {
      |  @optics
      |  data class InnerLensData(val field2: String) {
      |    companion object
      |  }
      |  companion object 
      |}
      |
      |@optics
      |data class OtherLensData(val field1: String) {
      |  @optics
      |  data class InnerLensData(val field2: String) {
      |    companion object
      |  }
      |  companion object 
      |}
      |
      |val i: Lens<LensData.InnerLensData, String> = LensData.InnerLensData.field2
      |val j: Lens<OtherLensData.InnerLensData, String> = OtherLensData.InnerLensData.field2
      |val r = i != null && j != null
      """.evals("r" to true)
  }

  @Test
  fun `Lenses for STAR arguments`() {
    """
      |$`package`
      |$imports
      |@optics
      |data class GenericType<A>(
      |  val field1: A
      |) { companion object }
      |
      |@optics
      |data class IsoData(val genericType: GenericType<*>) {
      |  companion object
      |}
      """.compilationSucceeds()
  }

  @Test
  fun `Lens for sealed class property, one choice`() {
    """
      |$`package`
      |$imports
      |@optics
      |sealed class LensSealed {
      |  abstract val property1: String
      |  
      |  data class dataChild(override val property1: String) : LensSealed()
      |   
      |  companion object 
      |}
      |
      |val l: Lens<LensSealed, String>? = LensSealed.property1
      |val r = l != null
      """.evals("r" to true)
  }

  @Test
  fun `Lens for sealed class property, three choices`() {
    """
      |$`package`
      |$imports
      |@optics
      |sealed class LensSealed {
      |  abstract val property1: String
      |  
      |  data class dataChild1(override val property1: String) : LensSealed()
      |  data class dataChild2(override val property1: String, val number: Int) : LensSealed()
      |  data class dataChild3(override val property1: String, val enabled: Boolean) : LensSealed()
      |   
      |  companion object 
      |}
      |
      |val l: Lens<LensSealed, String>? = LensSealed.property1
      |val r = l != null
      """.evals("r" to true)
  }

  @Test
  fun `Lens for sealed class property, three choices outside`() {
    """
      |$`package`
      |$imports
      |@optics
      |sealed class LensSealed {
      |  abstract val property1: String
      |  
      |  companion object
      |}
      |  
      |data class dataChild1(override val property1: String) : LensSealed()
      |data class dataChild2(override val property1: String, val number: Int) : LensSealed()
      |data class dataChild3(override val property1: String, val enabled: Boolean) : LensSealed()
      |
      |val l: Lens<LensSealed, String>? = LensSealed.property1
      |val r = l != null
      """.evals("r" to true)
  }

  @Test
  fun `Lens for sealed class property, zero choices`() {
    """
      |$`package`
      |$imports
      |@optics
      |sealed class LensSealed {
      |  abstract val property1: String
      |   
      |  companion object 
      |}
      |
      |val l: Lens<LensSealed, String>? = LensSealed.property1
      |val r = l != null
      """.compilationFails()
  }

  @Test
  fun `Lens for sealed class property, ignoring changed nullability`() {
    """
      |$`package`
      |$imports
      |@optics
      |sealed class LensSealed {
      |  abstract val property1: String?
      |  
      |  data class dataChild1(override val property1: String?) : LensSealed()
      |  data class dataChild2(override val property1: String?, val number: Int) : LensSealed()
      |  data class dataChild3(override val property1: String, val enabled: Boolean) : LensSealed()
      |   
      |  companion object 
      |}
      |
      |val l: Lens<LensSealed, String>? = LensSealed.property1
      |val r = l != null
      """.compilationFails()
  }

  @Test
  fun `Lens for sealed class property, ignoring changed types`() {
    """
      |$`package`
      |$imports
      |@optics
      |sealed interface Base<out T> {
      |    val prop: T
      |
      |    companion object
      |}
      |
      |@optics
      |data class Child1(override val prop: String) : Base<String> {
      |    companion object
      |}
      |
      |@optics
      |data class Child2(override val prop: Int) : Base<Int> {
      |    companion object
      |}
      |
      |val l: Lens<Base<String>, String> = Base.prop()
      |val r = l != null
      """.compilationFails()
  }
}
