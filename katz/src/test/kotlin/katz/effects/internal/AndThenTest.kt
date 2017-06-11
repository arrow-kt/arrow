package katz

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.fail
import io.kotlintest.matchers.shouldBe
import io.kotlintest.properties.forAll
import katz.effects.internal.AndThen
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class AndThenTest : UnitSpec() {

    init {
        "should chains operations with compose" {
            forAll { num: Int ->
                val f = AndThen { num: Int -> num.toString() }
                val g = AndThen { str: String -> Integer.parseInt(str) }
                num == g.compose(f)(num)
            }
        }

        "should chains operations with andThen" {
            forAll { num: Int ->
                val f = AndThen { num: Int -> num.toString() }
                val g = AndThen { str: String -> Integer.parseInt(str) }
                num == f.andThen(g)(num)
            }
        }

        "should not blow up the stack" {
            val limit = 5000
            val value: List<Int> = (0..limit).toList()
            val result = value.map { AndThen { num: Int -> num + 1 } }.reduce { acc, new -> acc.andThen(new) }(0)

            result shouldBe limit + 1
        }

        class MyException : RuntimeException()

        "should recover from Simple exceptions if there's an ErrorHandler on the chain" {
            forAll { num: Int ->
                val expected = 10
                val dummy = MyException()

                val f = AndThen { _: Int -> throw dummy }
                        .andThen(
                                AndThen({ it }, { err ->
                                    when (err) {
                                        is MyException -> expected
                                        else -> throw err
                                    }
                                })
                        )

                f(num) == expected
            }
        }

        "should recover from immediate ErrorHandler exceptions" {
            forAll { num: Int ->
                val expected = 10
                val dummy = MyException()

                val f = AndThen({ _: Int -> throw dummy }, { throw it })
                        .andThen(
                                AndThen({ it },
                                        { err ->
                                            when (err) {
                                                is MyException -> expected
                                                else -> throw err
                                            }
                                        })
                        )

                f(num) == expected
            }
        }

        "should recover from exceptions if there's an ErrorHandler on the chain" {
            forAll { num: Int ->
                val expected = 10
                val dummy = MyException()

                val f = AndThen { _: Int -> throw dummy }
                        .andThen(
                                AndThen({ it }, { err ->
                                    when (err) {
                                        is MyException -> expected
                                        else -> throw err
                                    }
                                })
                        )
                        .andThen(AndThen { num: Int ->
                            num.toString()
                        })

                f(num) == expected.toString()
            }
        }

        "should recover from raised errors if there's an ErrorHandler on the chain" {
            forAll { num: Int ->
                val expected = 10
                val dummy = MyException()

                val f = AndThen { num: Int -> num }
                        .andThen(AndThen { num: Int ->
                            num.toString()
                        })
                        .error(dummy, { expected.toString() })

                f == expected.toString()
            }
        }

        "should not recover from raised errors if the ErrorHandler function is incorrect" {
            val expected = 10
            val dummy = MyException()

            try {
                val value = AndThen { num: Int -> num }
                        .andThen(AndThen { num: Int ->
                            num.toString()
                        })
                        .error(dummy, { /* Expects a String */ expected })
                value shouldBe expected
                fail("should throw ClassCastException")
            } catch (cce: ClassCastException) {
                // Success!
            } catch (throwable: Throwable) {
                fail("should only throw ClassCastException")
            }
        }

        "should throw without ErrorHandler on exception" {
            val dummy = MyException()
            val f = AndThen({ _: Int -> throw dummy }, { throw it })
                    .andThen(AndThen { num: Int -> num })

            try {
                f(0)
                fail("Should throw exception")
            } catch (throwable: Throwable) {
                // Success!!
            }
        }
    }
}