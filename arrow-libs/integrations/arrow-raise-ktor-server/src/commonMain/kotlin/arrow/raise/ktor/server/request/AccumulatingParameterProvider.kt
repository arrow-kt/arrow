package arrow.raise.ktor.server.request

import arrow.core.raise.ExperimentalRaiseAccumulateApi
import arrow.core.raise.Raise
import arrow.core.raise.RaiseAccumulate
import io.ktor.http.*
import io.ktor.util.reflect.*
import kotlin.jvm.JvmName
import kotlin.properties.PropertyDelegateProvider
import kotlin.reflect.KProperty

@OptIn(ExperimentalRaiseAccumulateApi::class)
public abstract class AccumulatingParameterProvider internal constructor(
  private val accumulate: RaiseAccumulate<RequestError>,
  private val parameters: Parameters,
) {
  protected abstract fun parameter(name: String): Parameter

  public companion object {
    public inline operator fun invoke(accumulate: RaiseAccumulate<RequestError>, parameters: Parameters, crossinline parameter: (String) -> Parameter): AccumulatingParameterProvider =
      object : AccumulatingParameterProvider(accumulate, parameters) {
        override fun parameter(name: String): Parameter = parameter(name)
      }
  }

  @PublishedApi
  internal fun getString(name: String): RaiseAccumulate.Value<String> =
    accumulate.accumulating { parameterOrRaise(parameters, parameter(name)) }

  @PublishedApi
  internal fun <O : Any> getReified(name: String, typeInfo: TypeInfo): RaiseAccumulate.Value<O> =
    accumulate.accumulating { parameterOrRaise(parameters, parameter(name), typeInfo) }

  @PublishedApi
  internal fun <O : Any> getTransformed(name: String, transform: ParameterTransform<O>): RaiseAccumulate.Value<O> =
    accumulate.accumulating { parameterOrRaise(parameters, parameter(name), transform) }

  // type-bound delegate providers
  public operator fun invoke(): Provider<RaiseAccumulate.Value<String>> =
    Provider { _, prop -> getString(prop.name) }

  @PublishedApi
  internal fun <O : Any> invoke(typeInfo: TypeInfo): Provider<RaiseAccumulate.Value<O>> =
    Provider { _, prop -> getReified(prop.name, typeInfo) }

  public operator fun <O : Any> invoke(transform: ParameterTransform<O>): Provider<RaiseAccumulate.Value<O>> =
    Provider { _, prop -> getTransformed(prop.name, transform) }

  @JvmName("invokeReified")
  public inline operator fun <reified O : Any> invoke(): Provider<RaiseAccumulate.Value<O>> = invoke(typeInfo<O>())

  // name-bound delegate providers
  public operator fun invoke(name: String): RaiseAccumulate.Value<String> =
    getString(name)

  public operator fun <O : Any> invoke(name: String, transform: ParameterTransform<O>): RaiseAccumulate.Value<O> =
    getTransformed(name, transform)

  @JvmName("invokeReified")
  public inline operator fun <reified O : Any> invoke(name: String): RaiseAccumulate.Value<O> =
    getReified(name, typeInfo<O>())

  // unbound delegate
  public inline operator fun <reified O : Any> provideDelegate(
    thisRef: Nothing?,
    prop: KProperty<*>,
  ): RaiseAccumulate.Value<O> = getReified(prop.name, typeInfo<O>())
}

internal typealias Provider<T> = PropertyDelegateProvider<Nothing?, T>
