package arrow.data

import arrow.HK
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldNotBe
import arrow.test.laws.EqLaws
import org.junit.runner.RunWith
import arrow.test.UnitSpec
import arrow.test.laws.MonadLaws
import arrow.typeclasses.*

@RunWith(KTestJUnitRunner::class)
class WeakTest : UnitSpec() {
    val EQ: Eq<HK<WeakHK, Int>> = Eq { a, b ->
        a.ev().getOrElse { -1 } == b.ev().getOrElse { -2 }
    }

    init {

        "instances can be resolved implicitly" {
            functor<WeakKind<Int>>() shouldNotBe null
            applicative<WeakKind<Int>>() shouldNotBe null
            monad<WeakKind<Int>>() shouldNotBe null
            foldable<WeakKind<Int>>() shouldNotBe null
            traverse<WeakKind<Int>>() shouldNotBe null
            eq<Weak<Int>>() shouldNotBe null
        }

        testLaws(
            EqLaws.laws { Weak(it) },
            MonadLaws.laws(Weak.monad(), EQ)
        )
    }
}