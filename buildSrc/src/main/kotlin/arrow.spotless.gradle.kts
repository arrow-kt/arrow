import org.gradle.kotlin.dsl.configure

plugins.apply("com.diffplug.spotless")

configure<com.diffplug.gradle.spotless.SpotlessExtension> {
  kotlin {
    ktlint().editorConfigOverride(mapOf(
      "ktlint_standard_filename" to "disabled",
      "ktlint_standard_backing-property-naming" to "disabled",
      "ktlint_standard_property-naming" to "disabled"
    ))
  }
}
