package kategory

import io.kotlintest.KTestJUnitRunner
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class ListKWTest : UnitSpec() {
    val applicative = ListKW.applicative()
    init {
        testLaws(kategory.MonadLaws.laws(ListKW, Eq.any()))
        testLaws(kategory.SemigroupKLaws.laws(
                kategory.ListKW.semigroupK(),
                applicative,
                Eq.any()))
        testLaws(TraverseLaws.laws(ListKW.traverse(), applicative, { n: Int -> ListKW(listOf(n)) }, Eq.any()))


        "foldR should be stack-safe" {
            val loops = 70000

            val list: List<Int> = (1..loops).toList()


            val res = list.k().foldR(ListKW.foldable(),
                    Eval.later { 0 })
            { a, lb: Eval<Int> -> lb.map { it + a } }.value()

            println(res)
        }
    }
}