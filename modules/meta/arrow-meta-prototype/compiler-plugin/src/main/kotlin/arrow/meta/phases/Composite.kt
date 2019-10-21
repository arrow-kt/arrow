package arrow.meta.phases

data class Composite(val phases: List<ExtensionPhase>): ExtensionPhase {
  companion object {
    operator fun invoke(vararg phases: ExtensionPhase): ExtensionPhase =
      Composite(phases.toList())
  }
}