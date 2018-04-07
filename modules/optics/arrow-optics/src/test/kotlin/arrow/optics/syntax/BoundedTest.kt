package arrow.optics

import arrow.syntax
import arrow.optics.syntax.*
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

//TODO: Rewrite to annotation processor test when multiple file support is implemented
class BoundedTest: UnitSpec() {

    init {

        "@optics classes are generated properly" {

            val employee = Employee("John Doe", Company("Kategory", Address("Functional city", Street(42, "lambda street"))))
            val newEmployee = employee.setter().company.address.street.name.modify(String::toUpperCase)

            val expected = Employee("John Doe", Company("Kategory", Address("Functional city", Street(42, "LAMBDA STREET"))))

            newEmployee shouldBe expected
        }

    }

}