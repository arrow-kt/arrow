package arrow.typeclasses

import arrow.core.None
import arrow.core.Option
import arrow.core.ev
import arrow.core.monadFilter
import arrow.data.ListKW
import arrow.data.ev
import arrow.data.k
import arrow.data.monadFilter
import arrow.mtl.MonadFilter
import arrow.mtl.bindingFilter
import arrow.syntax.option.some
import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.FreeSpec


/***
See [http://arrow-kt.io/docs/typeclasses/monadfilter/](http://arrow-kt.io/docs/typeclasses/monadfilter/)

[MonadFilter] is a type class that abstracts away the option of interrupting computation if a given predicate is not satisfied.

All instances of MonadFilter provide syntax over their respective data types to comprehend monadically over their computation:

Definition:

```kt
@typeclass
interface MonadFilter<F> : Monad<F>, FunctorFilter<F>, TC {

    fun <A> empty(): HK<F, A>

    override fun <A, B> mapFilter(fa: HK<F, A>, f: (A) -> Option<B>): HK<F, B> =
        flatMap(fa, { a -> f(a).fold({ empty<B>() }, { pure(it) }) })
}
```

**/

class MonadFilterExample: FreeSpec() { init {

    /**
    Binding over MonadFilter instances with bindingFilter brings into scope the continueIf guard that requires a Boolean predicate as value.
    If the predicate is true the computation will continue and if the predicate returns false
    the computation is short-circuited returning monad filter instance empty() value.
    */

    "continueIf (Option)" {

        fun sumOfOptionsIfPositive(maybeA: Option<Int>, maybeB: Option<Int>): Option<Int> =
                Option.monadFilter().bindingFilter {
                    val a: Int = maybeA.bind()
                    val b: Int = maybeB.bind()
                    val c = a + b
                    continueIf(c > 0)
                    yields(c)
                }.ev()

        sumOfOptionsIfPositive(1.some(), 2.some()) shouldBe 3.some()
        sumOfOptionsIfPositive(1.some(), None) shouldBe None
        sumOfOptionsIfPositive(2.some(), (-4).some()) shouldBe None

    }

    "continueIf (ListKW)" {
        fun productOfListsIfPositive(listA: ListKW<Int>, listB: ListKW<Int>): ListKW<Int> =
            ListKW.monadFilter().bindingFilter {
                val a = listA.bind()
                val b = listB.bind()
                val c = a + b
                continueIf(c > 0)
                yields(c)
            }.ev()

        productOfListsIfPositive(
            listOf(5, 17).k(),
            listOf(5, -12).k()
        ) shouldBe listOf(10, 22, 5).k() // 5 + -12 == 7 not present!!

    }


    /** Binding over MonadFilter instances with bindingFilter brings into scope the bindWithFilter guard
     * that requires a Boolean predicate as value getting matched on the monad capturing inner value.
     * If the predicate is true the computation will continue and if the predicate returns false
     * the computation is short-circuited returning the monad filter instance empty() value. **/
    "bindWithFilter (ListKW)" {

        fun productOfListsWithFilter(listA: ListKW<Int>, listB: ListKW<Int>): ListKW<Int> =
            ListKW.monadFilter().bindingFilter {
                val a = listA.bind()
                val b = listB.bindWithFilter { it != a }
                val c = a + b
                yields(c)
            }.ev()

        productOfListsWithFilter(
            listOf(5, 17).k(),
            listOf(5, -12).k()
        ) shouldBe listOf(-7, 22, 5).k() // 5 + 5 == 10 not present!!

    }

}}