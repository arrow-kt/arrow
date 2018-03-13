package arrow.generic

import arrow.Kind
import arrow.core.*
import arrow.product
import arrow.test.UnitSpec
import arrow.typeclasses.*
import arrow.typeclasses.Eq
import arrow.typeclasses.Order
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldBe
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import org.junit.runner.RunWith

@product
data class Person(val name: String, val age: Int)

fun personGen(): Gen<Person> = with(Gen) {
    create {
        Person(string().generate(), int().generate())
    }
}

fun tuple2Gen(): Gen<Tuple2<String, Int>> = with(Gen) {
    create {
        Tuple2(string().generate(), int().generate())
    }
}

inline fun <reified F, A> Gen<A>.generateIn(applicative: Applicative<F> = applicative<F>()): Gen<Kind<F, A>> =
        Gen.create { applicative.pure(this.generate()) }

inline fun <reified F> personSyntax(applicative: Applicative<F> = applicative<F>()): PersonApplicativeSyntax<F> = object : PersonApplicativeSyntax<F> {
    override fun applicative(): Applicative<F> = applicative
}

@RunWith(KTestJUnitRunner::class)
class ProductTest : UnitSpec() {
    init {

        ".tupled()" {
            forAll(personGen(), {
                it.tupled() == it.name toT it.age
            })
        }

        ".toPerson()" {
            forAll(tuple2Gen(), {
                it.toPerson() == Person(it.a, it.b)
            })
        }

        ".tupledLabelled()" {
            forAll(personGen(), {
                it.tupledLabelled() == ("name" toT it.name) toT ("age" toT it.age)
            })
        }

        "Applicative Syntax" {
            forAll(Gen.string(), Gen.int(), { a, b ->
                with(personSyntax(Option.applicative())) {
                    mapToPerson(Option(a), Option(b)) == Some(Person(a, b))
                }
            })
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
                semigroup<Person>().combine(a, b) == Person(a.name + b.name, a.age + b.age)
            })
        }

        "Semigroup + syntax" {
            forAll(personGen(), personGen(), { a, b ->
                a + b == Person(a.name + b.name, a.age + b.age)
            })
        }

        "Monoid empty" {
            forAll(personGen(), personGen(), { a, b ->
                monoid<Person>().empty() == Person("", 0)
            })
        }

        "Monoid empty syntax" {
            forAll(personGen(), personGen(), { a, b ->
                emptyPerson() == Person("", 0)
            })
        }

        "Order" {
            forAll(personGen(), personGen(), { a, b ->
                order<Tuple2<String, Int>>().compare(a.tupled(), b.tupled()) == order<Person>().compare(a, b)
            })
        }

        //TODO look at typeclassless encoding to see if it's better than runtime lookups in these cases

//        testLaws(
//                TraverseFilterLaws.laws(Const.traverseFilter(), Const.applicative(IntMonoid), { Const(it) }, Eq.any()),
//                ApplicativeLaws.laws(Const.applicative(IntMonoid), Eq.any()),
//                EqLaws.laws(eq<Const<Int, String>>(), { Const(it) }),
//                ShowLaws.laws(show<Const<Int, String>>(), eq<Const<Int, String>>(), { Const(it) })
//        )
    }
}