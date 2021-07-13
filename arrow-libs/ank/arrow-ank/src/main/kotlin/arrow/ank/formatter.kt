package arrow.ank

private fun getMaxLength(strings: List<String>): Int {
  var len = Integer.MIN_VALUE
  for (str in strings) {
    len = Math.max(str.length, len)
  }
  return len
}

private fun padString(str: String, len: Int): String {
  val sb = StringBuilder(str)
  return sb.append(fill(' ', len - str.length)).toString()
}

private fun fill(ch: Char, len: Int): String {
  val sb = StringBuilder(len)
  for (i in 0 until len) {
    sb.append(ch)
  }
  return sb.toString()
}

public const val ANSI_RESET: String = "\u001B[0m"
public const val ANSI_BLACK: String = "\u001B[30m"
public const val ANSI_RED: String = "\u001B[31m"
public const val ANSI_GREEN: String = "\u001B[32m"
public const val ANSI_YELLOW: String = "\u001B[33m"
public const val ANSI_BLUE: String = "\u001B[34m"
public const val ANSI_PURPLE: String = "\u001B[35m"
public const val ANSI_CYAN: String = "\u001B[36m"
public const val ANSI_WHITE: String = "\u001B[37m"

public fun colored(color: String, message: String): String =
  "$color$message$ANSI_RESET"

public val AnkHeader: String =
  """
            |      :::     ::::    ::: :::    :::
            |    :+: :+:   :+:+:   :+: :+:   :+:
            |   +:+   +:+  :+:+:+  +:+ +:+  +:+
            |  +#+     ++: +#+ +:+ +#+ +#++:++
            |  +#+     +#+ +#+  +#+#+# +#+  +#+
            |  #+#     #+# #+#   #+#+# #+#   #+#
            |  ###     ### ###    #### ###    ###
            """.trimMargin()
