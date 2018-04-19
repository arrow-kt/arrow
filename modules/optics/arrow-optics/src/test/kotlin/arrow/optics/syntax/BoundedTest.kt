package arrow.optics

import arrow.data.*
import arrow.optics.syntax.*
import arrow.optics.instances.*
import arrow.test.UnitSpec
import io.kotlintest.matchers.shouldBe

@optics
data class Street(val number: Int, val name: String)

@optics
data class Address(val city: String, val street: Street)

@optics
data class Company(val name: String, val address: Address)

@optics
data class Employee(val name: String, val company: Company?)

@optics
data class CompanyEmployees(val employees: ListK<Employee>)

sealed class Keys
object One : Keys()
object Two : Keys()
object Three : Keys()
object Four : Keys()

@optics
data class Db(val content: MapK<Keys, String>)

//TODO: Rewrite to annotation processor test when multiple file support is implemented
class BoundedTest : UnitSpec() {

  init {

    val employee = Employee("John Doe", Company("Kategory", Address("Functional city", Street(42, "lambda street"))))

    val db = Db(mapOf(
      One to "one",
      Two to "two",
      Three to "three",
      Four to "four"
    ).k())

    "@optics generate DSL properly" {
      val newEmployee = employee.setter().company.address.street.name.modify(String::toUpperCase)

      val expected = Employee("John Doe", Company("Kategory", Address("Functional city", Street(42, "LAMBDA STREET"))))

      newEmployee shouldBe expected
    }

    "Working with At in BoundSetter should be same as in DSL" {
      db.setter().content.at(MapK.at(), One).some.modify(String::toUpperCase) shouldBe
        (dbContent() compose
          MapK.at<Keys, String>().at(One) compose
          somePrism()).modify(db, String::toUpperCase)
    }

    "Working with Each in BoundSetter should be same as in DSL" {
      db.setter().content.every(MapK.each()).modify(String::toUpperCase) shouldBe
        (dbContent() compose
          MapK.traversal()).modify(db, String::toUpperCase)
    }

  }

}