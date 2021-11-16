// This file was automatically generated from Validated.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleValidated06

import arrow.core.Validated
import arrow.core.computations.either
import arrow.core.valid
import arrow.core.invalid

data class Config(val map: Map<String, String>) {
  suspend fun <A> parse(read: Read<A>, key: String) = either<ConfigError, A> {
    val value = Validated.fromNullable(map[key]) {
      ConfigError.MissingConfig(key)
    }.bind()
    val readVal = Validated.fromNullable(read.read(value)) {
      ConfigError.ParseConfig(key)
    }.bind()
    readVal
  }
}
