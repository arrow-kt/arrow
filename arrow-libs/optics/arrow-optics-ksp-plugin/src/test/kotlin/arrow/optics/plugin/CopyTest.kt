package arrow.optics.plugin

import org.junit.jupiter.api.Test

// from https://kotlinlang.slack.com/archives/C5UPMM0A0/p1688822411819599
// and https://github.com/overfullstack/my-lab/blob/master/arrow/src/test/kotlin/ga/overfullstack/optics/OpticsLab.kt

val copyCode = """
@optics data class Person(val name: String, val age: Int, val address: Address) {
  companion object
}
@optics data class Address(val street: Street, val city: City, val coordinates: List<Int>) {
  companion object
}
@optics data class Street(val name: String, val number: Int?) {
  companion object
}
@optics data class City(val name: String, val country: String) {
  companion object
}

fun Person.moveToAmsterdamCopy(): Person = copy {
  Person.address.city.name set "Amsterdam"
  Person.address.city.country set "Netherlands"
  Person.address .coordinates set listOf(2, 3)
}

fun Person.moveToAmsterdamInside(): Person = copy {
  inside(Person.address.city) {
    City.name set "Amsterdam"
    City.country set "Netherlands"
  }
}

val me =
  Person(
    "Gopal",
    99,
    Address(Street("Kotlinstraat", 1), City("Hilversum", "Netherlands"), listOf(1, 2))
  )
"""

class CopyTest {
  @Test
  fun `code compiles`() {
    """
      |package PersonTest
      |$imports
      |$copyCode
      """.compilationSucceeds()
  }

  @Test
  fun `birthday increments`() {
    """
      |package PersonTest
      |$imports
      |$copyCode
      |val meAfterBirthdayParty = Person.age.modify(me) { it + 1 }
      |val r = Person.age.get(meAfterBirthdayParty)
      """.evals("r" to 100)
  }

  @Test
  fun `moving to another city`() {
    """
      |package PersonTest
      |$imports
      |$copyCode
      |val newAddress =
      |  Address(Street("Kotlinplein", null), City("Amsterdam", "Netherlands"), listOf(1, 2))
      |val meAfterMoving = Person.address.set(me, newAddress)
      |val r = Person.address.get(meAfterMoving).street.name
      """.evals("r" to "Kotlinplein")
  }

  @Test
  fun `optics composition`() {
    """
      |package PersonTest
      |$imports
      |$copyCode
      |val personCity: Lens<Person, String> = Person.address compose Address.city compose City.name
      |val meAtTheCapital = personCity.set(me, "Amsterdam")
      |val r = meAtTheCapital.address.city.name
      """.evals("r" to "Amsterdam")
  }

  @Test
  fun `optics copy to modify multiple fields`() {
    """
      |package PersonTest
      |$imports
      |$copyCode
      |val meAfterMoving = me.moveToAmsterdamInside()
      |val r = meAfterMoving.address.city.name
      """.evals("r" to "Amsterdam")
  }
}
