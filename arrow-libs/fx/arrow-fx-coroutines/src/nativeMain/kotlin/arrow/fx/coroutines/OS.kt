package arrow.fx.coroutines

import kotlin.native.Platform

public actual object OS {
  public actual val isApple: Boolean = when (Platform.osFamily) {
    OsFamily.MACOSX, OsFamily.IOS, OsFamily.TVOS, OsFamily.WATCHOS -> true
    else -> false
  }
}
