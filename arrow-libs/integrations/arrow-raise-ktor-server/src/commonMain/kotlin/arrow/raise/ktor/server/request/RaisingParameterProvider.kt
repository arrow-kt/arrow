package arrow.raise.ktor.server.request

import arrow.core.raise.Raise
import io.ktor.http.*
import io.ktor.util.reflect.*
import kotlin.jvm.JvmInline
import kotlin.jvm.JvmName
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

public abstract class RaisingParameterProvider internal constructor(
  private val raise: Raise<RequestError>,
  private val parameters: Parameters,
) {
  protected abstract fun parameter(name: String): Parameter

  public companion object {
    public operator fun invoke(raise: Raise<RequestError>, parameters: Parameters, parameter: (String) -> Parameter): RaisingParameterProvider =
      object : RaisingParameterProvider(raise, parameters) {
        override fun parameter(name: String): Parameter = parameter(name)
      }
  }

  @PublishedApi
  internal fun getString(name: String): String =
    raise.parameterOrRaise(parameters, parameter(name))

  @PublishedApi
  internal fun <O : Any> getReified(name: String, typeInfo: TypeInfo): O =
    raise.parameterOrRaise(parameters, parameter(name), typeInfo)

  @PublishedApi
  internal fun <O : Any> getTransformed(name: String, transform: ParameterTransform<O>): O =
    raise.parameterOrRaise(parameters, parameter(name), transform)

  // type-bound delegate providers
  public operator fun invoke(): Property<String> =
    Property { _, prop -> getString(prop.name) }

  @PublishedApi
  internal fun <O : Any> invoke(typeInfo: TypeInfo): Property<O> =
    Property { _, prop -> getReified(prop.name, typeInfo) }

  public operator fun <O : Any> invoke(transform: ParameterTransform<O>): Property<O> =
    Property { _, prop -> getTransformed(prop.name, transform) }

  @JvmName("invokeReified")
  public inline operator fun <reified O : Any> invoke(): Property<O> = invoke(typeInfo<O>())

  // name-bound delegate providers
  public operator fun invoke(name: String): Property<String> =
    Property { _, _ -> getString(name) }

  @PublishedApi
  internal fun <O : Any> invoke(name: String, typeInfo: TypeInfo): Property<O> =
    Property { _, _ -> getReified(name, typeInfo) }

  public operator fun <O : Any> invoke(name: String, transform: Raise<String>.(String) -> O): Property<O> =
    Property { _, _ -> getTransformed(name, transform) }

  @JvmName("invokeReified")
  public inline operator fun <reified O : Any> invoke(name: String): Property<O> = invoke(name, typeInfo<O>())

  // unbound delegate
  public inline operator fun <reified O : Any> getValue(thisRef: Nothing?, prop: KProperty<*>): O =
    getReified(prop.name, typeInfo<O>())
}

@JvmInline
public value class Value<T>(public val value: T) {
  @Suppress("NOTHING_TO_INLINE")
  public inline operator fun getValue(thisRef: Nothing?, property: KProperty<*>): T = value
}

private typealias Property<T> = ReadOnlyProperty<Nothing?, T>
