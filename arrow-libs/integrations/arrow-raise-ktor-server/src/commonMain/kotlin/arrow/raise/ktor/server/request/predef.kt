package arrow.raise.ktor.server.request

import io.ktor.util.reflect.*

internal inline val TypeInfo.simpleName get(): String = type.simpleName ?: type.toString()
