package arrow.dagger.instances

import arrow.core.*
import arrow.instances.*
import arrow.typeclasses.*
import dagger.Module
import dagger.Provides
import javax.inject.Inject

@Module
abstract class Tuple2Instances<A, B> {
    @Provides
    fun tuple2EqInstance(ev: DaggerTuple2EqInstance<A, B>): Eq<Tuple2<A, B>> = ev
}

@Module
abstract class Tuple3Instances<A, B, C> {
    @Provides
    fun tuple3EqInstance(ev: DaggerTuple3EqInstance<A, B, C>): Eq<Tuple3<A, B, C>> = ev
}

@Module
abstract class Tuple4Instances<A, B, C, D> {
    @Provides
    fun tuple4EqInstance(ev: DaggerTuple4EqInstance<A, B, C, D>): Eq<Tuple4<A, B, C, D>> = ev
}

@Module
abstract class Tuple5Instances<A, B, C, D, E> {
    @Provides
    fun tuple5EqInstance(ev: DaggerTuple5EqInstance<A, B, C, D, E>): Eq<Tuple5<A, B, C, D, E>> = ev
}

@Module
abstract class Tuple6Instances<A, B, C, D, E, F> {
    @Provides
    fun tuple6EqInstance(ev: DaggerTuple6EqInstance<A, B, C, D, E, F>): Eq<Tuple6<A, B, C, D, E, F>> = ev
}

@Module
abstract class Tuple7Instances<A, B, C, D, E, F, G> {
    @Provides
    fun tuple7EqInstance(ev: DaggerTuple7EqInstance<A, B, C, D, E, F, G>): Eq<Tuple7<A, B, C, D, E, F, G>> = ev
}

@Module
abstract class Tuple8Instances<A, B, C, D, E, F, G, H> {
    @Provides
    fun tuple8EqInstance(ev: DaggerTuple8EqInstance<A, B, C, D, E, F, G, H>): Eq<Tuple8<A, B, C, D, E, F, G, H>> = ev
}

@Module
abstract class Tuple9Instances<A, B, C, D, E, F, G, H, I> {
    @Provides
    fun tuple9EqInstance(ev: DaggerTuple9EqInstance<A, B, C, D, E, F, G, H, I>): Eq<Tuple9<A, B, C, D, E, F, G, H, I>> = ev
}

@Module
abstract class Tuple10Instances<A, B, C, D, E, F, G, H, I, J> {
    @Provides
    fun tuple10EqInstance(ev: DaggerTuple10EqInstance<A, B, C, D, E, F, G, H, I, J>): Eq<Tuple10<A, B, C, D, E, F, G, H, I, J>> = ev
}

class DaggerTuple2EqInstance<A, B> @Inject constructor(
        val eqA: Eq<A>,
        val eqB: Eq<B>
) : Tuple2EqInstance<A, B> {
    override fun EQA(): Eq<A> = eqA
    override fun EQB(): Eq<B> = eqB
}

class DaggerTuple3EqInstance<A, B, C> @Inject constructor(
        val eqA: Eq<A>,
        val eqB: Eq<B>,
        val eqC: Eq<C>
) : Tuple3EqInstance<A, B, C> {
    override fun EQA(): Eq<A> = eqA
    override fun EQB(): Eq<B> = eqB
    override fun EQC(): Eq<C> = eqC
}

class DaggerTuple4EqInstance<A, B, C, D> @Inject constructor(
        val eqA: Eq<A>,
        val eqB: Eq<B>,
        val eqC: Eq<C>,
        val eqD: Eq<D>
) : Tuple4EqInstance<A, B, C, D> {
    override fun EQA(): Eq<A> = eqA
    override fun EQB(): Eq<B> = eqB
    override fun EQC(): Eq<C> = eqC
    override fun EQD(): Eq<D> = eqD
}

class DaggerTuple5EqInstance<A, B, C, D, E> @Inject constructor(
        val eqA: Eq<A>,
        val eqB: Eq<B>,
        val eqC: Eq<C>,
        val eqD: Eq<D>,
        val eqE: Eq<E>
) : Tuple5EqInstance<A, B, C, D, E> {
    override fun EQA(): Eq<A> = eqA
    override fun EQB(): Eq<B> = eqB
    override fun EQC(): Eq<C> = eqC
    override fun EQD(): Eq<D> = eqD
    override fun EQE(): Eq<E> = eqE
}

class DaggerTuple6EqInstance<A, B, C, D, E, F> @Inject constructor(
        val eqA: Eq<A>,
        val eqB: Eq<B>,
        val eqC: Eq<C>,
        val eqD: Eq<D>,
        val eqE: Eq<E>,
        val eqF: Eq<F>
) : Tuple6EqInstance<A, B, C, D, E, F> {
    override fun EQA(): Eq<A> = eqA
    override fun EQB(): Eq<B> = eqB
    override fun EQC(): Eq<C> = eqC
    override fun EQD(): Eq<D> = eqD
    override fun EQE(): Eq<E> = eqE
    override fun EQF(): Eq<F> = eqF
}

class DaggerTuple7EqInstance<A, B, C, D, E, F, G> @Inject constructor(
        val eqA: Eq<A>,
        val eqB: Eq<B>,
        val eqC: Eq<C>,
        val eqD: Eq<D>,
        val eqE: Eq<E>,
        val eqF: Eq<F>,
        val eqG: Eq<G>
) : Tuple7EqInstance<A, B, C, D, E, F, G> {
    override fun EQA(): Eq<A> = eqA
    override fun EQB(): Eq<B> = eqB
    override fun EQC(): Eq<C> = eqC
    override fun EQD(): Eq<D> = eqD
    override fun EQE(): Eq<E> = eqE
    override fun EQF(): Eq<F> = eqF
    override fun EQG(): Eq<G> = eqG
}

class DaggerTuple8EqInstance<A, B, C, D, E, F, G, H> @Inject constructor(
        val eqA: Eq<A>,
        val eqB: Eq<B>,
        val eqC: Eq<C>,
        val eqD: Eq<D>,
        val eqE: Eq<E>,
        val eqF: Eq<F>,
        val eqG: Eq<G>,
        val eqH: Eq<H>
) : Tuple8EqInstance<A, B, C, D, E, F, G, H> {
    override fun EQA(): Eq<A> = eqA
    override fun EQB(): Eq<B> = eqB
    override fun EQC(): Eq<C> = eqC
    override fun EQD(): Eq<D> = eqD
    override fun EQE(): Eq<E> = eqE
    override fun EQF(): Eq<F> = eqF
    override fun EQG(): Eq<G> = eqG
    override fun EQH(): Eq<H> = eqH
}

class DaggerTuple9EqInstance<A, B, C, D, E, F, G, H, I> @Inject constructor(
        val eqA: Eq<A>,
        val eqB: Eq<B>,
        val eqC: Eq<C>,
        val eqD: Eq<D>,
        val eqE: Eq<E>,
        val eqF: Eq<F>,
        val eqG: Eq<G>,
        val eqH: Eq<H>,
        val eqI: Eq<I>
) : Tuple9EqInstance<A, B, C, D, E, F, G, H, I> {
    override fun EQA(): Eq<A> = eqA
    override fun EQB(): Eq<B> = eqB
    override fun EQC(): Eq<C> = eqC
    override fun EQD(): Eq<D> = eqD
    override fun EQE(): Eq<E> = eqE
    override fun EQF(): Eq<F> = eqF
    override fun EQG(): Eq<G> = eqG
    override fun EQH(): Eq<H> = eqH
    override fun EQI(): Eq<I> = eqI
}

class DaggerTuple10EqInstance<A, B, C, D, E, F, G, H, I, J> @Inject constructor(
        val eqA: Eq<A>,
        val eqB: Eq<B>,
        val eqC: Eq<C>,
        val eqD: Eq<D>,
        val eqE: Eq<E>,
        val eqF: Eq<F>,
        val eqG: Eq<G>,
        val eqH: Eq<H>,
        val eqI: Eq<I>,
        val eqJ: Eq<J>
) : Tuple10EqInstance<A, B, C, D, E, F, G, H, I, J> {
    override fun EQA(): Eq<A> = eqA
    override fun EQB(): Eq<B> = eqB
    override fun EQC(): Eq<C> = eqC
    override fun EQD(): Eq<D> = eqD
    override fun EQE(): Eq<E> = eqE
    override fun EQF(): Eq<F> = eqF
    override fun EQG(): Eq<G> = eqG
    override fun EQH(): Eq<H> = eqH
    override fun EQI(): Eq<I> = eqI
    override fun EQJ(): Eq<J> = eqJ
}