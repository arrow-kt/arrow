package arrow.instances

import arrow.common.messager.log
import arrow.common.messager.logW
import arrow.common.utils.*
import arrow.extension
import arrow.meta.ast.*
import arrow.meta.encoder.TypeClassInstance
import arrow.meta.processor.MetaProcessor
import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.FileSpec
import me.eugeniomarletti.kotlin.metadata.shadow.metadata.deserialization.TypeTable
import java.io.File
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement

@AutoService(Processor::class)
class InstanceProcessor : MetaProcessor<extension>(extension::class) {
  override fun transform(annotatedElement: AnnotatedElement): List<FileSpec.Builder> =
    when (annotatedElement) {
      is AnnotatedElement.Interface -> {
        logW("Processing: ${annotatedElement.typeElement.simpleName}")
        val info = annotatedElement.typeElement.typeClassInstance()
        log("[${info?.instance?.name?.simpleName}] : Generating [${info?.typeClass?.name?.simpleName}] extensions for [${info?.projectedCompanion}]")
        val fileSpec = info?.let {
          FileSpec.builder(
            "${annotatedElement.type.packageName.value}.${info.projectedCompanion.simpleName.substringAfterLast(".").toLowerCase()}.${info.typeClass.name.simpleName.decapitalize()}",
            annotatedElement.type.name.simpleName
          )
        }
        if (fileSpec != null) {
          val wrappedType = info.dataType.kindWrapper()
          val wrappedExtensions = if (wrappedType != null) {
            val wrappedTypeElement = elementUtils.getTypeElement(wrappedType.second.rawType.asPlatform().fqName)
            val wrappedTypeFunctions =
              try {
                elementUtils.getAllMembers(wrappedTypeElement).map { it.simpleName.toString() }
              } catch (e: Exception) {
                emptyList<String>()
              }
            info
              .genDataTypeExtensions(wrappedType)
              .asSequence()
              .filter { ext ->
                ext.name !in wrappedTypeFunctions
              }
              .toList()
          } else emptyList()
          val functions = info.genDataTypeExtensions() + listOf(info.genCompanionFactory(info.dataType.name))
          val mainFileSpec: FileSpec.Builder = functions.fold(fileSpec) { spec, func ->
            spec.addFunction(func.lyrics().toBuilder().build())
          }
          val wrappedFileSpec = if (wrappedType != null && wrappedExtensions.isNotEmpty()) {
            val wrappedFileBuilder = FileSpec.builder(
              "${annotatedElement.type.packageName.value}.${wrappedType.second.rawType.simpleName.substringAfterLast(".").toLowerCase()}.${info.typeClass.name.simpleName.decapitalize()}",
              annotatedElement.type.name.simpleName
            )
            val wrappedFunctionSpec = wrappedExtensions.fold(wrappedFileBuilder) { spec, func ->
              spec.addFunction(func.lyrics().toBuilder().build())
            }
            val wrappedSimpleName = wrappedType.second.rawType.simpleName
            val wrappedPackage = PackageName("${info.instance.packageName.value}.${wrappedSimpleName.toLowerCase()}")
            wrappedFunctionSpec.addType(
              Type(
                name = TypeName.Classy(
                  wrappedSimpleName,
                  "${wrappedPackage.value}.${wrappedSimpleName.toLowerCase()}",
                  PackageName("${info.instance.packageName.value}.${info.typeClass.name.simpleName.decapitalize()}.${wrappedSimpleName.toLowerCase()}")
                ),
                kind = Type.Shape.Object,
                packageName = annotatedElement.type.packageName,
                declaredFunctions = listOf(info.genCompanionFactory(wrappedType.second.rawType).copy(receiverType = null))
              ).lyrics()
            )
          } else null
          listOfNotNull(mainFileSpec, wrappedFileSpec)
        } else emptyList()
      }
      else -> notAnInstanceError()
    }

  private fun notAnInstanceError(): Nothing =
    knownError("@instance is only allowed on `interface` extending another interface of at least one type argument (type class) as first declaration in the instance list")

  fun TypeClassInstance.genCompanionFactory(targetType: TypeName): Func {
    val target = when (projectedCompanion) {
      is TypeName.Classy -> projectedCompanion.companion()
      else -> TypeName.Classy(
        simpleName = "Companion",
        fqName = "${targetType.rawName}.Companion",
        pckg = PackageName(targetType.rawName.substringBeforeLast(".") + "." + targetType.downKind().simpleName)
      )
    }
    return Func(
      name = typeClass.name.simpleName.decapitalize(),
      parameters = requiredParameters,
      receiverType = target,
      typeVariables = instance.typeVariables.map { it.removeConstrains() },
      returnType = instance.name,
      body = Code { "return object : ${+instance.name} { ${requiredAbstractFunctions.code()} }" }
    )
  }

  fun TypeClassInstance.genDataTypeExtensions(wrappedType: Pair<TypeName, TypeName.ParameterizedType>? = null): List<Func> {
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
      .map { it.removeConstrains() }
      .map { f ->
        val func = f.removeDummyArgs().downKindReturnType().wrap(wrappedType)
        val dummyArgsCount = f.countDummyArgs()
        val allArgs = func.parameters + requiredParameters
        val typeVariables = (instance.typeVariables + func.typeVariables)
          .asSequence()
          .map { it.removeConstrains() }
          .distinctBy { it.name }
          .toList()
        func
          .copy(
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
              //Const is a special case because it uses a phantom type arg and the Kotlin compiler chooses to
              //recursively call the extension if you ascribe the call types
              val typeVars =
                if (instance.name.simpleName == "ConstFunctorInstance") emptyList()
                else func.typeVariables
              val companionOrFactory =
                if (wrappedType != null) {
                  val wrappedSimpleName = wrappedType.second.rawType.simpleName
                  val wrappedPackage = PackageName("${instance.packageName.value}.${wrappedSimpleName.toLowerCase()}")
                  TypeName.Classy(
                    wrappedSimpleName,
                    "${wrappedPackage.value}.${wrappedSimpleName.toLowerCase()}",
                    PackageName("${instance.packageName.value}.${wrappedSimpleName.toLowerCase()}.${typeClass.name.simpleName.decapitalize()}")
                  )
                } else projectedCompanion
              if (receiverType != null) {
                val impl = if (wrappedType != null && receiverType.rawName == wrappedType.second.rawName) {
                  "${dataType.name.rawName}(this@${+func.name}).${+func.name}${+func.typeVariables}($wrappedArgs) as ${+func.returnType}"
                } else {
                  "this@${+func.name}.${+func.name}${+typeVars}($wrappedArgs) as ${+func.returnType}"
                }
                """|return ${+companionOrFactory}.${typeClass.name.simpleName.decapitalize()}${+instance.typeVariables}(${requiredParameters.code { +it.name }}).run {
                     |  $impl
                     |}
                     |
                   """
              } else {
                """|return ${+companionOrFactory}
                     |   .${typeClass.name.simpleName.decapitalize()}${+instance.typeVariables}(${requiredParameters.code { +it.name }})
                     |   .${+func.name}${+typeVars}($wrappedArgs) as ${+func.returnType}
                     |
                   """
              }
            })
      }
      .toList()

  }
}

//@AutoService(Processor::class)
class LegacyInstanceProcessor : AbstractProcessor() {

  private val annotatedList = mutableListOf<AnnotatedInstance>()

  override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.latestSupported()

  override fun getSupportedAnnotationTypes(): Set<String> = setOf(instanceAnnotationClass.canonicalName)

  /**
   * Processor entry point
   */
  override fun onProcess(annotations: Set<TypeElement>, roundEnv: RoundEnvironment) {
    annotatedList += roundEnv
      .getElementsAnnotatedWith(instanceAnnotationClass)
      .map { element ->
        when (element.kind) {
          ElementKind.INTERFACE -> processClass(this, element as TypeElement)
          else -> knownError("$instanceAnnotationName can only be used on interfaces")
        }
      }

    if (roundEnv.processingOver()) {
      val generatedDir = File(this.generatedDir!!, instanceAnnotationClass.simpleName).also { it.mkdirs() }
      InstanceFileGenerator(generatedDir, annotatedList, this).generate()
    }
  }

  companion object {

    private fun findNestedType(blob: String): String =
      blob.removeBackticks()
        .replace("arrow.Kind<(.*?)>".toRegex(), "$1")
        .substringAfter("<")
        .substringBeforeLast(">")
        .substringBefore("<")
        .replace(".For", ".")
        .removeSuffix("PartialOf")
        .removeSuffix("Of")
        .substringBefore(",")

    fun processClass(processor: AbstractProcessor, element: TypeElement): AnnotatedInstance {
      val proto: ClassOrPackageDataWrapper.Class = processor.getClassOrPackageDataWrapper(element) as ClassOrPackageDataWrapper.Class
      val typeTable = TypeTable(proto.classProto.typeTable)
      val superTypes: List<ClassOrPackageDataWrapper.Class> =
        processor.supertypes(proto, typeTable, processor, emptyList()).map { it as ClassOrPackageDataWrapper.Class }
      val typeClass = if (superTypes.isEmpty()) {
        knownError("@instance `${proto.fullName}` needs to extend a type class (interface with one type parameter) as it's first interface in the `extends` declaration")
      } else superTypes[0]
      val dataType: ClassOrPackageDataWrapper.Class = if (typeClass.typeParameters.isEmpty()) {
        knownError("@instance `${proto.fullName}` needs to extend a type class (interface with one type parameter) as it's first interface in the `extends` declaration")
      } else {
        val name = proto.classProto.getSupertype(0).extractFullName(proto)
        val target = findNestedType(name)
        val dataTypeElement = processor.elementUtils.getTypeElement(target)
          ?: knownError("found null datatype element on `${proto.fullName}` for $name, targeted at -> $target. `${proto.fullName}` needs to extend a type class (interface with one type parameter) as it's first interface in the `extends` declaration)")
        processor.getClassOrPackageDataWrapper(dataTypeElement) as ClassOrPackageDataWrapper.Class
      }
      return AnnotatedInstance(
        instance = element,
        dataTypeInstance = proto,
        superTypes = superTypes,
        processor = processor,
        dataType = dataType,
        typeClass = typeClass
      )
    }
  }

}