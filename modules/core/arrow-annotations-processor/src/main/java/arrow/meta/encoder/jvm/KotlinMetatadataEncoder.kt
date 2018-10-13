package arrow.meta.encoder.jvm

import arrow.common.utils.*
import arrow.meta.ast.Modifier
import arrow.meta.ast.PackageName
import arrow.meta.ast.TypeName
import me.eugeniomarletti.kotlin.metadata.escapedClassName
import me.eugeniomarletti.kotlin.metadata.shadow.metadata.ProtoBuf
import me.eugeniomarletti.kotlin.metadata.shadow.metadata.deserialization.NameResolver
import me.eugeniomarletti.kotlin.metadata.shadow.metadata.deserialization.TypeTable
import me.eugeniomarletti.kotlin.metadata.shadow.metadata.deserialization.supertypes

interface KotlinMetatadataEncoder {

  fun supertypes(
    current: ClassOrPackageDataWrapper.Class,
    typeTable: TypeTable,
    processorUtils: ProcessorUtils,
    acc: List<ClassOrPackageDataWrapper>): List<ClassOrPackageDataWrapper> =
    processorUtils.run {
    val interfaces = current.classProto.supertypes(typeTable).map {
      it.extractFullName(current)
    }.filter {
      it != "`kotlin`.`Any`"
    }
    when {
      interfaces.isEmpty() -> acc
      else -> {
        interfaces.flatMap { i ->
          try {
            val className = i.removeBackticks().substringBefore("<")
            val typeClassElement = processorUtils.elementUtils.getTypeElement(className)
            val parentInterface = getClassOrPackageDataWrapper(typeClassElement)
            val newAcc = acc + parentInterface
            supertypes(parentInterface as ClassOrPackageDataWrapper.Class, typeTable, processorUtils, newAcc)
          } catch (_: Throwable) {
            emptyList<ClassOrPackageDataWrapper>()
          }
        }
      }
    }
  }

  fun ProtoBuf.Visibility.asModifier(): Modifier? =
    when (this) {
      ProtoBuf.Visibility.INTERNAL -> Modifier.Internal
      ProtoBuf.Visibility.PRIVATE -> Modifier.Private
      ProtoBuf.Visibility.PROTECTED -> Modifier.Protected
      ProtoBuf.Visibility.PUBLIC -> Modifier.Public
      ProtoBuf.Visibility.PRIVATE_TO_THIS -> Modifier.Private
      ProtoBuf.Visibility.LOCAL -> null
    }

  fun ProtoBuf.Modality.asModifier(): Modifier =
    when (this) {
      ProtoBuf.Modality.FINAL -> Modifier.Final
      ProtoBuf.Modality.OPEN -> Modifier.Open
      ProtoBuf.Modality.ABSTRACT -> Modifier.Abstract
      ProtoBuf.Modality.SEALED -> Modifier.Sealed
    }

  fun ProtoBuf.Type.extractFullName(
    classData: ClassOrPackageDataWrapper,
    outputTypeAlias: Boolean = true
  ): String =
    extractFullName(
      nameResolver = classData.nameResolver,
      getTypeParameter = { classData.getTypeParameter(it) },
      outputTypeAlias = outputTypeAlias,
      throwOnGeneric = null
    )

  fun ProtoBuf.Type.asTypeName(meta: ClassOrPackageDataWrapper.Class): TypeName {
    val fullName = extractFullName(meta).asKotlin()
    val pck = fullName.substringBefore("<").substringBeforeLast(".")
    val simpleName = fullName.substringBefore("<").substringAfterLast(".")
    return if (argumentList.isEmpty()) TypeName.Classy(
      simpleName = simpleName,
      fqName = fullName,
      pckg = PackageName(pck),
      nullable = nullable
    )
    else TypeName.ParameterizedType(
      name = fullName.removeVariance(),
      rawType = TypeName.Classy(simpleName, "$pck.$simpleName".asKotlin(), PackageName(pck.asKotlin())),
      typeArguments = argumentList.map { TypeName.TypeVariable(it.type.extractFullName(meta).asKotlin()) },
      enclosingType = TypeName.Classy(
        simpleName = meta.simpleName.asKotlin(),
        fqName = meta.fullName.asKotlin(),
        pckg = PackageName(meta.`package`.asKotlin()),
        nullable = false
      )
    )
  }

  fun ProtoBuf.Modality.toMeta(): Modifier =
    when (this) {
      ProtoBuf.Modality.FINAL -> Modifier.Final
      ProtoBuf.Modality.OPEN -> Modifier.Open
      ProtoBuf.Modality.ABSTRACT -> Modifier.Abstract
      ProtoBuf.Modality.SEALED -> Modifier.Sealed
    }

  fun ClassOrPackageDataWrapper.Class.nameOf(id: Int): String =
    nameResolver.getString(id)

  private fun ProtoBuf.Type.extractFullName(
    nameResolver: NameResolver,
    getTypeParameter: (index: Int) -> ProtoBuf.TypeParameter?,
    outputTypeAlias: Boolean = true,
    throwOnGeneric: Throwable? = null
  ): String {

    if (!hasClassName() && throwOnGeneric != null) throw throwOnGeneric

    val typeParam = getTypeParameter(typeParameter)

    val name = when {
      hasTypeParameter() && typeParam != null -> typeParam.name
      hasTypeParameterName() -> typeParameterName
      outputTypeAlias && hasAbbreviatedType() -> abbreviatedType.typeAliasName
      else -> className
    }.let { nameResolver.getString(it).escapedClassName }

    val argumentList = when {
      outputTypeAlias && hasAbbreviatedType() -> abbreviatedType.argumentList
      else -> argumentList
    }
    val arguments = argumentList
      .takeIf { it.isNotEmpty() }
      ?.joinToString(prefix = "<", postfix = ">") {
        when {
          it.hasType() -> it.type.extractFullName(nameResolver, getTypeParameter, outputTypeAlias, throwOnGeneric)
          throwOnGeneric != null -> throw throwOnGeneric
          else -> "*"
        }
      } ?: ""
    val nullability = if (nullable) "?" else ""
    return name + arguments + nullability
  }

}