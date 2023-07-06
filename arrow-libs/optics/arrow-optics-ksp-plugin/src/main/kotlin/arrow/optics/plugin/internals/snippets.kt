package arrow.optics.plugin.internals

import arrow.optics.plugin.OpticsProcessorOptions

internal fun ADT.snippets(options: OpticsProcessorOptions): List<Snippet> =
  targets.map {
    when (it) {
      is IsoTarget -> options.generateIsos(this, it)
      is PrismTarget -> options.generatePrisms(this, it)
      is LensTarget -> options.generateLenses(this, it)
      is OptionalTarget -> options.generateOptionals(this, it)
      is SealedClassDsl -> generatePrismDsl(this, it)
      is DataClassDsl -> generateOptionalDsl(this, it) + generateLensDsl(this, it)
      is ValueClassDsl -> generateIsoDsl(this, it)
    }
  }

internal fun List<Snippet>.join() = reduce { acc, snippet ->
  acc.copy(imports = acc.imports + snippet.imports, content = "${acc.content}\n${snippet.content}")
}
