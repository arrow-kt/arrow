package arrow.test.laws

import arrow.typeclasses.Show
import arrow.typeclasses.show
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object ShowLaws {

    inline fun <reified F> laws(S: Show<F> = show<F>(), noinline cf: (Int) -> F): List<Law> =
            listOf(
                    Law("Show Laws: equality", { equalShow(S, cf) })
            )

    inline fun <reified F> equalShow(S: Show<F> = show(), crossinline cf: (Int) -> F): Unit =
            forAll(Gen.int(), { int: Int ->
                val a = cf(int)
                val b = cf(int)
                S.show(a) == S.show(b)
            })

}