package kategory.laws

import io.kotlintest.properties.forAll
import kategory.*

object TraverseFilterLaws {

    inline fun <reified F> laws(TF: TraverseFilter<F> = traverseFilter<F>(), GA: Applicative<F> = applicative<F>(), crossinline cf: (Int) -> HK<F, Int>, EQ: Eq<HK<F, Int>>): List<Law> =
            TraverseLaws.laws(TF, GA, cf, EQ) + FunctorLaws.laws(GA, cf, EQ) + listOf(
                    Law("TraverseFilter Laws: Identity", { identityTraverseFilter(TF, GA, cf, EQ) })
            )

    //def traverseIdentity[A, B](fa: F[A], f: A => B): IsEq[F[B]] = {
    //    fa.traverse[Id, B](f) <-> F.map(fa)(f)
    //}


    // external laws:
    //def traverseFilterIdentity[G[_] : Applicative, A](fa: F[A]): IsEq[G[F[A]]] = {
    //    fa.traverseFilter(_.some.pure[G]) <-> fa.pure[G]
    //}

    inline fun <reified F> identityTraverseFilter(FT: TraverseFilter<F>, GA: Applicative<F> = applicative<F>(), crossinline cf: (Int) -> HK<F, Int>, EQ: Eq<HK<F, Int>> = Eq.any()) =
            forAll(
                    genFunctionAToB<Int, HK<IdHK, Option<Int>>>(genConstructor(genOption(genIntSmall()), ::Id)),
                    genConstructor(genIntSmall(), cf),
                    { f: (Int) -> HK<IdHK, Option<Int>>, fa: HK<F, Int> ->
                        FT.traverseFilter(fa, f, Id.applicative()).value().equalUnderTheLaw(fa.pure(GA), EQ)
                    })
}