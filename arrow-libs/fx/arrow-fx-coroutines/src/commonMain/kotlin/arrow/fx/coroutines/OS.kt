package arrow.fx.coroutines

public expect object OS {
  /**
   * Used to detect if we are running on an Apple OS.
   */
  public val isApple: Boolean
}
