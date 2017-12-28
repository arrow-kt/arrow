package arrow.ank

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.DependencyResolutionListener
import org.gradle.api.artifacts.ResolvableDependencies
import org.gradle.api.tasks.JavaExec

class AnkPlugin : Plugin<Project> {

    companion object {
        private const val EXTENSION_NAME = "ank"
        private const val TASK_NAME = "runAnk"
        private const val ANK_CORE_DEPENDENCY = "io.arrow:ank-core:0.1.9-SNAPSHOT"
    }

    override fun apply(target: Project) {
        val compileDeps = target.configurations.getByName("compile").dependencies
        val extension = AnkExtension()
        target.extensions.add(EXTENSION_NAME, extension)
        target.gradle.addListener(object : DependencyResolutionListener {
            override fun beforeResolve(resolvableDependencies: ResolvableDependencies) {
                if (extension.includeAnkCore) {
                    compileDeps.add(target.dependencies.create(ANK_CORE_DEPENDENCY))
                }
                target.gradle.removeListener(this)
            }

            override fun afterResolve(resolvableDependencies: ResolvableDependencies) {}
        })
        target.afterEvaluate {
            target.tasks.create(TASK_NAME, JavaExec::class.java).apply {
                classpath = extension.classpath
                main = "arrow.ank.main"
            }.args = mutableListOf(
                    extension.source!!.absolutePath,
                    extension.target!!.absolutePath,
                    *extension.classpath!!.map { it.toURI().toURL().toString() }.toTypedArray()
            )
        }
    }
}