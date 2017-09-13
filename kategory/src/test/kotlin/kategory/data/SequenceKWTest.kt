package kategory.data

import io.kotlintest.KTestJUnitRunner
import kategory.*
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class SequenceKWTest : UnitSpec() {
    val applicative = SequenceKW.applicative()

    init {
        val eq: Eq<HK<SequenceKWHK, Int>> = object : Eq<HK<SequenceKWHK, Int>> {
            override fun eqv(a: HK<SequenceKWHK, Int>, b: HK<SequenceKWHK, Int>): Boolean =
                    a.toList() == b.toList()
        }

        testLaws(MonadLaws.laws(SequenceKW.monad(), eq))
        testLaws(MonoidKLaws.laws(SequenceKW.monoidK(), applicative, eq))
        testLaws(TraverseLaws.laws(SequenceKW.traverse(), applicative, { n: Int -> SequenceKW(sequenceOf(n)) }, eq))
    }
}