package arrow.optics.plugin

data class OpticsProcessorOptions(
  val useInline: Boolean,
) {
  val inlineText: String = if (useInline) "inline" else ""

  companion object {
    fun from(options: Map<String, String>): OpticsProcessorOptions =
      OpticsProcessorOptions(
        useInline = options.getOrDefault("inline", "true").toBooleanStrict(),
      )
  }
}
