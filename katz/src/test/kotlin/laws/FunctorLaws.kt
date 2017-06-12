package katz

import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

abstract class FunctorLaws<F, A, B, C>(
        val functor : Functor<F>,
        val generator: Gen<HK<F, A>>,
        val genB: Gen<B>,
        val genC: Gen<C>) : UnitSpec() {

    init {
        "Functor: covariant identity" {
            forAll(generator, { fa: HK<F, A> ->
                functor.map(fa, ::identity) == fa
            })
        }

        "Functor: covariant composition" {
            forAll(generator, genFunctionAToB<A, B>(genB), genFunctionAToB<B, C>(genC), { fa: HK<F, A>, f, g ->
                functor.map(functor.map(fa, f), g) == functor.map(fa, f andThen g)
            })
        }
    }
}

