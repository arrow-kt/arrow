package katz

import io.kotlintest.specs.StringSpec


/**
 * Base class for unit tests
 */
abstract class UnitSpec : StringSpec() {
    companion object {
        init {
            // To get the instances before tests are initialized the following global typeclasses are preloaded
            Id
            NonEmptyList
            Option
            Try
            Eval
        }
    }
}
