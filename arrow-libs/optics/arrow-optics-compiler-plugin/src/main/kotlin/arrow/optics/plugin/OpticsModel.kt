package arrow.optics.plugin

import org.jetbrains.kotlin.descriptors.Visibilities
import org.jetbrains.kotlin.descriptors.Visibility
import org.jetbrains.kotlin.name.Name

/** The kind of an `@optics`-annotated source class. */
enum class OpticsClassKind { DATA, VALUE, SEALED, INELIGIBLE }

/** A user-facing generation target, mirroring `arrow.optics.OpticsTarget` plus COPY. */
enum class OpticsTargetKind { ISO, LENS, PRISM, DSL, COPY }

/** The base optic actually produced for a single focus. */
enum class OpticKind { ISO, LENS, PRISM }

/** The optic kind of the *outer* optic in a DSL composition extension. */
enum class DslKind { ISO, LENS, PRISM, OPTIONAL, TRAVERSAL }

/**
 * Which outer-optic variants are produced for a base optic of [kind] (algo §8.2):
 * exactly the kinds `X` for which `X` composed with the base kind is still an `X`.
 */
fun dslVariantsFor(kind: OpticKind): List<DslKind> = when (kind) {
  OpticKind.LENS -> listOf(DslKind.LENS, DslKind.OPTIONAL, DslKind.TRAVERSAL)
  OpticKind.PRISM -> listOf(DslKind.OPTIONAL, DslKind.PRISM, DslKind.TRAVERSAL)
  OpticKind.ISO -> listOf(DslKind.ISO, DslKind.LENS, DslKind.OPTIONAL, DslKind.PRISM, DslKind.TRAVERSAL)
}

/**
 * Compute the effective target set for a class, per algo §2.3:
 * read the annotation's targets (OPTIONAL dropped), default to everything when empty,
 * then intersect with what the class kind supports, and add COPY when requested.
 *
 * @param annotationTargets the names found in `@optics(targets = [...])`, or `null`/empty for the no-arg case.
 */
fun computeTargets(
  kind: OpticsClassKind,
  annotationTargets: Set<OpticsTargetKind>,
  hasCopy: Boolean,
): Set<OpticsTargetKind> {
  val requested =
    annotationTargets.ifEmpty { setOf(OpticsTargetKind.ISO, OpticsTargetKind.LENS, OpticsTargetKind.PRISM, OpticsTargetKind.DSL) }
  val allowed = when (kind) {
    OpticsClassKind.SEALED -> setOf(OpticsTargetKind.PRISM, OpticsTargetKind.LENS, OpticsTargetKind.DSL)
    OpticsClassKind.VALUE -> setOf(OpticsTargetKind.ISO, OpticsTargetKind.DSL)
    OpticsClassKind.DATA -> setOf(OpticsTargetKind.LENS, OpticsTargetKind.DSL)
    OpticsClassKind.INELIGIBLE -> emptySet()
  }
  return buildSet {
    addAll(requested intersect allowed)
    if (hasCopy && kind != OpticsClassKind.INELIGIBLE) add(OpticsTargetKind.COPY)
  }
}

/** The optic name for a PRISM focus: subclass simple name with the first letter lowercased (algo §3.2). */
fun lowercaseFirst(name: Name): Name {
  val s = name.identifierOrNullIfSpecial ?: return name
  if (s.isEmpty() || !s[0].isUpperCase()) return name
  return Name.identifier(s.replaceFirstChar { it.lowercaseChar() })
}

/**
 * Most-restrictive combination of two visibilities (algo §3.3).
 * `public` is the identity; `private` dominates; `internal` and `protected` collapse to `private`.
 */
fun mostRestrictive(a: Visibility, b: Visibility): Visibility = when {
  a == Visibilities.Public -> b
  b == Visibilities.Public -> a
  a == Visibilities.Private || b == Visibilities.Private -> Visibilities.Private
  a == b -> a
  // mixing internal and protected
  else -> Visibilities.Private
}
