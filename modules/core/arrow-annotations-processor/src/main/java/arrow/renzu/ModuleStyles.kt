package arrow.renzu

/**
 * Renzu module will be parsed for test samples. We want to normalize that one to be treated as standard instances.
 */
fun normalizeModule(module: String) = if (module == "renzu") "instances" else module

/**
 * Provides nomnoml formatted style to use per module. That means different colors and font style per module.
 */
fun getModuleStyle(module: String): String = when (module) {
  "typeclasses" -> "fill=#64B5F6 visual=database bold"
  "instances" -> "fill=#B9F6CA visual=class italic bold dashed"
  else -> "fill=#B9F6CA visual=class italic bold dashed"
}
