package arrow

import arrow.core.Option
import arrow.effects.typeclasses.Concurrent
import arrow.typeclasses.Eq

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
    fun <F> run(CE: Concurrent<F>, func: (K) -> Kind<F, V>): Kind<F, V>
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

typealias Build<I, K, V> = (tasks: Tasks<K, V>, key: K, store: Store<I, K, V>) -> Store<I, K, V>

typealias Rebuilder<IR, K, V> = (key: K, value: V, task: Task<K, V>) -> Task<K, V>

typealias Scheduler<I, IR, K, V> = (rebuilder: Rebuilder<IR, K, V>) -> Build<I, K, V>

data class Hash<V> private constructor(private val valueHash: Int) {

    companion object {
        operator fun <V> invoke(hashable: arrow.typeclasses.Hash<V>, value: V) = hashable.run {
            Hash<V>(value.hash())
        }
    }

    fun <I, K> getHash(hashable: arrow.typeclasses.Hash<V>, key: K, store: Store<I, K, V>) = Hash(hashable, store.getValue(key))
}

