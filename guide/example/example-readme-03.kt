// This file was automatically generated from README.md by Knit tool. Do not edit.
package example.exampleReadme03

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

suspend fun <R, A> Cont<R, A>.toEither(): Either<R, A> =
  fold({ Either.Left(it) }) { Either.Right(it) }

suspend fun <A> Cont<None, A>.toOption(): Option<A> =
  fold(::identity) { Some(it) }
