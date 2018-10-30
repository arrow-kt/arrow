package arrow.generic

import arrow.Kind
import arrow.core.*
import arrow.instances.`try`.applicative.applicative
import arrow.instances.option.applicative.applicative
import arrow.instances.option.monoid.monoid
import arrow.product
import arrow.test.UnitSpec
import arrow.test.laws.EqLaws
import arrow.test.laws.MonoidLaws
import arrow.test.laws.SemigroupLaws
import arrow.typeclasses.Applicative
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldBe
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import org.junit.runner.RunWith

@product
data class Person(val name: String, val age: Int, val related: Option<Person>) {
  companion object
}

fun personGen(): Gen<Person> = with(Gen) {
  create {
    Person(
      string().generate(),
      int().generate(),
      oneOf(listOf(None, Some(Person(string().generate(), int().generate(), None)))).generate()
    )
  }
}

fun tuple3Gen(): Gen<Tuple3<String, Int, Option<Person>>> = with(Gen) {
  create {
    Tuple3(string().generate(), int().generate(), personGen().generate().some())
  }
}

inline fun <reified F, A> Gen<A>.generateIn(applicative: Applicative<F>): Gen<Kind<F, A>> =
  Gen.create { applicative.just(this.generate()) }

inline fun <reified F> Applicative<F>.testPersonApplicative() {
  forAll(Gen.string(), Gen.int(), personGen()) { a, b, c ->
    mapToPerson(just(a), just(b), just(c.some())) == just(Person(a, b, c.some()))
  }
}

@RunWith(KTestJUnitRunner::class)
class ProductTest : UnitSpec() {
  init {

    ".tupled()" {
      forAll(personGen()) {
        it.tupled() == Tuple3(it.name, it.age, it.related)
      }
    }

    ".toPerson()" {
      forAll(tuple3Gen()) {
        it.toPerson() == Person(it.a, it.b, it.c)
      }
    }

    ".tupledLabeled()" {
      forAll(personGen()) {
        it.tupledLabeled() == Tuple3(
          "name" toT it.name,
          "age" toT it.age,
          "related" toT it.related
        )
      }
    }

    "List<@product>.combineAll()" {
      forAll(Gen.list(personGen())) {
        it.combineAll() == it.reduce { a, b -> a + b }
      }
    }



    "Applicative Syntax" {
      Option.applicative().testPersonApplicative()
      Try.applicative().testPersonApplicative()
    }

    "Show instance defaults to .toString()" {
      with(Person.show()) {
        forAll(personGen(), {
          it.show() == it.toString()
        })
      }
    }

    "Eq instance defaults to .equals()" {
      with(Person.eq()) {
        forAll(personGen(), personGen(), { a, b ->
          a.eqv(b) == (a == b)
        })
      }
    }

    "Semigroup combine" {
      forAll(personGen(), personGen()) { a, b ->
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
      forAll(personGen(), personGen()) { a, b ->
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
      emptyPerson() shouldBe  Person("", 0, None)
    }

    testLaws(
      EqLaws.laws(Person.eq()) { personGen().generate().copy(age = it) },
      SemigroupLaws.laws(
        Person.semigroup(),
        personGen().generate(),
        personGen().generate(),
        personGen().generate(),
        Person.eq()
      ),
      MonoidLaws.laws(
        Person.monoid(),
        personGen().generate(),
        Person.eq()
      )
    )
  }
}
