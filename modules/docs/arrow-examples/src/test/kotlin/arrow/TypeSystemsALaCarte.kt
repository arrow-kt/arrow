package arrow

import arrow.core.Option
import arrow.core.Tuple2
import arrow.core.toOption
import arrow.core.toT
import arrow.mtl.typeclasses.MonadState
import arrow.typeclasses.Eq
import arrow.typeclasses.Order
import arrow.typeclasses.suspended.monad.Fx

/**

-- An abstract store containing a key/value map and persistent build information
data Store i k v --i=info,k=key,v=value
initialise :: i->(k->v)->Storeikv
::Store i kv->i ::i->Storeikv->Storeikv ::k->Storeikv->v ::Eqk=>k->v->Storeikv->Storeikv
getInfo
putInfo
getValue
putValue
data Hash v -- a compact summary of a value with a fast equality check

hash :: Hashable v => v -> Hash v getHash::Hashablev=>k->Storeikv->Hashv
-- Build tasks (see ğ3.2)

newtypeTask ckv = Task{
run::forallf.cf
=>(k->f v)-> f v}

type Tasks c k v= k -> Maybe(Task ckv)
-- Build system (see ğ3.3) type Build c i k v = Tasks c k v -> k -> Store i k v -> Store i k v
-- Build system components: a scheduler and a rebuilder (see ğ5)
type Scheduler c i ir k v=Rebuildercirkv->Buildcikv
type Rebuilderc ir k v = k-> v-> Task ckv-> Task(MonadStateir)kv

 */

/* Normal definition

interface Task<K, V> {
  fun <F, S> run(CE: Concurrent<F>, MS: MonadState<F, S>, func: (K) -> Kind<F, V>): Kind<F, V>
}

typealias Tasks<K, V> = (K) -> Option<Task<K, V>>

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

data class Hash<V> private constructor(private val valueHash: Int) {

  companion object {
    operator fun <V> invoke(hashable: arrow.typeclasses.Hash<V>, value: V) = hashable.run {
      Hash<V>(value.hash())
    }
  }

  fun <I, K> getHash(hashable: arrow.typeclasses.Hash<V>, key: K, store: Store<I, K, V>) = Hash(hashable, store.getValue(key))
}

typealias Build<I, K, V> = (tasks: Tasks<K, V>, key: K, store: Store<I, K, V>) -> Store<I, K, V>

typealias Rebuilder<IR, K, V> = (key: K, value: V, task: Task<K, V>) -> Task<K, V>

typealias Scheduler<I, IR, K, V> = (rebuilder: Rebuilder<IR, K, V>) -> Build<I, K, V>

*/


interface Task<F, K, V> {
  fun run(func: (K) -> Kind<F, V>): Kind<F, V>
}

typealias Tasks<F, K, V> = (K) -> Option<Task<F, K, V>>

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

data class Hash<V> private constructor(private val valueHash: Int) {

  companion object {
    operator fun <V> invoke(hashable: arrow.typeclasses.Hash<V>, value: V) = hashable.run {
      Hash<V>(value.hash())
    }
  }

  fun <I, K> getHash(hashable: arrow.typeclasses.Hash<V>, key: K, store: Store<I, K, V>) = Hash(hashable, store.getValue(key))
}

typealias Build<F, I, K, V> = (tasks: Tasks<F, K, V>, key: K, store: Store<I, K, V>) -> Store<I, K, V>

typealias Rebuilder<F, IR, K, V> = (key: K, value: V, task: Task<F, K, V>) -> Task<F, K, V>

typealias Scheduler<F, I, IR, K, V> = (rebuilder: Rebuilder<F, IR, K, V>) -> Build<F, I, K, V>

typealias Time = Int
typealias MakeInfo<K> = Tuple2<Time, Map<K, Time>>

fun <F, K, V> BuildSystem<K, F>.modTimeRebuilder(): Rebuilder<F, MakeInfo<K>, K, V> = { key, value, task ->
  object : Task<F, K, V> {
    override fun run(func: (K) -> Kind<F, V>): Kind<F, V> = fx {
      val (now: Time, modTimes: Map<K, Time>) = !get()
      modTimes[key].toOption().fold({
        set(now + 1 toT modTimes.plus(key to now))
        !task.run(func)
      }, {
        value
      })
    }
  }
}

typealias Graph<K> = List<K>

fun <K> Order<K>.topSort(depGraph: Graph<K>): List<K> = depGraph.sortedWith(Comparator { a, b -> a.compare(b) })

fun <K> reachable(f: ((K) -> List<K>), k: K): Graph<K> = f(k)

fun <F, K, V> dependencies(task: Task<F, K, V>): List<K> = listOf()

interface BuildSystem<K, F> : Order<K>, Fx<F>, MonadState<F, MakeInfo<K>>

fun <F, I, K, V> BuildSystem<K, F>.topological(rebuilder: Rebuilder<F, I, K, V>, tasks: Tasks<F, K, V>, target: K, store: Store<I, K, V>): Kind<F, Store<I, K, V>> = fx {
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

fun <F, K, V> BuildSystem<K, F>.make(): Kind<F, Build<F, MakeInfo<K>, K, V>> = fx {
  val build: Build<F, MakeInfo<K>, K, V> = { tasks: Tasks<F, K, V>, key: K, store: Store<MakeInfo<K>, K, V> ->
    val topological: Kind<F, Store<MakeInfo<K>, K, V>> = topological(modTimeRebuilder(), tasks, key, store)
    val bind: Store<MakeInfo<K>, K, V> = !topological
    bind
  }
  build
}

// -- A restarting task scheduler
// restarting :: Ord k => Scheduler Monad (ir, Chain k) ir k v
// restarting rebuilder tasks target = execState $ do
//     chain <- gets (snd . getInfo)
//     newChain <- liftChain $ go Set.empty $ chain ++ [target | target `notElem` chain]
//     modify $ mapInfo $ \(ir, _) -> (ir, newChain)
//   where
//     go :: Set k -> Chain k -> State (Store ir k v) (Chain k)
//     go _    []         = return []
//     go done (key:keys) = case tasks key of
//       Nothing -> (key :) <$> go (Set.insert key done) keys
//       Just task -> do
//         store <- get
//         let newTask :: Task (MonadState ir) k (Either k v)
//             newTask = try $ rebuilder key (getValue key store) task
//             fetch :: k -> State ir (Either k v)
//             fetch k | k `Set.member` done = return $ Right (getValue k store)
//                     | otherwise = return $ Left k
//         result <- liftStore (run newTask fetch) -- liftStore is defined in Fig. 7
//         case result of
//           Left dep -> go done $ dep: filter (/= dep) keys ++ [key]
//           Right newValue -> do modify $ putValue key newValue
//                                (key :) <$> go (Set.insert key done) keys

fun <F, IR> restarting(ord: Order<F>) = 1
