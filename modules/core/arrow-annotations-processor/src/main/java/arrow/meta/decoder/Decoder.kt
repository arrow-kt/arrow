package arrow.meta.decoder

import arrow.meta.ast.*
import arrow.meta.ast.Annotation
import arrow.meta.ast.TypeName
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.plusParameter

interface MetaDecoder<in A : Tree> {
  fun decode(tree: A): Code
}

interface TypeDecoder : MetaDecoder<Type> {

  override fun decode(tree: Type): Code =
    Code(tree.lyrics().toString())

  fun Type.lyrics(): TypeSpec {
    val className = name.toString()
    val builder = when (kind) {
      Type.Kind.Class -> TypeSpec.classBuilder(className)
      Type.Kind.Interface -> TypeSpec.interfaceBuilder(className)
      Type.Kind.Object -> TypeSpec.objectBuilder(className)
    }
    val enumConstantBuilder = enumConstants.keys.fold(builder) { b, key ->
      val typeSpec = enumConstants[key]?.lyrics()
      if (typeSpec != null) b.addEnumConstant(key, typeSpec)
      else b
    }
    val superclassConstructorParametersBuilder = superclassConstructorParameters.fold(enumConstantBuilder) { b, param ->
      b.addSuperclassConstructorParameter(param.lyrics())
    }
    val superClassBuilder =
      if (superclass == null) superclassConstructorParametersBuilder
      else superclassConstructorParametersBuilder.superclass(superclass.lyrics())
    val kDocBuilder =
      if (kdoc == null) superClassBuilder
      else superClassBuilder.addKdoc(kdoc.lyrics())
    return kDocBuilder
      .addFunctions(declaredFunctions.map { it.lyrics() })
      .addAnnotations(annotations.map { it.lyrics() })
      .addSuperinterfaces(superInterfaces.map { it.lyrics() })
      .addTypeVariables(typeVariables.map { it.lyrics() })
      .addProperties(properties.map { it.lyrics() })
      .addModifiers(*modifiers.map { it.lyrics() }.toTypedArray())
      .build()
  }

  fun CodeBlock.Companion.empty(): CodeBlock =
    CodeBlock.builder().build()

  fun Code.lyrics(): CodeBlock =
    CodeBlock.of(value)

  fun Parameter.lyrics(): ParameterSpec {
    val builder = ParameterSpec.builder(
      name = name,
      type = type.lyrics(),
      modifiers = *modifiers.map { it.lyrics() }.toTypedArray()
    )
    val builderDefaultValue = if (defaultValue != null) builder.defaultValue(defaultValue.lyrics()) else builder
    return builderDefaultValue.build()
  }

  fun UseSiteTarget.lyrics(): AnnotationSpec.UseSiteTarget =
    when (this) {
      UseSiteTarget.File -> AnnotationSpec.UseSiteTarget.FILE
      UseSiteTarget.Property -> AnnotationSpec.UseSiteTarget.PROPERTY
      UseSiteTarget.Field -> AnnotationSpec.UseSiteTarget.FIELD
      UseSiteTarget.Get -> AnnotationSpec.UseSiteTarget.GET
      UseSiteTarget.Set -> AnnotationSpec.UseSiteTarget.SET
      UseSiteTarget.Receiver -> AnnotationSpec.UseSiteTarget.RECEIVER
      UseSiteTarget.Param -> AnnotationSpec.UseSiteTarget.PARAM
      UseSiteTarget.SetParam -> AnnotationSpec.UseSiteTarget.SETPARAM
      UseSiteTarget.Delegate -> AnnotationSpec.UseSiteTarget.DELEGATE
    }

  fun Annotation.lyrics(): AnnotationSpec {
    val className = ClassName.bestGuess(type.toString())
    val builder = AnnotationSpec.builder(className).useSiteTarget(useSiteTarget?.lyrics())
    return members.fold(builder) { b, member ->
      b.addMember(member.lyrics())
    }.build()
  }

  fun Property.lyrics(): PropertySpec {
    val builder = PropertySpec.builder(name, type.lyrics())
      .addAnnotations(annotations.map { it.lyrics() })
      .addKdoc(kdoc?.lyrics() ?: CodeBlock.empty())
      .addModifiers(*modifiers.map { it.lyrics() }.toTypedArray())
    val builderWithReceiver =
      if (receiverType != null) builder.receiver(receiverType.lyrics())
      else builder
    return builderWithReceiver.build()
  }

  fun Func.lyrics(): FunSpec {
    val builder = FunSpec.builder(name)
      .addAnnotations(annotations.map { it.lyrics() })
      .addCode(body?.lyrics() ?: CodeBlock.empty())
      .addKdoc(kdoc?.lyrics() ?: CodeBlock.empty())
      .addModifiers(modifiers.map { it.lyrics() })
      .addParameters(parameters.map { it.lyrics() })
      .addTypeVariables(typeVariables.map { it.lyrics() })
    val builderWithReceiver =
      if (receiverType != null) builder.receiver(receiverType.lyrics())
      else builder
    val builderWithReturn =
      if (returnType != null) builderWithReceiver.returns(returnType.lyrics())
      else builderWithReceiver
    return builderWithReturn.build()
  }

  fun Modifier.lyrics(): KModifier =
    when (this) {
      Modifier.Public -> KModifier.PUBLIC
      Modifier.Protected -> KModifier.PROTECTED
      Modifier.Private -> KModifier.PRIVATE
      Modifier.Internal -> KModifier.INTERNAL
      Modifier.Expect -> KModifier.EXPECT
      Modifier.Actual -> KModifier.ACTUAL
      Modifier.Final -> KModifier.FINAL
      Modifier.Open -> KModifier.OPEN
      Modifier.Abstract -> KModifier.ABSTRACT
      Modifier.Sealed -> KModifier.SEALED
      Modifier.Const -> KModifier.CONST
      Modifier.External -> KModifier.EXTERNAL
      Modifier.Override -> KModifier.OVERRIDE
      Modifier.LateInit -> KModifier.LATEINIT
      Modifier.Tailrec -> KModifier.TAILREC
      Modifier.Suspend -> KModifier.SUSPEND
      Modifier.Inner -> KModifier.INNER
      Modifier.Enum -> KModifier.ENUM
      Modifier.Annotation -> KModifier.ANNOTATION
      Modifier.CompanionObject -> KModifier.COMPANION
      Modifier.Inline -> KModifier.INLINE
      Modifier.NoInline -> KModifier.NOINLINE
      Modifier.CrossInline -> KModifier.CROSSINLINE
      Modifier.Reified -> KModifier.REIFIED
      Modifier.Infix -> KModifier.INFIX
      Modifier.Operator -> KModifier.OPERATOR
      Modifier.Data -> KModifier.DATA
      Modifier.InVariance -> KModifier.IN
      Modifier.OutVariance -> KModifier.OUT
      Modifier.VarArg -> KModifier.VARARG
    }

  fun TypeName.TypeVariable.lyrics(): TypeVariableName {
    val name = TypeVariableName(name, variance?.lyrics())
      .withBounds(bounds.map { it.lyrics() })
      .reified(reified)
    return if (nullable) name.asNullable()
    else name.asNonNullable()
  }

  fun TypeName.WildcardType.lyrics(): com.squareup.kotlinpoet.TypeName =
    when {
      lowerBounds.isNotEmpty() -> lowerBounds[0].lyrics()
      upperBounds.isNotEmpty() -> upperBounds[0].lyrics()
      else -> WildcardTypeName.STAR
    }

  private fun String.removeVariance(): String =
    replace("out ", "").replace("in ", "")

  fun TypeName.ParameterizedType.lyrics(): com.squareup.kotlinpoet.TypeName =
    ParameterizedTypeName.run {
      val className = rawType.lyrics()
      className.parameterizedBy(*typeArguments.map { it.lyrics() }.toTypedArray())
    }

  fun TypeName.Classy.lyrics(): ClassName =
    ClassName(packageName = pckg.value, simpleName = simpleName)

  fun TypeName.lyrics(): com.squareup.kotlinpoet.TypeName =
    when (this) {
      is TypeName.TypeVariable -> lyrics()
      is TypeName.WildcardType -> lyrics()
      is TypeName.ParameterizedType -> lyrics()
      is TypeName.Classy -> lyrics()
    }
}
