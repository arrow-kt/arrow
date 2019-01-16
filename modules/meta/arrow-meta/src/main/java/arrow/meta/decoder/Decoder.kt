package arrow.meta.decoder

import arrow.meta.ast.Annotation
import arrow.meta.ast.Code
import arrow.meta.ast.Func
import arrow.meta.ast.Modifier
import arrow.meta.ast.Parameter
import arrow.meta.ast.Property
import arrow.meta.ast.Tree
import arrow.meta.ast.Type
import arrow.meta.ast.TypeName
import arrow.meta.ast.UseSiteTarget
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.LambdaTypeName
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.TypeVariableName
import com.squareup.kotlinpoet.WildcardTypeName

/**
 * Provides ways to go from a [Tree] to [Code] for the purposes of code gen and reporting
 */
interface MetaDecoder<in A : Tree> {
  fun decode(tree: A): Code
}

/**
 * Type decoder that leverages Kotlin Poet to organize imports and output formatted code
 */
interface TypeDecoder : MetaDecoder<Type> {

  override fun decode(tree: Type): Code =
    Code(tree.lyrics().toString())

  fun Type.lyrics(): TypeSpec {
    val className = (name as TypeName.Classy).lyrics()
    val builder = when (kind) {
      Type.Shape.Class -> TypeSpec.classBuilder(className)
      Type.Shape.Interface -> TypeSpec.interfaceBuilder(className)
      Type.Shape.Object -> TypeSpec.objectBuilder(className)
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
    val className = if (type is TypeName.Classy) ClassName(type.pckg.value, type.simpleName) else ClassName.bestGuess(type.toString())
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

  @Suppress("ComplexMethod")
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
      name == "*" -> WildcardTypeName.STAR
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

  fun TypeName.FunctionLiteral.lyrics(): com.squareup.kotlinpoet.TypeName {
    val lambdaName = LambdaTypeName.get(
      receiver = receiverType?.lyrics(),
      parameters = *parameters.map { it.lyrics() }.toTypedArray(),
      returnType = returnType.lyrics())
    return if (modifiers.contains(Modifier.Suspend))
      lambdaName.asSuspending()
    else
      lambdaName
  }

  fun TypeName.Classy.lyrics(): ClassName =
    ClassName(packageName = pckg.value, simpleName = simpleName)

  fun TypeName.lyrics(): com.squareup.kotlinpoet.TypeName =
    when (this) {
      is TypeName.TypeVariable -> lyrics()
      is TypeName.WildcardType -> lyrics()
      is TypeName.ParameterizedType -> lyrics()
      is TypeName.Classy -> lyrics()
      is TypeName.FunctionLiteral -> lyrics()
    }

  operator fun Code.Companion.invoke(f: () -> String): Code =
    Code(CodeBlock.of(f().trimMargin()).toString())

  operator fun TypeName?.unaryPlus(): Code =
    if (this != null) Code(CodeBlock.of("%T", this.lyrics()).toString())
    else Code("")

  operator fun String.unaryPlus(): Code =
    Code(CodeBlock.of("%N", this).toString())

  operator fun List<String>.unaryPlus(): Code =
    Code(joinToCode("%N").toString())

  fun Iterable<Parameter>.codeNames(): Code =
    code {
      if (it.modifiers.contains(Modifier.VarArg)) Code("*${it.name}")
      else Code(it.name)
    }

  fun Iterable<Parameter>.code(f: (Parameter) -> Code = { Code(it.lyrics().toString()) }): Code {
    val list = toList()
    return if (list.isEmpty()) Code.empty
    else Code(list.joinToString(", ") {
      f(it).toString()
    })
  }

  fun Iterable<Func>.code(dummy: Unit = Unit): Code {
    val list = toList()
    return if (list.isEmpty()) Code.empty
    else Code(list.joinToString("\n\n  ") {
      it.lyrics().toString()
    })
  }

  operator fun Iterable<TypeName.TypeVariable>.unaryPlus(): Code {
    val list = toList()
    return if (list.isEmpty()) Code.empty
    else Code("<${list.joinToCode("%T")}>")
  }

  fun <A : Any> List<A>.joinToCode(separator: String): Code =
    if (isEmpty()) Code(CodeBlock.of("").toString())
    else {
      val code = joinToString(", ") { separator }
      val args = map { it.resolveDynamicArg() }.toTypedArray()
      Code((if (args.isEmpty()) CodeBlock.of(code) else CodeBlock.of(code, *args)).toString())
    }

  private fun Any?.resolveDynamicArg(): Any? =
    when (this) {
      is TypeName -> lyrics()
      is Parameter -> lyrics()
      else -> this
    }

}
