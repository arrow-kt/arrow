package arrow.optics.plugin

data class OpticsProcessorOptions(
  val useInline: Boolean,
  val companionCheck: Boolean = true,
) {
  val inlineText: String = if (useInline) "inline" else ""

  companion object {
    fun from(options: Map<String, String>): OpticsProcessorOptions = OpticsProcessorOptions(
      useInline = options.get("inline")?.toBooleanStrict() ?: false,
      companionCheck = options.get("companionCheck")?.toBooleanStrict() ?: true,
    )
  }
}
