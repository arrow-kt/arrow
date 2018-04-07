package lens.docs.domain

import arrow.lenses
import arrow.syntax

@syntax
data class Street(val number: Int, val name: String)
@syntax
data class Address(val city: String, val street: Street)
@syntax
data class Company(val name: String, val address: Address)
@lenses
data class Employee(val name: String, val company: Company)
