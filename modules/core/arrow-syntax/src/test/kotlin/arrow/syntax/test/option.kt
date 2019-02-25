package arrow.syntax.test

import arrow.core.Option
import arrow.core.Some
import arrow.syntax.collections.firstOption
import arrow.syntax.unsafe.get
import arrow.test.UnitSpec
import io.kotlintest.runner.junit4.KotlinTestRunner
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import org.junit.runner.RunWith

@RunWith(KotlinTestRunner::class)
class OptionSyntaxTest : UnitSpec() {
    init {
        "get throws NoSuchElementException if Option is empty" {
            shouldThrow<NoSuchElementException> {
                val option = Option.empty<Any>()
                option.get()
            }
        }

        "get returns value if Option is not empty" {
            val option = Option.just("Foo")
            option.get() shouldBe "Foo"
        }
    }
}
