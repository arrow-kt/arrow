package arrow.optics

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KProperty1
import kotlin.reflect.cast
import kotlin.reflect.full.instanceParameter
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.safeCast

/** Focuses on those elements of the specified [klass] */
public fun <S : Any, A : S> instance(klass: KClass<A>): Prism<S, A> = object : Prism<S, A> {
  override fun getOrModify(source: S): Either<S, A> = klass.safeCast(source)?.right() ?: source.left()
  override fun reverseGet(focus: A): S = focus
}

/** Focuses on those elements of the specified class */
public inline fun <S : Any, reified A : S> instance(): Prism<S, A> = object : Prism<S, A> {
  override fun getOrModify(source: S): Either<S, A> = (source as? A)?.right() ?: source.left()
  override fun reverseGet(focus: A): S = focus
}

/**
 * [Lens] that focuses on a field in a data class
 *
 * WARNING: this should only be called on data classes,
 *          but that is checked only at runtime!
 *          The check happens when the lens is created.
 */
public val <S, A> KProperty1<S, A>.lens: Lens<S, A>
  get() = PLens(
    get = this,
    set = reflectiveCopy,
  )

/** [Optional] that focuses on a nullable field */
public val <S, A> KProperty1<S, A?>.optional: Optional<S, A>
  get() = lens compose Optional.nullable()

public val <S, A> KProperty1<S, List<A>>.every: Traversal<S, A>
  get() = lens compose Every.list()

public val <S, K, A> KProperty1<S, Map<K, A>>.values: Traversal<S, A>
  get() = lens compose Every.map()

private val <S, A> KProperty1<S, A>.reflectiveCopy: (S, A) -> S get() {
  // based on https://stackoverflow.com/questions/49511098/call-data-class-copy-via-reflection
  val klass = kClass
  val copy = klass.copyMethod
  val fieldParam = copy.parameters.first { it.name == name }
  val instanceParam = copy.instanceParameter!!
  return { value, newField -> klass.cast(copy.callBy(mapOf(instanceParam to value, fieldParam to newField))) }
}

@Suppress("UNCHECKED_CAST")
private val <S> KProperty1<S, *>.kClass: KClass<S & Any>
  get() = instanceParameter?.type?.classifier as? KClass<S & Any> ?: throw IllegalArgumentException("may only be used with instance properties")

private val KClass<*>.copyMethod: KFunction<*> get() {
  val copy = memberFunctions.firstOrNull { it.name == "copy" }
  if (!isData || copy == null) {
    throw IllegalArgumentException("may only be used with data classes")
  }
  return copy
}
