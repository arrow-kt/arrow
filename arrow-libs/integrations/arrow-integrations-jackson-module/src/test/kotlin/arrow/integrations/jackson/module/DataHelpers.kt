package arrow.integrations.jackson.module

import io.kotlintest.properties.Gen

data class SomeObject(val someString: String, val someInt: Int)

fun Gen.Companion.someObject(): Gen<SomeObject> = bind(string(), int()) { str, int -> SomeObject(str, int) }
