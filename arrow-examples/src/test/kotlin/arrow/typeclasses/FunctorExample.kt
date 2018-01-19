package arrow.typeclasses

import arrow.HK
import arrow.core.Option
import arrow.core.OptionHK
import arrow.core.Some
import arrow.core.functor
import arrow.data.*
import arrow.data.Try.Success
import arrow.internal.debug
import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.StringSpec


/**
 * See [http://arrow-kt.io/docs/typeclasses/functor/](http://arrow-kt.io/docs/typeclasses/functor/)
 *
 * The [Functor] typeclass abstracts the ability to map over the computational context of a type constructor.
 *
 * Examples of type constructors that can implement instances of the Functor typeclass include [Option], [NonEmptyList], [List] and many other datatypes that include a map function with the shape `fun F<B>.map(f: (A) -> B): F<B>` where `F` refers to [Option], [List] or any other type constructor whose contents can be transformed.
 *
 * **/


val square: (Int) -> Int = { i : Int -> i * i }

inline fun <reified F> multiplyBy2(fa: HK<F, Int>, FT: Functor<F> = functor()): HK<F, Int> =
    FT.map(fa, { it * 2 })

class FunctorExample: StringSpec(){ init {


    "map is available for both Try and Option" {
        val t: Try<Int> = Try { "2".toInt() }
        val o: Option<Int> = Option(2)

        t.map(square) shouldBe Success(4)
        o.map(square) shouldBe Some(4)

    }

    "Ad-hoc polymorphism" {
        // Arrow allows abstract polymorphic code that operates over the evidence of having an instance of a typeclass available.
        // This enables programs that are not coupled to specific datatype implementations.
        // The technique demonstrated below to write polymorphic code is available for all other Typeclasses beside Functor

        multiplyBy2(Option(1)) shouldBe Option(2)
        multiplyBy2(Success(1)) shouldBe Success(2)
        multiplyBy2(listOf(1, 2, 3).k()) shouldBe  listOf(2, 4, 6).k()
        multiplyBy2(NonEmptyList.of(1, 2, 3)) shouldBe NonEmptyList.of(2, 4, 6)

        // Here are the functors instance we were using implicitly thanks to the reified function
        multiplyBy2(Option(1), Option.functor()) shouldBe Option(2)
        multiplyBy2(Success(1), Try.functor()) shouldBe Success(2)

    }

}}