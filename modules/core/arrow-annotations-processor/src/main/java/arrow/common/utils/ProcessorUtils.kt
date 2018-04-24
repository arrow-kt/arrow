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

  fun Element.getConstructorParamNames(): List<String> = kotlinMetadata
    .let { it as KotlinClassMetadata }.data
    .let { (nameResolver, classProto) ->
      classProto.constructorOrBuilderList
        .first()
        .valueParameterList
        .map(ProtoBuf.ValueParameter::getName)
        .map(nameResolver::getString)
    }

  fun Element.getClassData(): ClassOrPackageDataWrapper.Class = kotlinMetadata
    .let { it as KotlinClassMetadata }
    .data
    .asClassOrPackageDataWrapper(elementUtils.getPackageOf(this).toString())

  fun Element.getConstructorTypesNames(): List<String> = kotlinMetadata
    .let { it as KotlinClassMetadata }.data
    .let { data ->
      data.proto.constructorOrBuilderList
        .first()
        .valueParameterList
        .map { it.type.extractFullName(data) }
    }

  fun KotlinMetadata.asClassOrPackageDataWrapper(classElement: TypeElement): ClassOrPackageDataWrapper? {
    val `package` = elementUtils.getPackageOf(classElement).toString()
    return when (this) {
      is KotlinClassMetadata -> data.asClassOrPackageDataWrapper(`package`)
      is KotlinPackageMetadata -> data.asClassOrPackageDataWrapper(`package`)
      else -> null
    }
  }

  fun getClassOrPackageDataWrapper(classElement: TypeElement): ClassOrPackageDataWrapper {
    val metadata = classElement.kotlinMetadata
      ?: knownError("Arrow's annotations can only be used on Kotlin classes. Not valid for $classElement")

    return metadata.asClassOrPackageDataWrapper(classElement)
      ?: knownError("Arrow's annotation can't be used on $classElement")
  }

  fun TypeElement.methods(): List<MethodElement> =
    enclosedElements.mapNotNull { it as? MethodElement }

  fun ClassOrPackageDataWrapper.getFunction(methodElement: ExecutableElement) =
    getFunctionOrNull(methodElement, nameResolver, functionList)
      ?: knownError("Can't find annotated method ${methodElement.jvmMethodSignature}")

  private fun kindedRex() = "(?i)Kind<(.)>".toRegex()

  fun ProtoBuf.Function.overrides(o: ProtoBuf.Function): Boolean = false

  fun ClassOrPackageDataWrapper.Class.declaredTypeClassInterfaces(
    typeTable: TypeTable): List<ClassOrPackageDataWrapper> {
    val interfaces = this.classProto.supertypes(typeTable).map {
      it.extractFullName(this)
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
      it.extractFullName(current)
    }.filter {
      it != "`kotlin`.`Any`"
    }
    return when {
      interfaces.isEmpty() -> acc
      else -> {
        interfaces.flatMap { i ->
          try {
            val className = i.removeBackticks().substringBefore("<")
            val typeClassElement = elementUtils.getTypeElement(className)
            val parentInterface = getClassOrPackageDataWrapper(typeClassElement)
            val newAcc = acc + parentInterface
            recurseTypeclassInterfaces(parentInterface as ClassOrPackageDataWrapper.Class, typeTable, newAcc)
          } catch (_: Throwable) {
            emptyList<ClassOrPackageDataWrapper>()
          }
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

val ClassOrPackageDataWrapper.Class.simpleName: String
  get() = fullName.substringAfterLast("/")

fun ClassOrPackageDataWrapper.getParameter(function: ProtoBuf.Function, parameterElement: VariableElement) =
  getValueParameterOrNull(nameResolver, function, parameterElement)
    ?: knownError("Can't find annotated parameter ${parameterElement.simpleName} in ${function.getJvmMethodSignature(nameResolver)}")

fun ClassOrPackageDataWrapper.getPropertyOrNull(methodElement: ExecutableElement) =
  getPropertyOrNull(methodElement, nameResolver, this::propertyList)

fun ProtoBuf.Type.extractFullName(
  classData: ClassOrPackageDataWrapper,
  outputTypeAlias: Boolean = true
): String =
  extractFullName(
    nameResolver = classData.nameResolver,
    getTypeParameter = { classData.getTypeParameter(it)!! },
    outputTypeAlias = outputTypeAlias,
    throwOnGeneric = null
  )

fun ClassOrPackageDataWrapper.typeConstraints(): String =
  typeParameters.flatMap { typeParameter ->
    val name = nameResolver.getString(typeParameter.name)
    typeParameter.upperBoundList.map { constraint ->
      name to constraint
        .extractFullName(this)
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

