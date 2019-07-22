package arrow.core.extensions

import arrow.common.messager.log
import arrow.common.utils.knownError
import arrow.extension
import arrow.meta.ast.Code
import arrow.meta.ast.Func
import arrow.meta.ast.Modifier
import arrow.meta.ast.PackageName
import arrow.meta.ast.Property
import arrow.meta.ast.Type
import arrow.meta.ast.TypeName
import arrow.meta.encoder.TypeClassInstance
import arrow.meta.processor.MetaProcessor
import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.FileSpec
import javax.annotation.processing.Processor
import javax.lang.model.element.TypeElement

@AutoService(Processor::class)
class ExtensionProcessor : MetaProcessor<extension>(extension::class), PolyTemplateGenerator {

  override fun transform(annotatedElement: AnnotatedElement): List<FileSpec.Builder> =
    when (annotatedElement) {
      is AnnotatedElement.Interface -> {
        val info = annotatedElement.typeElement.typeClassInstance()
        log("[${info?.instance?.name?.simpleName}] : Generating [${info?.typeClass?.name?.simpleName}] extensions for [${info?.projectedCompanion}]")
        val fileSpec = annotatedElement.fileSpecBuilder(info)
        info.processTypeClassExtensions(fileSpec, annotatedElement)
      }
      else -> notAnInstanceError()
    }

  private fun TypeClassInstance?.processTypeClassExtensions(
    fileSpec: FileSpec.Builder?,
    annotatedElement: AnnotatedElement.Interface
  ): List<FileSpec.Builder> =
    when {
      this != null && fileSpec != null -> {
        val wrappedType = dataType.kindWrapper
        val wrappedExtensions = wrappedTypeExtensions(wrappedType)
        val properties = if (supportsCache()) listOf(genCachedExtension()) else emptyList()
        val functions = genDataTypeExtensions() + listOf(genCompanionFactory(dataType.name))
        val mainFileSpec: FileSpec.Builder = functions.inMainFileSpec(fileSpec.addProperties(properties))
        val wrappedFileSpec = wrappedExtensions.inWrappedFileSpec(wrappedType, annotatedElement, this, properties)
        listOfNotNull(mainFileSpec, wrappedFileSpec)
      }
      else -> emptyList()
    }

  private fun List<Func>.inWrappedFileSpec(
    wrappedType: Pair<TypeName, TypeName.ParameterizedType>?,
    annotatedElement: AnnotatedElement.Interface,
    info: TypeClassInstance,
    properties: List<Property>
  ): FileSpec.Builder? =
    if (wrappedType != null && isNotEmpty()) {
      val wrappedFileBuilder = info.wrappedFileBuilder(annotatedElement, wrappedType)
      val wrappedFunctionSpec = inMainFileSpec(wrappedFileBuilder).addProperties(properties)
      val wrappedSimpleName = wrappedType.second.rawType.simpleName
      val wrappedPackage = PackageName("${info.instance.packageName.value}.${wrappedSimpleName.toLowerCase()}")
      wrappedType.fakeCompanion(wrappedFunctionSpec, wrappedSimpleName, wrappedPackage, info, annotatedElement)
    } else null

  private fun Pair<TypeName, TypeName.ParameterizedType>.fakeCompanion(
    wrappedFunctionSpec: FileSpec.Builder,
    wrappedSimpleName: String,
    wrappedPackage: PackageName,
    info: TypeClassInstance,
    annotatedElement: AnnotatedElement.Interface
  ): FileSpec.Builder =
    wrappedFunctionSpec.addType(
      Type(
        name = TypeName.Classy(
          wrappedSimpleName,
          "${wrappedPackage.value}.${wrappedSimpleName.toLowerCase()}",
          PackageName("${info.instance.packageName.value}.${info.typeClass.name.simpleName.decapitalize()}.${wrappedSimpleName.toLowerCase()}")
        ),
        kind = Type.Shape.Object,
        packageName = annotatedElement.type.packageName,
        declaredFunctions = listOf(info.genCompanionFactory(second.rawType).copy(receiverType = null))
      ).lyrics()
    )

  private fun TypeClassInstance.wrappedFileBuilder(annotatedElement: AnnotatedElement.Interface, wrappedType: Pair<TypeName, TypeName.ParameterizedType>): FileSpec.Builder =
    FileSpec.builder(
      packageName = annotatedElement.type.packageName.value + "." +
        wrappedType.second.rawType.simpleName.substringAfterLast(".").toLowerCase() + "." +
        typeClass.name.simpleName.decapitalize(),
      fileName = annotatedElement.type.name.simpleName
    )

  private fun List<Func>.inMainFileSpec(fileSpec: FileSpec.Builder): FileSpec.Builder =
    fold(fileSpec) { spec, func ->
      spec.addFunction(func.lyrics().toBuilder().build())
    }

  private fun FileSpec.Builder.addProperties(properties: List<Property>): FileSpec.Builder =
    properties.fold(this) { spec, prop -> spec.addProperty(prop.lyrics().toBuilder().build()) }

  private fun TypeClassInstance.wrappedTypeExtensions(wrappedType: Pair<TypeName, TypeName.ParameterizedType>?): List<Func> =
    if (wrappedType != null) {
      val wrappedTypeElement = elementUtils.getTypeElement(wrappedType.second.rawType.asPlatform().fqName)
      val wrappedTypeFunctions = wrappedTypeElement.wrappedTypeFunctions()
      wrappedTypeExtensions(wrappedType, wrappedTypeFunctions)
    } else emptyList()

  private fun TypeClassInstance.wrappedTypeExtensions(
    wrappedType: Pair<TypeName, TypeName.ParameterizedType>?,
    wrappedTypeFunctions: List<String>
  ): List<Func> =
    genDataTypeExtensions(wrappedType)
      .asSequence()
      .filter { ext -> ext.name !in wrappedTypeFunctions }
      .toList()

  /**
   * Not all wrapped types can be introspected as TypeElement
   */
  private fun TypeElement?.wrappedTypeFunctions(): List<String> =
    @Suppress("SwallowedException")
    try {
      elementUtils.getAllMembers(this).map { it.simpleName.toString() }
    } catch (e: Exception) {
      emptyList()
    }

  private fun AnnotatedElement.Interface.fileSpecBuilder(info: TypeClassInstance?): FileSpec.Builder? =
    info?.let {
      FileSpec.builder(
        type.packageName.value +
          "." + info.projectedCompanion.simpleName.substringAfterLast(".").toLowerCase() +
          "." + info.typeClass.name.simpleName.decapitalize(),
        type.name.simpleName
      ).indent("  ")
    }

  private fun notAnInstanceError(): Nothing =
    knownError("@instance is only allowed on `interface` extending another interface of at least one type argument (type class) as first declaration in the instance list")

  private fun TypeClassInstance.supportsCache(): Boolean =
    requiredAbstractFunctions.isEmpty()

  private fun TypeClassInstance.cachedInstanceName(): String =
    typeClass.name.simpleName.decapitalize() + "_singleton"

  private fun TypeClassInstance.genCachedExtension(): Property =
    Property(
      kdoc = Code { "cached extension" },
      name = cachedInstanceName(),
      annotations = listOf(PublishedApi()),
      modifiers = listOf(Modifier.Internal),
      type = instance.name.widenTypeArgs(),
      initializer = Code {
        if (instance.typeVariables.isEmpty()) "object : ${+instance.name} {}"
        else "object : ${+instance.name.simpleName}<${instance.typeVariables.joinToString { it.widenTypeArgs().rawName }}> {}"
      }
    )

  private fun TypeClassInstance.genCompanionFactory(targetType: TypeName): Func {
    val target = when (projectedCompanion) {
      is TypeName.Classy -> projectedCompanion.companion()
      else -> TypeName.Classy(
        simpleName = "Companion",
        fqName = "${targetType.rawName}.Companion",
        pckg = PackageName(targetType.rawName.substringBeforeLast(".") + "." + targetType.downKind.simpleName)
      )
    }
    return Func(
      kdoc = typeClass.kdoc?.eval(this),
      modifiers = listOf(Modifier.Inline),
      annotations = listOf(
        SuppressAnnotation(
          """"UNCHECKED_CAST"""",
          """"NOTHING_TO_INLINE""""
        )
      ),
      name = typeClass.name.simpleName.decapitalize(),
      parameters = requiredParameters,
      receiverType = target,
      typeVariables = instance.typeVariables.map { it.removeConstrains() },
      returnType = instance.name,
      body = Code {
        if (supportsCache() && instance.typeVariables.isEmpty()) "return ${cachedInstanceName()}"
        else if (supportsCache() && instance.typeVariables.isNotEmpty()) "return ${cachedInstanceName()} as ${+instance.name}"
        else "return object : ${+instance.name} { ${requiredAbstractFunctions.code()} }"
      }
    )
  }

  private fun TypeClassInstance.genDataTypeExtensions(wrappedType: Pair<TypeName, TypeName.ParameterizedType>? = null): List<Func> {
    val extensionSet = typeClass.declaredFunctions.map { it.jvmMethodSignature }
    return instance
      .allFunctions
      .asSequence()
      .filter { extensionSet.contains(it.jvmMethodSignature) }
      .filterNot { it.receiverType?.simpleName == "MonadContinuation" }
      .distinctBy { func ->
        func.name + func.parameters.asSequence()
          .filterNot { it.type is TypeName.TypeVariable }
          .map { p -> p.type.simpleName }.toList()
      }
      .map { it.removeConstrains(keepModifiers = setOf(Modifier.Infix, Modifier.Operator, Modifier.Suspend)) }
      .map { f ->
        val func = f.removeDummyArgs().downKindReturnType().wrap(wrappedType)
        val dummyArgsCount = f.countDummyArgs()
        val allArgs = requiredParameters + func.parameters
        val typeVariables = extensionTypeVariables(func)
        func
          .copy(
            kdoc = func.kdoc?.eval(this),
            modifiers =
            func.modifiers.filter { it == Modifier.Suspend } +
              when {
                allArgs.size > 1 -> emptyList()
                func.receiverType == null -> emptyList()
                else -> func.modifiers
              }, // remove infix and operator mods
            typeVariables = typeVariables,
            annotations = listOf(
              JvmName(func.name + if (dummyArgsCount > 0) dummyArgsCount else ""),
              SuppressAnnotation(
                """"UNCHECKED_CAST"""",
                """"USELESS_CAST"""",
                """"EXTENSION_SHADOWED_BY_MEMBER"""",
                """"UNUSED_PARAMETER""""
              )
            ),
            parameters = allArgs,
            body = Code {
              val receiverType = func.receiverType
              val wrappedArgs =
                if (wrappedType != null) {
                  func.parameters.code {
                    if (it.type.rawName == wrappedType.second.rawName) Code("${dataType.name.rawName}(${it.name})")
                    else Code(it.name)
                  }
                } else func.parameters.codeNames()
              // Const is a special case because it uses a phantom type arg and the Kotlin compiler chooses to
              // recursively call the extension if you ascribe the call types
              val typeVars =
                if (instance.name.simpleName == "ConstFunctor") emptyList()
                else func.typeVariables
              val companionOrFactory = extensionsCompanionOrFactory(wrappedType)
              if (receiverType != null) {
                val impl = receiverTypeExtensionImpl(wrappedType, receiverType, func, wrappedArgs, typeVars)
                receiverTypeExtensionCode(companionOrFactory, impl)
              } else {
                staticExtensionImpl(companionOrFactory, func, typeVars, wrappedArgs)
              }
            })
      }
      .toList()
  }

  private fun TypeClassInstance.extensionTypeVariables(func: Func): List<TypeName.TypeVariable> = (instance.typeVariables + func.typeVariables)
    .asSequence()
    .map { it.removeConstrains() }
    .distinctBy { it.name }
    .toList()

  private fun TypeClassInstance.staticExtensionImpl(
    companionOrFactory: TypeName,
    func: Func,
    typeVars: List<TypeName.TypeVariable>,
    wrappedArgs: Code
  ): String =
    """|return ${+companionOrFactory}
           |   .${typeClass.name.simpleName.decapitalize()}${+instance.typeVariables}(${requiredParameters.code { +it.name }})
           |   .${+func.name}${+typeVars}($wrappedArgs) as ${+func.returnType}
           |
         """

  private fun TypeClassInstance.receiverTypeExtensionCode(
    companionOrFactory: TypeName,
    impl: String
  ): String =
    """|return ${+companionOrFactory}.${typeClass.name.simpleName.decapitalize()}${+instance.typeVariables}(${requiredParameters.code { +it.name }}).run·{
           |  $impl
           |}
           |
         """

  private fun TypeClassInstance.receiverTypeExtensionImpl(
    wrappedType: Pair<TypeName, TypeName.ParameterizedType>?,
    receiverType: TypeName,
    func: Func,
    wrappedArgs: Code,
    typeVars: List<TypeName.TypeVariable>
  ): String =
    if (wrappedType != null && receiverType.rawName == wrappedType.second.rawName) {
      "${dataType.name.rawName}(this@${+func.name}).${+func.name}${+func.typeVariables}($wrappedArgs) as ${+func.returnType}"
    } else {
      "this@${+func.name}.${+func.name}${+typeVars}($wrappedArgs) as ${+func.returnType}"
    }

  private fun TypeClassInstance.extensionsCompanionOrFactory(wrappedType: Pair<TypeName, TypeName.ParameterizedType>?): TypeName = if (wrappedType != null) {
    val wrappedSimpleName = wrappedType.second.rawType.simpleName
    val wrappedPackage = PackageName("${instance.packageName.value}.${wrappedSimpleName.toLowerCase()}")
    TypeName.Classy(
      wrappedSimpleName,
      "${wrappedPackage.value}.${wrappedSimpleName.toLowerCase()}",
      PackageName("${instance.packageName.value}.${wrappedSimpleName.toLowerCase()}.${typeClass.name.simpleName.decapitalize()}")
    )
  } else projectedCompanion
}
