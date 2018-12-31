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

const val ANSI_RESET = "\u001B[0m"
const val ANSI_BLACK = "\u001B[30m"
const val ANSI_RED = "\u001B[31m"
const val ANSI_GREEN = "\u001B[32m"
const val ANSI_YELLOW = "\u001B[33m"
const val ANSI_BLUE = "\u001B[34m"
const val ANSI_PURPLE = "\u001B[35m"
const val ANSI_CYAN = "\u001B[36m"
const val ANSI_WHITE = "\u001B[37m"

fun colored(color: String, message: String) =
  "$color$message${ANSI_RESET}"

val AnkHeader =
  """
            |      :::     ::::    ::: :::    :::
            |    :+: :+:   :+:+:   :+: :+:   :+:
            |   +:+   +:+  :+:+:+  +:+ +:+  +:+
            |  +#+     ++: +#+ +:+ +#+ +#++:++
            |  +#+     +#+ +#+  +#+#+# +#+  +#+
            |  #+#     #+# #+#   #+#+# #+#   #+#
            |  ###     ### ###    #### ###    ###
            """.trimMargin()
