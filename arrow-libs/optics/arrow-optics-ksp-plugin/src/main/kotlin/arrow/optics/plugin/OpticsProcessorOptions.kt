package arrow.optics.plugin

data class OpticsProcessorOptions(
  val useInline: Boolean,
  val companionCheck: Boolean = true,
) {
  val inlineText: String = if (useInline) "inline" else ""

  companion object {
    fun from(options: Map<String, String>): OpticsProcessorOptions = OpticsProcessorOptions(
      useInline = options["inline"]?.toBooleanStrict() ?: false,
      companionCheck = options["companionCheck"]?.toBooleanStrict() ?: true,
    )
  }
}
