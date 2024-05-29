package arrow.optics.match

import arrow.optics.Lens
import arrow.optics.Prism
import io.kotest.matchers.shouldBe
import kotlin.test.Test

data class Name(
  val firstName: String, val lastName: String
) {
  companion object {
    val firstName: Lens<Name, String> = Lens(
      get = { it.firstName },
      set = { name, firstName -> name.copy(firstName = firstName) }
    )
    val lastName: Lens<Name, String> = Lens(
      get = { it.lastName },
      set = { name, lastName -> name.copy(lastName = lastName) }
    )
  }
}

sealed interface User {
  companion object {
    val person: Prism<User, Person> = Prism.instanceOf<User, Person>()
    val company: Prism<User, Company> = Prism.instanceOf<User, Company>()
  }
}

data class Person(
  val name: Name, val age: Int
): User {
  companion object {
    val name: Lens<Person, Name> = Lens(
      get = { it.name },
      set = { person, name -> person.copy(name = name) }
    )
    val age: Lens<Person, Int> = Lens(
      get = { it.age },
      set = { person, age -> person.copy(age = age) }
    )
  }
}

data class Company(
  val name: String, val director: Name, val address: String
): User {
  companion object {
    val name: Lens<Company, String> = Lens(
      get = { it.name },
      set = { company, name -> company.copy(name = name) }
    )
    val director: Lens<Company, Name> = Lens(
      get = { it.director },
      set = { company, director -> company.copy(director = director) }
    )
    val address: Lens<Company, String> = Lens(
      get = { it.address },
      set = { company, address -> company.copy(address = address) }
    )
  }
}

val User.shownName: String get() = this.matchOrThrow {
  User.person(Person.name(Name.firstName), Person.age.suchThat { it < 18 }) then { (fn, _) -> fn }
  User.person(Person.name(Name.firstName, Name.lastName)) then { (fn, ln) -> "Sir/Madam $fn $ln" }
  User.company(Company.name, Company.director(Name.lastName)) then { (nm, d) -> "$nm, att. $d" }
}

val Person.shownNameForPerson: String get() = this.matchOrThrow {
  it(Person.name(Name.firstName), Person.age.suchThat { it < 18 }) then { (fn, _) -> fn }
  it(Person.name(Name.firstName, Name.lastName)) then { (fn, ln) -> "Sir/Madam $fn $ln" }
}

class MatchTest {
  @Test fun userKid() {
    val p = Person(Name("Jimmy", "Jones"), 7)
    p.shownName shouldBe "Jimmy"
    p.shownNameForPerson shouldBe "Jimmy"
  }

  @Test fun userAdult() {
    val p = Person(Name("Jimmy", "Jones"), 20)
    p.shownName shouldBe "Sir/Madam Jimmy Jones"
    p.shownNameForPerson shouldBe "Sir/Madam Jimmy Jones"
  }
}
