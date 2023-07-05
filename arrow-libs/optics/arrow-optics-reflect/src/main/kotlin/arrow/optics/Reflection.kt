package arrow.optics

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.instanceParameter
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.safeCast

/** Focuses on those elements of the specified [klass] */
public fun <S : Any, A : S> instance(klass: KClass<A>): Prism<S, A> =
  object : Prism<S, A> {
    override fun getOrModify(source: S): Either<S, A> =
      klass.safeCast(source)?.right() ?: source.left()
    override fun reverseGet(focus: A): S = focus
  }

/** Focuses on those elements of the specified class */
public inline fun <S : Any, reified A : S> instance(): Prism<S, A> =
  object : Prism<S, A> {
    override fun getOrModify(source: S): Either<S, A> =
      (source as? A)?.right() ?: source.left()
    override fun reverseGet(focus: A): S = focus
  }

/** Focuses on a given field */
public val <S, A> ((S) -> A).ogetter: Getter<S, A>
  get() = Getter { s -> this(s) }

/**
 * [Lens] that focuses on a field in a data class
 *
 * WARNING: this should only be called on data classes,
 *          but that is checked only at runtime!
 */
public val <S, A> KProperty1<S, A>.lens: Lens<S, A>
  get() = PLens(
    get = this,
    set = { s, a -> clone(this, s, a) },
  )

/** [Optional] that focuses on a nullable field */
public val <S, A> KProperty1<S, A?>.optional: Optional<S, A>
  get() = lens compose Optional.nullable()

public val <S, A> ((S) -> Iterable<A>).iter: Fold<S, A>
  get() = ogetter compose Fold.iterable()

public val <S, A> KProperty1<S, List<A>>.every: Every<S, A>
  get() = lens compose Every.list()

public val <S, K, A> KProperty1<S, Map<K, A>>.values: Every<S, A>
  get() = lens compose Every.map()

private fun <S, A> clone(prop: KProperty1<S, A>, value: S, newField: A): S {
  // based on https://stackoverflow.com/questions/49511098/call-data-class-copy-via-reflection
  val klass = prop.instanceParameter?.type?.classifier as? KClass<*>
  val copy = klass?.memberFunctions?.firstOrNull { it.name == "copy" }
  if (klass == null || !klass.isData || copy == null) {
    throw IllegalArgumentException("may only be used with data classes")
  }
  val fieldParam = copy.parameters.first { it.name == prop.name }
  return copy.callBy(mapOf(copy.instanceParameter!! to value, fieldParam to newField)) as S
}
