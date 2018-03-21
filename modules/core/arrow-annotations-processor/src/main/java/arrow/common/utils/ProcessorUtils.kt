package arrow.common.utils

import me.eugeniomarletti.kotlin.metadata.*
import me.eugeniomarletti.kotlin.metadata.jvm.getJvmMethodSignature
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.java.MethodElement
import org.jetbrains.kotlin.serialization.ProtoBuf
import org.jetbrains.kotlin.serialization.deserialization.TypeTable
import org.jetbrains.kotlin.serialization.deserialization.supertypes
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
        return metadata.asClassOrPackageDataWrapper(classElement)
                ?: knownError("This annotation can't be used on this element")
    }

    fun TypeElement.methods(): List<MethodElement> =
            enclosedElements.map { if (it is MethodElement) it as MethodElement else null }.filterNotNull()

    fun ClassOrPackageDataWrapper.getFunction(methodElement: ExecutableElement) =
            getFunctionOrNull(methodElement, nameResolver, functionList)
                    ?: knownError("Can't find annotated method ${methodElement.jvmMethodSignature}")

    fun ProtoBuf.Function.overrides(o: ProtoBuf.Function): Boolean = false

    fun ClassOrPackageDataWrapper.Class.declaredTypeClassInterfaces(
            typeTable: TypeTable): List<ClassOrPackageDataWrapper> {
        val interfaces = this.classProto.supertypes(typeTable).map {
            it.extractFullName(this, failOnGeneric = false)
        }.filter {
                    it != "`arrow`.`TC`"
                }
        return interfaces.map { i ->
            val className = i.removeBackticks().substringBefore("<")
            val typeClassElement = elementUtils.getTypeElement(className)
            val parentInterface = getClassOrPackageDataWrapper(typeClassElement)
            parentInterface as ClassOrPackageDataWrapper.Class
        }
    }

    fun recurseTypeclassInterfaces(
            current: ClassOrPackageDataWrapper.Class,
            typeTable: TypeTable,
            acc: List<ClassOrPackageDataWrapper>): List<ClassOrPackageDataWrapper> {
        val interfaces = current.classProto.supertypes(typeTable).map {
            it.extractFullName(current, failOnGeneric = false)
        }.filter {
                    it != "`arrow`.`TC`"
                }
        return when {
            interfaces.isEmpty() -> acc
            else -> {
                interfaces.flatMap { i ->
                    val className = i.removeBackticks().substringBefore("<")
                    val typeClassElement = elementUtils.getTypeElement(className)
                    val parentInterface = getClassOrPackageDataWrapper(typeClassElement)
                    val newAcc = acc + parentInterface
                    recurseTypeclassInterfaces(parentInterface as ClassOrPackageDataWrapper.Class, typeTable, newAcc)
                }
            }
        }
    }
}

fun String.removeBackticks() = this.replace("`", "")

fun knownError(message: String, element: Element? = null): Nothing = throw KnownException(message, element)

val ProtoBuf.Class.Kind.isCompanionOrObject
    get() = when (this) {
        ProtoBuf.Class.Kind.OBJECT,
        ProtoBuf.Class.Kind.COMPANION_OBJECT -> true
        else -> false
    }

val ProtoBuf.Class.isSealed
    get() = modality == ProtoBuf.Modality.SEALED

val ClassOrPackageDataWrapper.Class.fullName: String
    get() = nameResolver.getName(classProto.fqName).asString()

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
                throwOnGeneric = if (!failOnGeneric) null else KnownException("Generic implicit types are not yet supported", null)
        )

fun ClassOrPackageDataWrapper.typeConstraints(): String =
        typeParameters.flatMap { typeParameter ->
            val name = nameResolver.getString(typeParameter.name)
            typeParameter.upperBoundList.map { constraint ->
                name to constraint
                        .extractFullName(this, failOnGeneric = false)
                        .removeBackticks()
            }
        }.let { constraints ->
                    if (constraints.isNotEmpty()) {
                        constraints.joinToString(
                                prefix = " where ",
                                separator = ", ",
                                transform = { (a, b) -> "$a : $b" }
                        )
                    } else {
                        ""
                    }
                }

