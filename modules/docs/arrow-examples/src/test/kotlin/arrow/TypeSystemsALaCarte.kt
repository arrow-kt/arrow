package arrow

/**

-- An abstract store containing a key/value map and persistent build information dataStoreikv--i=info,k=key,v=value initialise::i->(k->v)->Storeikv
::Storeikv->i ::i->Storeikv->Storeikv ::k->Storeikv->v ::Eqk=>k->v->Storeikv->Storeikv
getInfo
putInfo
getValue
putValue
data Hash v -- a compact summary of a value with a fast equality check hash :: Hashable v => v -> Hash v getHash::Hashablev=>k->Storeikv->Hashv
-- Build tasks (see ğ3.2)
newtypeTask ckv=Task{run::forallf.cf=>(k->fv)->fv} type Tasksckv=k->Maybe(Taskckv)
-- Build system (see ğ3.3) typeBuildcikv=Tasksckv->k->Storeikv->Storeikv
-- Build system components: a scheduler and a rebuilder (see ğ5)
typeSchedulerciirkv=Rebuildercirkv->Buildcikv typeRebuilderc irkv=k->v->Taskckv->Task(MonadStateir)kv

*/


