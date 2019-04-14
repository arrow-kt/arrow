package arrow.dagger.extensions

import dagger.Module

@Module(includes = [
  EvalInstances::class,
  Function0Instances::class,
  IdInstances::class,
  ListKInstances::class,
  NonEmptyListInstances::class,
  NumberInstances::class,
  OptionInstances::class,
  SequenceKInstances::class,
  SetKInstances::class,
  StringInstances::class,
  TryInstances::class
])
abstract class ArrowInstances