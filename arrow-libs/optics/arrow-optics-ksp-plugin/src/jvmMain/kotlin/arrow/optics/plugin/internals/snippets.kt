package arrow.optics.plugin.internals

internal fun ADT.snippets(): List<Snippet> =
  targets.map {
    when (it) {
      is IsoTarget -> generateIsos(this, it)
      is PrismTarget -> generatePrisms(this, it)
      is LensTarget -> generateLenses(this, it)
      is OptionalTarget -> generateOptionals(this, it)
      is SealedClassDsl -> generatePrismDsl(this, it)
      is DataClassDsl -> generateOptionalDsl(this, it) + generateLensDsl(this, it)
    }
  }

internal fun List<Snippet>.join() = reduce { acc, snippet ->
  acc.copy(imports = acc.imports + snippet.imports, content = "${acc.content}\n${snippet.content}")
}
