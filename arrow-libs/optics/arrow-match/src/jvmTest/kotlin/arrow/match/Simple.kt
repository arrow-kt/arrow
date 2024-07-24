package arrow.match

import io.kotest.matchers.shouldBe
import kotlin.test.Test

data class Name(
  val firstName: String, val lastName: String
)

sealed interface User
data class Person(
  val name: Name, val age: Int
) : User

data class Company(
  val name: String, val director: Name, val address: String
): User

val User.shownName: String get() = this.matchOrThrow {
  Person::class.of(Person::name.of(Name::firstName), Person::age.suchThat { it < 18 }) then { (fn, _) -> fn }
  Person::class.of(Person::name.of(Name::firstName, Name::lastName)) then { (fn, ln) -> "Sir/Madam $fn $ln" }
  Company::class.of(Company::name, Company::director.of(Name::lastName)) then { (nm, d) -> "$nm, att. $d" }
}

val Person.shownNameForPerson: String get() = this.matchOrThrow {
  it.of(Person::name.of(Name::firstName), Person::age.suchThat { it < 18 }) then { (fn, _) -> fn }
  it.of(Person::name.of(Name::firstName, Name::lastName)) then { (fn, ln) -> "Sir/Madam $fn $ln" }
}

class MatchTest {
  @Test
  fun userKid() {
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
