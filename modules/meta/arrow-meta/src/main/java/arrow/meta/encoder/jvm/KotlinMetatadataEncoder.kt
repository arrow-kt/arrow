package arrow.meta.encoder.jvm

import arrow.common.utils.ClassOrPackageDataWrapper
import arrow.common.utils.ProcessorUtils
import arrow.common.utils.fullName
import arrow.common.utils.removeBackticks
import arrow.common.utils.simpleName
import arrow.meta.ast.Func
import arrow.meta.ast.Modifier
import arrow.meta.ast.PackageName
import arrow.meta.ast.Parameter
import arrow.meta.ast.TypeName
import me.eugeniomarletti.kotlin.metadata.escapedClassName
import me.eugeniomarletti.kotlin.metadata.jvm.jvmMethodSignature
import me.eugeniomarletti.kotlin.metadata.modality
import me.eugeniomarletti.kotlin.metadata.shadow.metadata.ProtoBuf
import me.eugeniomarletti.kotlin.metadata.shadow.metadata.deserialization.Flags
import me.eugeniomarletti.kotlin.metadata.shadow.metadata.deserialization.NameResolver
import me.eugeniomarletti.kotlin.metadata.shadow.metadata.deserialization.TypeTable
import me.eugeniomarletti.kotlin.metadata.shadow.metadata.deserialization.supertypes
import javax.lang.model.element.ExecutableElement

interface KotlinMetatadataEncoder {

  fun supertypes(
    current: ClassOrPackageDataWrapper.Class,
    typeTable: TypeTable,
    processorUtils: ProcessorUtils,
    acc: List<ClassOrPackageDataWrapper>): List<ClassOrPackageDataWrapper> =
    processorUtils.run {
      val interfaces = current.classProto.supertypes(typeTable).asSequence().map {
        it.extractFullName(current)
      }.filter {
        it != "`kotlin`.`Any`"
      }.toList()
      when {
        interfaces.isEmpty() -> acc
        else -> {
          interfaces.flatMap { i ->
            @Suppress("SwallowedException")
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

  fun modifiersFromFlags(flags: Int): List<Modifier> =
    supportedFlags.filter { it.first.get(flags) }.map { it.second }

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

  fun ProtoBuf.ValueParameter.toMeta(owner: ClassOrPackageDataWrapper.Class): Parameter =
    Parameter(
      name = owner.nameResolver.getString(name),
      type = type.asTypeName(owner)
    )

  fun ProtoBuf.TypeParameter.Variance.toMeta(): Modifier =
    when (this) {
      ProtoBuf.TypeParameter.Variance.IN -> Modifier.InVariance
      ProtoBuf.TypeParameter.Variance.OUT -> Modifier.OutVariance
      ProtoBuf.TypeParameter.Variance.INV -> Modifier.InVariance
    }

  fun ProtoBuf.TypeParameter.toMeta(owner: ClassOrPackageDataWrapper.Class): TypeName.TypeVariable =
    TypeName.TypeVariable(
      name = owner.nameResolver.getString(name),
      bounds = upperBoundList.map { it.asTypeName(owner) },
      variance = if (hasVariance()) variance.toMeta() else null,
      reified = if (hasReified()) reified else false
    )

  fun ProtoBuf.Function.toMeta(owner: ClassOrPackageDataWrapper.Class, executableElement: ExecutableElement): Func =
    Func(
      name = owner.nameResolver.getString(name),
      parameters = valueParameterList.map { it.toMeta(owner) },
      receiverType = if (receiverType != null) receiverType.asTypeName(owner) else null,
      returnType = returnType.asTypeName(owner),
      typeVariables = typeParameterList.map { it.toMeta(owner) },
      modifiers = listOf(modality?.asModifier()).requireNoNulls(),
      jvmMethodSignature = jvmMethodSignature.toString()
    )

}

private val supportedFlags: List<Pair<Flags.BooleanFlagField, Modifier>> =
  listOf(
    Flags.IS_INLINE to Modifier.Inline,
    Flags.IS_INFIX to Modifier.Infix,
    Flags.IS_OPERATOR to Modifier.Operator,
    Flags.IS_SUSPEND to Modifier.Suspend,
    Flags.IS_TAILREC to Modifier.Tailrec
  )