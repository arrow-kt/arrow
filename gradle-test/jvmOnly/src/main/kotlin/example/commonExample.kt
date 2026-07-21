package example

import arrow.optics.optics
import kotlinx.serialization.Serializable

@optics @optics.copy @Serializable
data class Person(val name: String, val age: Int, val address: Address) {
  companion object
}

@optics @Serializable
data class Address(val street: String, val city: String) {
  companion object
}

@optics
internal data class Thing(val essence: String)

@optics
data class Generic<A>(val value: A)
