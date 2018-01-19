package arrow.data

import arrow.HK
import arrow.core.*
import arrow.mtl.traverseFilter
import arrow.syntax.`try`.optionTry
import arrow.syntax.collections.firstOption
import arrow.syntax.collections.option
import arrow.syntax.collections.optionSequential
import arrow.syntax.option.some
import arrow.test.UnitSpec
import arrow.test.generators.genOption
import arrow.test.laws.EqLaws
import arrow.test.laws.MonadFilterLaws
import arrow.test.laws.ShowLaws
import arrow.test.laws.TraverseFilterLaws
import arrow.typeclasses.*
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.fail
import io.kotlintest.matchers.shouldBe
import io.kotlintest.matchers.shouldNotBe
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class OptionTest : UnitSpec() {

    val some: Option<String> = Some("kotlin")
    val none: Option<String> = Option.empty()

    init {

        "instances can be resolved implicitly" {
            functor<OptionHK>() shouldNotBe null
            applicative<OptionHK>() shouldNotBe null
            monad<OptionHK>() shouldNotBe null
            foldable<OptionHK>() shouldNotBe null
            traverse<OptionHK>() shouldNotBe null
            traverseFilter<OptionHK>() shouldNotBe null
            semigroup<Option<Int>>() shouldNotBe null
            monoid<Option<Int>>() shouldNotBe null
            monadError<OptionHK, Unit>() shouldNotBe null
            eq<Option<Int>>() shouldNotBe null
            show<Option<Int>>() shouldNotBe null
        }

        val EQ_EITHER: Eq<HK<OptionHK, Either<Unit, Int>>> = Eq { a, b ->
            a.ev().fold(
                    { b.ev().fold({ true }, { false }) },
                    { eitherA: Either<Unit, Int> ->
                        b.ev().fold(
                                { false },
                                { eitherB: Either<Unit, Int> ->
                                    eitherA.fold(
                                            { eitherB.fold({ true /* Ignore the error kind */ }, { false }) },
                                            { ia -> eitherB.fold({ false }, { ia == it }) })
                                })
                    })
        }

        testLaws(
                EqLaws.laws(eq(), { genOption(Gen.int()).generate() }),
                ShowLaws.laws(show(), eq(), { Some(it) }),
                //testLaws(MonadErrorLaws.laws(monadError<OptionHK, Unit>(), Eq.any(), EQ_EITHER)) TODO reenable once the MonadErrorLaws are parametric to `E`
                TraverseFilterLaws.laws(Option.traverseFilter(), Option.monad(), ::Some, Eq.any()),
                MonadFilterLaws.laws(Option.monadFilter(), ::Some, Eq.any())
        )

        "fromNullable should work for both null and non-null values of nullable types" {
            forAll { a: Int? ->
                // This seems to be generating only non-null values, so it is complemented by the next test
                val o: Option<Int> = Option.fromNullable(a)
                if (a == null) o == None else o == Some(a)
            }
        }

        "fromNullable should return none for null values of nullable types" {
            val a: Int? = null
            Option.fromNullable(a) shouldBe None
        }

        "option" {

            val option = some
            when (option) {
                is Some<String> -> {
                    option.get() shouldBe "kotlin"
                }
                is None -> fail("")
            }

            val otherOption = none

            when (otherOption) {
                is Some<String> -> fail("")
                is None -> otherOption shouldBe None
            }

        }

        "getOrElse" {
            some.getOrElse { "java" } shouldBe "kotlin"
            none.getOrElse { "java" } shouldBe "java"
        }

        "orNull" {
            some.orNull() shouldNotBe null
            none.orNull() shouldBe null
        }

        "map" {
            some.map(String::toUpperCase).get() shouldBe "KOTLIN"
            none.map(String::toUpperCase) shouldBe None

            some.map(Some(12)) { name, version -> "${name.toUpperCase()} M$version" }.get() shouldBe "KOTLIN M12"
            none.map(Some(12)) { name, version -> "${name.toUpperCase()} M$version" } shouldBe None
        }

        "fold" {
            some.fold({ 0 }) { it.length } shouldBe 6
            none.fold({ 0 }) { it.length } shouldBe 0
        }

        "flatMap" {
            some.flatMap { Some(it.toUpperCase()) }.get() shouldBe "KOTLIN"
            none.flatMap { Some(it.toUpperCase()) } shouldBe None
        }

        "filter" {
            some.filter { it == "java" } shouldBe None
            none.filter { it == "java" } shouldBe None
            some.filter { it.startsWith('k') }.get() shouldBe "kotlin"
        }

        "filterNot" {
            some.filterNot { it == "java" }.get() shouldBe "kotlin"
            none.filterNot { it == "java" } shouldBe None
            some.filterNot { it.startsWith('k') } shouldBe None
        }

        "exists" {
            some.exists { it.startsWith('k') } shouldBe true
            none.exists { it.startsWith('k') } shouldBe false

        }

        "forEach" {
            some.forEach {
                it shouldBe "kotlin"
            }

            none.forEach {
                fail("")
            }

        }

        "orElse" {
            some.orElse { Some("java") }.get() shouldBe "kotlin"
            none.orElse { Some("java") }.get() shouldBe "java"
        }

        "toList" {
            some.toList() shouldBe listOf("kotlin")
            none.toList() shouldBe listOf<String>()
        }

        "getAsOption" {
            val map = mapOf(1 to "uno", 2 to "dos", 4 to null)
            map.option[1] shouldBe Some("uno")
            map.option[3] shouldBe None
            map.option[4] shouldBe None
        }

        "firstOption" {
            val l = listOf(1, 2, 3, 4, 5, 6)
            l.firstOption() shouldBe Some(1)
            l.firstOption { it > 2 } shouldBe Some(3)
        }

        "optionBody" {
            optionTry { "1".toInt() } shouldBe Some(1)
            optionTry { "foo".toInt() } shouldBe None
        }

        "sequential" {
            fun parseInts(ints: List<String>): Option<List<Int>> {
                return ints.map { optionTry { it.toInt() } }.optionSequential()
            }

            parseInts(listOf("1", "2", "3")) shouldBe Some(listOf(1, 2, 3))
            parseInts(listOf("1", "foo", "3")) shouldBe None
        }

        "and" {
            val x = Some(2)
            val y = Some("Foo")
            x and y shouldBe Some("Foo")
            x and None shouldBe None
            None and x shouldBe None
            None and None shouldBe None

        }

        "or" {
            val x = Some(2)
            val y = Some(100)
            x or y shouldBe Some(2)
            x or None shouldBe Some(2)
            None or x shouldBe Some(2)
            None or None shouldBe None

        }
    }

}
