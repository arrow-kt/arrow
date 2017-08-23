package kategory

import io.kotlintest.KTestJUnitRunner
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class EitherTTest : UnitSpec() {
    init {

        testLaws(MonadErrorLaws.laws(EitherT.monadError<IdHK, Throwable>(Id.monad()), Eq.any()))
        testLaws(TraverseLaws.laws(EitherT.traverse<IdHK, Int>(), EitherT.applicative(), { EitherT(Id(Either.Right(it))) }, Eq.any()))
        testLaws(SemigroupKLaws.laws<EitherTKindPartial<IdHK, Int>>(
                EitherT.semigroupK(Id.monad()),
                EitherT.applicative(Id.monad()),
                object : Eq<HK<EitherTKindPartial<IdHK, Int>, Int>> {
                    override fun eqv(a: HK<EitherTKindPartial<IdHK, Int>, Int>, b: HK<EitherTKindPartial<IdHK, Int>, Int>): Boolean =
                            a.ev() == b.ev()
                }))

    }
}
