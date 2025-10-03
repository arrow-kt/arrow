package arrow.optics.plugin

import com.google.devtools.ksp.gradle.KspAATask
import com.google.devtools.ksp.gradle.KspExtension
import com.google.devtools.ksp.gradle.KspGradleSubplugin
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Provider
import org.gradle.internal.extensions.stdlib.capitalized
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinSingleTargetExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilerPluginSupportPlugin
import org.jetbrains.kotlin.gradle.plugin.KotlinTarget
import org.jetbrains.kotlin.gradle.plugin.SubpluginArtifact
import org.jetbrains.kotlin.gradle.plugin.SubpluginOption

fun KotlinSingleTargetExtension<*>.arrowOptics() {
  project.dependencies.add("ksp", BuildConfig.KSP_PLUGIN_LIBRARY_COORDINATES)

  for (sourceSet in sourceSets) {
    sourceSet.dependencies {
      implementation(BuildConfig.ANNOTATIONS_LIBRARY_COORDINATES)
      implementation(BuildConfig.OPTICS_LIBRARY_COORDINATES)
    }
  }
}

fun KotlinMultiplatformExtension.arrowOpticsCommon() =
  arrowOptics(metadata())

fun KotlinMultiplatformExtension.arrowOptics(target: KotlinTarget, vararg moreTargets: KotlinTarget) =
  arrowOptics(listOf(target) + moreTargets.toList())

fun KotlinMultiplatformExtension.arrowOptics(targets: List<KotlinTarget> = this.targets.toList()) {
  for (target in targets) {
    val miniTarget = target.name.lowercase()

    // add 'kspThing' to every source set
    val kspTargetName = when (miniTarget) {
      "metadata" -> "CommonMainMetadata"
      else -> target.name.capitalized()
    }
    project.dependencies.add(
      "ksp$kspTargetName",
      BuildConfig.KSP_PLUGIN_LIBRARY_COORDINATES
    )

    for (compilation in target.compilations) {
      for (sourceSet in compilation.kotlinSourceSets) {
        sourceSet.kotlin.srcDir("build/generated/ksp/${target.name}/${sourceSet.name}")
        sourceSet.dependencies {
          implementation(BuildConfig.ANNOTATIONS_LIBRARY_COORDINATES)
          implementation(BuildConfig.OPTICS_LIBRARY_COORDINATES)
        }
      }
    }
  }

  project.afterEvaluate {
    project.tasks.withType(KspAATask::class.java).configureEach {
      if (it.name != "kspCommonMainKotlinMetadata") {
        it.dependsOn("kspCommonMainKotlinMetadata")
      }
    }
  }
}

class ArrowOpticsPlugin : KotlinCompilerPluginSupportPlugin {
  override fun apply(target: Project) {
    target.pluginManager.apply(KspGradleSubplugin::class.java)
    target.extensions.configure(KspExtension::class.java) {
      it.arg("companionCheck", "false")
    }
    target.extensions.create("optics", OpticsGradleExtension::class.java)

  }

  override fun applyToCompilation(kotlinCompilation: KotlinCompilation<*>): Provider<List<SubpluginOption>> =
    kotlinCompilation.target.project.provider { emptyList() }

  override fun getCompilerPluginId(): String = BuildConfig.KOTLIN_PLUGIN_ID

  override fun getPluginArtifact(): SubpluginArtifact = SubpluginArtifact(
    groupId = BuildConfig.KOTLIN_PLUGIN_GROUP,
    artifactId = BuildConfig.KOTLIN_PLUGIN_NAME,
    version = BuildConfig.KOTLIN_PLUGIN_VERSION,
  )

  override fun isApplicable(kotlinCompilation: KotlinCompilation<*>): Boolean = true
}

open class OpticsGradleExtension(objectFactory: ObjectFactory)
