package arrow.extensions

import arrow.common.Package
import arrow.common.messager.logW
import arrow.common.utils.ClassOrPackageDataWrapper
import arrow.common.utils.ProcessorUtils
import arrow.common.utils.extractFullName
import arrow.common.utils.fullName
import arrow.common.utils.removeBackticks
import arrow.common.utils.typeConstraints
import arrow.derive.FunctionSignature
import arrow.derive.HKArgs
import arrow.derive.asKotlin
import me.eugeniomarletti.kotlin.metadata.modality
import me.eugeniomarletti.kotlin.metadata.shadow.metadata.ProtoBuf
import me.eugeniomarletti.kotlin.metadata.shadow.metadata.deserialization.TypeTable
import java.io.File

data class FunctionMapping(
  val name: String,
  val typeClass: ClassOrPackageDataWrapper.Class,
  val function: ProtoBuf.Function,
  val retTypeName: String)

data class Instance(
  val `package`: Package,
  val target: AnnotatedInstance,
  val processorUtils: ProcessorUtils
) {
  val name = target.instance.simpleName

  val receiverTypeName = target.dataType.nameResolver.getString(target.dataType.classProto.fqName).replace("/", ".")

  val receiverTypeSimpleName = receiverTypeName.substringAfterLast(".")

  val companionFactoryName = name.toString()
    .replace(receiverTypeSimpleName, "")
    .replace("Instance", "")
    .decapitalize()

  fun typeArgs(reified: Boolean = false, includeBounds: Boolean = false): List<String> =
    if (target.dataTypeInstance.typeParameters.isNotEmpty()) {
      target.dataTypeInstance.typeParameters.map {
        val name = target.dataTypeInstance.nameResolver.getString(it.name)
        val upperBound = if (includeBounds && it.upperBoundList.isNotEmpty())
          it.upperBoundList
            .map { it.extractFullName(target.dataTypeInstance).asKotlin() }
            .joinToString(separator = " : ", prefix = ": ")
        else ""
        if (reified) "reified $name$upperBound" else name + upperBound
      }
    } else {
      emptyList()
    }

  fun expandedTypeArgs(reified: Boolean = false, includeBounds: Boolean = false): String {
    val ta = typeArgs(reified, includeBounds)
    return if (ta.isEmpty()) ""
    else ta.joinToString(prefix = "<", separator = ", ", postfix = ">")
  }

  fun typeConstraints() = target.dataTypeInstance.typeConstraints()

  private val abstractFunctions: List<FunctionMapping> =
    getTypeclassReturningFunctions().fold(emptyList(), normalizeOverridenFunctions())

  private fun normalizeOverridenFunctions(): (List<FunctionMapping>, FunctionMapping) -> List<FunctionMapping> =
    { acc, func ->
      val retType = func.function.returnType.extractFullName(func.typeClass).asKotlin()
      val existingParamInfo = getParamInfo(func.name to retType)
      when {
        acc.contains(func) -> acc //if the same param was already added ignore it
        else -> { //remove accumulated functions whose return types  supertypes of the current evaluated one and add the current one
          val remove = acc.find { av ->
            val avRetType = av.function.returnType.extractFullName(av.typeClass)
              .asKotlin().substringBefore("<")
            existingParamInfo.superTypes.contains(avRetType)
          }
          val ignore = acc.any { av ->
            val avRetTypeUnparsed = av.function.returnType.extractFullName(av.typeClass)
              .removeBackticks()
            val parsedRetType = retType.asKotlin().substringBefore("<")
            val avParamInfo = getParamInfo(av.name to avRetTypeUnparsed)
            avParamInfo.superTypes.contains(parsedRetType)
          }
          when {
            remove != null -> (acc - remove) + listOf(func)
            ignore -> acc
            else -> acc + listOf(func)
          }
        }
      }
    }

  fun extendingFunctions(): List<FunctionMapping> =
    (listOf(target.typeClass)).flatMap { tc ->
      tc.functionList
        .map { it to it.returnType.extractFullName(tc).removeBackticks() }
        .distinctBy { (_, name) -> name }
        .flatMap { (it, name) ->
          val retTypeName = name.substringBefore("<")
          val retType = target.processor.elementUtils.getTypeElement(retTypeName)
          when {
            retType != null -> {
              listOf(FunctionMapping(tc.nameResolver.getString(it.name), tc, it, retTypeName))
            }
            else -> emptyList()
          }
        }
    }

  private fun getTypeclassReturningFunctions(): List<FunctionMapping> {
    val concreteFunctionNames: List<String> = (target.superTypes + listOf(target.dataTypeInstance)).flatMap { tc ->
      tc.functionList
        .filter { it.modality != ProtoBuf.Modality.ABSTRACT }
        .map { tc.nameResolver.getString(it.name) }
    }
    return (target.superTypes + listOf(target.dataTypeInstance)).flatMap { tc ->
      tc.functionList
        .filter { it.modality == ProtoBuf.Modality.ABSTRACT }
        .map { it to it.returnType.extractFullName(tc).removeBackticks() }
        // FIXME(paco): number of parameters and naming convention, used to be based off the TC interface
        .filter { (it, name) -> it.valueParameterCount == 0 && name.contains("typeclass") }
        .distinctBy { (_, name) -> name }
        .flatMap { (it, name) ->
          val retTypeName = name.substringBefore("<")
          val retType = target.processor.elementUtils.getTypeElement(retTypeName)
          when {
            retType != null -> {
              listOf(FunctionMapping(tc.nameResolver.getString(it.name), tc, it, retTypeName))
            }
            else -> emptyList()
          }
        }
    }.filterNot { currentFunction ->
      concreteFunctionNames.contains(currentFunction.name)
    }.distinctBy { it.name }
  }

  data class ParamInfo(
    val param: Pair<String, String>,
    val superTypes: List<String>,
    val paramTypeName: String,
    val typeVarName: String)

  private fun getParamInfo(param: Pair<String, String>): ParamInfo {
    val typeVarName = param.second.substringAfter("<").substringBefore(">")
    val rt = target.processor.elementUtils.getTypeElement(param.second.substringBefore("<"))
    val paramType = target.processor.getClassOrPackageDataWrapper(rt) as ClassOrPackageDataWrapper.Class
    val paramTypeName = paramType.nameResolver.getString(paramType.classProto.fqName)
    val typeTable = TypeTable(paramType.classProto.typeTable)
    val superTypes = target.processor.supertypes(paramType, typeTable, processorUtils, emptyList()).map {
      val t = it as ClassOrPackageDataWrapper.Class
      t.nameResolver.getString(t.classProto.fqName)
    }
    return ParamInfo(param, superTypes, paramTypeName, typeVarName)
  }

  val args: List<Pair<String, String>> = abstractFunctions.sortedBy { f ->
    val typeClassTypeArgs = f.typeClass.typeParameters.map { f.typeClass.nameResolver.getString(it.name) }
    val functionRetTypeTypeArgs = f.function.returnType.extractFullName(f.typeClass)
      .removeBackticks().substringAfter("<").substringBefore(">")
    typeClassTypeArgs.indexOf(functionRetTypeTypeArgs)
  }.map { (name, tc, func) ->
    val retType = func.returnType.extractFullName(tc)
    name to retType.removeBackticks()
  }

  val targetImplementations = args.joinToString(
    separator = "",
    prefix = "{",
    transform = {
      "\n\toverride fun ${it.first}(): ${it.second} = ${it.first}"
    },
    postfix = "}"
  )

}

class InstanceFileGenerator(
  private val generatedDir: File,
  private val annotatedList: List<AnnotatedInstance>,
  private val processorUtils: ProcessorUtils
) {

  private val instances: List<Instance> = annotatedList.map { Instance(it.dataType.`package`, it, processorUtils) }

  /**
   * Main entry point for deriving instance generation.
   */
  fun generate() {
    instances.forEach {
      val elementsToGenerate: List<String> =
        listOf(genImports(it), genCompanionExtensions(it), genDatatypeExtensions(it))
      val source: String = elementsToGenerate.joinToString(prefix = "package ${it.`package`}.${it.companionFactoryName}\n\n", separator = "\n", postfix = "\n")
      val file = File(generatedDir, instanceAnnotationClass.simpleName + ".${it.target.instance.qualifiedName}.kt")
      file.writeText(source)
    }
  }

  private fun genImports(i: Instance): String = """
            |import ${i.`package`}.*
            |import ${i.target.dataType.`package`}.*
            |""".trimMargin()

  private fun genCompanionExtensions(i: Instance): String =
    """|
                |fun ${i.expandedTypeArgs(reified = false)} ${i.receiverTypeName}.Companion.${i.companionFactoryName}(${(i.args.map {
      "${it.first}: ${it.second}"
    } + (if (i.args.isNotEmpty()) listOf("@Suppress(\"UNUSED_PARAMETER\") dummy: kotlin.Unit = kotlin.Unit") else emptyList())).joinToString(", ")
    }): ${i.name}${i.expandedTypeArgs()}${i.typeConstraints()} =
                |    object : ${i.name}${i.expandedTypeArgs()} ${i.targetImplementations}
                |""".trimMargin()

  private fun genDatatypeExtensions(i: Instance): String = i.extendingFunctions().joinToString(separator = "\n", transform = { fm ->
    @Suppress("SwallowedException")
    try {
      val typeClassTypeArgs = i.target.typeClass.typeParameters.drop(1).flatMap {
        if (it.hasName()) listOf(i.target.typeClass.nameResolver.getString(it.name))
        else emptyList()
      }
      val appliedTypeArgs = i.target.dataTypeInstance.classProto.supertypeList[0].argumentList.drop(1).flatMap {
        if (it.type.hasClassName()) listOf(i.target.dataTypeInstance.nameResolver.getString(it.type.className))
        else emptyList()
      } + i.typeArgs()
      val typesToApply: List<Pair<String, String>> =
        if (typeClassTypeArgs.size == appliedTypeArgs.size) typeClassTypeArgs.zip(appliedTypeArgs)
        else emptyList()

      val invariantTypeArgs =
        if (i.target.dataType.typeParameters.size > 2) {
          i.typeArgs() - typeClassTypeArgs
        } else i.typeArgs()

      val sg: FunctionSignature = FunctionSignature.from(
        recType = i.target.dataType,
        typeClass = i.target.typeClass,
        f = fm.function,
        invariantTypeArgs = invariantTypeArgs,
        unappliedTypeArgs = typesToApply
      )

      val altFunction = i.target.dataTypeInstance.functionList.map {
        val name = i.target.dataTypeInstance.nameResolver.getString(it.name)
        val retType = it.returnType.extractFullName(i.target.dataTypeInstance)
        name to retType
      }

      i.target.processor.logW(
        "\n${i.target.dataTypeInstance.fullName.asKotlin()}#${sg.name}*" +
          "\naltFunction : \t\t$altFunction" +
          "\ndatatype typeargs : \t\t${i.typeArgs()}" +
          "\ntypeClassTypeArgs: \t\t$typeClassTypeArgs" +
          "\nappliedTypeArgs: \t\t$appliedTypeArgs" +
          "\ntypesToApply: \t\t$typesToApply"
      )

      val typeArgs = sg.tparams.joinToString(separator = ", ", prefix = "<", postfix = ">")
      val args = i.args + sg.args
      val combinedTypeArgs = (i.typeArgs(includeBounds = true) + (sg.tparams - i.typeArgs()))
      val biasedTypeArgs =
        if (combinedTypeArgs.isEmpty()) ""
        else combinedTypeArgs.joinToString(separator = ", ", prefix = "<", postfix = ">")

      val retType = sg.retType.removeBackticks()
      if (sg.hkArgs is HKArgs.First) {
        """|
         |@Suppress("UNCHECKED_CAST")
         |fun ${biasedTypeArgs} ${sg.hkArgs.receiver.removeBackticks()}.`${fm.name}`(${args.joinToString(",") { "\n\t${it.first}: ${it.second.removeBackticks()}" }}): $retType =
         |  ${i.receiverTypeName}.${i.companionFactoryName}${i.expandedTypeArgs()}(${i.args.joinToString(", ") { it.first }}).run {
         |    this@`${fm.name}`.`${fm.name}`${typeArgs}(${sg.args.joinToString(", ") { it.first }}) as $retType
         |  }
         |
         |""".trimMargin()
      } else {
        """|
         |@Suppress("UNCHECKED_CAST")
         |fun ${biasedTypeArgs} ${i.receiverTypeName}.Companion.`${fm.name}`(${args.joinToString(",") { "\n\t${it.first}: ${it.second.removeBackticks()}" }}): $retType =
         |  ${i.receiverTypeName}.${i.companionFactoryName}${i.expandedTypeArgs()}(${i.args.joinToString(", ") { it.first }}).run {
         |    this.`${fm.name}`${typeArgs}(${sg.args.joinToString(", ") { it.first }}) as $retType
         |  }
         |""".trimMargin()
      }
    } catch (iob: Throwable) {
      i.target.processor.logW("skipped instance for fm: ${fm.name}")
      ""
    }
  })

}
