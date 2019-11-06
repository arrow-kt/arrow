package arrow.optics

import arrow.core.None
import arrow.core.ListK
import arrow.core.MapK
import arrow.core.k
import arrow.optics.dsl.at
import arrow.optics.extensions.listk.index.index
import arrow.optics.extensions.mapk.at.at
import arrow.optics.extensions.mapk.each.each
import arrow.optics.extensions.traversal
import arrow.test.UnitSpec
import io.kotlintest.shouldBe

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

class BoundedTest : UnitSpec() {

  init {

    val john = Employee(
      "John Doe",
      Company("Kategory", Address("Functional city", Street(42, "lambda street")))
    )
    val jane = Employee(
      "Jane Doe",
      Company("Kategory", Address("Functional city", Street(42, "lambda street")))
    )

    val employees = CompanyEmployees(listOf(john, jane).k())

    val db = Db(
      mapOf(
        One to "one",
        Two to "two",
        Three to "three",
        Four to "four"
      ).k()
    )

    "@optics generate DSL properly" {
      Employee.company.address.street.name.modify(
        john,
        String::toUpperCase
      ) shouldBe (Employee.company compose
          Company.address compose
          Address.street compose
          Street.name).modify(john, String::toUpperCase)
    }

    "Index enables special Index syntax" {
      ListK.index<Employee>().run {
        CompanyEmployees.employees[1].company.address.street.name.modify(
          employees,
          String::toUpperCase
        )
      } shouldBe (CompanyEmployees.employees compose
          ListK.index<Employee>().index(1) compose
          Employee.company compose
          Company.address compose
          Address.street compose
          Street.name).modify(employees, String::toUpperCase)
    }

    "Working with At in Optics should be same as in DSL" {
      MapK.at<Keys, String>().run {
        Db.content.at(MapK.at(), One).set(db, None)
      } shouldBe (Db.content compose MapK.at<Keys, String>().at(One)).set(db, None)
    }

    "Working with Each in Optics should be same as in DSL" {
      MapK.each<Keys, String>().run {
        Db.content.every.modify(db, String::toUpperCase)
      } shouldBe (Db.content compose MapK.traversal()).modify(db, String::toUpperCase)
    }
  }
}
