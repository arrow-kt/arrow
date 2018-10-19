package arrow.meta.encoder.jvm

import arrow.meta.ast.*
import arrow.meta.ast.Annotation
import arrow.meta.ast.TypeName
import arrow.meta.encoder.MetaApi
import com.squareup.kotlinpoet.*
import me.eugeniomarletti.kotlin.metadata.KotlinMetadataUtils
import javax.lang.model.element.ExecutableElement

interface KotlinPoetEncoder {

  fun metaApi(): MetaApi

  fun kotlinMetadataUtils(): KotlinMetadataUtils

  private fun com.squareup.kotlinpoet.CodeBlock.toMeta(): Code =
    Code(this.toString())

  private fun com.squareup.kotlinpoet.AnnotationSpec.UseSiteTarget.toMeta(): UseSiteTarget =
    when (this) {
      AnnotationSpec.UseSiteTarget.FILE -> UseSiteTarget.File
      AnnotationSpec.UseSiteTarget.PROPERTY -> UseSiteTarget.Property
      AnnotationSpec.UseSiteTarget.FIELD -> UseSiteTarget.Field
      AnnotationSpec.UseSiteTarget.GET -> UseSiteTarget.Get
      AnnotationSpec.UseSiteTarget.SET -> UseSiteTarget.Set
      AnnotationSpec.UseSiteTarget.RECEIVER -> UseSiteTarget.Receiver
      AnnotationSpec.UseSiteTarget.PARAM -> UseSiteTarget.Param
      AnnotationSpec.UseSiteTarget.SETPARAM -> UseSiteTarget.SetParam
      AnnotationSpec.UseSiteTarget.DELEGATE -> UseSiteTarget.Delegate
    }

  private fun com.squareup.kotlinpoet.ParameterSpec.toMeta(): Parameter =
    Parameter(
      name = name,
      modifiers = modifiers.map { it.toMeta() },
      annotations = annotations.map { it.toMeta() },
      type = type.toMeta(),
      defaultValue = defaultValue?.toMeta()
    )

  fun com.squareup.kotlinpoet.FunSpec.toMeta(element: ExecutableElement): Func =
    Func(
      name = element.simpleName.toString(),
      annotations = annotations.map { it.toMeta() },
      typeVariables = typeVariables.map { it.toMeta() },
      modifiers = modifiers.map { it.toMeta() },
      returnType = returnType?.toMeta(),
      receiverType = receiverType?.toMeta(),
      kdoc = kdoc.toMeta(),
      body = body.toMeta(),
      parameters = parameters.map { it.toMeta() },
      jvmMethodSignature = kotlinMetadataUtils().run { element.jvmMethodSignature }
    )

  fun com.squareup.kotlinpoet.AnnotationSpec.toMeta(): Annotation =
    Annotation(type.toMeta(), members = members.map { it.toMeta() }, useSiteTarget = useSiteTarget?.toMeta())

  private fun com.squareup.kotlinpoet.WildcardTypeName.toMeta(): TypeName.WildcardType =
    TypeName.WildcardType(
      name = toString().removeVariance().asKotlin(),
      upperBounds = upperBounds.map { it.toMeta() },
      lowerBounds = lowerBounds.map { it.toMeta() },
      nullable = nullable,
      annotations = annotations.map { it.toMeta() }
    )

  private fun com.squareup.kotlinpoet.ClassName.toMeta(): TypeName.Classy =
    TypeName.Classy(
      simpleName = simpleName.asKotlin(),
      fqName = canonicalName.asKotlin(),
      annotations = annotations.map { it.toMeta() },
      nullable = nullable,
      pckg = PackageName(packageName.asKotlin())
    )

  private fun com.squareup.kotlinpoet.ParameterizedTypeName.toMeta(): TypeName =
    TypeName.ParameterizedType(
      name = toString().removeVariance().asKotlin(),
      nullable = nullable,
      annotations = annotations.map { it.toMeta() },
      enclosingType = null,
      rawType = rawType.toMeta(),
      typeArguments = typeArguments.map { it.toMeta() }
    )

  private fun com.squareup.kotlinpoet.KModifier.toMeta(): Modifier =
    when (this) {
      KModifier.PUBLIC -> Modifier.Public
      KModifier.PROTECTED -> Modifier.Protected
      KModifier.PRIVATE -> Modifier.Private
      KModifier.INTERNAL -> Modifier.Internal
      KModifier.EXPECT -> Modifier.Expect
      KModifier.ACTUAL -> Modifier.Actual
      KModifier.FINAL -> Modifier.Final
      KModifier.OPEN -> Modifier.Open
      KModifier.ABSTRACT -> Modifier.Abstract
      KModifier.SEALED -> Modifier.Sealed
      KModifier.CONST -> Modifier.Const
      KModifier.EXTERNAL -> Modifier.External
      KModifier.OVERRIDE -> Modifier.Override
      KModifier.LATEINIT -> Modifier.LateInit
      KModifier.TAILREC -> Modifier.Tailrec
      KModifier.VARARG -> Modifier.VarArg
      KModifier.SUSPEND -> Modifier.Suspend
      KModifier.INNER -> Modifier.Inner
      KModifier.ENUM -> Modifier.Enum
      KModifier.ANNOTATION -> Modifier.Annotation
      KModifier.COMPANION -> Modifier.CompanionObject
      KModifier.INLINE -> Modifier.Inline
      KModifier.NOINLINE -> Modifier.NoInline
      KModifier.CROSSINLINE -> Modifier.CrossInline
      KModifier.REIFIED -> Modifier.Reified
      KModifier.INFIX -> Modifier.Infix
      KModifier.OPERATOR -> Modifier.Operator
      KModifier.DATA -> Modifier.Data
      KModifier.IN -> Modifier.InVariance
      KModifier.OUT -> Modifier.OutVariance
    }

  fun com.squareup.kotlinpoet.TypeVariableName.toMeta(): TypeName.TypeVariable =
    TypeName.TypeVariable(
      name = name.asKotlin(),
      bounds = bounds.map
      { metaApi().run { it.toMeta().removeConstrains() } },
      annotations = annotations.map
      { it.toMeta() },
      nullable = nullable,
      reified = reified,
      variance = variance?.toMeta()
    )

  val typeNameToMeta: (typeName: com.squareup.kotlinpoet.TypeName) -> TypeName

  fun com.squareup.kotlinpoet.TypeName.toMeta(): TypeName =
    typeNameToMeta(this)

  fun typeNameToMetaImpl(typeName: com.squareup.kotlinpoet.TypeName): TypeName =
    when (typeName) {
      is ClassName -> typeName.toMeta()
      is ParameterizedTypeName -> typeName.toMeta()
      is TypeVariableName -> typeName.toMeta()
      is WildcardTypeName -> typeName.toMeta()
      else -> throw IllegalArgumentException("arrow-meta has no bindings for unsupported type name: $typeName")
    }
}