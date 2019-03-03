package arrow

import arrow.core.*
import arrow.core.extensions.order
import arrow.effects.rx2.*
import arrow.effects.rx2.extensions.observablek.monad.monad
import arrow.mtl.typeclasses.MonadState
import arrow.typeclasses.Eq
import arrow.typeclasses.Hash
import arrow.typeclasses.Monad
import arrow.typeclasses.Order
import arrow.typeclasses.suspended.monad.Fx
import io.reactivex.Observable

/**
 * This is a working Kotlin transcription of Build Systems a la Carte
 *
 * Andrey Mokhov, Neil Mitchell, and Simon Peyton Jones. 2018. Build Systems à la Carte. Proc. ACM Program.
 * Lang. 2, ICFP, Article 79 (September 2018), 29 pages. https://doi.org/10.1145/3236774
 *
 * You can find the paper here: https://www.microsoft.com/en-us/research/uploads/prod/2018/03/build-systems.pdf
 * A video presentation: https://www.youtube.com/watch?v=BQVT6wiwCxM
 *
 * And a port to Rust syntax: https://github.com/theindigamer/bsalc-alt-code/blob/master/BSalC.rs
 *
 *
 * This version is worse than the one in the paper because Kotlin won't allow us to put
 * a constraint C on Task that is a typeclass like Functor<F>, Monad<F>, MonadState<F, X>...
 *
 * interface Task<C, K, V> {
 *   fun <F> run(func: C.(K) -> Kind<F, V>): Kind<F, V>
 * }
 *
 * Instead, we fix to a single F and work with it using receivers for the typeclasses required.
 * This has undesirable side-effects such as making other types monadic.
 */

/** The basic types */

typealias Task<F, K, V> = ((K) -> Kind<F, V>) -> Kind<F, V>

typealias Tasks<F, K, V> = (K) -> Option<Task<F, K, V>>

typealias Build<F, I, K, V> =
  BuildSystem<K, F>.(Tasks<F, K, V>, K, Store<I, K, V>) ->
  /* Because tasks are fixed to F all builds are forced to be wrapped in F */ Kind<F, Store<I, K, V>>

typealias Rebuilder<F, IR, K, V> = BuildComponents<K, F, IR>.(K, V, Task<F, K, V>) -> Task<F, K, V>

typealias Scheduler<F, I, IR, K, V> = BuildComponents<K, F, IR>.(Rebuilder<F, IR, K, V>) -> Build<F, I, K, V>

interface BuildSystem<K, F> : Order<K>, Fx<F>

interface BuildComponents<K, F, IR> : Order<K>, Fx<F>, MonadState<F, IR>, BuildSystem<K, F>

data class Store<I, K, V>(val information: I, private val get: (K) -> V) {
  fun putInfo(information: I) =
    Store(information, get)

  fun getValue(key: K): V =
    get(key)

  fun putValue(eq: Eq<K>, key: K, value: V): Store<I, K, V> =
    Store(information) { newKey ->
      val equals = eq.run {
        key.eqv(newKey)
      }
      if (equals) value else get(newKey)
    }
}

data class Hashable<V> private constructor(private val valueHash: Int) {

  companion object {
    operator fun <V> invoke(hashable: Hash<V>, value: V) = hashable.run {
      Hashable<V>(value.hash())
    }
  }

  fun <I, K> getHash(hashable: arrow.typeclasses.Hash<V>, key: K, store: Store<I, K, V>) =
    Hashable(hashable, store.getValue(key))
}

/** Scheduling algorithms */

typealias Graph<K> = List<K>

fun <K> Order<K>.topSort(depGraph: Graph<K>): List<K> = depGraph.sortedWith(Comparator { a, b -> a.compare(b) })

fun <K> reachable(f: ((K) -> List<K>), k: K): Graph<K> = f(k)

fun <F, K, V> dependencies(task: Task<F, K, V>): List<K> = listOf()

// I didn't like the original version using State, so I rewrote it as a fold
fun <F, I, K, V> BuildComponents<K, F, I>.topological(): Scheduler<F, I, I, K, V> = { rebuilder: Rebuilder<F, I, K, V> ->
  { tasks: Tasks<F, K, V>, target: K, startStore: Store<I, K, V> ->
    fx {
      val dep: (K) -> Graph<K> = { k: K -> tasks(k).fold({ emptyList() }, { dependencies(it) }) }
      val order: List<K> = topSort(reachable(dep, target))
      order.fold(startStore) { store: Store<I, K, V>, currTarget: K ->
        tasks(currTarget).fold({
          store
        }, { task ->
          val value: V = store.getValue(currTarget)

          // In the original Rebuilder works for all F so it's possible to get an Id one to run with State + Id without a Monad instance
          // This causes Build to return a Kind, and will probably make Rebuilder and Scheduler return one too if I dug a bit more on it
          //
          // val newTask: Task<ForId, K, V> = rebuilder(currTarget, value, task)
          // val newValue =
          //   newTask.run { State<I, V> { it toT store.getValue(currTarget) }.run(Id.monad(), acc.information).map { it.b } }.extract()

          val newTask: Task<F, K, V> = rebuilder(currTarget, value, task)
          val newValue: V = !newTask { just(store.getValue(it)) }
          store.putValue(this@topological, currTarget, newValue)
        })
      }
    }
  }
}

// I didn't like the original version using State, so I FUCKED IT UP *sigh*
fun <F, I, K, V> suspending(): Scheduler<F, I, I, K, V> = { rebuilder: Rebuilder<F, I, K, V> ->
  val eqInstance = this // implicit label missing
  { tasks: Tasks<F, K, V>, target: K, startStore: Store<I, K, V> ->
    fun fetch(key: K, store: Store<I, K, V>, completedTasks: Set<K>): Kind<F, Store<I, K, V>> =
      tasks(key).fold({
        store.just()
      }, { task ->
        // FIXME completedTasks is never updated!!!
        if (completedTasks.contains(key)) {
          store.just()
        } else {
          fx {
            val value: V = store.getValue(key)
            val newTask: Task<F, K, V> = rebuilder(key, value, task)
            val newValue: V = !newTask { kk ->
              fx {
                fetch(kk, store, completedTasks).bind().getValue(key)
              }
            }
            println("Writing $key $newValue")

            store.putValue(eqInstance, key, newValue)
          } // TODO handleErrorWith if you ever make a serious build system
        }
      })

    fetch(target, startStore, emptySet())
  }
}

/** Rebuilding Strategies */

typealias Time = Int

typealias MakeInfo<K> = Tuple2<Time, Map<K, Time>>

fun <F, K, V> modTimeRebuilder(): Rebuilder<F, MakeInfo<K>, K, V> = { key: K, value: V, task: Task<F, K, V> ->
  { func: (K) -> Kind<F, V> ->
    fx {
      val (now: Time, modTimes: Map<K, Time>) = !get()
      modTimes[key].toOption().fold({
        !set(now + 1 toT modTimes.plus(key to now))
        !task(func)
      }, {
        value
      })
    }
  }
}

/** Existing build systems **/

fun <F, K, V> BuildComponents<K, F, MakeInfo<K>>.make(): Build<F, MakeInfo<K>, K, V> = { tasks: Tasks<F, K, V>, key: K, store: Store<MakeInfo<K>, K, V> ->
  val topological: Scheduler<F, MakeInfo<K>, MakeInfo<K>, K, V> = topological()
  val build: Build<F, MakeInfo<K>, K, V> = topological(modTimeRebuilder())
  build(tasks, key, store)
}

fun <F, K, V> BuildComponents<K, F, MakeInfo<K>>.mine(): Build<F, MakeInfo<K>, K, V> = { tasks: Tasks<F, K, V>, key: K, store: Store<MakeInfo<K>, K, V> ->
  val suspending: Scheduler<F, MakeInfo<K>, MakeInfo<K>, K, V> = suspending()
  val build: Build<F, MakeInfo<K>, K, V> = suspending(modTimeRebuilder())
  build(tasks, key, store)
}

fun main() {
  val a = object : BuildComponents<Int, ForObservableK, MakeInfo<Int>> {
    var i: MakeInfo<Int> = 0 toT emptyMap()

    override fun Int.compare(b: Int): Int = Int.order().run { compare(b) }

    override fun monad(): Monad<ForObservableK> = ObservableK.monad()

    override fun get(): Kind<ForObservableK, MakeInfo<Int>> = ObservableK.just(i)

    override fun set(s: MakeInfo<Int>): Kind<ForObservableK, Unit> = ObservableK {
      i = s
    }

    override fun <A, B> Kind<ForObservableK, A>.flatMap(f: (A) -> Kind<ForObservableK, B>): Kind<ForObservableK, B> =
      fix().concatMap { f(it).fix() }

    override fun <A, B> tailRecM(a: A, f: (A) -> Kind<ForObservableK, Either<A, B>>): Kind<ForObservableK, B> = ObservableK.tailRecM(a, f)

    override fun <A> just(a: A): Kind<ForObservableK, A> = ObservableK.just(a)
  }

  val build: ObservableK<Store<MakeInfo<Int>, Int, String>> = a.mine<ForObservableK, Int, String>().invoke(a, {
    println("Building $it")
    when (it) {
      0 -> Some({ buildDep: (Int) -> Kind<ForObservableK, String> ->
        Observable.range(1, 5).concatMap { buildDep(it).value() }.k()
      })
      1 -> Some({ buildDep: (Int) -> Kind<ForObservableK, String> ->
        Observable.range(2, 5).concatMap { buildDep(it).value() }.k()
      })
      2 -> Some({ buildDep: (Int) -> Kind<ForObservableK, String> ->
        Observable.range(100, 5).concatMap { buildDep(it).value() }.k()
      })
      100 -> Some({ buildDep: (Int) -> Kind<ForObservableK, String> ->
        Observable.range(300, 5).concatMap { buildDep(it).value() }.k()
      })
      else -> Some({ func: (Int) -> Kind<ForObservableK, String> ->
        Observable.just("$it").k()
      })
    }
  }, 0, Store(0 toT emptyMap()) { it.toString() }).fix()

  println(build.value().blockingFirst().information)
}
