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
 * Any other visibility (`local`, `private-to-this`, ...) is treated as `private`.
 */
fun mostRestrictive(a: Visibility, b: Visibility): Visibility {
  val na = normalized(a)
  val nb = normalized(b)
  return when {
    na == Visibilities.Public -> nb

    nb == Visibilities.Public -> na

    na == Visibilities.Private || nb == Visibilities.Private -> Visibilities.Private

    na == nb -> na

    // mixing internal and protected
    else -> Visibilities.Private
  }
}

private fun normalized(v: Visibility): Visibility = when (v.normalize()) {
  Visibilities.Public, Visibilities.Internal, Visibilities.Protected, Visibilities.Private -> v.normalize()
  else -> Visibilities.Private
}

/**
 * Visibility of a generated companion member whose body relies on declarations with the given
 * [visibilities] (constructors, properties, subclasses). `protected` collapses to `private`: the
 * classes whose constructors and properties are used are final, so both mean the same there.
 */
fun memberVisibility(visibilities: Iterable<Visibility>): Visibility = visibilities
  .map { if (normalized(it) == Visibilities.Protected) Visibilities.Private else it }
  .fold(Visibilities.Public, ::mostRestrictive)

/** Whether a top-level declaration may have [visibility] and still be usable from the source's file. */
fun isUsableTopLevelVisibility(visibility: Visibility): Boolean = normalized(visibility).let { it == Visibilities.Public || it == Visibilities.Internal }
