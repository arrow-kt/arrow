package arrow

import arrow.core.*
import arrow.mtl.typeclasses.MonadState
import arrow.typeclasses.Eq
import arrow.typeclasses.Hash
import arrow.typeclasses.Order
import arrow.typeclasses.suspended.monad.Fx

/** The basic types */

data class Store<I, K, V>(val information: I, private val get: (K) -> V) {
  fun putInfo(information: I) = Store(information, get)

  fun getValue(key: K): V = get(key)

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

  fun <I, K> getHash(hashable: arrow.typeclasses.Hash<V>, key: K, store: Store<I, K, V>) = Hashable(hashable, store.getValue(key))
}

interface Task<F, K, V> {
  fun run(func: (K) -> Kind<F, V>): Kind<F, V>
}

typealias Tasks<F, K, V> = (K) -> Option<Task<F, K, V>>

typealias Build<F, I, K, V> = BuildSystem<K, F>.(tasks: Tasks<F, K, V>, key: K, store: Store<I, K, V>) -> Kind<F, Store<I, K, V>>

typealias Rebuilder<F, IR, K, V> = BuildComponents<K, F, IR>.(key: K, value: V, task: Task<F, K, V>) -> Task<F, K, V>

typealias Scheduler<F, I, IR, K, V> = BuildComponents<K, F, IR>.(rebuilder: Rebuilder<F, IR, K, V>) -> Build<F, I, K, V>

interface BuildSystem<K, F> : Order<K>, Fx<F>

interface BuildComponents<K, F, IR> : Order<K>, Fx<F>, MonadState<F, IR>, BuildSystem<K, F>

/** Scheduling algorithms */

fun <F, I, K, V> BuildComponents<K, F, I>.topological(): Scheduler<F, I, I, K, V> = { rebuilder: Rebuilder<F, I, K, V> ->
  { tasks: Tasks<F, K, V>, target: K, store: Store<I, K, V> ->
    fx {
      val dep: (K) -> Graph<K> = { k: K -> tasks(k).fold({ emptyList() }, { dependencies(it) }) }
      val order: List<K> = topSort(reachable(dep, target))
      order.foldRight(store) { currTarget: K, acc: Store<I, K, V> ->
        tasks(currTarget).fold({
          acc
        }, { task ->
          val value: V = store.getValue(currTarget)
          val newTask: Task<F, K, V> = rebuilder(currTarget, value, task)
          val newValue: V = !newTask.run { just(store.getValue(it)) }
          store.putValue(this@topological, currTarget, newValue)
        })
      }
    }
  }
}

fun <F, I, K, V> suspending(): Scheduler<F, I, I, K, V> = { rebuilder: Rebuilder<F, I, K, V> ->
  { tasks: Tasks<F, K, V>, target: K, store: Store<I, K, V> ->
    tailRecM(store toT emptySet<K>()) { state ->
      val (store, done) = state
      tasks(target).fold({
        store.right().just()
      }, { task ->
        if (done.contains(target)) {
          store.right().just()
        } else {
          val value: V = store.getValue(target)
          fx {
            val newTask: Task<F, K, V> = rebuilder(target, value, task)
            val newValue: V = !newTask.run { just(store.getValue(it)) }
            val newStore: Store<I, K, V> = store.putValue(this@suspending, target, newValue)
            (newStore toT done.plus(target)).left()
          } // TODO handleErrorWith
        }
      })
    }
  }
}

/** Rebuilding Strategies */

/* The MAKE build system */

typealias Time = Int

typealias MakeInfo<K> = Tuple2<Time, Map<K, Time>>

fun <F, K, V> modTimeRebuilder(): Rebuilder<F, MakeInfo<K>, K, V> = { key, value, task ->
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

/** Full examples **/

fun <F, K, V> BuildComponents<K, F, MakeInfo<K>>.make(): Build<F, MakeInfo<K>, K, V> = { tasks: Tasks<F, K, V>, key: K, store: Store<MakeInfo<K>, K, V> ->
  val topological: Scheduler<F, MakeInfo<K>, MakeInfo<K>, K, V> = topological()
  val build: Build<F, MakeInfo<K>, K, V> = topological(modTimeRebuilder())
  build(tasks, key, store)
}

/* Helpers */

typealias Graph<K> = List<K>

fun <K> Order<K>.topSort(depGraph: Graph<K>): List<K> = depGraph.sortedWith(Comparator { a, b -> a.compare(b) })

fun <K> reachable(f: ((K) -> List<K>), k: K): Graph<K> = f(k)

fun <F, K, V> dependencies(task: Task<F, K, V>): List<K> = listOf()
