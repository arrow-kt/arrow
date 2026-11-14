import org.gradle.api.publish.PublishingExtension
import org.gradle.kotlin.dsl.configure

plugins.apply("com.vanniktech.maven.publish.base")

configure<com.vanniktech.maven.publish.MavenPublishBaseExtension> {
  configureBasedOnAppliedPlugins()
  pomFromGradleProperties()
  if (project.findProperty("onlyLocal")?.toString()?.toBooleanStrict() != true) {
    publishToMavenCentral(automaticRelease = true)
    signAllPublications()
  }
}

if (project.findProperty("onlyLocal")?.toString()?.toBooleanStrict() == true) {
  configure<PublishingExtension> {
    repositories {
      maven {
        name = "localPluginRepository"
        url = uri("${rootProject.projectDir.absolutePath}/build/local-plugin-repository")
      }
    }
  }
}
