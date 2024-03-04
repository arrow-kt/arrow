package arrow.optics.plugin.internals

val String.sealedLensNoTargets
  get() =
    """
      |Cannot generate arrow.optics.Lens for $this 
      |                                       ^
      |To generate Lens for a sealed class make sure it has abstract properties without extension receiver.
    """.trimMargin()

val String.sealedLensNonDataClassChildren
  get() =
    """
      |Cannot generate arrow.optics.Lens for $this 
      |                                       ^
      |To generate Lens for a sealed class make sure it has only data class subclasses.
    """.trimMargin()

val String.sealedLensConstructorOverridesOnly
  get() =
    """
      |Cannot generate arrow.optics.Lens for $this 
      |                                       ^
      |To generate Lens for a sealed class make sure all children override target properties in constructors.
    """.trimMargin()

