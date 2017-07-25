package kategory

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldEqual
import io.kotlintest.properties.forAll
import io.reactivex.Observable.just
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class ObservableMonoidTest : UnitSpec() {
    init {
        "Int"{
            forAll { value: Int ->
                val left = ObservableMonoid(IntMonoid)
                        .combine(just(value), just(value)).toList().blockingGet()

                val right = just(IntMonoid.combine(value, value)).toList().blockingGet()

                left == right
            }

            ObservableMonoid(IntMonoid).empty().toList().blockingGet() shouldEqual just(IntMonoid.empty()).toList().blockingGet()
        }

        "List"{
            ObservableMonoid<List<Any>>(ListMonoid())
                    .combine(just(listOf(1, 2)), just(listOf(3, 4))).toList().blockingGet() shouldEqual just(listOf(1, 2, 3, 4)).toList().blockingGet()

            ObservableMonoid<List<Any>>(ListMonoid()).empty().toList().blockingGet() shouldEqual just(ListMonoid<Any>().empty()).toList().blockingGet()
        }
    }
}
