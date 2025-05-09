package arrow.raise.ktor.server

import arrow.core.NonEmptyList
import arrow.core.raise.Raise
import arrow.core.raise.withError
import arrow.raise.ktor.server.request.RequestError

@PublishedApi
internal inline fun <R> RaiseRoutingContext.raiseError(f: Raise<RequestError>.() -> R): R = withError(call::errorResponse, f)

@PublishedApi
internal inline fun <R> RaiseRoutingContext.raiseErrors(f: Raise<NonEmptyList<RequestError>>.() -> R): R = withError(call::errorsResponse, f)
