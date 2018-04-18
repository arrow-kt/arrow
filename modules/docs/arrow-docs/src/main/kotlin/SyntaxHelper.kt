package com.example.domain

import arrow.data.*
import arrow.optics.*

@optics
data class Street(val number: Int, val name: String)

@optics
data class Address(val city: String, val street: Street)

@optics
data class Company(val name: String, val address: Address)

@optics
data class Employee(val name: String, val company: Company?)

@optics
data class Employees(val employees: ListK<Employee>)

@optics
data class Db(val content: MapK<Int, String>)

@optics
sealed class NetworkResult

@optics
data class Success(val content: String) : NetworkResult()

@optics
sealed class NetworkError : NetworkResult()

@optics
data class HttpError(val message: String) : NetworkError()

object TimeoutError : NetworkError()
