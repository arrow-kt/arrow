package arrow.optics

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

// from https://kotlinlang.slack.com/archives/C5UPMM0A0/p1688822411819599
// and https://github.com/overfullstack/my-lab/blob/master/arrow/src/test/kotlin/ga/overfullstack/optics/OpticsLab.kt

data class Person(val name: String, val age: Int, val address: Address) {
  companion object {
    val name: Lens<Person, String> = Lens(
      get = { it.name },
      set = { p, x -> p.copy(name = x) }
    )
    val age: Lens<Person, Int> = Lens(
      get = { it.age },
      set = { p, x -> p.copy(age = x) }
    )
    val address: Lens<Person, Address> = Lens(
      get = { it.address },
      set = { p, x -> p.copy(address = x) }
    )
  }
}
data class Address(val street: Street, val city: City, val coordinates: List<Int>) {
  companion object {
    val city: Lens<Address, City> = Lens(
      get = { it.city },
      set = { a, x -> a.copy(city = x) }
    )
    val coordinates: Lens<Address, List<Int>> = Lens(
      get = { it.coordinates },
      set = { a, x -> a.copy(coordinates = x) }
    )
  }
}
data class Street(val name: String, val number: Int?)
data class City(val name: String, val country: String) {
  companion object {
    val name: Lens<City, String> = Lens(
      get = { it.name },
      set = { c, x -> c.copy(name = x) }
    )
    val country: Lens<City, String> = Lens(
      get = { it.country },
      set = { c, x -> c.copy(country = x) }
    )
  }
}

fun Person.moveToAmsterdamCopy(): Person = copy {
  Person.address + Address.city + City.name set "Amsterdam"
  Person.address + Address.city + City.country set "Netherlands"
  Person.address + Address.coordinates set listOf(2, 3)
}

fun Person.moveToAmsterdamInside(): Person = copy {
  inside(Person.address + Address.city) {
    City.name set "Amsterdam"
    City.country set "Netherlands"
  }
}

class CopyTest : StringSpec({
  "optics" {
    val me =
      Person(
        "Gopal",
        99,
        Address(Street("Kotlinstraat", 1), City("Hilversum", "Netherlands"), listOf(1, 2))
      )

    Person.name.get(me) shouldBe "Gopal"

    val meAfterBirthdayParty = Person.age.modify(me) { it + 1 }
    Person.age.get(meAfterBirthdayParty) shouldBe 100

    val newAddress =
      Address(Street("Kotlinplein", null), City("Amsterdam", "Netherlands"), listOf(1, 2))
    val meAfterMoving = Person.address.set(me, newAddress)
    Person.address.get(meAfterMoving) shouldBe newAddress
  }

  "optics composition" {
    val personCity: Lens<Person, String> = Person.address compose Address.city compose City.name

    val me =
      Person(
        "Alejandro",
        35,
        Address(Street("Kotlinstraat", 1), City("Hilversum", "Netherlands"), listOf(1, 2))
      )

    personCity.get(me) shouldBe "Hilversum"
    val meAtTheCapital = personCity.set(me, "Amsterdam")
    meAtTheCapital.address.city.name shouldBe "Amsterdam"
  }

  "optics copy to modify multiple fields" {
    val me =
      Person(
        "Alejandro",
        35,
        Address(Street("Kotlinstraat", 1), City("Hilversum", "Netherlands"), listOf(1, 2))
      )
    val meAfterMoving1 = me.moveToAmsterdamInside()
    val meAfterMoving2 = me.moveToAmsterdamInside()
    meAfterMoving1 shouldBe meAfterMoving2
    meAfterMoving1.address.city.name shouldBe "Amsterdam"
  }
})
