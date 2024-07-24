package arrow.match

import kotlin.reflect.KParameter
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty1
import kotlin.reflect.KType
import kotlin.reflect.KTypeParameter
import kotlin.reflect.KVisibility

public actual fun <S, A> Matcher(
  name: String,
  get: (S) -> A
): Matcher<S, A> = object : Matcher<S, A>, KProperty1.Getter<S, A> {
  override val name: String = name
  override fun get(receiver: S): A = get(receiver)
  override fun invoke(receiver: S): A = get(receiver)

  override val getter: KProperty1.Getter<S, A> = this
  override val property: KProperty<A> = this

  override val annotations: List<Annotation> = emptyList()
  override val isAbstract: Boolean = false
  override val isConst: Boolean = false
  override val isFinal: Boolean = false
  override val isLateinit: Boolean = false
  override val isOpen: Boolean = false
  override val isSuspend: Boolean = false
  override val isExternal: Boolean = false
  override val isInfix: Boolean = false
  override val isInline: Boolean = false
  override val isOperator: Boolean = false

  override val parameters: List<KParameter>
    get() = throw IllegalStateException("no parameters information")
  override val returnType: KType
    get() = throw IllegalStateException("no returnType information")
  override val typeParameters: List<KTypeParameter> = emptyList()
  override val visibility: KVisibility? = null

  override fun call(vararg args: Any?): A =
    get(args.single() as S)
  override fun callBy(args: Map<KParameter, Any?>): A =
    get(args.entries.single().value as S)
  override fun getDelegate(receiver: S): Any? = null
}
