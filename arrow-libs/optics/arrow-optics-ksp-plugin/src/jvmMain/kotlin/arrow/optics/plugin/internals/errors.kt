package arrow.optics.plugin.internals

val String.otherClassTypeErrorMessage
  get() =
    """
      |$this cannot be annotated with @optics
      | ^
      |
      |Only data and sealed classes can be annotated with @optics annotation""".trimMargin()

val String.lensErrorMessage
  get() =
    """
      |Cannot generate arrow.optics.Lens for $this
      |                                       ^
      |arrow.optics.OpticsTarget.LENS is an invalid @optics argument for $this.
      |It is only valid for data classes.
      """.trimMargin()

val String.optionalErrorMessage
  get() =
    """
      |Cannot generate arrow.optics.Optional for $this
      |                                           ^
      |arrow.optics.OpticsTarget.OPTIONAL is an invalid @optics argument for $this.
      |It is only valid for data classes.
      """.trimMargin()

val String.prismErrorMessage
  get() =
    """
      |Cannot generate arrow.optics.Prism for $this
      |                                        ^
      |arrow.optics.OpticsTarget.PRISM is an invalid @optics argument for $this.
      |It is only valid for sealed classes.
      """.trimMargin()

val String.isoErrorMessage
  get() =
    """
      |Cannot generate arrow.optics.Iso for $this
      |                                      ^
      |arrow.optics.OpticsTarget.ISO is an invalid @optics argument for $this.
      |It is only valid for data classes.
      """.trimMargin()

val String.isoTooBigErrorMessage
  get() =
    """
      |Cannot generate arrow.optics.Iso for $this
      |                                      ^
      |Iso generation is supported for data classes with up to 22 constructor parameters.
      """.trimMargin()

val String.dslErrorMessage
  get() =
    """
      |Cannot generate DSL (arrow.optics.BoundSetter) for $this
      |                                           ^
      |arrow.optics.OpticsTarget.DSL is an invalid @optics argument for $this.
      |It is only valid for data classes and sealed classes.
      """.trimMargin()

val String.noCompanion
  get() = "@optics annotated class $this needs to declare companion object."
