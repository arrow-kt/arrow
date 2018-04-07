package arrow.optics

import arrow.core.Option

@optics
data class Point2D(val x: Int, val y: Int)

@optics
data class Street(val number: Int, val name: String)

@optics
data class Address(val city: String, val street: Street)

@optics
data class Company(val name: String, val address: Address)

@optics
data class Employee(val name: String, val company: Company)

@optics
data class Person(val age: Int?, val address: Option<Address>)

@optics sealed class NetworkResult {
  data class Success(val content: String) : NetworkResult()
  object Failure : NetworkResult()
}