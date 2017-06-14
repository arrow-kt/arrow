package katz

import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

//abstract class ApplicativeLaws<F, A, B, C>(val applicative : Applicative<F>,
//                                           val applicativeGen: Gen<HK<F, A>>,
//                                           val applicativeGenB: Gen<B>,
//                                           val applicativeGenC: Gen<C>) :
//        FunctorLaws<F, A, B, C>(applicative, applicativeGen, applicativeGenB, applicativeGenC) {
//
//    init {
//        "Applicative: ap composition" {
//            forAll(functorGen, { fa: HK<F, A> ->
//                functor.map(fa, ::identity) == fa
//            })
//        }
//
//    }
//}