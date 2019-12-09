package arrow.generic

import arrow.core.None
import arrow.core.Option
import arrow.core.Try
import arrow.core.Tuple3
import arrow.core.extensions.`try`.applicative.applicative
import arrow.core.extensions.option.applicative.applicative
import arrow.core.extensions.option.monoid.monoid
import arrow.core.some
import arrow.core.toT
import arrow.product
import arrow.test.UnitSpec
import arrow.test.generators.nonEmptyList
import arrow.test.generators.option
import arrow.test.generators.tuple3
import arrow.test.laws.EqLaws
import arrow.test.laws.MonoidLaws
import arrow.typeclasses.Applicative
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import io.kotlintest.shouldBe

@product
data class Person(val name: String, val age: Int, val related: Option<Person>) {
  companion object
}

fun genPerson(): Gen<Person> {
  val genRelated =
    Gen.bind(Gen.string(), Gen.int()) { name: String, age: Int -> Person(name, age, None) }
  return Gen.bind(
    Gen.string(),
    Gen.int(),
    Gen.option(genRelated)
  ) { name: String, age: Int, related: Option<Person> -> Person(name, age, related) }
}

fun tuple3PersonGen(): Gen<Tuple3<String, Int, Option<Person>>> =
  Gen.tuple3(Gen.string(), Gen.int(), Gen.option(genPerson()))

inline fun <reified F> Applicative<F>.testPersonApplicative() {
  forAll(Gen.string(), Gen.int(), genPerson()) { a, b, c ->
    mapToPerson(just(a), just(b), just(c.some())) == just(Person(a, b, c.some()))
  }
}

class ProductTest : UnitSpec() {

  init {

    ".tupled()" {
      forAll(genPerson()) {
        it.tupled() == Tuple3(it.name, it.age, it.related)
      }
    }

    ".toPerson()" {
      forAll(tuple3PersonGen()) {
        it.toPerson() == Person(it.a, it.b, it.c)
      }
    }

    ".tupledLabeled()" {
      forAll(genPerson()) {
        it.tupledLabeled() == Tuple3(
          "name" toT it.name,
          "age" toT it.age,
          "related" toT it.related
        )
      }
    }

    "List<@product>.combineAll()" {
      forAll(Gen.nonEmptyList(genPerson()).map { it.all }) {
        it.combineAll() == it.reduce { a, b -> a + b }
      }
    }

    "Applicative Syntax" {
      Option.applicative().testPersonApplicative()
      Try.applicative().testPersonApplicative()
    }

    "Show instance defaults to .toString()" {
      with(Person.show()) {
        forAll(genPerson()) {
          it.show() == it.toString()
        }
      }
    }

    "Eq instance defaults to .equals()" {
      with(Person.eq()) {
        forAll(genPerson(), genPerson()) { a, b ->
          a.eqv(b) == (a == b)
        }
      }
    }

    "Semigroup combine" {
      forAll(genPerson(), genPerson()) { a, b ->
        with(Person.semigroup()) {
          a.combine(b) == Person(
            a.name + b.name,
            a.age + b.age,
            Option.monoid(this).combineAll(listOf(a.related, b.related))
          )
        }
      }
    }

    "Semigroup + syntax" {
      forAll(genPerson(), genPerson()) { a, b ->
        a + b == Person(
          a.name + b.name,
          a.age + b.age,
          Option.monoid(Person.monoid()).combineAll(listOf(a.related, b.related))
        )
      }
    }

    "Monoid empty" {
      Person.monoid().empty() shouldBe Person("", 0, None)
    }

    "Monoid empty syntax" {
      emptyPerson() shouldBe Person("", 0, None)
    }

    val getPersonWithAge: (Int) -> Person = { age: Int -> Person("", age, None) }

    testLaws(
      EqLaws.laws(Person.eq(), getPersonWithAge),
      MonoidLaws.laws(Person.monoid(), genPerson(), Person.eq())
    )
  }
}
