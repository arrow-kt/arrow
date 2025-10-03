package example

import arrow.optics.optics

@optics
data class Person(val name: String, val age: Int)

data class Address(val street: String, val city: String)
