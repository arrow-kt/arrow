package com.example.domain

import arrow.core.Tuple2
import arrow.core.toT
import arrow.data.*
import arrow.optics.*
import arrow.optics.dsl.every

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

@optics
data class GameBoard(val player: Player) {
  companion object
}

@optics
data class Player(val name: String, val pos: Pair<Long, Long>) {
  companion object
}

@optics
fun <A, B> first(): Lens<Pair<A, B>, A> = Iso(
  get = { a: Pair<A, B> -> a.first toT a.second },
  reverseGet = { a: Tuple2<A, B> -> a.a to a.b }
) compose Tuple2.first()

@optics
fun xPos(): Lens<Player, Long> = Player.pos.first

@optics
fun everyEmployee(): Traversal<Employees, Employee> = Employees.employees.every(ListK.each())