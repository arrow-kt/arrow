package com.example.domain

import arrow.data.*
import arrow.optics.*

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
data class Employees(val employees: ListK<Employee>) {
  companion object
}

@optics
data class Db(val content: MapK<Int, String>) {
  companion object
}

@optics
@Suppress("UtilityClassWithPublicConstructor")
sealed class NetworkResult {
  companion object
}

@optics
data class Success(val content: String) : NetworkResult() {
  companion object
}

@optics
sealed class NetworkError : NetworkResult() {
  companion object
}

@optics
data class HttpError(val message: String) : NetworkError() {
  companion object
}

object TimeoutError : NetworkError()
