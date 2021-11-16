// This file was automatically generated from Validated.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleValidated14

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.left
import arrow.core.right
import arrow.core.Validated
import arrow.core.computations.either
import arrow.core.valid
import arrow.core.invalid

abstract class Read<A> {
 abstract fun read(s: String): A?

 companion object {

  val stringRead: Read<String> =
   object : Read<String>() {
    override fun read(s: String): String? = s
   }

  val intRead: Read<Int> =
   object : Read<Int>() {
    override fun read(s: String): Int? =
     if (s.matches(Regex("-?[0-9]+"))) s.toInt() else null
   }
 }
}

data class Config(val map: Map<String, String>) {
  suspend fun <A> parse(read: Read<A>, key: String) = either<ConfigError, A> {
    val value = Validated.fromNullable(map[key]) {
      ConfigError.MissingConfig(key)
    }.bind()
    val readVal = Validated.fromNullable(read.read(value)) {
      ConfigError.ParseConfig(key)
    }.bind()
    readVal
  }.toValidatedNel()
}

sealed class ConfigError {
 data class MissingConfig(val field: String) : ConfigError()
 data class ParseConfig(val field: String) : ConfigError()
}

fun positive(field: String, i: Int): Either<ConfigError, Int> =
 if (i >= 0) i.right()
 else ConfigError.ParseConfig(field).left()

val config = Config(mapOf("house_number" to "-42"))

suspend fun main() {
  val houseNumber = config.parse(Read.intRead, "house_number").withEither { either ->
    either.flatMap { positive("house_number", it) }
  }
 println(houseNumber)
}

