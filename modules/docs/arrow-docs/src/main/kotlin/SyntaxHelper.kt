package com.example.domain

import arrow.data.*
import arrow.optics.*
import arrow.optics.instances.*
import arrow.optics.typeclasses.*
import com.example.domain.syntax.*

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

sealed class Keys
object One : Keys()
object Two : Keys()
object Three : Keys()
object Four : Keys()

@optics
data class Db(val content: MapK<Keys, String>)

@optics
sealed class Json

@optics
data class JsBoolean(val value: Boolean) : Json()

@optics
data class JsString(val value: CharSequence) : Json()

@optics
sealed class JsNumber : Json()

@optics
data class JsInt(val int: Int) : Json()

@optics
data class JsDouble(val double: Double) : Json()

@optics
data class JsArray(val value: List<Json>) : Json()

@optics
data class JsObject(val value: Map<String, Json>)

fun jsArrayEach(): Each<JsArray, Json> = object : Each<JsArray, Json> {
  override fun each(): Traversal<JsArray, Json> = jsArrayIso() compose ListTraversal()
}

@optics
sealed class NetworkResult

@optics
data class Success(val content: String) : NetworkResult()

@optics
sealed class NetworkError : NetworkResult()

@optics
data class HttpError(val message: String) : NetworkError()

object TimeoutError : NetworkError()
