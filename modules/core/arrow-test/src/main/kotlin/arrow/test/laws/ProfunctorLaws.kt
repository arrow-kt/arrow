package arrow.test.laws

import arrow.Kind2
import arrow.core.andThen
import arrow.test.generators.genDoubleConstructor
import arrow.test.generators.genFunctionAToB
import arrow.typeclasses.Eq
import arrow.typeclasses.Profunctor
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object ProfunctorLaws {
    fun <F> laws(PF: Profunctor<F>, f: (Int) -> Kind2<F, Int, Int>, EQ: Eq<Kind2<F, Int, Int>>): List<Law> =
        listOf(
            Law("Profunctor Laws: Identity") { PF.identity(f, EQ) },
            Law("Profunctor Laws: Composition") { PF.composition(f, EQ) },
            Law("Profunctor Laws: Lmap Identity") { PF.lMapIdentity(f, EQ) },
            Law("Profunctor Laws: Rmap Identity") { PF.rMapIdentity(f, EQ) },
            Law("Profunctor Laws: Lmap Composition") { PF.lMapComposition(f, EQ) },
            Law("Profunctor Laws: Rmap Composition") { PF.rMapComposition(f, EQ) }
        )

    fun <F> Profunctor<F>.identity(f: (Int) -> Kind2<F, Int, Int>, EQ: Eq<Kind2<F, Int, Int>>): Unit =
        forAll(genDoubleConstructor(Gen.int(), f)) { fa: Kind2<F, Int, Int> ->
            fa.dimap<Int, Int, Int, Int>({ it }, { it }).equalUnderTheLaw(fa, EQ)
        }

    fun <F> Profunctor<F>.composition(f: (Int) -> Kind2<F, Int, Int>, EQ: Eq<Kind2<F, Int, Int>>): Unit =
        forAll(
            genDoubleConstructor(Gen.int(), f),
            genFunctionAToB<Int, Int>(Gen.int()),
            genFunctionAToB<Int, Int>(Gen.int()),
            genFunctionAToB<Int, Int>(Gen.int()),
            genFunctionAToB<Int, Int>(Gen.int())
        ) { fa: Kind2<F, Int, Int>, f, g, x, y ->
            fa.dimap(f, g).dimap(x, y).equalUnderTheLaw(fa.dimap(x andThen f, g andThen y), EQ)
        }

    fun <F> Profunctor<F>.lMapIdentity(f: (Int) -> Kind2<F, Int, Int>, EQ: Eq<Kind2<F, Int, Int>>): Unit =
        forAll(genDoubleConstructor(Gen.int(), f)) { fa: Kind2<F, Int, Int> ->
            fa.lmap<Int, Int, Int> { it }.equalUnderTheLaw(fa, EQ)
        }

    fun <F> Profunctor<F>.rMapIdentity(f: (Int) -> Kind2<F, Int, Int>, EQ: Eq<Kind2<F, Int, Int>>): Unit =
        forAll(genDoubleConstructor(Gen.int(), f)) { fa: Kind2<F, Int, Int> ->
            fa.rmap { it }.equalUnderTheLaw(fa, EQ)
        }

    fun <F> Profunctor<F>.lMapComposition(f: (Int) -> Kind2<F, Int, Int>, EQ: Eq<Kind2<F, Int, Int>>): Unit =
        forAll(
            genDoubleConstructor(Gen.int(), f),
            genFunctionAToB<Int, Int>(Gen.int()),
            genFunctionAToB<Int, Int>(Gen.int())
        ) { fa: Kind2<F, Int, Int>, f, g ->
            fa.lmap(g).lmap(f).equalUnderTheLaw(fa.lmap(f andThen g), EQ)
        }

    fun <F> Profunctor<F>.rMapComposition(f: (Int) -> Kind2<F, Int, Int>, EQ: Eq<Kind2<F, Int, Int>>): Unit =
        forAll(
            genDoubleConstructor(Gen.int(), f),
            genFunctionAToB<Int, Int>(Gen.int()),
            genFunctionAToB<Int, Int>(Gen.int())
        ) { fa: Kind2<F, Int, Int>, f, g ->
            fa.lmap(f).lmap(g).equalUnderTheLaw(fa.lmap(f andThen g), EQ)
        }
}
