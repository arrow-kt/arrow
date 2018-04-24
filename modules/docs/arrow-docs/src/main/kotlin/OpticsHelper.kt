package arrow.optics

import arrow.core.Option
import arrow.optics.optics

@optics
data class Pos(val x: Int, val y: Int)

@optics
data class Account(val balance: Int, val available: Int)

@optics
data class Person(val age: Int?, val address: Option<Address>)

@optics
data class Street(val number: Int, val name: String)

@optics
data class Address(val city: String, val street: Street)

@optics
data class Company(val name: String, val address: Address)

@optics
data class Employee(val name: String, val company: Company)

@optics
sealed class Shape {
  data class Circle(val radius: Double) : Shape()
  data class Rectangle(val width: Double, val height: Double) : Shape()
}