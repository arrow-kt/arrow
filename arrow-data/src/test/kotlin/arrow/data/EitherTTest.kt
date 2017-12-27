package arrow

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldNotBe
import org.junit.runner.RunWith

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

    }
}
