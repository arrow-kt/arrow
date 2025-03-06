class JvmSpec : SuspendAppTest() {
  override fun prepareProcess(mode: String) =
    ProcessBuilder("java", "-jar", System.getProperty("jvmJar"), mode)
}
