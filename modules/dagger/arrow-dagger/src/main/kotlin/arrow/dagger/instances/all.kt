package arrow.dagger.instances

import dagger.Module

@Module(includes = [
    EvalInstances::class,
    Function0Instances::class,
    IdInstances::class,
    ListKWInstances::class,
    NonEmptyListInstances::class,
    NumberInstances::class,
    OptionInstances::class,
    SequenceKWInstances::class,
    SetKWInstances::class,
    StringInstances::class,
    TryInstances::class
])
abstract class ArrowInstances