package arrow.optics

import arrow.bounded
import arrow.lenses
import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.StringSpec

@bounded data class Street(val number: Int, val name: String)
@bounded data class Address(val city: String, val street: Street)
@bounded data class Company(val name: String, val address: Address)
@bounded data class Employee(val name: String, val company: Company?)

//TODO: Rewrite to annotation processor test when multiple file support is implemented
class BoundedTest: StringSpec() {

    init {

        "@bounded classes are generated properly" {

            val employee = Employee("John Doe", Company("Kategory", Address("Functional city", Street(42, "lambda street"))))
            val newEmployee = employee.setter().company.nullable.address.street.name.modify { it.capitalize() }

            val expected = Employee("John Doe", Company("Kategory", Address("Functional city", Street(42, "Lambda street"))))

            newEmployee shouldBe expected
        }

    }

}