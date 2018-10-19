package arrow.common.utils

import arrow.meta.encoder.jvm.asKotlin
import me.eugeniomarletti.kotlin.metadata.*
import me.eugeniomarletti.kotlin.metadata.jvm.getJvmMethodSignature
import me.eugeniomarletti.kotlin.metadata.shadow.metadata.ProtoBuf
import me.eugeniomarletti.kotlin.metadata.shadow.metadata.deserialization.TypeTable
import me.eugeniomarletti.kotlin.metadata.shadow.metadata.deserialization.supertypes
import me.eugeniomarletti.kotlin.metadata.shadow.serialization.deserialization.getName
import java.io.File
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

  val Element.hasNoCompanion: Boolean
    get() = (kotlinMetadata as? KotlinClassMetadata)?.data?.run {
      nameResolver.getName(proto.companionObjectName).asString() != "Companion"
    } ?: true

  fun KotlinMetadata.asClassOrPackageDataWrapper(classElement: TypeElement): ClassOrPackageDataWrapper? {
    val `package` = elementUtils.getPackageOf(classElement).toString()
    return when (this) {
      is KotlinClassMetadata -> data.asClassOrPackageDataWrapper(`package`)
      is KotlinPackageMetadata -> data.asClassOrPackageDataWrapper(`package`)
      else -> null
    }
  }

  fun getClassOrPackageDataWrapper(classElement: TypeElement): ClassOrPackageDataWrapper {
    val metadata = (
      if (classElement.kotlinMetadata == null)
        elementUtils.getTypeElement(classElement.qualifiedName.toString().asKotlin()).kotlinMetadata
      else classElement.kotlinMetadata
      ) ?: knownError("Arrow's annotations can only be used on Kotlin classes. Not valid for $classElement")

    return metadata.asClassOrPackageDataWrapper(classElement)
      ?: knownError("Arrow's annotation can't be used on $classElement")
  }

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

}

fun String.removeBackticks() = replace("`", "")

fun knownError(message: String, element: Element? = null): Nothing =
  throw KnownException(message, element)

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

fun recurseFilesUpwards(fileNames: Set<String>): File =
  recurseFilesUpwards(fileNames, File(".").absoluteFile)

fun recurseFilesUpwards(fileNames: Set<String>, currentDirectory: File): File {

  val filesInDir = currentDirectory.list()

  return if ((filesInDir.intersect(fileNames)).isNotEmpty()) {
    currentDirectory
  } else {
    recurseFilesUpwards(fileNames, currentDirectory.parentFile)
  }
}
