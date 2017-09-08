package kategory

import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.lang.reflect.WildcardType
import java.util.Arrays
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

/**
 * Marker trait that all Functional typeclasses such as Monad, Functor, etc... must implement to be considered
 * candidates to pair with global instances
 */
interface Typeclass

/**
 * A parametrized type calculated from walking up the interface chain in a Typeclass and finding other relevant
 * typeclasses that should be registered
 */
class InstanceParametrizedType(val raw: Type, val typeArgs: List<Type>) : ParameterizedType {

    fun typeArgsAreParameterized(): Boolean = typeArgs.isNotEmpty() && typeArgs[0] is ParameterizedType

    override fun getRawType(): Type = raw

    override fun getOwnerType(): Type? = null

    override fun getActualTypeArguments(): Array<Type> = typeArgs.toTypedArray()

    override fun toString(): String = "Type: [$raw for $typeArgs]"

    override fun equals(other: Any?): Boolean {
        if (other is ParameterizedType) {
            // Check that information is equivalent
            val that = other

            if (this === that)
                return true

            val thatOwner = that.ownerType
            val thatRawType = that.rawType

            return ownerType == thatOwner &&
                    rawType == thatRawType &&
                    Arrays.equals(actualTypeArguments, // avoid clone
                            that.actualTypeArguments)
        } else {
            return false
        }
    }

    override fun hashCode(): Int = Arrays.hashCode(actualTypeArguments) xor
            hashCode(ownerType) xor
            hashCode(rawType)

    fun hashCode(o: Any?): Int = o?.hashCode() ?: 0

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
 * interface OptionFunctorInstance : Functor<OptionHK> {
 *   companion object {
 *     fun instance(): OptionHKFunctorInstance = object : OptionHKFunctorInstance {}
 *   }
 * }
 */
fun <T : Typeclass> registerInstance(value: T, of: KClass<T>, on: KClass<*>, vararg typeArgs: KClass<*>): Unit {
    val args = listOf(on.java) + typeArgs.map { it.java }
    val t = InstanceParametrizedType(of.java, args)
    GlobalInstances.putIfAbsent(t, value)
}

/**
 * Obtains a global registered typeclass instance when fast unsafe runtime lookups are desired over passing instances
 * as args to functions. Use with care. This lookup will throw an exception if a typeclass is summoned and can't be found
 * in the GlobalInstances map or the companion factory methods for the `on` target type
 */
@Suppress("UNCHECKED_CAST")
fun <T : Typeclass> instance(t: InstanceParametrizedType): T =
        if (GlobalInstances.containsKey(t)) {
            GlobalInstances.getValue(t) as T
        } else {
            val value = if (t.typeArgsAreParameterized()) {
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

private fun parametricInstanceFromImplicitObject(t: InstanceParametrizedType): Any? {
    val resolved = t.typeArgs
            .filterIsInstance<ParameterizedType>()
            .flatMap { it.actualTypeArguments.toList() }
            .filterIsInstance<WildcardType>()
            .filter { it.upperBounds.isNotEmpty() }
            .flatMap { it.upperBounds.toList() }
            .filterIsInstance<Class<*>>()
    return instanceFromImplicitObject(InstanceParametrizedType(t.raw, resolved))
}

private fun instanceFromImplicitObject(t: InstanceParametrizedType): Any? {
    val of = t.raw as Class<*>
    val on = t.typeArgs[0] as Class<*>
    val targetPackage = on.name.substringBeforeLast(".")
    val derivationPackage = if (targetPackage == "java.lang") {
        "java_lang"
    } else {
        targetPackage
    }
    val providerQualifiedName: String = "$derivationPackage.${on.simpleName.replaceFirst("HK", "")}${of.simpleName}InstanceImplicits"
    val globalInstanceProvider = Class.forName(providerQualifiedName)
    val allCompanionFunctions = globalInstanceProvider.methods
    val factoryFunction = allCompanionFunctions.find { it.name == "instance" }
    return if (factoryFunction != null) {
        val values: List<Any> = factoryFunction.parameters.mapNotNull {
            if (Typeclass::class.java.isAssignableFrom(it.type)) {
                val classifier = it.parameterizedType as ParameterizedType
                val argClasses = classifier.actualTypeArguments.map { it.asKotlinClass() }.filterNotNull()
                val kClassifier = classifier.asKotlinClass()
                if (argClasses.isNotEmpty() && kClassifier != null) {
                    instance(InstanceParametrizedType(kClassifier.java, listOf(argClasses[0].java) + argClasses.drop(1).map { it.java }))
                } else null
            } else null
        }
        factoryFunction.invoke(globalInstanceProvider, *values.toTypedArray())
    } else null
}

private fun Type.asKotlinClass(): KClass<*>? =
        if (this is Class<*>) this.kotlin else null
