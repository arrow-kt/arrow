package kategory.common.utils

import kategory.implicits.implicitAnnotationName
import me.eugeniomarletti.kotlin.metadata.KotlinClassMetadata
import me.eugeniomarletti.kotlin.metadata.KotlinMetadata
import me.eugeniomarletti.kotlin.metadata.KotlinMetadataUtils
import me.eugeniomarletti.kotlin.metadata.KotlinPackageMetadata
import me.eugeniomarletti.kotlin.metadata.extractFullName
import me.eugeniomarletti.kotlin.metadata.getPropertyOrNull
import me.eugeniomarletti.kotlin.metadata.getValueParameterOrNull
import me.eugeniomarletti.kotlin.metadata.jvm.getJvmMethodSignature
import me.eugeniomarletti.kotlin.metadata.kotlinMetadata
import org.jetbrains.kotlin.serialization.ProtoBuf
import javax.lang.model.element.Element
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement

interface ProcessorUtils : KotlinMetadataUtils {

    fun KotlinMetadata.asClassOrPackageDataWrapper(classElement: TypeElement): ClassOrPackageDataWrapper? {
        val `package` = elementUtils.getPackageOf(classElement).toString()
        return when (this) {
            is KotlinClassMetadata -> data.asClassOrPackageDataWrapper(`package`)
            is KotlinPackageMetadata -> data.asClassOrPackageDataWrapper(`package`)
            else -> null
        }
    }

    fun getClassOrPackageDataWrapper(classElement: TypeElement): ClassOrPackageDataWrapper {
        val metadata = classElement.kotlinMetadata ?: knownError("These annotations can only be used in Kotlin")
        return metadata.asClassOrPackageDataWrapper(classElement) ?: knownError("This annotation can't be used on this element")
    }

    fun ClassOrPackageDataWrapper.getFunction(methodElement: ExecutableElement) =
        getFunctionOrNull(methodElement, nameResolver, functionList)
            ?: knownError("Can't find annotated method ${methodElement.jvmMethodSignature}")
}

fun knownError(message: String, element: Element? = null): Nothing = throw KnownException(message, element)

val ProtoBuf.Class.Kind.isCompanionOrObject get() = when (this) {
    ProtoBuf.Class.Kind.OBJECT,
    ProtoBuf.Class.Kind.COMPANION_OBJECT -> true
    else -> false
}

fun ClassOrPackageDataWrapper.getParameter(function: ProtoBuf.Function, parameterElement: VariableElement) =
    getValueParameterOrNull(nameResolver, function, parameterElement)
        ?: knownError("Can't find annotated parameter ${parameterElement.simpleName} in ${function.getJvmMethodSignature(nameResolver)}")

fun ClassOrPackageDataWrapper.getPropertyOrNull(methodElement: ExecutableElement) =
    getPropertyOrNull(methodElement, nameResolver, this::propertyList)

fun ProtoBuf.Type.extractFullName(
    classData: ClassOrPackageDataWrapper,
    outputTypeAlias: Boolean = true,
    failOnGeneric: Boolean = true
): String =
    extractFullName(
        nameResolver = classData.nameResolver,
        getTypeParameter = { classData.getTypeParameter(it)!! },
        outputTypeAlias = outputTypeAlias,
        throwOnGeneric = if (!failOnGeneric) null else KnownException("Generic $implicitAnnotationName types are not yet supported", null)
    )
