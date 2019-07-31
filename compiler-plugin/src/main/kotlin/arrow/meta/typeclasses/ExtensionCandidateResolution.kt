package arrow.meta.typeclasses

sealed class ExtensionCandidateResolution {
  data class Resolved(val candidate: ExtensionCandidate) : ExtensionCandidateResolution()
  data class Unresolved(val message: String) : ExtensionCandidateResolution()
}