package arrow.raise.ktor.server

import arrow.core.raise.Raise
import arrow.core.raise.recover
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.routing.*
import kotlin.jvm.JvmName

@JvmName("getOrRaiseString")
public fun Route.getOrRaise(
  path: String,
  body: suspend context(Raise<HttpStatusCode>, Raise<OutgoingContent>) RoutingContext.() -> Unit
): Route = perform(wrapper = { get(path, it) }, body)

@JvmName("getOrRaiseRegex")
public fun Route.getOrRaise(
  path: Regex,
  body: suspend context(Raise<HttpStatusCode>, Raise<OutgoingContent>) RoutingContext.() -> Unit
): Route = perform(wrapper = { get(path, it) }, body)

@JvmName("putOrRaiseString")
public fun Route.putOrRaise(
  path: String,
  body: suspend context(Raise<HttpStatusCode>, Raise<OutgoingContent>) RoutingContext.() -> Unit
): Route = perform(wrapper = { put(path, it) }, body)

@JvmName("putOrRaiseRegex")
public fun Route.putOrRaise(
  path: Regex,
  body: suspend context(Raise<HttpStatusCode>, Raise<OutgoingContent>) RoutingContext.() -> Unit
): Route = perform(wrapper = { put(path, it) }, body)

private fun perform(
  wrapper: (suspend RoutingContext.() -> Unit) -> Route,
  body: suspend context(Raise<HttpStatusCode>, Raise<OutgoingContent>) RoutingContext.() -> Unit
): Route =
  wrapper block@ {
    perform {
      recover(statusCode@ {
        recover(content@ {
          body(this@statusCode, this@content, this@block)
        }) { content: OutgoingContent -> raise(content) }
      }) { statusCode: HttpStatusCode -> raise(statusCode) }
    }
  }
