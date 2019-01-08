package arrow.extensions

import arrow.meta.ast.Code
import arrow.meta.ast.Func
import arrow.meta.ast.PackageName
import arrow.meta.ast.TypeName
import arrow.meta.encoder.MetaApi
import arrow.meta.encoder.TypeClassInstance
import arrow.meta.encoder.jvm.quote
import arrow.undocumented

private val polyFunEvalRegex = "_(.*?)_\\((.*?)\\)".toRegex(RegexOption.MULTILINE)

private const val ConstantTypeConstructor = "ForId"
private const val ConstantType1 = "String"
private const val ConstantType2 = "CharSequence"

interface PolyTemplateGenerator : MetaApi {

  fun Code.eval(info: TypeClassInstance): Code =
    when {
      info.instance.annotations.find { it.type.rawName == undocumented::class.qualifiedName } != null ->
        Code("")
      else ->
        copy(
          value = value
            .removeExtensionDirective()
            .replaceApplicativeImports(info)
            .replaceMonadDeferImports(info)
            .replaceImports(info)
            .replaceExtensionFactory(info)
            .replaceDataType(info)
            .replaceUserFunctions(info)
        )
    }

  private fun TypeClassInstance.wrapsTypeConstructor(): Boolean =
    dataType.primaryConstructor?.parameters?.find {
      val typeName: TypeName = it.type
      when (typeName) {
        is TypeName.ParameterizedType -> typeName.name.contains("arrow.Kind")
        else -> typeName.rawName.contains("arrow.Kind")
      }
    } != null

  private fun TypeClassInstance.projectedType(n: Int): String =
    if (dataType.typeVariables.size >= 2 && n == 0)
      if (wrapsTypeConstructor()) ConstantTypeConstructor
      else ConstantType1
    else if (dataType.typeVariables.size >= 2 && n == 1) ConstantType1
    else if (dataType.typeVariables.size >= 2 && n == 2) ConstantType2
    else ConstantType1

  private fun String.replaceExtensionFactory(info: TypeClassInstance): String {
    val invocationTypeArgs = info.invocationTypeArgs()
    val instanceArgs = info.instanceArgs()
    return replace(
      "_extensionFactory_",
      info.extensionFactory(invocationTypeArgs, instanceArgs)
    )
  }

  private fun String.replaceUserFunctions(info: TypeClassInstance): String =
    replace(polyFunEvalRegex) {
      val function = it.groupValues[1]
      val userArgsToken = it.groupValues[2]
      val userArgs = if (userArgsToken == "<>") "" else userArgsToken
      val instanceArgs = info.instanceArgs()
      val funcTypeArgs = info.funcTypeArgs()
      val typeArgs =
        if ((userArgs.isNotBlank() || userArgsToken == "<>") && info.dataType.typeVariables.size >= 2)
          funcTypeArgs + ConstantType1
        else funcTypeArgs
      val args =
        when {
          info.applicativeRequiresMonoid() && function == "just" -> listOf("String.monoid()", userArgs)
          instanceArgs.isBlank() -> listOf(userArgs)
          else -> listOf(instanceArgs, userArgs)
        }
      val renderedArgs = args.filter { arg -> arg.isNotBlank() }.joinToString(", ")
      "$function${typeArgs.expandTypeArgs()}($renderedArgs)"
    }

  private fun String.replaceDataType(info: TypeClassInstance): String =
    replace(
      "_dataType_",
      info.dataType.name.simpleName
    )

  private fun String.removeExtensionDirective(): String =
    replace("```(.*?):extension".toRegex()) {
      val snippetDeclaration = it.groupValues[1]
      "```$snippetDeclaration"
    }

  private fun TypeClassInstance.applicativeRequiresMonoid(): Boolean =
    setOf("Const", "Tuple").find { it in dataType.name.simpleName } != null

  private fun String.replaceApplicativeImports(info: TypeClassInstance): String {
    val applicativePackageName = PackageName(info.instance.packageName.value +
      "." + info.projectedCompanion.simpleName.substringAfterLast(".").toLowerCase() +
      ".applicative")
    val monoidImports = if (info.applicativeRequiresMonoid()) "\nimport arrow.core.extensions.monoid" else ""
    return replace(
      "_imports_applicative_",
      "import ${applicativePackageName.value.quote()}.just$monoidImports"
    )
  }

  private fun String.replaceMonadDeferImports(info: TypeClassInstance): String {
    val monadDeferPackageName = PackageName(info.instance.packageName.value +
      "." + info.projectedCompanion.simpleName.substringAfterLast(".").toLowerCase() +
      ".monadDefer")
    return replace(
      "_imports_monaddefer_",
      """
        |import ${monadDeferPackageName.value.quote()}.defer
        |import ${monadDeferPackageName.value.quote()}.delay
      """.trimMargin()
    )
  }

  private fun String.replaceImports(info: TypeClassInstance): String {
    val packageName = PackageName(info.instance.packageName.value +
      "." + info.projectedCompanion.simpleName.substringAfterLast(".").toLowerCase() +
      "." + info.typeClass.name.simpleName.decapitalize())
    val factoryImports = info.factoryImports()
    val funcs = info.functionsIn(this)
    val additionalImports = additionalImports(funcs)
    return replace(
      "_imports_",
      """|import ${info.dataType.name.rawName.substringBeforeLast(".")}.*
             |import ${packageName.value.quote()}.*
             |import arrow.core.*
             |$factoryImports
             |$additionalImports""".trimMargin()
    )
  }

  private fun Iterable<Int>.typeArgs(info: TypeClassInstance): List<String> =
    map { info.projectedType(it) }

  private fun List<String>.expandTypeArgs(): String =
    if (isEmpty()) ""
    else joinToString(separator = ", ", prefix = "<", postfix = ">")

  private fun TypeClassInstance.factoryImports(): String =
    when {
      requiredAbstractFunctions.isEmpty() -> ""
      else -> requiredAbstractFunctions.joinToString("\n") {
        val factory = it.returnType?.simpleName?.decapitalize()?.substringBefore("<") ?: ""
        val fact = if (factory == "functor") "monad" else factory
        """|import arrow.core.extensions.id.$fact.$fact
           |import arrow.core.*""".trimMargin()
      }
    }

  private fun TypeClassInstance.extensionFactory(invocationTypeArgs: List<String>, instanceArgs: String) =
    "${dataType.name.simpleName}.${typeClass.name.simpleName.decapitalize()}${invocationTypeArgs.expandTypeArgs()}($instanceArgs)"

  private fun additionalImports(funcs: List<Func>): String =
    funcs.flatMap { f ->
      f.parameters.filter { it.type.simpleName.startsWith("function") }.map { p ->
        val factory = p.type.simpleName.decapitalize()
        "import arrow.core.extensions.id.$factory.$factory"
      }
    }.joinToString("\n")

  private fun TypeClassInstance.functionsIn(value: String): List<Func> =
    polyFunEvalRegex.findAll(value).mapNotNull {
      val function = it.groupValues[1]
      instance.allFunctions.find { f -> f.name == function }
    }.toList()

  private fun TypeClassInstance.instanceArgs(): String =
    when {
      requiredAbstractFunctions.isEmpty() -> ""
      else -> requiredAbstractFunctions
        .joinToString(", ") {
          val factory = it.returnType?.simpleName?.decapitalize()?.substringBefore("<") ?: ""
          val fact = if (factory == "functor") "monad" else factory
          val dataType =
            if (dataType.typeVariables.size >= 2) "Id"
            else "String"
          "$dataType.$fact()"
        }
    }

  private fun TypeClassInstance.funcTypeArgs(): List<String> =
    when {
      applicativeRequiresMonoid() && dataType.name.simpleName.contains("Tuple") ->
        listOf(ConstantType1, ConstantType1)
      applicativeRequiresMonoid() -> listOf(ConstantType1)
      dataType.typeVariables.size == 1 -> emptyList()
      else -> (0 until dataType.typeVariables.size).typeArgs(this)
    }

  private fun TypeClassInstance.invocationTypeArgs(): List<String> =
    if (dataType.typeVariables.size < 2) emptyList()
    else (0 until dataType.typeVariables.size - 1).typeArgs(this)

}
