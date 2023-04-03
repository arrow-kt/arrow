package arrow.core

import arrow.core.test.endo
import arrow.core.test.laws.MonoidLaws
import arrow.core.test.testLaws
import io.kotest.core.spec.style.StringSpec
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int

class EndoTest : StringSpec({

    testLaws(
      MonoidLaws("Endo", Endo(::identity), { f, g -> Endo(f.f.compose(g.f)) }, Arb.endo(Arb.int())) { a, b ->
        a.f(1) == b.f(1)
      }
    )

})
