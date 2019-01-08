package arrow.dagger.effects.extensions.coroutines

import dagger.Module

@Module(includes = [
  DeferredKInstances::class
])
abstract class ArrowEffectsCoroutinesInstances