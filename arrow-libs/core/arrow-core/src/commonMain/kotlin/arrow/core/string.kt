package arrow.core

public fun String.escaped(): String =
  replace("\n", "\\n").replace("\r", "\\r")
    .replace("\"", "\\\"").replace("\'", "\\\'")
    .replace("\t", "\\t").replace("\b", "\\b")
