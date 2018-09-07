package arrow.generic.tup

class Tup<out N : TupN> internal constructor(internal vararg val data: Any?)

sealed class TupN

class TupCons<out H, out T : TupN> private constructor() : TupN()

@Suppress("CanSealedSubClassBeObject")
class TupNil private constructor() : TupN()
