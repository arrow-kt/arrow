repositories {
  mavenCentral()
  maven(url = "http://dl.bintray.com/arturbosch/code-analysis")
}

val detektConf by configurations.creating

dependencies {
  detektConf("io.gitlab.arturbosch.detekt:detekt-cli:1.0.0.M13")
  detektConf("io.gitlab.arturbosch.detekt:detekt-formatting:1.0.0.M13")
}

val detekt by tasks.creating(JavaExec::class) {
  group = "verification"
  main = "io.gitlab.arturbosch.detekt.cli.Main"
  classpath = detektConf
  val input = "${project.projectDir.absolutePath}"
  val config = "${project.projectDir}/detekt.yml"
  val reports = "${project.projectDir.absolutePath}/reports/report.detekt"
  val baseline = "${project.projectDir.absolutePath}/reports/baseline.xml"
  val filters = ".*test.*"
  val rulesets = ""
  val params = listOf("-p", input, "-c", config, "-f", filters, "-r", rulesets, "-o", reports, "-b", baseline)
  args(params)
}

val detektEstablishAcceptedErrors by tasks.creating(JavaExec::class) {
  group = "verification"
  main = "io.gitlab.arturbosch.detekt.cli.Main"
  classpath = detektConf
  val input = "${project.projectDir.absolutePath}"
  val config = "${project.projectDir}/detekt.yml"
  val reports = "${project.projectDir.absolutePath}/reports/report.detekt"
  val baseline = "${project.projectDir.absolutePath}/reports/baseline.xml"
  val filters = ".*test.*"
  val rulesets = ""
  val params = listOf("-p", input, "-c", config, "-f", filters, "-r", rulesets, "-o", reports, "-b", baseline, "-cb")
  args(params)
}