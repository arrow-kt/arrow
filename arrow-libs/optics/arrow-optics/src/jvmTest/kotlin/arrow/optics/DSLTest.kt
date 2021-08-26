package arrow.optics

import arrow.core.None
import arrow.core.test.UnitSpec
import arrow.optics.typeclasses.At
import arrow.optics.typeclasses.Index
import io.kotest.matchers.shouldBe

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
data class CompanyEmployees(val employees: List<Employee>) {
  companion object
}

sealed class Keys
object One : Keys()
object Two : Keys()
object Three : Keys()
object Four : Keys()

@optics
data class Db(val content: Map<Keys, String>) {
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

    val employees = CompanyEmployees(listOf(john, jane))

    val db = Db(
      mapOf(
        One to "one",
        Two to "two",
        Three to "three",
        Four to "four"
      )
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
      Index.list<Employee>().run {
        CompanyEmployees.employees[1].company.address.street.name.modify(
          employees,
          String::toUpperCase
        )
      } shouldBe (CompanyEmployees.employees compose
        Index.list<Employee>().index(1) compose
        Employee.company compose
        Company.address compose
        Address.street compose
        Street.name).modify(employees, String::toUpperCase)
    }

    "Working with At in Optics should be same as in DSL" {
      At.map<Keys, String>().run {
        Db.content.at(One).set(db, None)
      } shouldBe (Db.content compose At.map<Keys, String>().at(One)).set(db, None)
    }

    "Working with Every in Optics should be same as in DSL" {
      Every.map<Keys, String>().run {
        Db.content.every.modify(db, String::toUpperCase)
      } shouldBe (Db.content compose Every.map()).modify(db, String::toUpperCase)
    }
  }
}
