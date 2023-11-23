package arrow.fx.stm.compose

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import kotlin.jvm.JvmInline

@JvmInline
public value class TVar<A>(internal val variable: MutableState<A>) {
  public fun unsafeRead(): A = variable.value

  public companion object {
    public fun <A> new(value: A): TVar<A> = TVar(mutableStateOf(value))
  }
}

@JvmInline
public value class TMap<K, V>(internal val map: SnapshotStateMap<K, V>) {
  public companion object {
    public fun <K, V> new(): TMap<K, V> = TMap(mutableStateMapOf())
  }
}

@JvmInline
public value class TSet<A>(internal val map: SnapshotStateMap<A, Boolean>) {
  public companion object {
    public fun <A> new(): TSet<A> = TSet(mutableStateMapOf())
  }
}

@JvmInline
public value class TList<A>(internal val list: SnapshotStateList<A>) {
  public companion object {
    public fun <A> new(): TList<A> = TList(mutableStateListOf())
  }
}

@JvmInline
public value class TQueue<A>(internal val list: SnapshotStateList<A>) {
  public companion object {
    public fun <A> new(): TQueue<A> = TQueue(mutableStateListOf())
  }
}
