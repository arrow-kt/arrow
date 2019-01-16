package arrow.aql.tests

import arrow.aql.Ord
import arrow.aql.extensions.list.from.join
import arrow.aql.extensions.list.groupBy.groupBy
import arrow.aql.extensions.list.select.query
import arrow.aql.extensions.list.orderBy.orderBy
import arrow.aql.extensions.list.orderBy.orderMap
import arrow.aql.extensions.list.orderBy.value
import arrow.aql.extensions.list.sum.sum
import arrow.aql.extensions.list.sum.value
import arrow.aql.extensions.list.union.union
import arrow.aql.extensions.list.where.where
import arrow.aql.extensions.list.where.whereSelection
import arrow.aql.extensions.list.count.count
import arrow.aql.extensions.listk.select.select
import arrow.aql.extensions.listk.select.selectAll
import arrow.aql.extensions.listk.select.value
import arrow.aql.extensions.option.select.query
import arrow.aql.extensions.option.select.select
import arrow.aql.extensions.option.select.value
import arrow.core.Option
import arrow.core.Some
import arrow.core.extensions.order
import arrow.test.UnitSpec
import io.kotlintest.shouldBe
import io.kotlintest.runner.junit4.KotlinTestRunner
import org.junit.runner.RunWith

@RunWith(KotlinTestRunner::class)
class AQLTests : UnitSpec() {

  init {

    "AQL is able to `select`" {
      listOf(1, 2, 3).query {
        select { this * 10 }
      }.value() shouldBe listOf(10, 20, 30)
    }

    "AQL is able to `select` Option" {
      Option(1).query {
        select { this * 10 }
      }.value() shouldBe Some(10)
    }

    "AQL is able to `select count`" {
      listOf(1, 2, 3).query { select { this } }.count()
        .value() shouldBe listOf(3L)
    }

    "AQL is able to `select`, transform and filter data with `where`" {
      listOf(1, 2, 3).query {
        selectAll() where { this > 2 }
      }.value() shouldBe listOf(3)
    }

    "AQL is able to `select`, transform and filter data with `in`" {
      listOf(1, 2, 3).query {
        selectAll() where { this in listOf(3) }
      }.value() shouldBe listOf(3)
    }

    "AQL is able to `join` and transform data for List" {
      (listOf(1) join listOf("a")).query {
        select { "$a$b" } where { a > 0 } whereSelection { startsWith("1") }
      }.value() shouldBe listOf("1a")
    }

    "AQL is able to `groupBy`" {
      data class Student(val name: String, val age: Int)

      val john = Student("John", 30)
      val jane = Student("Jane", 32)
      val jack = Student("Jack", 32)
      listOf(john, jane, jack).query {
        selectAll() groupBy { age }
      }.value() shouldBe mapOf(30 to listOf(john), 32 to listOf(jane, jack))
    }

    data class Student(val name: String, val age: Int)

    val john = Student("John", 30)
    val jane = Student("Jane", 32)
    val jack = Student("Jack", 32)
    val chris = Student("Chris", 40)

    "AQL is able to filter using `where` and then `groupBy`" {
      listOf(john, jane, jack).query {
        selectAll() where { age > 30 } groupBy { age }
      }.value() shouldBe mapOf(32 to listOf(jane, jack))
    }

    "AQL is able to `sum`" {
      listOf(john, jane, jack).query {
        selectAll() where { age > 30 } sum { age.toLong() }
      }.value() shouldBe 64L
    }

    "AQL is able to `orderBy by Asc` simple selects" {
      listOf(1, 2, 3).query {
        select { this * 10 } orderBy Ord.Asc(Int.order())
      }.value() shouldBe listOf(10, 20, 30)
    }

    "AQL is able to `orderBy by Desc` simple selects" {
      listOf(1, 2, 3).query {
        select { this * 10 } orderBy Ord.Desc(Int.order())
      }.value() shouldBe listOf(30, 20, 10)
    }

    "AQL is able to `groupBy` and then orderBy `keys`"{
      listOf(john, jane, jack).query {
        selectAll() where { age > 30 } groupBy { age } orderMap Ord.Desc(Int.order())
      }.value() shouldBe mapOf(32 to listOf(jane, jack))
    }

    "AQL is able to `union`" {
      val queryA = listOf("customer" to john, "customer" to jane).query { selectAll() }
      val queryB = listOf("sales" to jack, "sales" to chris).query { selectAll() }
      queryA.union(queryB).value() shouldBe listOf(
        "customer" to john,
        "customer" to jane,
        "sales" to jack,
        "sales" to chris
      )
    }
  }
}

object BOOM : RuntimeException("BOOM!")
