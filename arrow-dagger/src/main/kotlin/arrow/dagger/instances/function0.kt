package arrow.dagger.instances

import arrow.data.*
import arrow.typeclasses.*
import dagger.Module
import dagger.Provides

@Module
class Function0Instances {

    @Provides
    fun function0Functor(): Functor<Function0HK> = Function0.functor()

    @Provides
    fun function0Applicative(): Applicative<Function0HK> = Function0.applicative()

    @Provides
    fun function0Monad(): Monad<Function0HK> = Function0.monad()

    @Provides
    fun function0Comonad(): Comonad<Function0HK> = Function0.comonad()

    @Provides
    fun function0Bimonad(): Bimonad<Function0HK> = Function0.bimonad()

}

