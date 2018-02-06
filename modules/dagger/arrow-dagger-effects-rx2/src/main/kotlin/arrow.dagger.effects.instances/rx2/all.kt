package arrow.dagger.effects.instances.rx2

import dagger.Module

@Module(includes = [
    ObservableKWInstances::class,
    FlowableKWInstances::class
])
abstract class ArrowEffectsRx2Instances