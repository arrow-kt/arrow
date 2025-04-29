@file:Suppress("UNCHECKED_CAST")

package arrow.fx.stm.internal

import arrow.fx.stm.TVar
import arrow.fx.stm.STM

/**
 * Low level stm datastructure which can be used to efficiently implement other datastructures like Map/Set on top.
 *
 * Based on http://lampwww.epfl.ch/papers/idealhashtrees.pdf and https://hackage.haskell.org/package/stm-hamt.
 */
internal data class Hamt<A>(val branches: TVar<Array<Branch<A>?>>) {
  companion object {
    suspend fun <A> new(): Hamt<A> = Hamt(TVar.new(arrayOfNulls(ARR_SIZE)))
  }
}

internal fun <A> STM.lookupHamtWithHash(hmt: Hamt<A>, hash: Int, test: (A) -> Boolean): A? {
  var depth = 0
  var hamt = hmt
  while (true) {
    val branchInd = hash.indexAtDepth(depth)
    val branches = hamt.branches.read()
    when (val branch = branches[branchInd]) {
      null -> return null
      is Branch.Leaf -> return branch.value.find(test)
      is Branch.Branches -> {
        depth = depth.nextDepth()
        hamt = branch.sub
      }
    }
  }
}

internal fun <A> STM.pair(depth: Int, hash1: Int, branch1: Branch<A>, hash2: Int, branch2: Branch<A>): Hamt<A> {
  val branchInd1 = hash1.indexAtDepth(depth)
  val branchInd2 = hash2.indexAtDepth(depth)
  val branches = arrayOfNulls<Branch<A>>(ARR_SIZE)
  if (branchInd1 == branchInd2) {
    val deeper = pair(depth.nextDepth(), hash1, branch1, hash2, branch2)
    branches[branchInd1] = Branch.Branches(deeper)
  } else {
    branches[branchInd1] = branch1
    branches[branchInd2] = branch2
  }
  return Hamt(newTVar(branches))
}

// internal fun <A> STM.clearHamt(hamt: Hamt<A>): Unit = hamt.branches.write(arrayOfNulls(ARR_SIZE))

internal fun <A> STM.alterHamtWithHash(
  hamt: Hamt<A>,
  hash: Int,
  test: (A) -> Boolean,
  fn: (A?) -> A?
): Boolean {
  var depth = 0
  var hmt = hamt
  while (true) {
    val branchInd = hash.indexAtDepth(depth)
    val branches = hmt.branches.read()
    when (val branch = branches[branchInd]) {
      null -> {
        val el = fn(null) ?: return false
        val new = branches.copyOf()
        new[branchInd] = Branch.leaf(hash, el)
        hmt.branches.write(new)
        return true
      }
      is Branch.Leaf -> {
        val atIndex = if (hash == branch.hash) {
          val ind = branch.value.indexOfFirst(test).takeIf { it != -1 }
          when (val el = ind?.let { branch.value[it] }) {
            null -> {
              // insert new value with a colliding hash
              val newEl = fn(null) ?: return false
              Branch.leaf(hash, newEl, *branch.value)
            }
            else -> {
              val newEl = fn(el)
              when {
                newEl == null && branch.value.size <= 1 ->
                  null
                newEl == null -> {
                  // remove element
                  val newLeafArray = branch.value.copyOf(branch.value.size - 1) as Array<A>
                  branch.value.copyInto(newLeafArray, ind, ind + 1)
                  Branch.Leaf(hash, newLeafArray)
                }
                else -> {
                  // update element
                  val newLeafArr = branch.value.copyOf()
                  newLeafArr[ind] = newEl
                  Branch.Leaf(hash, newLeafArr)
                }
              }
            }
          }
        } else {
          val el = fn(null) ?: return false
          val newHamt = pair(
            depth.nextDepth(),
            hash,
            Branch.leaf(hash, el),
            branch.hash,
            branch
          )
          Branch.Branches(newHamt)
        }
        val new = branches.copyOf()
        new[branchInd] = atIndex
        hmt.branches.write(new)
        return true
      }
      is Branch.Branches -> {
        depth = depth.nextDepth()
        hmt = branch.sub
      }
    }
  }
}

internal fun <A> STM.newHamt(): Hamt<A> = Hamt(newTVar(arrayOfNulls(ARR_SIZE)))

internal sealed class Branch<A> {
  data class Branches<A>(val sub: Hamt<A>) : Branch<A>()
  data class Leaf<A>(val hash: Int, val value: Array<A>) : Branch<A>() {
    override fun equals(other: Any?): Boolean {
      if (this === other) return true
      if (other !is Leaf<A>) return false
      return hash == other.hash && value.contentEquals(other.value)
    }
    override fun hashCode(): Int {
      var result = hash
      result = 31 * result + value.contentHashCode()
      return result
    }
  }

  companion object {
    fun <A> leaf(hash: Int, vararg value: A): Leaf<A> = Leaf(hash, value) as Leaf<A>
  }
}

internal const val ARR_SIZE: Int = 32 // 2^DEPTH_STEP
internal const val DEPTH_STEP: Int = 5
internal const val MASK: Int = 1.shl(DEPTH_STEP) - 1

internal fun Int.index(): Int = MASK.and(this)
internal fun Int.atDepth(d: Int): Int = shr(d)
internal fun Int.indexAtDepth(d: Int): Int = atDepth(d).index()
internal fun Int.nextDepth(): Int = this + DEPTH_STEP
