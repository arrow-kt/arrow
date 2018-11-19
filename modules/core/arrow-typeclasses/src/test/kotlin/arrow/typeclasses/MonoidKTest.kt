package arrow.typeclasses

import arrow.Kind
import arrow.core.ForOption
import arrow.core.Option
import arrow.core.fix
import arrow.instances.list.foldable.fold
import arrow.instances.option.monoidK.monoidK
import arrow.test.UnitSpec
import arrow.test.laws.MonoidKLaws
import io.kotlintest.KTestJUnitRunner
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class MonoidKTest : UnitSpec() {
    val EQ: Eq<Kind<ForOption, Int>> = Eq.invoke { a, b ->
        a.fix().run { listOf(a).fold(Option.monoidK().algebra()) } == b.fix().run { listOf(b).fold(Option.monoidK().algebra()) }
    }

    init {
        testLaws(
            MonoidKLaws.laws(Option.monoidK(), { Option.just(it) }, EQ)
        )
    }
}
