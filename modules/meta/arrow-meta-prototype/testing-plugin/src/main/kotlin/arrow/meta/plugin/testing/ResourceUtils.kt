package arrow.meta.plugin.testing

fun contentFromResource(fromClass: Class<Any>, resourceName: String): String =
  fromClass.getResource(resourceName).readText()
