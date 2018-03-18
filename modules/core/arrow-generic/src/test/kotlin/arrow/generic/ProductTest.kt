package arrow.generic

import arrow.Kind
import arrow.core.*
import arrow.effects.IO
import arrow.effects.applicative
import arrow.product
import arrow.syntax.applicative.pure
import arrow.syntax.monoid.combineAll
import arrow.syntax.option.some
import arrow.test.UnitSpec
import arrow.test.laws.EqLaws
import arrow.test.laws.MonoidLaws
import arrow.test.laws.SemigroupLaws
import arrow.test.laws.ShowLaws
import arrow.typeclasses.*
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import org.junit.runner.RunWith

@product
data class Person(val name: String, val age: Int, val related: Option<Person>)

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

inline fun <reified F, A> Gen<A>.generateIn(applicative: Applicative<F> = applicative<F>()): Gen<Kind<F, A>> =
        Gen.create { applicative.pure(this.generate()) }

inline fun <reified F> Applicative<F>.testPersonApplicative() {
    forAll(Gen.string(), Gen.int(), personGen(), { a, b, c ->
        mapToPerson(pure(a), pure(b), pure(c.some())) == pure(Person(a, b, c.some()))
    })
}

@RunWith(KTestJUnitRunner::class)
class ProductTest : UnitSpec() {
    init {

        ".tupled()" {
            forAll(personGen(), {
                it.tupled() == Tuple3(it.name, it.age, it.related)
            })
        }

        ".toPerson()" {
            forAll(tuple3Gen(), {
                it.toPerson() == Person(it.a, it.b, it.c)
            })
        }

        ".tupledLabelled()" {
            forAll(personGen(), {
                it.tupledLabelled() == Tuple3(
                        "name" toT it.name,
                        "age" toT it.age,
                        "related" toT it.related
                )
            })
        }

        "List<@product>.combineAll()" {
            forAll(Gen.list(personGen()), {
                it.combineAll() == it.reduce { a, b -> a + b }
            })
        }



        "Applicative Syntax" {
            Option.applicative().testPersonApplicative()
            Try.applicative().testPersonApplicative()
        }

        "Show instance defaults to .toString()" {
            val showInstance: arrow.typeclasses.Show<Person> = show()
            val syntax = object : ShowSyntax<Person> {
                override fun show(): arrow.typeclasses.Show<Person> = showInstance
            }
            forAll(personGen(), {
                with(syntax) {
                    it.show() == it.toString()
                }
            })
        }

        "Eq instance defaults to .equals()" {
            val instance: arrow.typeclasses.Eq<Person> = eq()
            forAll(personGen(), personGen(), { a, b ->
                instance.eqv(a, b) == (a == b)
            })
        }

        "Semigroup combine" {
            forAll(personGen(), personGen(), { a, b ->
                semigroup<Person>().combine(a, b) == Person(
                        a.name + b.name,
                        a.age + b.age,
                        a.related.flatMap { ap -> b.related.map { bp -> ap + bp } }
                )
            })
        }

        "Semigroup + syntax" {
            forAll(personGen(), personGen(), { a, b ->
                a + b == Person(
                        a.name + b.name,
                        a.age + b.age,
                        a.related.flatMap { ap -> b.related.map { bp -> ap + bp } }
                )
            })
        }

        "Monoid empty" {
            forAll(personGen(), personGen(), { a, b ->
                monoid<Person>().empty() == Person("", 0, None)
            })
        }

        "Monoid empty syntax" {
            forAll(personGen(), personGen(), { a, b ->
                emptyPerson() == Person("", 0, None)
            })
        }

        //TODO look at typeclassless encoding to see if it's better than runtime lookups in these cases
        with(Gen) {
            testLaws(
                    EqLaws.laws(eq(), { personGen().generate().copy(age = it) }),
                    SemigroupLaws.laws(
                            semigroup(),
                            personGen().generate(),
                            personGen().generate(),
                            personGen().generate(),
                            eq()
                    ),
                    MonoidLaws.laws(
                            monoid(),
                            personGen().generate(),
                            eq()
                    )
            )
        }
    }
}