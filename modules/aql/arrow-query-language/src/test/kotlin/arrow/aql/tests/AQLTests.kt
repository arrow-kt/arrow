package arrow.aql.tests

import arrow.aql.Ord
import arrow.aql.instances.id.select.value
import arrow.aql.instances.list.count.count
import arrow.aql.instances.list.count.value
import arrow.aql.instances.list.from.join
import arrow.aql.instances.list.groupBy.groupBy
import arrow.aql.instances.list.orderBy.order
import arrow.aql.instances.list.orderBy.orderMap
import arrow.aql.instances.list.select.query
import arrow.aql.instances.list.select.select
import arrow.aql.instances.list.select.value
import arrow.aql.instances.list.sum.sum
import arrow.aql.instances.list.union.union
import arrow.aql.instances.list.where.where
import arrow.aql.instances.list.where.whereSelection
import arrow.aql.instances.listk.select.select
import arrow.aql.instances.listk.select.selectAll
import arrow.core.Id
import arrow.instances.order
import arrow.test.UnitSpec
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldBe
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class AQLTests : UnitSpec() {

  init {

    "AQL is able to `select`" {
      listOf(1, 2, 3).query {
        select { this * 10 }
      }.value() shouldBe listOf(10, 20, 30)
    }

    "AQL is able to `select count`" {
      listOf(1, 2, 3).query { select { this } }.count()
        .value() shouldBe 3L
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
      }.value() shouldBe Id(mapOf(30 to listOf(john), 32 to listOf(jane, jack)))
    }

    data class Student(val name: String, val age: Int)

    val john = Student("John", 30)
    val jane = Student("Jane", 32)
    val jack = Student("Jack", 32)
    val chris = Student("Chris", 40)

    "AQL is able to `groupBy`" {
      listOf(john, jane, jack).query {
        selectAll() where { age > 30 } groupBy { age }
      }.value() shouldBe mapOf(32 to listOf(jane, jack))
    }

    "AQL is able to `sum`" {
      listOf(john, jane, jack).query {
        selectAll() where { age > 30 } sum { age.toLong() }
      }.value() shouldBe 64L
    }

    "AQL is able to `order by Asc` simple selects" {
      listOf(1, 2, 3).query {
        select { this * 10 } order Ord.Asc(Int.order())
      }.value() shouldBe listOf(10, 20, 30)
    }

    "AQL is able to `order by Desc` simple selects" {
      listOf(1, 2, 3).query {
        select { this * 10 } order Ord.Desc(Int.order())
      }.value() shouldBe listOf(30, 20, 10)
    }

    "AQL is able to `order by Desc` simple selects with explicit instance" {
      listOf(1, 2, 3).query {
        select { this * 10 } order Ord.Desc(Int.order())
      }.value() shouldBe listOf(30, 20, 10)
    }


    "AQL is able to `groupBy` and then order `keys`"{
      listOf(john, jane, jack).query {
        selectAll() where { age > 30 } groupBy { age } orderMap Ord.Desc(Int.order())
      }.value() shouldBe mapOf(32 to listOf(jane, jack))
    }

    "AQL is able to `union`" {
      val queryA = listOf(
        "customer" to john,
        "customer" to jane
      ).select { this }
      val queryB = listOf(
        "sales" to jack,
        "sales" to chris
      ).select { this }
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