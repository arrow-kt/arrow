package arrow.typeclasses

import arrow.HK
import arrow.core.*
import arrow.data.*
import arrow.syntax.applicative.tupled
import arrow.syntax.option.some
import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.StringSpec


/** [http://arrow-kt.io/docs/typeclasses/applicative/](http://arrow-kt.io/docs/typeclasses/applicative/)
 *
 * The [Applicative] typeclass abstracts the ability to lift values and apply functions over the computational context of a type constructor.
 *
 * Examples of type constructors that can implement instances of the Applicative typeclass
 * include [Option], [NonEmptyList], [List] and many other datatypes that include a `pure` and either `ap` function.
 *
 * `ap` may be derived for monadic types that include a Monad instance via flatMap.
 * Applicative includes all combinators present in Functor.
 * ***/


class ApplicativeExample : StringSpec() { init {


    "pure() lifts a value into the computational context of a type constructor" {
        Option.pure(1) shouldBe Option(1)
        Try.pure(1) shouldBe Try.Success(1)
        ListKW.pure(1) shouldBe listOf(1).k()
        NonEmptyList.pure(1) shouldBe 1.nel()
    }

    "ap() apply a function inside of the type constructor's context" {
        fun multiplyBy2(i: Int): Int = i * 2

        Option(1).ap(Option(::multiplyBy2)) shouldBe Some(2)
        None.ap(Option(::multiplyBy2)) shouldBe None
    }

    "Map 2 values inside the type constructor context and apply a function to their cartesian product" {
        Option.applicative().map2(
            Option(1),
            Option("x"),
            { z: Tuple2<Int, String> -> "${z.a}-${z.b}" }
        ) shouldBe Some("1-x")

    }

    "map2Eval does the computation lazily when .value() is invoked" {
        val lazy: Eval<HK<OptionHK, String>> = Option.applicative().map2Eval(
            Option(1),
            Eval.later { Option("x") },
            { z: Tuple2<Int, String> -> "${z.a}-${z.b}" }
        )
        lazy.value() shouldBe Some("1-x")
    }


    "Applicative builder" {
        fun profileService(): Option<String> = Option("Alfredo Lambda")
        fun phoneService(): Option<Int> = Option(55555555)
        fun addressService(): Option<List<String>> = Option(listOf("1 Main Street", "11130", "NYC"))

        val r: Option<Tuple3<String, Int, List<String>>> = Option.applicative().tupled(
            profileService(), phoneService(), addressService()
        ).ev()

        val profileOrNone: Option<Profile> = r.map { (name: String, phone: Int, address: List<String>) ->
            Profile(name, phone, address)
        }
        profileOrNone shouldBe Profile("Alfredo Lambda", 55555555, listOf("1 Main Street", "11130", "NYC")).some()

    }
}
}


private data class Profile(val name: String, val phone: Int, val address: List<String>)
