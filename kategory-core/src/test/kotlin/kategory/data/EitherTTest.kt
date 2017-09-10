package kategory

import io.kotlintest.KTestJUnitRunner
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class EitherTTest : UnitSpec() {
    init {

        testLaws(MonadErrorLaws.laws(EitherT.monadError<IdHK, Throwable>(Id.monad()), Eq.any(), Eq.any()))
        testLaws(TraverseLaws.laws(EitherT.traverse<IdHK, Int>(), EitherT.applicative(), { EitherT(Id(Either.Right(it))) }, Eq.any()))
        testLaws(SemigroupKLaws.laws<EitherTKindPartial<IdHK, Int>>(
                EitherT.semigroupK(Id.monad()),
                EitherT.applicative(Id.monad()),
                Eq.any()))

    }
}
