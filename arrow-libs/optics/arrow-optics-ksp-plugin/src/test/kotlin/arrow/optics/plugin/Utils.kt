package arrow.optics.plugin

const val `package` = "package `if`.`this`.`object`.`is`.`finally`.`null`.`expect`.`annotation`"

const val imports =
  """
      import arrow.core.None
      import arrow.optics.*
      import arrow.optics.dsl.*
      import arrow.optics.typeclasses.*
      import kotlin.time.Duration.Companion.hours
      """

const val dslModel =
  """
    @optics data class Street(val number: Int, val name: String) {
      companion object
    }
    @optics data class Address(val city: String, val street: Street) {
      companion object
    }
    @optics data class Company(val name: String, val address: Address) {
      companion object
    }
    @optics data class Employee(val name: String, val company: Company?, val weeklyWorkingHours: kotlin.time.Duration = 5.hours) {
      companion object
    }
    @optics data class Employees(val employees: List<Employee>) {
      companion object
    }
    sealed class Keys
    object One : Keys() {
      override fun toString(): String = "One"
    }
    object Two : Keys() {
      override fun toString(): String = "Two"
    }
    object Three : Keys() {
      override fun toString(): String = "Three"
    }
    object Four : Keys() {
      override fun toString(): String = "Four"
    }
    @optics data class Db(val content: Map<Keys, String>) {
      companion object
    }
    """

const val dslValues =
  """      
      |val john = Employee("Audrey Tang",
      |       Company("Arrow",
      |               Address("Functional city",
      |                       Street(42, "lambda street"))))
      |val jane = Employee("Bestian Tang",
      |       Company("Arrow",
      |               Address("Functional city",
      |                       Street(42, "lambda street"))))
      |val employees = Employees(listOf(john, jane))
      |val db = Db(
      |  mapOf(
      |    One to "one",
      |    Two to "two",
      |    Three to "three",
      |    Four to "four"
      |  )
      |)"""
