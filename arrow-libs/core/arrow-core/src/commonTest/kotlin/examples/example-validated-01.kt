// This file was automatically generated from Validated.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleValidated01

import arrow.core.Either
import arrow.core.Either.Left
import arrow.core.flatMap

data class ConnectionParams(val url: String, val port: Int)

fun <A> config(key: String): Either<String, A> = Left(key)

config<String>("url").flatMap { url ->
 config<Int>("port").map { ConnectionParams(url, it) }
}
