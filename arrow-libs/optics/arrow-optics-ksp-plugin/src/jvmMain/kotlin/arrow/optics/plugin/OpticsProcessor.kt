package arrow.optics.plugin

import arrow.optics.plugin.internals.Snippet
import arrow.optics.plugin.internals.adt
import arrow.optics.plugin.internals.asFileText
import arrow.optics.plugin.internals.join
import arrow.optics.plugin.internals.noCompanion
import arrow.optics.plugin.internals.otherClassTypeErrorMessage
import arrow.optics.plugin.internals.snippets
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration

class OpticsProcessor(private val codegen: CodeGenerator, private val logger: KSPLogger) :
  SymbolProcessor {
  override fun process(resolver: Resolver): List<KSAnnotated> {
    resolver
      .getSymbolsWithAnnotation("arrow.optics.optics")
      .filterIsInstance<KSClassDeclaration>()
      .forEach(::processClass)

    // the docs say that [process] should return
    // "deferred symbols that the processor can't process"
    // and in theory we have none
    return emptyList()
  }

  private fun processClass(klass: KSClassDeclaration) {
    // check that it is sealed or data
    if (!klass.isSealed && !klass.isData) {
      logger.error(klass.simpleName.asString().otherClassTypeErrorMessage, klass)
      return
    }

    // check that the companion object exists
    val companion = klass.companionObject
    if (companion == null) {
      logger.error(klass.simpleName.asString().noCompanion, klass)
      return
    }

    adt(klass, logger).snippets().groupBy(Snippet::fqName).values.map(List<Snippet>::join).forEach {
      val writer =
        codegen
          .createNewFile(
            Dependencies(aggregating = true, *listOfNotNull(klass.containingFile).toTypedArray()),
            it.`package`,
            it.name
          )
          .writer()
      writer.write(it.asFileText())
      writer.flush()
    }
  }
}
