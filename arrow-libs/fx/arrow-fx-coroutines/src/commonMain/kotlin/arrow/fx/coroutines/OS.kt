package arrow.fx.coroutines

internal expect object OS {
  /**
   * Used to detect if we are running on an Apple OS.
   */
  public val isApple: Boolean
}
