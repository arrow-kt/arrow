package arrow

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldBe
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class StateTests : UnitSpec() {

    private val addOne = State<Int, Int> { n -> n * 2 toT n }

    init {
        "addOne.run(1) should return Pair(2, 1)" {
            addOne.run(1) shouldBe Tuple2(2, 1)
        }

        "addOne.map(n -> n).run(1) should return same Pair(2, 1)" {
            addOne.map({ n -> n }, Id.functor()).run(1) shouldBe Tuple2(2, 1)
        }

        "addOne.map(n -> n.toString).run(1) should return same Pair(2, \"1\")" {
            addOne.map(Int::toString, Id.functor()).run(1) shouldBe Tuple2(2, "1")
        }

        "addOne.runS(1) should return 2" {
            addOne.runS(1) shouldBe 2
        }

        "addOne.runA(1) should return 1" {
            addOne.runA(1) shouldBe 1
        }
    }
}
