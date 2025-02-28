package arrow.raise.ktor.server.request

import arrow.core.raise.ExperimentalRaiseAccumulateApi
import arrow.core.raise.Raise
import arrow.core.raise.RaiseAccumulate
import io.ktor.http.*
import io.ktor.util.reflect.*
import kotlin.jvm.JvmName
import kotlin.properties.PropertyDelegateProvider
import kotlin.reflect.KProperty

public sealed interface RequestComponent

public data object ReceiveBody : RequestComponent

public sealed interface Parameter : RequestComponent {
  public val name: String

  public class Path(override val name: String) : Parameter
  public class Query(override val name: String) : Parameter
}

public sealed class ParameterDelegateProvider protected constructor(
  protected val parameters: Parameters,
) {
  protected abstract fun parameter(name: String): Parameter

  public inline operator fun <reified O : Any> provideDelegate(
    thisRef: Nothing?,
    prop: KProperty<*>,
  ): DelegatedParameter<O> = provideDelegateImpl(prop.name, typeInfo<O>())

  public operator fun invoke(name: String): DelegatedParameter<String> = invoke(name) { it }

  public operator fun <O : Any> invoke(
    name: String,
    transform: Raise<String>.(String) -> O,
  ): DelegatedParameter<O> = provideDelegate(parameter(name), transform)

  @JvmName("invokeReified")
  public inline operator fun <reified O : Any> invoke(name: String): DelegatedParameter<O> = provideDelegateImpl(name, typeInfo<O>())

  public operator fun <O : Any> invoke(transform: Raise<String>.(String) -> O): WithTransform<O> =
    WithTransformFn { provideDelegate(parameter(it), transform) }

  @PublishedApi
  internal fun <O : Any> provideDelegateImpl(
    parameterName: String,
    typeInfo: TypeInfo,
  ): DelegatedParameter<O> =
    provideDelegate(parameter(parameterName), typeInfo)

  protected abstract fun <O : Any> provideDelegate(
    parameter: Parameter,
    typeInfo: TypeInfo,
  ): DelegatedParameter<O>

  protected abstract fun <O : Any> provideDelegate(
    parameter: Parameter,
    transform: Raise<String>.(String) -> O,
  ): DelegatedParameter<O>

  public sealed interface WithTransform<O : Any> : PropertyDelegateProvider<Nothing?, DelegatedParameter<O>> {
    public fun get(name: String): DelegatedParameter<O>
  }

  private fun interface WithTransformFn<O : Any> : WithTransform<O> {
    override fun provideDelegate(
      thisRef: Nothing?,
      property: KProperty<*>,
    ): DelegatedParameter<O> = get(property.name)
  }
}

internal abstract class RaisingParameterProvider(
  private val raise: Raise<RequestError>,
  parameters: Parameters,
) : ParameterDelegateProvider(parameters) {
  override fun <O : Any> provideDelegate(parameter: Parameter, typeInfo: TypeInfo): DelegatedParameter<O> =
    Eager(raise.parameterOrRaise(parameters, parameter, typeInfo))

  override fun <O : Any> provideDelegate(parameter: Parameter, transform: Raise<String>.(String) -> O): DelegatedParameter<O> =
    Eager(raise.parameterOrRaise(parameters, parameter, transform))
}

@OptIn(ExperimentalRaiseAccumulateApi::class)
internal abstract class AccumulatingParameterProvider(
  private val raise: RaiseAccumulate<RequestError>,
  parameters: Parameters,
) : ParameterDelegateProvider(parameters) {
  override fun <O : Any> provideDelegate(parameter: Parameter, typeInfo: TypeInfo): DelegatedParameter<O> {
    val value = raise.accumulating<O> { parameterOrRaise(parameters, parameter, typeInfo) }
    return Deferred { value.value }
  }

  override fun <O : Any> provideDelegate(parameter: Parameter, transform: Raise<String>.(String) -> O): DelegatedParameter<O> {
    val value = raise.accumulating { parameterOrRaise(parameters, parameter, transform) }
    return Deferred { value.value }
  }
}

public sealed class DelegatedParameter<A : Any> {
  public abstract val value: A

  @Suppress("NOTHING_TO_INLINE")
  public inline operator fun getValue(
    thisRef: Nothing?,
    property: KProperty<*>,
  ): A = value
}

private class Eager<A : Any>(
  override val value: A,
) : DelegatedParameter<A>()

private class Deferred<A : Any>(
  private val f: () -> A,
) : DelegatedParameter<A>() {
  override val value: A get() = f()
}
