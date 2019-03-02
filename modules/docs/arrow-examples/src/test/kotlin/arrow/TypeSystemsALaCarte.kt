package arrow

import arrow.core.Option
import arrow.core.Tuple2
import arrow.core.toOption
import arrow.core.toT
import arrow.data.State
import arrow.effects.extensions.io.concurrentEffect.concurrentEffect
import arrow.effects.typeclasses.Concurrent
import arrow.mtl.typeclasses.MonadState
import arrow.typeclasses.Const
import arrow.typeclasses.Eq
import arrow.typeclasses.Order

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


typealias Time = Int
typealias MakeInfo<K> = Tuple2<Time, Map<K, Time>>

fun <K, V> modTimeRebuilder(): Rebuilder<MakeInfo<K>, K, V> = { key, value, task ->
  object : Task<K, V> {

    override fun <F, S> run(CE: Concurrent<F>, MS: MonadState<F, S>, func: (K) -> Kind<F, V>): Kind<F, V> =
      (MS.get() as Tuple2<Time, Map<K, Time>>).let { (now: Time, modTimes: Map<K, Time>) ->
        modTimes[key].toOption().fold({
          MS.set((now + 1 toT modTimes.plus(key to now)) as S)
          task.run(CE, MS, func)
        }, {
          CE.just(value)
        })
      }
  }
}


typealias Graph<K> = List<K>

fun <K> topSort(ord: Order<K>, depGraph: Graph<K>): List<K> = depGraph.sortedWith( Comparator { a, b -> ord.run { a.compare(b) } })

fun <K> reachable(f: ((K) -> List<K>), k: K): Graph<K> = f(k)

fun <K, V> dependencies(task: Task<K, V>): List<K> = listOf()

//reachable :: Ord k => (k -> [k]) -> k -> Graph k

fun <I, K, V> topological(ord: Order<K>, rebuilder: Rebuilder<I, K, V>, tasks: Tasks<K, V>, target: K, store: Store<I, K, V>): Scheduler<I, I, K, V> {
  val dep: (K) -> Graph<K> = { k: K -> tasks(k).fold({ emptyList() }, { dependencies(it) }) }
  val order: List<K> = topSort(ord, reachable(dep, target))
  val build: (K) -> State<Store<I, K, V>, Unit> = { k: K ->
    tasks(k).fold({
      State { s -> s toT Unit }
    }, { task ->
      val value: V = store.getValue(k)
      val newTask: Task<K, V> = rebuilder(k, value, task)
      val fetch: (K) -> State<I, V> = {
        store.getValue(it)
      }
    })
  }
}


// topological :: Ord k => Scheduler Applicative i i k v
// topological rebuilder tasks target = execState $ mapM_ build order
//   where
//     build :: k -> State (Store i k v) ()
//     build key = case tasks key of
//       Nothing -> return ()
//       Just task -> do
//         store <- get
//         let value = getValue key store
//             newTask :: Task (MonadState i) k v
//             newTask = rebuilder key value task
//             fetch :: k -> State i v
//             fetch k = return (getValue k store)
//         newValue <- liftStore (run newTask fetch)
//         modify $ putValue key newValue
//     order = topSort (reachable dep target)
//     dep k = case tasks k of { Nothing -> []; Just task -> dependencies task }


fun <K, V> make(): Build<MakeInfo<K>, K, V> =
  { tasks, key, store ->
    topological(modTimeRebuilder, tasks, key, store)
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
