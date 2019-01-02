package arrow.derive

import arrow.common.utils.ClassOrPackageDataWrapper
import arrow.common.utils.extractFullName
import arrow.common.utils.fullName
import arrow.common.utils.removeBackticks
import arrow.higherkinds.HKMarkerPreFix
import arrow.higherkinds.KindPartialPostFix
import arrow.higherkinds.KindPostFix
import me.eugeniomarletti.kotlin.metadata.modality
import me.eugeniomarletti.kotlin.metadata.shadow.metadata.ProtoBuf
import me.eugeniomarletti.kotlin.metadata.shadow.metadata.deserialization.hasReceiver
import java.io.File

private fun List<String>.prependTypeArgs(): String =
  when {
    isEmpty() -> ""
    else -> joinToString(", ", postfix = ", ")
  }

private fun kindedRegex(typeClassFirstTypeArg: String): Regex = "arrow.Kind<$typeClassFirstTypeArg,\\s".toRegex()

fun String.asKotlin(): String =
  removeBackticks()
    .replace("/", ".")
    .replace("kotlin.jvm.functions", "kotlin")
    .replace("java.util.Collection", "kotlin.collections.Collection")
    .replace("java.lang.Throwable", "kotlin.Throwable")

fun String.appendTypePrefix(prefix: String): String {
  val pkg = substringBeforeLast(".")
  val type = substringAfterLast(".")
  return "$pkg.$prefix$type"
}

fun String.applyUnappliedTypeArgs(unappliedTypeArgs: List<Pair<String, String>>): String {
  tailrec fun loop(remainingTypeArgs: List<Pair<String, String>>, acc: String): String =
    if (remainingTypeArgs.isEmpty()) acc
    else {
      val head = remainingTypeArgs[0]
      val replacement = head.second.asKotlin()
      val newAcc = acc
        .replace("<${head.first}>", "<$replacement>")
        .replace("<${head.first}", "<$replacement")
        .replace(",\\s${head.first},".toRegex(), ", $replacement,")
        .replace(", ${head.first}>", ", $replacement>")
      loop(remainingTypeArgs.drop(1), newAcc)
    }
  return loop(unappliedTypeArgs, this)
}

fun argAsSeenFromReceiver(typeClassFirstTypeArg: String, abstractType: String, recType: ClassOrPackageDataWrapper.Class, invariantTypeArgs: List<String>, unappliedTypeArgs: List<Pair<String, String>>): String {
  val extraTypeArgs = invariantTypeArgs.prependTypeArgs()
  val receiverType = recType.fullName.asKotlin()
  val receiverPostFix = if (recType.typeParameters.size > 1) "$KindPartialPostFix<${invariantTypeArgs.joinToString(", ")}>" else ""
  val receiverPrefix = if (recType.typeParameters.size == 1) HKMarkerPreFix else ""
  return abstractType
    .replace(kindedRegex(typeClassFirstTypeArg), "$receiverType$KindPostFix<$extraTypeArgs")
    .replace("<$typeClassFirstTypeArg>".toRegex(), "<${receiverType.appendTypePrefix(receiverPrefix)}$receiverPostFix>")
    .applyUnappliedTypeArgs(unappliedTypeArgs)
}

fun retTypeAsSeenFromReceiver(typeClassFirstTypeArg: String, abstractType: String, recType: ClassOrPackageDataWrapper.Class, invariantTypeArgs: List<String>, unappliedTypeArgs: List<Pair<String, String>>): String {
  val extraTypeArgs = invariantTypeArgs.prependTypeArgs()
  val receiverType = recType.fullName.asKotlin()
  return when {
  //abstractType.matches("arrow.Kind<(.*?), arrow.Kind<$typeClassFirstTypeArg, (.*?)>>".toRegex()) -> abstractType.replace(kindedRegex(typeClassFirstTypeArg), "$receiverType$KindPostFix<$extraTypeArgs")
    abstractType.startsWith("arrow.Kind<") -> abstractType.replace(kindedRegex(typeClassFirstTypeArg), "$receiverType<$extraTypeArgs")
    else -> abstractType.replace(kindedRegex(typeClassFirstTypeArg), "$receiverType$KindPostFix<$extraTypeArgs")
  }.applyUnappliedTypeArgs(unappliedTypeArgs)
}

sealed class HKArgs {
  object None : HKArgs()
  data class First(val receiver: String) : HKArgs()
  object Unknown : HKArgs()
}

data class FunctionSignature(
  val tparams: List<String>,
  val name: String,
  val args: List<Pair<String, String>>,
  val retType: String,
  val hkArgs: HKArgs,
  val receiverType: String,
  val isAbstract: Boolean,
  val retTypeIsKinded: Boolean

) {

  fun generate(): String {
    val typeParamsS = tparams.joinToString(prefix = "<`", separator = "`, `", postfix = "`>")
    val argsS = args.joinToString(prefix = "(`", separator = "`, `", postfix = "`)") { "${it.first}: ${it.second}" }
    val receiver = if (hkArgs is HKArgs.First) "`${hkArgs.receiver}.`" else ""
    return """|override fun $typeParamsS $receiver`$name`$argsS: $retType =
                  |    ${implBody()}""".removeBackticks().trimMargin()
  }

  fun implBody(): String =
    when (hkArgs) {
      HKArgs.None -> "$receiverType.$name()"
      is HKArgs.First -> "fix().$name(${args.joinToString(", ") { it.first }})"
      HKArgs.Unknown -> "$receiverType.$name(${args.joinToString(", ") { it.first }})"
    }

  companion object {

    fun from(recType: ClassOrPackageDataWrapper.Class,
             typeClass: ClassOrPackageDataWrapper,
             f: ProtoBuf.Function,
             invariantTypeArgs: List<String> = emptyList(),
             unappliedTypeArgs: List<Pair<String, String>> = emptyList()): FunctionSignature {
      fun Int.get() =
        typeClass.nameResolver.getString(this)

      val receiverType = recType.fullName.asKotlin()
      val typeParams = f.typeParameterList.map { it.name.get() }
      val typeClassAbstractKind = typeClass.typeParameters[0].name.get().asKotlin()
      val functionName = typeClass.nameResolver.getString(f.name)

      val args = f.valueParameterList.map {
        val argName = it.name.get()
        val argType = it.type.extractFullName(typeClass).asKotlin()
        argName to argAsSeenFromReceiver(typeClassAbstractKind, argType, recType, invariantTypeArgs, unappliedTypeArgs)
      }

      val abstractReturnType = f.returnType.extractFullName(typeClass, outputTypeAlias = true).asKotlin()
      val concreteType = retTypeAsSeenFromReceiver(
        typeClassAbstractKind,
        abstractReturnType,
        recType, invariantTypeArgs, unappliedTypeArgs)
      val isAbstract = f.modality == ProtoBuf.Modality.ABSTRACT
      return FunctionSignature(
        tparams = typeParams,
        name = functionName,
        args = args,
        retType = concreteType,
        hkArgs = findArgs(f, typeClassAbstractKind.asKotlin(), typeClass, recType, invariantTypeArgs, unappliedTypeArgs),
        receiverType = receiverType,
        isAbstract = isAbstract,
        retTypeIsKinded = abstractReturnType.contains(kindedRegex(typeClassAbstractKind))
      )
    }

    private fun findArgs(f: ProtoBuf.Function, typeClassAbstractKind: String, typeClass: ClassOrPackageDataWrapper, receiverType: ClassOrPackageDataWrapper.Class, invariantTypeArgs: List<String>, unappliedTypeArgs: List<Pair<String, String>>): HKArgs =
      when {
        f.hasReceiver() ->
          HKArgs.First(argAsSeenFromReceiver(typeClassAbstractKind, f.receiverType.extractFullName(typeClass).asKotlin(), receiverType, invariantTypeArgs, unappliedTypeArgs))
        f.valueParameterList.isEmpty() -> HKArgs.None
        else -> HKArgs.Unknown
      }
  }
}

class TypeclassInstanceGenerator(
  val targetType: AnnotatedDeriving,
  val typeClass: ClassOrPackageDataWrapper.Class) {

  val target: ClassOrPackageDataWrapper.Class = targetType.classOrPackageProto as ClassOrPackageDataWrapper.Class

  val receiverType: String = targetType.classElement.qualifiedName.toString()

  val receiverSimpleName: String = receiverType.substringAfterLast(".")

  val receiverName: String = "$HKMarkerPreFix$receiverSimpleName"

  val typeClassFQName: String =
    typeClass.nameResolver.getString(typeClass.classProto.fqName).replace("/", ".")

  val typeClassName: String = typeClassFQName.substringAfterLast(".")

  val tparams: List<String> = typeClass.typeParameters.map { typeClass.nameResolver.getString(it.name) }

  val tparamsAsSeenFromReceiver: List<String> = listOf(receiverName) + tparams.drop(1)

  fun functionSignatures(): List<FunctionSignature> {
    val tcs = listOf(typeClass) + targetType.typeclassSuperTypes[typeClass]!!
    return tcs.flatMap { tc ->
      tc.functionList.map {
        FunctionSignature.from(target, tc, it)
      }
    }.distinctBy { it.name }
  }

  val companionFactoryName: String = typeClassName[0].toLowerCase() + typeClassName.drop(1)

  val instanceName: String = "$receiverSimpleName${typeClassName}Instance"

  fun targetHasFunction(f: FunctionSignature, c: ClassOrPackageDataWrapper): Boolean =
    c.functionList.any {
      val typeClassFunctionName = c.nameResolver.getString(it.name)
      typeClassFunctionName == f.name
    }

  fun targetRequestsDelegation(f: FunctionSignature): Boolean = when (f.hkArgs) {
    HKArgs.None -> f.isAbstract || targetHasFunction(f, targetType.companionClassProto)
    is HKArgs.First -> f.isAbstract || targetHasFunction(f, target)
    HKArgs.Unknown -> f.isAbstract || targetHasFunction(f, targetType.companionClassProto)
  }

  val delegatedFunctions: List<String> = functionSignatures().filter(this::targetRequestsDelegation).map { it.generate() }

  fun generate(): String {
    val tArgs = tparamsAsSeenFromReceiver.joinToString(", ")
    return """
            |interface $instanceName : $typeClassFQName<$tArgs> {
            |  ${delegatedFunctions.joinToString("\n\n  ")}
            |}
            |
            |fun $receiverType.Companion.$companionFactoryName(): $instanceName =
            |  object : $instanceName, $typeClassFQName<$tArgs> {}
            |
        """.removeBackticks().trimMargin()
  }
}

class DerivingFileGenerator(
  private val generatedDir: File,
  private val annotatedList: List<AnnotatedDeriving>
) {

  /**
   * Main entry point for deriving instance generation
   */
  fun generate() {
    annotatedList.forEachIndexed { _, c ->
      val elementsToGenerate = listOf(genImpl(c))
      val source: String = elementsToGenerate.joinToString(prefix = "package ${c.classOrPackageProto.`package`}\n\n", separator = "\n")
      val file = File(generatedDir, derivingAnnotationClass.simpleName + ".${c.classElement.qualifiedName}.kt")
      file.writeText(source)
    }
  }

  private fun genImpl(c: AnnotatedDeriving): String =
    c.derivingTypeclasses.filter { it is ClassOrPackageDataWrapper.Class }.joinToString("\n\n") {
      TypeclassInstanceGenerator(c, it as ClassOrPackageDataWrapper.Class).generate()
    }

}
