package kategory.common.utils

import kategory.implicits.implicitAnnotationName
import me.eugeniomarletti.kotlin.metadata.KotlinClassMetadata
import me.eugeniomarletti.kotlin.metadata.KotlinMetadata
import me.eugeniomarletti.kotlin.metadata.KotlinMetadataUtils
import me.eugeniomarletti.kotlin.metadata.KotlinPackageMetadata
import me.eugeniomarletti.kotlin.metadata.getJvmMethodSignature
import me.eugeniomarletti.kotlin.metadata.kotlinMetadata
import me.eugeniomarletti.kotlin.metadata.kotlinPropertyAnnotationsFunPostfix
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
        methodElement.jvmMethodSignature.let { methodSignature ->
            functionList
                .firstOrNull { methodSignature == it.getJvmMethodSignature(nameResolver) }
                ?: knownError("Can't find annotated method $methodSignature")
        }
}

fun knownError(message: String, element: Element? = null): Nothing = throw KnownException(message, element)

fun String.plusIfNotBlank(
    postfix: String = "",
    prefix: String = ""
) = if (this.isNotBlank()) prefix + this + postfix else this

val String.escapedClassName
    get() = split('/', '.').joinToString("`.`").plusIfNotBlank(prefix = "`", postfix = "`")

val ProtoBuf.Class.Kind.isCompanionOrObject get() = when (this) {
    ProtoBuf.Class.Kind.OBJECT,
    ProtoBuf.Class.Kind.COMPANION_OBJECT -> true
    else -> false
}

fun ClassOrPackageDataWrapper.getParameter(function: ProtoBuf.Function, parameterElement: VariableElement) =
    parameterElement.simpleName.toString().let { parameterName ->
        function.valueParameterList
            .firstOrNull { parameterName == nameResolver.getString(it.name) }
            ?: knownError("Can't find annotated parameter $parameterName in ${function.getJvmMethodSignature(nameResolver)}")
    }

fun ClassOrPackageDataWrapper.getPropertyOrNull(methodElement: ExecutableElement) =
    methodElement.simpleName.toString()
        .takeIf { it.endsWith(kotlinPropertyAnnotationsFunPostfix) }
        ?.substringBefore(kotlinPropertyAnnotationsFunPostfix)
        ?.let { propertyName -> propertyList.firstOrNull { propertyName == nameResolver.getString(it.name) } }

fun ProtoBuf.Type.extractFullName(
    classData: ClassOrPackageDataWrapper,
    outputTypeAlias: Boolean = true,
    failOnGeneric: Boolean = true
): String {
    val nameResolver = classData.nameResolver

    if (failOnGeneric && !hasClassName()) knownError("Generic $implicitAnnotationName types are not yet supported")

    val name = when {
        hasTypeParameter() -> classData.getTypeParameter(typeParameter)!!.name
        hasTypeParameterName() -> typeParameterName
        outputTypeAlias && hasAbbreviatedType() -> abbreviatedType.typeAliasName
        else -> className
    }.let { nameResolver.getString(it).escapedClassName }

    val argumentList = if (outputTypeAlias && hasAbbreviatedType()) abbreviatedType.argumentList else argumentList
    val arguments = argumentList
        .takeIf { it.isNotEmpty() }
        ?.joinToString(prefix = "<", postfix = ">") {
            when {
                it.hasType() -> it.type.extractFullName(classData, outputTypeAlias, failOnGeneric)
                !failOnGeneric -> "*"
                else -> knownError("Wildcard $implicitAnnotationName types are not yet supported")
            }
        }
        ?: ""

    val nullability = if (nullable) "?" else ""

    return name + arguments + nullability
}
