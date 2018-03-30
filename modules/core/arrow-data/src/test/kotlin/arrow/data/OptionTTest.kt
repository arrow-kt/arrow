package arrow.data

import arrow.Kind
import arrow.core.*
import arrow.test.UnitSpec
import arrow.test.laws.*
import arrow.typeclasses.Eq
import arrow.typeclasses.Monad
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.properties.forAll
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class OptionTTest : UnitSpec() {

    fun <A> EQ(): Eq<Kind<OptionTPartialOf<A>, Int>> = Eq { a, b ->
        a.value() == b.value()
    }

    fun <A> EQ_NESTED(): Eq<Kind<OptionTPartialOf<A>, Kind<OptionTPartialOf<A>, Int>>> = Eq { a, b ->
        a.value() == b.value()
    }

    val NELM: Monad<ForNonEmptyList> = NonEmptyList.monad()

    init {

        testLaws(
                MonadLaws.laws(OptionT.monad(NonEmptyList.monad()), Eq.any()),
                SemigroupKLaws.laws(
                        OptionT.semigroupK(Id.monad()),
                        OptionT.applicative(Id.monad()),
                        EQ()),

                MonoidKLaws.laws(
                        OptionT.monoidK(Id.monad()),
                        OptionT.applicative(Id.monad()),
                        EQ()),

                FunctorFilterLaws.laws(
                        OptionT.functorFilter(Id.functor()),
                        { OptionT(Id(Some(it))) },
                        EQ()),

                TraverseFilterLaws.laws(
                        OptionT.traverseFilter(Option.traverseFilter()),
                        OptionT.applicative(Option.monad()),
                        { OptionT(Option(Some(it))) },
                        EQ(),
                        EQ_NESTED())
        )

        "toLeft for Some should build a correct EitherT" {
            forAll { a: Int, b: String ->
                OptionT.fromOption(this.NELM, Some(a)).toLeft(this.NELM, { b }) == EitherT.left<ForNonEmptyList, Int, String>(this.NELM, a)
            }
        }

        "toLeft for None should build a correct EitherT" {
            forAll { a: Int, b: String ->
                OptionT.fromOption<ForNonEmptyList, Int>(this.NELM, None).toLeft(this.NELM, { b }) == EitherT.right<ForNonEmptyList, Int, String>(this.NELM, b)
            }
        }

        "toRight for Some should build a correct EitherT" {
            forAll { a: Int, b: String ->
                OptionT.fromOption(this.NELM, Some(b)).toRight(this.NELM, { a }) == EitherT.right<ForNonEmptyList, Int, String>(this.NELM, b)
            }
        }

        "toRight for None should build a correct EitherT" {
            forAll { a: Int, b: String ->
                OptionT.fromOption<ForNonEmptyList, String>(this.NELM, None).toRight(this.NELM, { a }) == EitherT.left<ForNonEmptyList, Int, String>(this.NELM, a)
            }
        }

    }
}
