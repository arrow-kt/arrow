package arrow.optics

import arrow.core.*
import arrow.data.*
import arrow.optics.instances.*
import arrow.optics.dsl.*
import arrow.test.UnitSpec
import io.kotlintest.matchers.shouldBe

@optics
data class Street(val number: Int, val name: String) {
  companion object
}

@optics
data class Address(val city: String, val street: Street) {
  companion object
}

@optics
data class Company(val name: String, val address: Address) {
  companion object
}

@optics
data class Employee(val name: String, val company: Company?) {
  companion object
}

@optics
data class CompanyEmployees(val employees: ListK<Employee>) {
  companion object
}

sealed class Keys
object One : Keys()
object Two : Keys()
object Three : Keys()
object Four : Keys()

@optics
data class Db(val content: MapK<Keys, String>) {
  companion object
}

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
      Employee.company.address.street.name.modify(employee, String::toUpperCase) shouldBe
        (Employee.company compose
          Company.address compose
          Address.street compose
          Street.name).modify(employee, String::toUpperCase)
    }

    "Working with At in Optics should be same as in DSL" {
      Db.content.at(MapK.at(), One).some.modify(db, String::toUpperCase) shouldBe
        (Db.content compose
          MapK.at<Keys, String>().at(One) compose
          Option.some()).modify(db, String::toUpperCase)
    }

    "Working with Each in Optics should be same as in DSL" {
      Db.content.every(MapK.each()).modify(db, String::toUpperCase) shouldBe
        (Db.content compose
          MapK.traversal()).modify(db, String::toUpperCase)
    }

  }

}
