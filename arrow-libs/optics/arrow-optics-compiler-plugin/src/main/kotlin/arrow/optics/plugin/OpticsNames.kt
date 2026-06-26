package arrow.optics.plugin

import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

/**
 * Central registry of the fully-qualified names of the `arrow.optics` API that the
 * generated optics refer to. FIR uses these to build cone types; IR uses them to
 * resolve external symbols.
 */
object OpticsNames {
  val ARROW_OPTICS_PACKAGE = FqName("arrow.optics")

  val OPTICS_ANNOTATION = ClassId(ARROW_OPTICS_PACKAGE, Name.identifier("optics"))
  val OPTICS_ANNOTATION_FQNAME: FqName = OPTICS_ANNOTATION.asSingleFqName()
  val OPTICS_COPY_ANNOTATION =
    OPTICS_ANNOTATION.createNestedClassId(Name.identifier("copy"))
  val OPTICS_COPY_ANNOTATION_FQNAME: FqName = OPTICS_COPY_ANNOTATION.asSingleFqName()

  val OPTICS_TARGET = ClassId(ARROW_OPTICS_PACKAGE, Name.identifier("OpticsTarget"))

  // Optic type aliases (the user-facing names, 2 type arguments each).
  val LENS = ClassId(ARROW_OPTICS_PACKAGE, Name.identifier("Lens"))
  val ISO = ClassId(ARROW_OPTICS_PACKAGE, Name.identifier("Iso"))
  val PRISM = ClassId(ARROW_OPTICS_PACKAGE, Name.identifier("Prism"))
  val OPTIONAL = ClassId(ARROW_OPTICS_PACKAGE, Name.identifier("Optional"))
  val TRAVERSAL = ClassId(ARROW_OPTICS_PACKAGE, Name.identifier("Traversal"))

  // Underlying poly interfaces (these carry the companion objects with the factories).
  val PLENS = ClassId(ARROW_OPTICS_PACKAGE, Name.identifier("PLens"))
  val PISO = ClassId(ARROW_OPTICS_PACKAGE, Name.identifier("PIso"))
  val PPRISM = ClassId(ARROW_OPTICS_PACKAGE, Name.identifier("PPrism"))
  val POPTIONAL = ClassId(ARROW_OPTICS_PACKAGE, Name.identifier("POptional"))
  val PTRAVERSAL = ClassId(ARROW_OPTICS_PACKAGE, Name.identifier("PTraversal"))

  private val INVOKE = Name.identifier("invoke")
  private val PLUS = Name.identifier("plus")

  val PLENS_COMPANION = PLENS.createNestedClassId(Name.identifier("Companion"))
  val PISO_COMPANION = PISO.createNestedClassId(Name.identifier("Companion"))
  val PPRISM_COMPANION = PPRISM.createNestedClassId(Name.identifier("Companion"))

  val LENS_INVOKE = CallableId(PLENS_COMPANION, INVOKE)
  val ISO_INVOKE = CallableId(PISO_COMPANION, INVOKE)
  val PRISM_INSTANCE_OF = CallableId(PPRISM_COMPANION, Name.identifier("instanceOf"))

  val ISO_PLUS = CallableId(PISO, PLUS)
  val LENS_PLUS = CallableId(PLENS, PLUS)
  val PRISM_PLUS = CallableId(PPRISM, PLUS)
  val OPTIONAL_PLUS = CallableId(POPTIONAL, PLUS)
  val TRAVERSAL_PLUS = CallableId(PTRAVERSAL, PLUS)

  val ARROW_OPTICS_COPY = CallableId(ARROW_OPTICS_PACKAGE, Name.identifier("copy"))
  val COPY = ClassId(ARROW_OPTICS_PACKAGE, Name.identifier("Copy"))

  /** Poly-interface ClassId for a DSL outer-optic kind. */
  fun polyClassFor(kind: DslKind): ClassId = when (kind) {
    DslKind.ISO -> PISO
    DslKind.LENS -> PLENS
    DslKind.PRISM -> PPRISM
    DslKind.OPTIONAL -> POPTIONAL
    DslKind.TRAVERSAL -> PTRAVERSAL
  }

  /** `plus` CallableId for a DSL outer-optic kind. */
  fun plusFor(kind: DslKind): CallableId = when (kind) {
    DslKind.ISO -> ISO_PLUS
    DslKind.LENS -> LENS_PLUS
    DslKind.PRISM -> PRISM_PLUS
    DslKind.OPTIONAL -> OPTIONAL_PLUS
    DslKind.TRAVERSAL -> TRAVERSAL_PLUS
  }
}
