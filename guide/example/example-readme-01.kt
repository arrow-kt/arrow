// This file was automatically generated from README.md by Knit tool. Do not edit.
package example.exampleReadme01

import arrow.*
import arrow.core.*
import arrow.fx.coroutines.*
import kotlinx.coroutines.*
import io.kotest.matchers.collections.*
import io.kotest.assertions.*
import io.kotest.matchers.*
import io.kotest.matchers.types.*
import kotlin.coroutines.cancellation.CancellationException
import io.kotest.property.*
import io.kotest.property.arbitrary.*

object EmptyPath

fun readFile(path: String): Cont<EmptyPath, Unit> = cont {
  if (path.isNotEmpty()) shift(EmptyPath) else Unit
}

fun readFile2(path: String?): Cont<EmptyPath, Unit> = cont {
  ensure(!path.isNullOrBlank()) { EmptyPath }
}
