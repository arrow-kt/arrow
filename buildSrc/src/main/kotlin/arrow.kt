import org.gradle.api.provider.Provider
import org.gradle.api.provider.ProviderFactory
import org.gradle.kotlin.dsl.assign
import org.jetbrains.kotlin.gradle.dsl.KotlinCommonCompilerOptions
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

fun Provider<String>.ifAvailable(block: (String) -> Unit) =
  orNull?.takeIf(String::isNotBlank)?.also(block)

fun KotlinCommonCompilerOptions.commonCompilerOptions(providers: ProviderFactory) {
  apiVersion = KotlinVersion.KOTLIN_2_3
  languageVersion = KotlinVersion.KOTLIN_2_3
  freeCompilerArgs.addAll(
    "-Xreport-all-warnings",
    "-Xrender-internal-diagnostic-names",
    "-Xreturn-value-checker=full",
    "-Xexpect-actual-classes",
    "-Xcontext-parameters",
    "-Xcollection-literals",
    "-Xname-based-destructuring=name-mismatch",
    "-Xwarning-level=ERROR_SUPPRESSION:disabled",
    "-Xwarning-level=NOTHING_TO_INLINE:disabled",
    "-Xwarning-level=DSL_MARKER_APPLIED_TO_WRONG_TARGET:disabled",
    "-Xwarning-level=RETURN_VALUE_NOT_USED_COERCION:disabled",
  )
  // required to be part of the Kotlin User Projects repository
  providers.gradleProperty("kotlin_language_version").ifAvailable { languageVersion = KotlinVersion.fromVersion(it) }
  providers.gradleProperty("kotlin_api_version").ifAvailable { apiVersion = KotlinVersion.fromVersion(it) }
  providers.gradleProperty("kotlin_additional_cli_options").ifAvailable { freeCompilerArgs.addAll(it.split(" ")) }
}
