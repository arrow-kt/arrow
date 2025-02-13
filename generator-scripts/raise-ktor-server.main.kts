@file:DependsOn("me.alllex.parsus:parsus-jvm:0.6.1")

import me.alllex.parsus.parser.Grammar
import me.alllex.parsus.parser.between
import me.alllex.parsus.parser.choose
import me.alllex.parsus.parser.map
import me.alllex.parsus.parser.maybe
import me.alllex.parsus.parser.or
import me.alllex.parsus.parser.parseOrNull
import me.alllex.parsus.parser.parser
import me.alllex.parsus.parser.times
import me.alllex.parsus.parser.unaryMinus
import me.alllex.parsus.token.literalToken
import me.alllex.parsus.token.regexToken
import java.io.File
import java.net.URI

sealed interface KtorExtension {
  val extensionName: String
  val pathType: String?
}

data class StandardKtorExtension(override val extensionName: String, override val pathType: String?) : KtorExtension
data class ReceivingKtorExtension(override val extensionName: String, override val pathType: String?) : KtorExtension

//region simple abi
object KtorExtensionAbiGrammer : Grammar<KtorExtension>() {
  val funPrefixStandard by literalToken("final fun (io.ktor.server.routing/Route).io.ktor.server.routing/")
  val funPrefixReceiving by literalToken("final inline fun <#A: reified kotlin/Any> (io.ktor.server.routing/Route).io.ktor.server.routing/")

  val funName by regexToken("[a-zA-Z_][a-zA-Z0-9_]*") map { it.text }

  val pathParamType by regexToken("[^,]+") * -literalToken(", ") map { it.text.split("/").last() }

  val bodyParamStandard by literalToken("kotlin.coroutines/SuspendFunction1<io.ktor.server.routing/RoutingContext, kotlin/Unit>")
  val bodyParamReceiving by literalToken("crossinline kotlin.coroutines/SuspendFunction2<io.ktor.server.routing/RoutingContext, #A, kotlin/Unit>")
  val bodyParamType by bodyParamStandard or bodyParamReceiving map { it.text }

  val pathAndBody by pathParamType * bodyParamType map { (path, body) -> path to body }
  val bodyOnly by bodyParamType map { null to it }

  val params by (pathAndBody or bodyOnly)
    .between(literalToken("("), literalToken(")"))

  val returnRoute by -literalToken(": io.ktor.server.routing/Route")
  val trailingComment by -regexToken("\\s+//.+")

  val ktorRoutingExtension by parser {
    val receiving = choose(funPrefixStandard.map { false }, funPrefixReceiving.map { true })

    val name = funName()
    val (pathType, _) = params()
    returnRoute()

    if (receiving) ReceivingKtorExtension(name, pathType)
    else StandardKtorExtension(name, pathType)
  }

  override val root = ktorRoutingExtension * -maybe(trailingComment)

  fun parseDump(abiDump: String): List<KtorExtension> = abiDump.lines()
    .mapNotNull(::parseOrNull)
}
//endregion

//region function rendering
val KtorExtension.jvmNameSuffix
  get() = when (this) {
    is StandardKtorExtension -> ""
    is ReceivingKtorExtension -> "Typed"
  } + when (pathType) {
    null -> ""
    "String" -> "Path"
    else -> pathType
  }

val KtorExtension.jvmName get() = "${extensionName}OrRaise${jvmNameSuffix}"

val KtorExtension.bodyParameterType
  get() = when (this) {
    is StandardKtorExtension -> "suspend RaiseRoutingContext.() -> R"
    is ReceivingKtorExtension -> "suspend RaiseRoutingContext.(B) -> R"
  }

val KtorExtension.typeParameters
  get() = when (this) {
    is StandardKtorExtension -> "<reified R>"
    is ReceivingKtorExtension -> "<reified B : Any, reified R>"
  }

val KtorExtension.invocation
  get() = when (this) {
    is StandardKtorExtension -> "$extensionName$invocationParams { respondOrRaise<R>(statusCode, body) }"
    is ReceivingKtorExtension -> "$extensionName<B>$invocationParams { respondOrRaise(statusCode) { body(it) } }"
  }

val KtorExtension.invocationParams
  get() = when (pathType) {
    null -> ""
    else -> "(path)"
  }

fun KtorExtension.generate() = buildString {
  appendLine("@KtorDsl")
  appendLine("@RaiseDSL")
  appendLine("@JvmName(\"$jvmName\")")
  appendLine("public inline fun $typeParameters Route.${extensionName}OrRaise(")
  if (pathType != null) appendLine("  path: $pathType,")
  appendLine("  statusCode: HttpStatusCode? = null,")
  appendLine("  crossinline body: $bodyParameterType,")
  appendLine("): Route = $invocation")
}
//endregion

val dumpFile = File("../build/ktor-server-core.klib.api")
val dumpString = when {
  dumpFile.exists() -> dumpFile.readText()
  else -> URI.create("https://raw.githubusercontent.com/ktorio/ktor/refs/heads/main/ktor-server/ktor-server-core/api/ktor-server-core.klib.api").toURL().readText().also { dumpFile.writeText(it) }
}

KtorExtensionAbiGrammer.parseDump(dumpString)
  .sortedBy(KtorExtension::jvmName)
  .groupBy { it::class.simpleName }
  .forEach { (extensionType, extensionFunctions) ->
    val destination = File(
      "../arrow-libs/integrations/arrow-raise-ktor-server/src/commonMain/kotlin/arrow/raise/ktor/server/" + when (extensionType) {
        "StandardKtorExtension" -> "routing.kt"
        "ReceivingKtorExtension" -> "routingReceiving.kt"
        else -> return@forEach
      }
    )
    destination.writer().use {
      it.appendLine(
        """
        // Generated by raise-ktor-server.main.kts
        
        package arrow.raise.ktor.server

        import arrow.core.raise.RaiseDSL
        import io.ktor.http.*
        import io.ktor.server.routing.*
        import io.ktor.utils.io.*
        import kotlin.jvm.JvmName
      """.trimIndent()
      )
      extensionFunctions.forEach {  extensionFunction ->
        it.appendLine()
        it.append(extensionFunction.generate())
      }
    }
  }
