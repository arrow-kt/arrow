package arrow.optics

import arrow.core.*

@optics
data class Pos(val x: Int, val y: Int) {
  companion object
}

@optics
data class Account(val balance: Int, val available: Int) {
  companion object
}

@optics
data class Person(val age: Int?, val address: Option<Address>) {
  companion object
}

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
data class Employee(val name: String, val company: Company) {
  companion object
}

@optics
sealed class Shape {
  companion object {}

  @optics
  data class Circle(val radius: Double) : Shape() {
    companion object
  }

  @optics
  data class Rectangle(val width: Double, val height: Double) : Shape() {
    companion object
  }

}

@optics data class HealthPack(val amountLeft: Int) {
  companion object
}
object OutOfPacks

@optics data class Inventory(val item: Either<OutOfPacks, HealthPack>) {
  companion object
}
