@file:JvmName("RequestRaise")
@file:JvmMultifileClass
@file:OptIn(ExperimentalContracts::class)

package arrow.raise.ktor.server.request

import arrow.core.raise.Raise
import arrow.core.raise.catch
import arrow.core.raise.ensureNotNull
import arrow.core.raise.recover
import io.ktor.http.*
import io.ktor.util.converters.*
import io.ktor.util.reflect.*
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind.AT_MOST_ONCE
import kotlin.contracts.contract
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName

public typealias ParameterTransform<O> = Raise<String>.(String) -> O

@PublishedApi
internal inline fun <P : Parameter, A : Any> Raise<RequestError>.parameterOrRaise(
  parameters: Parameters,
  parameter: P,
  transform: ParameterTransform<A>,
): A {
  contract { callsInPlace(transform, AT_MOST_ONCE) }
  val value = parameterOrRaise(parameters, parameter)
  return recover({ transform(value) }) {
    raise(Malformed(parameter, it))
  }
}

@PublishedApi
internal fun <P : Parameter> Raise<MissingParameter>.parameterOrRaise(
  parameters: Parameters,
  parameter: P,
): String = ensureNotNull(parameters[parameter.name]) { MissingParameter(parameter) }

@PublishedApi
internal fun <A : Any> Raise<RequestError>.parameterOrRaise(
  parameters: Parameters,
  parameter: Parameter,
  typeInfo: TypeInfo,
): A {
  // following what [Parameters.getOrFail] does...
  val values = ensureNotNull(parameters.getAll(parameter.name)) { MissingParameter(parameter) }
  return catch({
    @Suppress("UNCHECKED_CAST")
    DefaultConversionService.fromValues(values, typeInfo) as A
  }) {
    raise(
      Malformed(
        component = parameter,
        message = "couldn't be parsed/converted to ${typeInfo.simpleName}",
        cause = it,
      ),
    )
  }
}
