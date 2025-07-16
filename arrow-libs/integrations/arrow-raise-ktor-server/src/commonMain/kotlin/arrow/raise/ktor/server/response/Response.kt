package arrow.raise.ktor.server.response

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.response.*
import io.ktor.util.reflect.*

public sealed interface Response {
  public suspend fun respondTo(call: ApplicationCall)

  public companion object {
    @PublishedApi
    internal fun Response(statusCode: HttpStatusCode, value: Any?, typeInfo: TypeInfo): Response = Typed(statusCode, value, typeInfo)

    // faux constructors
    public fun Response(outgoingContent: OutgoingContent): Response = Raw(outgoingContent)
    public fun Response(statusCode: HttpStatusCode): Response = Raw(HttpStatusCodeContent(statusCode))
    public inline fun <reified T> Response(statusCode: HttpStatusCode, value: T): Response = Response(statusCode, value, typeInfo<T>())

    // TODO: not sure if we want these three or not - allows for `Response.empty(OK)` and `Response.of(myPayload)` etc
    public fun empty(statusCode: HttpStatusCode = HttpStatusCode.NoContent): Response = Response(statusCode)
    public fun raw(outgoingContent: OutgoingContent): Response = Response(outgoingContent)
    public inline fun <reified T> payload(value: T, statusCode: HttpStatusCode = HttpStatusCode.OK): Response = Response(statusCode, value, typeInfo<T>())

    // allows using a HttpStatusCode as a "constructor" of a response, i.e. `NotFound("user was missing")`
    public inline operator fun <reified T : Any> HttpStatusCode.invoke(payload: T): Response = Response(this, payload, typeInfo<T>())
  }
}

private data class Typed(val statusCode: HttpStatusCode, val content: Any?, val typeInfo: TypeInfo) : Response {
  override suspend fun respondTo(call: ApplicationCall) = call.respond(statusCode, content, typeInfo)
}

private data class Raw(val outgoingContent: OutgoingContent) : Response {
  override suspend fun respondTo(call: ApplicationCall) = call.respond(outgoingContent, null)
}
