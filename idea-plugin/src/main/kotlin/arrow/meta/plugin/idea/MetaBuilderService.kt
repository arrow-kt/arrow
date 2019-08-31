package arrow.meta.plugin.idea

import org.jetbrains.jps.builders.BuildTargetType
import org.jetbrains.jps.incremental.BuilderService
import org.jetbrains.jps.incremental.ModuleLevelBuilder
import org.jetbrains.jps.incremental.TargetBuilder

class MetaBuilderService : BuilderService() {
  override fun createModuleLevelBuilders(): MutableList<out ModuleLevelBuilder> =
    super.createModuleLevelBuilders().run {
      println("MetaBuilderService.createModuleLevelBuilders: $this")
      this
    }

  override fun getTargetTypes(): MutableList<out BuildTargetType<*>> =
    super.getTargetTypes().run {
      println("MetaBuilderService.getTargetTypes: $this")
      this
    }

  override fun createBuilders(): MutableList<out TargetBuilder<*, *>> =
    super.createBuilders().run {
      println("MetaBuilderService.createBuilders: $this")
      this
    }
}