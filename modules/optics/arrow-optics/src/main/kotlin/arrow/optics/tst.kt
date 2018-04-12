package arrow.optics

import arrow.core.*
import arrow.data.*
import arrow.optics.syntax.*
import arrow.optics.typeclasses.*

/* Domain */
@optics
data class Employee(val name: String)

@optics
data class CompanyEmployees(val employees: ListK<Employee>)

@optics
data class CompanyJavaEmployees(val employees: List<Employee>)

val john = Employee("John Doe")
val jane = Employee("Jane Doe")

val employees = CompanyEmployees(listOf(john, jane).k())

sealed class Keys
object One : Keys()
object Two : Keys()
object Three : Keys()
object Four : Keys()

@optics
data class Db(val content: MapK<Keys, String>)

val db = Db(mapOf(
  One to "one",
  Two to "two",
  Three to "three",
  Four to "four"
).k())

val complex: ComplexDomain = ComplexDomain.SomeEmployee(john)

@optics
sealed class ComplexDomain {
  @optics
  data class SomeEmployee(val employee: Employee) : ComplexDomain()

  object NoEmployee : ComplexDomain()
}

/* New DSL functionality */
fun <F, T, A> BoundSetter<T, F>.every(EA: Each<F, A>): BoundSetter<T, A> =
  compose(EA.each())

fun <F, T, I, A> BoundSetter<T, F>.at(AT: At<F, I, A>, i: I): BoundSetter<T, A> =
  compose(AT.at(i))

/* Usage example */
fun main(args: Array<String>) {

  employees.setter().employees.every(ListK.each()).name.modify(String::toUpperCase).let(::println)
  /* Syntax could be enabled if we pollute the Each typeclass with DSL specific methods or create an intermediate class... (Right?) */
//    with(ListK.each<Employee>()) {
//        employees.setter().employees.every().name.modify { it.toUpperCase() }
//    }.let(::println)

  db.setter().content.every(MapK.each()).modify(String::reversed).let(::println)
//    with(MapK.each<Keys, String>()) {
//        db.setter().content.every().modify(String::reversed)
//    }.let(::println)

  db.setter().content.at(MapK.at(), One).compose(somePrism()).modify(String::reversed).let(::println)
  db.setter().content.at(MapK.at(), One).some.modify(String::reversed).let(::println)

  //Prism support
  complex.setter().someEmployee.employee.name.modify(String::toUpperCase).let(::println)

}

//Should go in Optics STD
inline val <T, A> BoundSetter<T, Option<A>>.some: BoundSetter<T, A>
  get() = this.compose(arrow.optics.somePrism())

//Everything below will be generated
fun ComplexDomain.setter() = arrow.optics.syntax.BoundSetter(this, arrow.optics.PSetter.id())

inline val <T> BoundSetter<T, ComplexDomain>.someEmployee: BoundSetter<T, ComplexDomain.SomeEmployee>
  get() = compose(complexDomainSomeEmployee())

inline val <T> BoundSetter<T, ComplexDomain.SomeEmployee>.employee: BoundSetter<T, Employee>
  get() = compose(someEmployeeEmployee())