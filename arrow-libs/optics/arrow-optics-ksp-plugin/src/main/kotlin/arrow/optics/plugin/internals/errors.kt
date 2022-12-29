package arrow.optics.plugin.internals

val String.otherClassTypeErrorMessage
  get() =
    """
      |$this cannot be annotated with @optics
      | ^
      |Only data and sealed classes can be annotated with @optics""".trimMargin()

val String.lensErrorMessage
  get() =
    """
      |Cannot generate arrow.optics.Lens for $this
      |                                       ^
      |arrow.optics.OpticsTarget.LENS is an invalid @optics argument for $this.
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

val String.noCompanion
  get() =
    """
      |$this must declare a companion object
      | ^
      |A companion object is required for the generated optics""".trimMargin()
