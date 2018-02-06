package arrow

import java.lang.IllegalArgumentException
import java.lang.reflect.*
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

/**
 * A parametrized type calculated from walking up the interface chain in a Typeclass and finding other relevant
 * typeclasses that should be registered
 */
class InstanceParametrizedType(val raw: Type, val typeArgs: List<Type>) : ParameterizedType {

    fun typeArgsAreParameterized(): Boolean = typeArgs.isNotEmpty() && typeArgs[0] is ParameterizedType

    fun typeArgsIsHKRepresented(): Boolean =
            if (typeArgsAreParameterized()) {
                val firstTypeArg = typeArgs[0]
                when (firstTypeArg) {
                    is ParameterizedType -> firstTypeArg.rawType == HK::class.java
                    else -> false
                }
            } else false

    override fun getRawType(): Type = raw

    override fun getOwnerType(): Type? = null

    override fun getActualTypeArguments(): Array<Type> = typeArgs.toTypedArray()

    override fun toString(): String = "Type: [$raw for $typeArgs]"

    override fun equals(other: Any?): Boolean {
        if (other is ParameterizedType) {
            // Check that information is equivalent

            if (this === other)
                return true

            val thatOwner = other.ownerType
            val thatRawType = other.rawType

            return ownerType == thatOwner &&
                    rawType == thatRawType &&
                    Arrays.equals(actualTypeArguments, // avoid clone
                            other.actualTypeArguments)
        } else {
            return false
        }
    }

    override fun hashCode(): Int = Arrays.hashCode(actualTypeArguments) xor
            hashCode(ownerType) xor
            hashCode(rawType)

    private fun hashCode(o: Any?): Int = o?.hashCode() ?: 0

}

/**
 * Instrospects the generic type arguments to locate generic interfaces and their type args
 */
open class TypeLiteral<T> {

    val isParameterizedType: Boolean = javaClass.genericSuperclass is ParameterizedType

    val type: Type = (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[0]
}

inline fun <reified T> typeLiteral(): Type {
    val t = object : TypeLiteral<T>() {}
    return if (t.isParameterizedType) t.type else T::class.java
}

/**
 * A concurrent hash map of local global instances that may be invoked at runtime as if they were implicitly summoned
 */
object GlobalInstances : ConcurrentHashMap<Type, Any>()

data class TypeClassInstanceNotFound(val type: Type)
    : RuntimeException("Thread: ${Thread.currentThread().name} Time: ${System.nanoTime()} : \n$type not found in Global Typeclass Instances registry. " +
        "\nPlease ensure your instances implement `GlobalInstance<$type>` for automatic registration." +
        "\nAlternatively invoke `GlobalInstances.put(typeLiteral<$type>(), instance)` if you wish to register " +
        "\nor override a typeclass manually" +
        "\n Current global instances are : \n\n" +
        GlobalInstances.map { "${it.key} -> ${it.value}" }.joinToString("\n") +
        "\n"
)

/**
 * Allow users to register custom instances when they don't use auto derivation or they don't provide naming conventions
 * for instance discovery via convention name. ex.
 * interface OptionFunctorInstance : Functor<ForOption> {
 *   companion object {
 *     fun instance(): ForOptionFunctorInstance = object : ForOptionFunctorInstance {}
 *   }
 * }
 */
fun registerInstance(t: InstanceParametrizedType, value: Any): Any {
    GlobalInstances.putIfAbsent(t, value)
    return GlobalInstances.getValue(t)
}

/**
 * Obtains a global registered typeclass instance when fast unsafe runtime lookups are desired over passing instances
 * as args to functions. Use with care. This lookup will throw an exception if a typeclass is summoned and can't be found
 * in the GlobalInstances map or the companion factory methods for the `on` target type
 */
@Suppress("UNCHECKED_CAST")
fun <T : TC> instance(t: InstanceParametrizedType): T =
        if (GlobalInstances.containsKey(t)) {
            GlobalInstances.getValue(t) as T
        } else {
            val value = if (t.typeArgsAreParameterized() && t.typeArgsIsHKRepresented()) {
                parametricInstanceFromImplicitObject(t)
            } else {
                instanceFromImplicitObject(t)
            }
            if (value != null) {
                GlobalInstances.putIfAbsent(t, value)
                value as T
            } else {
                val e = TypeClassInstanceNotFound(t)
                println(e.message)
                throw e
            }
        }

private fun resolveNestedTypes(l: List<Type>): List<Type> {
    tailrec fun loop(remaining: List<Type>, acc: List<Type>): List<Type> =
            when {
                remaining.isEmpty() -> acc
                else -> {
                    val head = remaining[0]
                    val tail = remaining.drop(1)
                    val (newTail, newAcc) = when (head) {
                        is ParameterizedType -> Pair(head.actualTypeArguments.toList() + tail, acc)
                        is WildcardType -> Pair(head.upperBounds.toList() + tail, acc)
                        is Class<*> -> Pair(tail, acc + listOf(head))
                        else -> Pair(tail, acc)
                    }
                    loop(newTail, newAcc)
                }
            }
    return loop(l, emptyList())
}

private fun parametricInstanceFromImplicitObject(t: InstanceParametrizedType): Any? {
    val resolved = resolveNestedTypes(t.typeArgs)
    return instanceFromImplicitObject(InstanceParametrizedType(t.raw, resolved))
}

private fun instanceFromImplicitObject(t: InstanceParametrizedType): Any? {
    val of = t.raw as Class<*>
    val firstTypeArg = t.typeArgs[0]
    val on = when (firstTypeArg) {
        is Class<*> -> firstTypeArg
        is ParameterizedType -> firstTypeArg.rawType as Class<*>
        else -> throw IllegalArgumentException("$firstTypeArg not a Class or ParameterizedType")
    }
    val targetPackage = on.name.substringBeforeLast(".")
    val derivationPackage = when {
        targetPackage.startsWith("java.") -> targetPackage.replace(".", "_")
        targetPackage == "kotlin" -> "kotlin_"
        else -> targetPackage
    }
    val providerQualifiedName = "$derivationPackage.${on.simpleName.replaceFirst("For", "")}${of.simpleName}InstanceImplicits"
    val globalInstanceProvider = Class.forName(providerQualifiedName)
    val allCompanionFunctions = globalInstanceProvider.methods
    val factoryFunction = allCompanionFunctions.find { it.name == "instance" }
    return if (factoryFunction != null) {
        val values = factoryFunction.parameterTypes.mapIndexedNotNull { n, p ->
            if (TC::class.java.isAssignableFrom(p)) {
                val classifier = InstanceParametrizedType(p, p.typeParameters.toList())
                val vType = reifyRawParameterizedType(t, classifier, n)
                val value = instanceFromImplicitObject(vType)
                if (value != null) registerInstance(vType, value)
                value
            } else null
        }.toTypedArray()

        factoryFunction.isAccessible = true

        val instance =
                // If the function has the modifier we don't need an instance to call it.
                if (factoryFunction.isStatic()) null
                // Otherwise we need to obtain the singleton object in order to call the
                // 'instance' function on it
                else globalInstanceProvider.fields.firstOrNull { it.name == "INSTANCE" }?.get(null)

        if (values.isEmpty()) {
            factoryFunction.invoke(instance)
        } else {
            factoryFunction.invoke(instance, *values)
        }

    } else null
}

private fun Method.isStatic() =
        Modifier.isStatic(modifiers)

private fun reifyRawParameterizedType(carrier: InstanceParametrizedType, classifier: ParameterizedType, index: Int): InstanceParametrizedType =
        if (classifier.actualTypeArguments.any { it is TypeVariable<*> } && carrier.actualTypeArguments.size > index + 1) {
            InstanceParametrizedType(classifier.rawType, listOf(carrier.actualTypeArguments[index + 1]))
        } else if (classifier.actualTypeArguments.any { it is TypeVariable<*> }) {
            val nestedTypes = resolveNestedTypes(carrier.actualTypeArguments.toList())
            if (index < nestedTypes.size)
                InstanceParametrizedType(classifier.rawType, listOf(nestedTypes[index]))
            else
                InstanceParametrizedType(classifier.rawType, nestedTypes)
        } else {
            InstanceParametrizedType(classifier, classifier.actualTypeArguments.filterNotNull())
        }

private fun Type.asKotlinClass(): KClass<*>? =
        (this as? Class<*>)?.kotlin
