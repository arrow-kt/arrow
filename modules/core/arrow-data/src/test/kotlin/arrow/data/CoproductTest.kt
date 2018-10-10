package arrow.data

import arrow.Kind3
import arrow.core.*
import arrow.instances.syntax.coproduct.comonad.comonad
import arrow.instances.syntax.coproduct.functor.functor
import arrow.instances.syntax.coproduct.traverse.traverse
import arrow.instances.syntax.id.comonad.comonad
import arrow.instances.syntax.id.functor.functor
import arrow.instances.syntax.id.traverse.traverse
import arrow.test.UnitSpec
import arrow.test.laws.ComonadLaws
import arrow.test.laws.TraverseLaws
import arrow.typeclasses.Eq
import io.kotlintest.KTestJUnitRunner
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class CoproductTest : UnitSpec() {
  val EQ: Eq<Kind3<ForCoproduct, ForId, ForId, Int>> = Eq { a, b ->
    a.fix().fix() == b.fix().fix()
  }

  init {
    val TF = Coproduct.traverse(Id.traverse(), Id.traverse())
    val FF = Coproduct.functor(Id.functor(), Id.functor())
    val CM = Coproduct.comonad(Id.comonad(), Id.comonad())

    testLaws(
      TraverseLaws.laws(TF, FF, { Coproduct(Right(Id(it))) }, EQ),
      ComonadLaws.laws(CM, { Coproduct(Right(Id(it))) }, EQ)
    )

  }
}
