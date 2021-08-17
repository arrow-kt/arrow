package arrow.fx.stm.internal

import arrow.fx.stm.TVar
import arrow.fx.stm.STM

/**
 * Low level stm datastructure which can be used to efficiently implement other datastructures like Map/Set on top.
 *
 * Based on http://lampwww.epfl.ch/papers/idealhashtrees.pdf and https://hackage.haskell.org/package/stm-hamt.
 */
public data class Hamt<A>(val branches: TVar<Array<Branch<A>?>>) {
  public companion object {
    public suspend fun <A> new(): Hamt<A> = Hamt(TVar.new(arrayOfNulls(ARR_SIZE)))
  }
}

public inline fun <A> STM.lookupHamtWithHash(hmt: Hamt<A>, hash: Int, test: (A) -> Boolean): A? {
  var depth = 0
  var hamt = hmt
  while (true) {
    val branchInd = hash.indexAtDepth(depth)
    val branches = hamt.branches.read()
    when (val branch = branches[branchInd]) {
      null -> return null
      is Branch.Leaf -> return@lookupHamtWithHash (branch.value as Array<A>).find(test)
      is Branch.Branches -> {
        depth = depth.nextDepth()
        hamt = branch.sub
      }
    }
  }
}

public fun <A> STM.pair(depth: Int, hash1: Int, branch1: Branch<A>, hash2: Int, branch2: Branch<A>): Hamt<A> {
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

public fun <A> STM.clearHamt(hamt: Hamt<A>): Unit = hamt.branches.write(arrayOfNulls(ARR_SIZE))

public inline fun <A> STM.alterHamtWithHash(
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
        new[branchInd] = Branch.Leaf(hash, arrayOf(el))
        hmt.branches.write(new)
        return true
      }
      is Branch.Leaf -> {
        if (hash == branch.hash) {
          val ind = (branch.value as Array<A>).indexOfFirst(test).takeIf { it != -1 }
          when (val el = ind?.let { branch.value[it] }) {
            null -> {
              // insert new value with a colliding hash
              val newEl = fn(null) ?: return false
              val new = branches.copyOf()
              new[branchInd] = Branch.Leaf(hash, arrayOf(newEl, *branch.value))
              hmt.branches.write(new)
              return true
            }
            else -> {
              when (val newEl = fn(el)) {
                null -> {
                  // remove element
                  if (branch.value.size > 1) {
                    val newLeafArray: Array<Any?> = Array(branch.value.size - 1) { i ->
                      if (i >= ind) branch.value[i + 1]
                      else branch.value[i]
                    }
                  } else {
                    val new = branches.copyOf()
                    new[branchInd] = null
                    hmt.branches.write(new)
                  }
                  return true
                }
                else -> {
                  // update element
                  val newLeafArr = branch.value.copyOf()
                  newLeafArr[ind] = newEl
                  val new = branches.copyOf()
                  new[branchInd] = Branch.Leaf(hash, newLeafArr as Array<Any?>)
                  hmt.branches.write(new)
                  return true
                }
              }
            }
          }
        } else {
          val el = fn(null) ?: return false
          val newHamt = pair(
            depth.nextDepth(),
            hash,
            Branch.Leaf(hash, arrayOf(el)),
            branch.hash,
            branch
          )
          val new = branches.copyOf()
          new[branchInd] = Branch.Branches(newHamt)
          hmt.branches.write(new)
          return true
        }
      }
      is Branch.Branches -> {
        depth = depth.nextDepth()
        hmt = branch.sub
      }
    }
  }
}

public fun <A> STM.newHamt(): Hamt<A> = Hamt(newTVar(arrayOfNulls(ARR_SIZE)))

public sealed class Branch<out A> {
  public data class Branches<A>(val sub: Hamt<A>) : Branch<A>()
  public data class Leaf<A>(val hash: Int, val value: Array<Any?>) : Branch<A>()
}

public const val ARR_SIZE: Int = 32 // 2^DEPTH_STEP
public const val DEPTH_STEP: Int = 5
public const val MASK: Int = 1.shl(DEPTH_STEP) - 1

public fun Int.index(): Int = MASK.and(this)
public fun Int.atDepth(d: Int): Int = shr(d)
public fun Int.indexAtDepth(d: Int): Int = atDepth(d).index()
public fun Int.nextDepth(): Int = this + DEPTH_STEP
