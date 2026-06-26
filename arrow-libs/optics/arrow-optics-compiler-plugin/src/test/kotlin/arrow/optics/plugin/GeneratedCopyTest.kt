package arrow.optics.plugin

import kotlin.test.Test

val generatedCopyCode = """
@optics @optics.copy data class Person(val name: String, val age: Int, val address: Address) {
  companion object
}
@optics @optics.copy data class Address(val street: Street, val city: City, val coordinates: List<Int>) {
  companion object
}
@optics @optics.copy data class Street(val name: String, val number: Int?) {
  companion object
}
@optics @optics.copy data class City(val name: String, val country: String) {
  companion object
}

val me =
  Person(
    "Gopal",
    99,
    Address(Street("Kotlinstraat", 1), City("Hilversum", "Netherlands"), listOf(1, 2))
  )
"""

class GeneratedCopyTest {
  @Test
  fun `code compiles`() {
    """
      |package PersonTest
      |$imports
      |$generatedCopyCode
      """.compilationSucceeds(contextParameters = true)
  }

  @Test
  fun `birthday increments`() {
    """
      |package PersonTest
      |$imports
      |$generatedCopyCode
      |val meAfterBirthdayParty = me.copy {
      |  age transform { it + 1 }
      |}
      |val r = Person.age.get(meAfterBirthdayParty)
      """.evals("r" to 100, contextParameters = true)
  }

  @Test
  fun `moving to another city`() {
    """
      |package PersonTest
      |$imports
      |$generatedCopyCode
      |val newAddress =
      |  Address(Street("Kotlinplein", null), City("Amsterdam", "Netherlands"), listOf(1, 2))
      |val meAfterMoving = me.copy {
      |  address set newAddress
      |}
      |val r = Person.address.get(meAfterMoving).street.name
      """.evals("r" to "Kotlinplein", contextParameters = true)
  }

  @Test
  fun `optics copy to modify multiple fields`() {
    """
      |package PersonTest
      |$imports
      |$generatedCopyCode
      |val meAfterMoving = me.copy {
      |  address.city.name set "Amsterdam"
      |  address.city.country set "Netherlands"
      |  address.coordinates set listOf(2, 3)
      |}
      |val r = meAfterMoving.address.city.name
      """.evals("r" to "Amsterdam", contextParameters = true)
  }
}
