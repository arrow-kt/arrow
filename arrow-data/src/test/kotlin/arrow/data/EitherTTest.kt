package arrow.data

import arrow.core.*
import arrow.instances.*
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldNotBe
import io.kotlintest.properties.forAll
import org.junit.runner.RunWith
import arrow.test.UnitSpec
import arrow.test.laws.MonadErrorLaws
import arrow.test.laws.SemigroupKLaws
import arrow.test.laws.TraverseLaws
import arrow.typeclasses.*

@RunWith(KTestJUnitRunner::class)
class EitherTTest : UnitSpec() {
    init {

        "instances can be resolved implicitly" {
            functor<EitherTKindPartial<OptionHK, Throwable>>() shouldNotBe null
            applicative<EitherTKindPartial<OptionHK, Throwable>>() shouldNotBe null
            monad<EitherTKindPartial<OptionHK, Throwable>>() shouldNotBe null
            foldable<EitherTKindPartial<OptionHK, Throwable>>() shouldNotBe null
            traverse<EitherTKindPartial<OptionHK, Throwable>>() shouldNotBe null
            monadError<EitherTKindPartial<OptionHK, Throwable>, Throwable>() shouldNotBe null
            semigroupK<EitherTKindPartial<OptionHK, Throwable>>() shouldNotBe null
        }

        testLaws(
            MonadErrorLaws.laws(EitherT.monadError<IdHK, Throwable>(Id.monad()), Eq.any(), Eq.any()),
            TraverseLaws.laws(EitherT.traverse<IdHK, Int>(), EitherT.applicative(), { EitherT(Id(Right(it))) }, Eq.any()),
            SemigroupKLaws.laws<EitherTKindPartial<IdHK, Int>>(
                EitherT.semigroupK(Id.monad()),
                EitherT.applicative(Id.monad()),
                Eq.any())
        )

        "mapLeft should alter left instance only" {
            forAll { i: Int, j: Int ->
                val left: Either<Int, Int> = Left(i)
                val right: Either<Int, Int> = Right(j)
                EitherT(Option(left)).mapLeft({it + 1}, Option.functor()) == EitherT(Option(Left(i+1))) &&
                        EitherT(Option(right)).mapLeft({it + 1}, Option.functor()) ==  EitherT(Option(right)) &&
                        EitherT(Option.empty<Either<Int, Int>>()).mapLeft({it +1}, Option.functor()) == EitherT(Option.empty<Either<Int, Int>>())
            }
        }

    }
}
