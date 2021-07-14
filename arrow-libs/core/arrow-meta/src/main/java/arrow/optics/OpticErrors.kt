package arrow.optics

import javax.lang.model.element.Element

public val Element.otherClassTypeErrorMessage: String
  get() = """
      |$this cannot be annotated with @optics
      | ^
      |
      |Only data and sealed classes can be annotated with @optics annotation""".trimMargin()

public val Element.lensErrorMessage: String
  get() = """
      |Cannot generate arrow.optics.Lens for $this
      |                                       ^
      |arrow.optics.OpticsTarget.LENS is an invalid @optics argument for $this.
      |It is only valid for data classes.
      """.trimMargin()

public val Element.optionalErrorMessage: String
  get() = """
      |Cannot generate arrow.optics.Optional for $this
      |                                           ^
      |arrow.optics.OpticsTarget.OPTIONAL is an invalid @optics argument for $this.
      |It is only valid for data classes.
      """.trimMargin()

public val Element.prismErrorMessage: String
  get() = """
      |Cannot generate arrow.optics.Prism for $this
      |                                        ^
      |arrow.optics.OpticsTarget.PRISM is an invalid @optics argument for $this.
      |It is only valid for sealed classes.
      """.trimMargin()

public val Element.isoErrorMessage: String
  get() = """
      |Cannot generate arrow.optics.Iso for $this
      |                                      ^
      |arrow.optics.OpticsTarget.ISO is an invalid @optics argument for $this.
      |It is only valid for data classes.
      """.trimMargin()

public val Element.isoTooBigErrorMessage: String
  get() = """
      |Cannot generate arrow.optics.Iso for $this
      |                                      ^
      |Iso generation is supported for data classes with up to 22 constructor parameters.
      """.trimMargin()

public val Element.dslErrorMessage: String
  get() = """
      |Cannot generate DSL (arrow.optics.BoundSetter) for $this
      |                                           ^
      |arrow.optics.OpticsTarget.DSL is an invalid @optics argument for $this.
      |It is only valid for data classes and sealed classes.
      """.trimMargin()
