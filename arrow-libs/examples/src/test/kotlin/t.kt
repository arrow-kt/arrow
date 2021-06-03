

import arrow.core.computations.either
import arrow.core.Either
import arrow.core.Either.Left
import arrow.core.Either.Right

/* A simple model of student and a university */
object NotFound
data class Name(val value: String)
data class UniversityId(val value: String)
data class University(val name: Name, val deanName: Name)
data class Student(val name: Name, val universityId: UniversityId)
data class Dean(val name: Name)

/* in memory db of students */
private val students = mapOf(
  Name("Alice") to Student(Name("Alice"), UniversityId("UCA"))
)

/* in memory db of universities */
private val universities = mapOf(
  UniversityId("UCA") to University(Name("UCA"), Name("James"))
)

/* in memory db of deans */
private val deans = mapOf(
  UniversityId("UCA") to Dean(Name("James"))
)

/* gets a student by name */
suspend fun student(name: Name): Either<NotFound, Student> =
  students[name]?.let(::Right) ?: Left(NotFound)

/* gets a university by id */
suspend fun university(id: UniversityId): Either<NotFound, University> =
  universities[id]?.let(::Right) ?: Left(NotFound)

/* gets a university by id */
suspend fun dean(id: UniversityId): Either<NotFound, Dean> =
  deans[id]?.let(::Right) ?: Left(NotFound)

suspend fun main(): Unit {
  //sampleStart
  val dean = either<NotFound, Dean> {
    val alice = student(Name("Alice")).bind()
    val uca = university(alice.universityId).bind()
    val james = dean(alice.universityId).bind()
    james
  }
  //sampleEnd
  println(dean)
}
