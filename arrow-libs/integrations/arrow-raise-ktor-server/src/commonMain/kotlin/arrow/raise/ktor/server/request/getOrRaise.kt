@file:JvmName("RequestRaise")
@file:JvmMultifileClass

package arrow.raise.ktor.server.request

import arrow.core.raise.Raise
import arrow.core.raise.catch
import arrow.core.raise.ensureNotNull
import arrow.core.raise.withError
import io.ktor.http.*
import io.ktor.serialization.ContentConvertException
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.ContentTransformationException
import io.ktor.server.request.*
import io.ktor.server.routing.*
import io.ktor.util.converters.*
import io.ktor.util.reflect.*
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName

@PublishedApi
internal inline fun <A : Any> Raise<RequestError>.parameterOrRaise(
  parameters: Parameters,
  parameter: Parameter,
  transform: Raise<String>.(String) -> A,
): A {
  val value = ensureNotNull(parameters[parameter.name]) { Missing(parameter) }
  return withError({ Malformed(parameter, it) }) { transform(value) }
}

@PublishedApi
internal fun <A : Any> Raise<RequestError>.parameterOrRaise(
  parameters: Parameters,
  parameter: Parameter,
  typeInfo: TypeInfo,
): A {
  // following what [Parameters.getOrFail] does...
  val values = ensureNotNull(parameters.getAll(parameter.name)) { Missing(parameter) }
  return catch({
    @Suppress("UNCHECKED_CAST")
    DefaultConversionService.fromValues(values, typeInfo) as A
  }) {
    raise(
      Malformed(
        component = parameter,
        message = it.message ?: "couldn't be parsed/converted to ${typeInfo.simpleName}",
        cause = it,
      ),
    )
  }
}

@PublishedApi
internal suspend fun <A : Any> Raise<Malformed<ReceiveBody>>.receiveOrRaise(
  call: RoutingCall,
  typeInfo: TypeInfo,
): A = receiveOrRaise(typeInfo) { call.receive(typeInfo) }

@PublishedApi
internal suspend fun <A : Any> Raise<Malformed<ReceiveBody>>.receiveNullableOrRaise(
  call: RoutingCall,
  typeInfo: TypeInfo,
): A? = receiveOrRaise(typeInfo) { call.receiveNullable(typeInfo) }

private inline fun <A> Raise<Malformed<ReceiveBody>>.receiveOrRaise(
  typeInfo: TypeInfo,
  function: () -> A
): A = catch(function) {
  val cause = when (it) {
    is ContentTransformationException,
    is ContentConvertException -> it
    is BadRequestException -> it.cause ?: it // TODO: do we want to unwrap this?
    else -> throw it
  }
  raise(Malformed(ReceiveBody, cause.message ?: "Could not deserialize ${typeInfo.simpleName} from request body", cause))
}

private inline val TypeInfo.simpleName get(): String = type.simpleName ?: type.toString()
