package arrow

import arrow.core.*
import arrow.mtl.typeclasses.MonadState
import arrow.typeclasses.Eq
import arrow.typeclasses.Hash
import arrow.typeclasses.Order
import arrow.typeclasses.suspended.monad.Fx

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

interface Task<F, K, V> {
  fun run(func: (K) -> Kind<F, V>): Kind<F, V>
}

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
          val newValue: V = !newTask.run { just(store.getValue(it)) }
          store.putValue(this@topological, currTarget, newValue)
        })
      }
    }
  }
}

// I didn't like the original version using State, so I rewrote it as a tailRecM
fun <F, I, K, V> suspending(): Scheduler<F, I, I, K, V> = { rebuilder: Rebuilder<F, I, K, V> ->
  val eqInstance = this // implicit label missing
  { tasks: Tasks<F, K, V>, target: K, startStore: Store<I, K, V> ->
    tailRecM(startStore toT emptySet<K>()) { (store, completedTasks) ->
      tasks(target).fold({
        store.right().just()
      }, { task ->
        if (completedTasks.contains(target)) {
          store.right().just()
        } else {
          val value: V = store.getValue(target)
          fx {
            val newTask: Task<F, K, V> = rebuilder(target, value, task)
            val newValue: V = !newTask.run { just(store.getValue(it)) }
            val newStore: Store<I, K, V> = store.putValue(eqInstance, target, newValue)
            (newStore toT completedTasks.plus(target)).left()
          } // TODO handleErrorWith if you ever make a serious build system
        }
      })
    }
  }
}

/** Rebuilding Strategies */

typealias Time = Int

typealias MakeInfo<K> = Tuple2<Time, Map<K, Time>>

fun <F, K, V> modTimeRebuilder(): Rebuilder<F, MakeInfo<K>, K, V> = { key: K, value: V, task: Task<F, K, V> ->
  object : Task<F, K, V> {
    override fun run(func: (K) -> Kind<F, V>): Kind<F, V> = fx {
      val (now: Time, modTimes: Map<K, Time>) = !get()
      modTimes[key].toOption().fold({
        !set(now + 1 toT modTimes.plus(key to now))
        !task.run(func)
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

/** Mock Helpers */

typealias Graph<K> = List<K>

fun <K> Order<K>.topSort(depGraph: Graph<K>): List<K> = depGraph.sortedWith(Comparator { a, b -> a.compare(b) })

fun <K> reachable(f: ((K) -> List<K>), k: K): Graph<K> = f(k)

fun <F, K, V> dependencies(task: Task<F, K, V>): List<K> = listOf()
