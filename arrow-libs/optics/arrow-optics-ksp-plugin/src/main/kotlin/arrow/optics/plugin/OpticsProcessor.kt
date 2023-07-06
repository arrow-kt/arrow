package arrow.optics.plugin

import arrow.optics.plugin.internals.Snippet
import arrow.optics.plugin.internals.adt
import arrow.optics.plugin.internals.asFileText
import arrow.optics.plugin.internals.join
import arrow.optics.plugin.internals.noCompanion
import arrow.optics.plugin.internals.otherClassTypeErrorMessage
import arrow.optics.plugin.internals.qualifiedNameOrSimpleName
import arrow.optics.plugin.internals.snippets
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration

class OpticsProcessor(
  private val codegen: CodeGenerator,
  private val logger: KSPLogger,
  private val options: OpticsProcessorOptions,
) :
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
    if (!klass.isSealed && !klass.isDataClass && !klass.isValue) {
      logger.error(klass.qualifiedNameOrSimpleName.otherClassTypeErrorMessage, klass)
      return
    }

    // check that the companion object exists
    if (klass.companionObject == null) {
      logger.error(klass.qualifiedNameOrSimpleName.noCompanion, klass)
      return
    }

    val adts = adt(klass, logger)
    val snippets = adts.snippets(options)
    snippets.groupBy(Snippet::fqName).values.map(List<Snippet>::join).forEach {
      val writer =
        codegen
          .createNewFile(
            Dependencies(aggregating = true, *listOfNotNull(klass.containingFile).toTypedArray()),
            it.`package`,
            it.name + "__Optics",
          )
          .writer()
      writer.write(it.asFileText())
      writer.flush()
    }
  }
}
