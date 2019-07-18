@file:Suppress("UNCHECKED_CAST", "RemoveExplicitTypeArguments")

package arrow.persistent.internal

import arrow.core.Option
import arrow.core.Tuple2

/**
 * An immutable [Hash array mapped trie (HAMT)](https://en.wikipedia.org/wiki/Hash_array_mapped_trie).
 */
interface HashArrayMappedTrie<K, V> : Iterable<Tuple2<K, V>> {

  val isEmpty: Boolean
  val size: Int

  operator fun get(key: K): Option<V>

  fun getOrElse(key: K, defaultValue: V): V

  fun containsKey(key: K): Boolean

  fun put(key: K, value: V): HashArrayMappedTrie<K, V>

  fun remove(key: K): HashArrayMappedTrie<K, V>

  fun keysIterator(): Iterator<K>

  fun valuesIterator(): Iterator<V>

  companion object {

    fun <K, V> empty(): HashArrayMappedTrie<K, V> {
      return EmptyNode.instance()
    }
  }
}

class LeafNodeIterator<K, V>(root: HAMTNode<K, V>) : Iterator<LeafNode<K, V>> {

  private val total = root.size
  private val nodes = arrayOfNulls<Any>(MAX_LEVELS)
  private val indexes = IntArray(MAX_LEVELS)

  private var level: Int = 0
  private var ptr = 0

  init {
    level = downstairs(nodes, indexes, root, 0)
  }

  override fun hasNext(): Boolean {
    return ptr < total
  }

  override fun next(): LeafNode<K, V> {
    if (!hasNext()) {
      throw NoSuchElementException()
    }
    var node = nodes[level]
    while (node !is LeafNode<*, *>) {
      node = findNextLeaf()
    }
    ptr++
    return if (node is LeafList<*, *>) {
      val leaf = node as LeafList<K, V>
      nodes[level] = leaf.tail
      leaf
    } else {
      nodes[level] = EmptyNode.instance<Any, Any>()
      node as LeafSingleton<K, V>
    }
  }

  private fun findNextLeaf(): Any? {
    var node: HAMTNode<K, V>? = null
    while (level > 0) {
      level--
      indexes[level]++
      node = getChild(nodes[level] as HAMTNode<K, V>, indexes[level])
      if (node != null) {
        break
      }
    }
    level = downstairs(nodes, indexes, node, level + 1)
    return nodes[level]
  }

  companion object {

    private const val MAX_LEVELS = Int.SIZE_BITS / HAMTNode.SIZE + 2

    private fun <K, V> downstairs(nodes: Array<Any?>, indexes: IntArray, root: HAMTNode<K, V>?, level: Int): Int {
      var currentRoot = root
      var currentLevel = level
      while (true) {
        nodes[currentLevel] = currentRoot
        indexes[currentLevel] = 0
        currentRoot = getChild(currentRoot, 0)
        if (currentRoot == null) {
          break
        } else {
          currentLevel++
        }
      }
      return currentLevel
    }

    private fun <K, V> getChild(node: HAMTNode<K, V>?, index: Int): HAMTNode<K, V>? {
      if (node is IndexedNode<*, *>) {
        val subNodes = (node as IndexedNode<K, V>).subNodes
        return if (index < subNodes.size) subNodes[index] as HAMTNode<K, V> else null
      } else if (node is ArrayNode<*, *>) {
        val arrayNode = node as ArrayNode<K, V>?
        return if (index < HAMTNode.BUCKET_SIZE) arrayNode!!.subNodes[index] as HAMTNode<K, V> else null
      }
      return null
    }
  }
}

/**
 * An abstract base class for nodes of a HAMT.
 */
abstract class HAMTNode<K, V> : HashArrayMappedTrie<K, V> {

  abstract fun lookup(shift: Int, keyHash: Int, key: K): Option<out V>

  abstract fun lookup(shift: Int, keyHash: Int, key: K, defaultValue: V): V

  abstract fun put(shift: Int, keyHash: Int, key: K, value: V): HAMTNode<K, V>

  abstract fun remove(shift: Int, keyHash: Int, key: K): HAMTNode<K, V>

  open fun nodes(): Iterator<LeafNode<K, V>> {
    return LeafNodeIterator(this)
  }

  override fun iterator(): Iterator<Tuple2<K, V>> {
    return nodes().map { node -> Tuple2(node.key, node.value) }
  }

  override fun keysIterator(): Iterator<K> {
    return nodes().map { obj: LeafNode<K, V> -> obj.key }
  }

  override fun valuesIterator(): Iterator<V> {
    return nodes().map { obj: LeafNode<K, V> -> obj.value }
  }

  override fun get(key: K): Option<V> {
    return lookup(0, key.hashCode(), key)
  }

  override fun getOrElse(key: K, defaultValue: V): V {
    return lookup(0, key.hashCode(), key, defaultValue)
  }

  override fun containsKey(key: K): Boolean {
    return get(key).isDefined()
  }

  override fun put(key: K, value: V): HashArrayMappedTrie<K, V> {
    return put(0, key.hashCode(), key, value)
  }

  override fun remove(key: K): HashArrayMappedTrie<K, V> {
    return remove(0, key.hashCode(), key)
  }

  companion object {

    const val SIZE = 5
    const val BUCKET_SIZE = 1 shl SIZE
    const val MAX_INDEX_NODE = BUCKET_SIZE shr 1
    const val MIN_ARRAY_NODE = BUCKET_SIZE shr 2

    fun hashFragment(shift: Int, hash: Int): Int {
      return hash.ushr(shift) and BUCKET_SIZE - 1
    }

    fun toBitmap(hash: Int): Int {
      return 1 shl hash
    }

    fun fromBitmap(bitmap: Int, bit: Int): Int {
      return bitCount(bitmap and bit - 1)
    }

    fun update(arr: Array<Any?>, index: Int, newElement: Any): Array<Any?> {
      val newArr = arr.copyOf()
      newArr[index] = newElement
      return newArr
    }

    fun remove(arr: Array<Any?>, index: Int): Array<Any?> {
      val newArr = arrayOfNulls<Any>(arr.size - 1)
      arr.copyInto(startIndex = 0, destination = newArr, destinationOffset = 0, endIndex = index)
      arr.copyInto(startIndex = index + 1, destination = newArr, destinationOffset = index, endIndex = arr.size)
      return newArr
    }

    fun insert(arr: Array<Any?>, index: Int, newElem: Any): Array<Any?> {
      val newArr = arrayOfNulls<Any>(arr.size + 1)
      arr.copyInto(startIndex = 0, destination = newArr, destinationOffset = 0, endIndex = index)
      newArr[index] = newElem
      arr.copyInto(startIndex = index, destination = newArr, destinationOffset = index + 1, endIndex = arr.size)
      return newArr
    }
  }
}

/**
 * Returns the number of one-bits in the two's complement binary
 * representation of the specified `int` value.  This function is
 * sometimes referred to as the *population count*.
 */
fun bitCount(i: Int): Int {
  var currentI = i
  currentI -= (currentI.ushr(1) and 0x55555555)
  currentI = (currentI and 0x33333333) + (currentI.ushr(2) and 0x33333333)
  currentI = currentI + currentI.ushr(4) and 0x0f0f0f0f
  currentI += currentI.ushr(8)
  currentI += currentI.ushr(16)
  return currentI and 0x3f
}

/**
 * The empty node.
 */
class EmptyNode<K, V> private constructor() : HAMTNode<K, V>() {

  override val isEmpty = true
  override val size = 0

  override fun lookup(shift: Int, keyHash: Int, key: K): Option<V> = Option.empty()

  override fun lookup(shift: Int, keyHash: Int, key: K, defaultValue: V): V = defaultValue

  override fun put(shift: Int, keyHash: Int, key: K, value: V): HAMTNode<K, V> {
    return LeafSingleton(keyHash, key, value)
  }

  override fun remove(shift: Int, keyHash: Int, key: K): HAMTNode<K, V> {
    return this
  }

  override fun nodes(): Iterator<LeafNode<K, V>> = EmptyIterator

  companion object {

    private val INSTANCE = EmptyNode<Any, Any>()

    fun <K, V> instance(): EmptyNode<K, V> {
      return INSTANCE as EmptyNode<K, V>
    }
  }
}

/**
 * Representation of a HAMT leaf.
 */
abstract class LeafNode<K, V> : HAMTNode<K, V>() {

  override val isEmpty = false

  abstract val key: K
  abstract val value: V
  abstract val hash: Int

  companion object {

    fun <K, V> mergeLeaves(shift: Int, leaf1: LeafNode<K, V>, leaf2: LeafSingleton<K, V>): HAMTNode<K, V> {
      val leaf1Hash = leaf1.hash
      val leaf2Hash = leaf2.hash
      if (leaf1Hash == leaf2Hash) {
        return LeafList(leaf1Hash, leaf2.key, leaf2.value, leaf1)
      }
      val leaf1HashFragment = hashFragment(shift, leaf1Hash)
      val leaf2HashFragment = hashFragment(shift, leaf2Hash)
      val newBitmap = toBitmap(leaf1HashFragment) or toBitmap(leaf2HashFragment)
      return if (leaf1HashFragment == leaf2HashFragment) {
        val newLeaves = mergeLeaves(shift + SIZE, leaf1, leaf2)
        IndexedNode<K, V>(newBitmap, newLeaves.size, arrayOf(newLeaves))
      } else {
        IndexedNode(newBitmap, leaf1.size + leaf2.size,
          if (leaf1HashFragment < leaf2HashFragment) {
            arrayOf<Any?>(leaf1, leaf2)
          } else {
            arrayOf<Any?>(leaf2, leaf1)
          })
      }
    }
  }
}

/**
 * Representation of a HAMT leaf node with single element.
 */
class LeafSingleton<K, V>(override val hash: Int,
                          override val key: K,
                          override val value: V) : LeafNode<K, V>() {

  override val size: Int = 1

  override fun lookup(shift: Int, keyHash: Int, key: K): Option<V> {
    return if (equals(keyHash, key)) {
      Option.fromNullable(value)
    } else Option.empty()
  }

  override fun lookup(shift: Int, keyHash: Int, key: K, defaultValue: V): V {
    return if (equals(keyHash, key)) value ?: defaultValue else defaultValue
  }

  override fun put(shift: Int, keyHash: Int, key: K, value: V): HAMTNode<K, V> {
    return if (keyHash == hash && key == this.key) {
      LeafSingleton(hash, key, value)
    } else {
      mergeLeaves<K, V>(shift, this, LeafSingleton(keyHash, key, value))
    }
  }

  override fun remove(shift: Int, keyHash: Int, key: K): HAMTNode<K, V> {
    return if (keyHash == hash && key == this.key) {
      EmptyNode.instance()
    } else this
  }

  override fun nodes(): Iterator<LeafNode<K, V>> = SingletonIterator(this)

  private fun equals(keyHash: Int, key: K?) = keyHash == hash && key == this.key
}

/**
 * Representation of a HAMT leaf node with more than one element.
 */
class LeafList<K, V>(override val hash: Int,
                     override val key: K,
                     override val value: V,
                     val tail: LeafNode<K, V>) : LeafNode<K, V>() {

  override val size: Int = 1 + tail.size

  override fun lookup(shift: Int, keyHash: Int, key: K): Option<V> {
    return if (hash != keyHash) {
      Option.empty()
    } else nodes().find { node -> node.key == key }.map { it.value!! }
  }

  override fun lookup(shift: Int, keyHash: Int, key: K, defaultValue: V): V {
    if (hash != keyHash) {
      return defaultValue
    }
    var result: V = defaultValue
    val iterator = nodes()
    while (iterator.hasNext()) {
      val node = iterator.next()
      if (node.key == key) {
        result = node.value
        break
      }
    }
    return result
  }

  override fun put(shift: Int, keyHash: Int, key: K, value: V): HAMTNode<K, V> {
    return if (keyHash == hash) {
      LeafList(hash, key, value, removeElement(key) as LeafNode<K, V>)
    } else {
      mergeLeaves<K, V>(shift, this, LeafSingleton(keyHash, key, value))
    }
  }

  override fun remove(shift: Int, keyHash: Int, key: K): HAMTNode<K, V> {
    return if (keyHash == hash) {
      removeElement(key)
    } else this
  }

  private fun removeElement(k: K): HAMTNode<K, V> {
    if (k == this.key) {
      return tail
    }
    var leaf1: LeafNode<K, V> = LeafSingleton(hash, key, value)
    var leaf2: LeafNode<K, V>? = tail
    var found = false
    while (!found && leaf2 != null) {
      if (k == leaf2.key) {
        found = true
      } else {
        leaf1 = LeafList(leaf2.hash, leaf2.key, leaf2.value, leaf1)
      }
      leaf2 = if (leaf2 is LeafList<*, *>) (leaf2 as LeafList<K, V>).tail else null
    }
    return mergeNodes(leaf1, leaf2)
  }

  override fun nodes(): Iterator<LeafNode<K, V>> {
    return object : Iterator<LeafNode<K, V>> {
      var node: LeafNode<K, V>? = this@LeafList

      override fun hasNext(): Boolean {
        return node != null
      }

      override fun next(): LeafNode<K, V> {
        if (!hasNext()) {
          throw NoSuchElementException()
        }
        val result = node!!
        if (node is LeafSingleton<*, *>) {
          node = null
        } else {
          node = (node as LeafList<K, V>).tail
        }
        return result
      }
    }
  }

  companion object {

    private fun <K, V> mergeNodes(leaf1: LeafNode<K, V>, leaf2: LeafNode<K, V>?): HAMTNode<K, V> {
      if (leaf2 == null) {
        return leaf1
      }
      if (leaf1 is LeafSingleton<*, *>) {
        return LeafList(leaf1.hash, leaf1.key, leaf1.value, leaf2)
      }
      if (leaf2 is LeafSingleton<*, *>) {
        return LeafList(leaf2.hash, leaf2.key, leaf2.value, leaf1)
      }
      var result = leaf1
      var tail: LeafNode<K, V> = leaf2
      while (tail is LeafList<*, *>) {
        val list = tail as LeafList<K, V>
        result = LeafList(list.hash, list.key, list.value, result)
        tail = list.tail
      }
      return LeafList(tail.hash, tail.key, tail.value, result)
    }
  }
}

/**
 * Representation of a HAMT indexed node.
 */
class IndexedNode<K, V>(private val bitmap: Int,
                        override val size: Int,
                        val subNodes: Array<Any?>) : HAMTNode<K, V>() {

  override val isEmpty = false

  override fun lookup(shift: Int, keyHash: Int, key: K): Option<out V> {
    val frag = hashFragment(shift, keyHash)
    val bit = toBitmap(frag)
    return if (bitmap and bit != 0) {
      val n = subNodes[fromBitmap(bitmap, bit)] as HAMTNode<K, V>
      n.lookup(shift + SIZE, keyHash, key)
    } else {
      Option.empty()
    }
  }

  override fun lookup(shift: Int, keyHash: Int, key: K, defaultValue: V): V {
    val frag = hashFragment(shift, keyHash)
    val bit = toBitmap(frag)
    return if (bitmap and bit != 0) {
      val n = subNodes[fromBitmap(bitmap, bit)] as HAMTNode<K, V>
      n.lookup(shift + SIZE, keyHash, key, defaultValue)
    } else {
      defaultValue
    }
  }

  override fun put(shift: Int, keyHash: Int, key: K, value: V): HAMTNode<K, V> {
    val frag = hashFragment(shift, keyHash)
    val bit = toBitmap(frag)
    val index = fromBitmap(bitmap, bit)
    val mask = bitmap
    val exists = mask and bit != 0
    val atIndx = if (exists) subNodes[index] as HAMTNode<K, V> else null
    val child = if (exists) {
      atIndx!!.put(shift + SIZE, keyHash, key, value)
    } else {
      EmptyNode.instance<K, V>().put(shift + SIZE, keyHash, key, value)
    }

    val removed = exists && child.isEmpty
    val added = !exists && !child.isEmpty
    val newBitmap = if (removed) mask and bit.inv() else if (added) mask or bit else mask
    return if (newBitmap == 0) {
      EmptyNode.instance()
    } else if (removed) {
      if (subNodes.size <= 2 && subNodes[index xor 1] is LeafNode<*, *>) {
        subNodes[index xor 1] as HAMTNode<K, V> // collapse
      } else {
        IndexedNode<K, V>(newBitmap, size - atIndx!!.size, remove(subNodes, index))
      }
    } else if (added) {
      if (subNodes.size >= MAX_INDEX_NODE) {
        expand(frag, child, mask, subNodes)
      } else {
        IndexedNode<K, V>(newBitmap, size + child.size, insert(subNodes, index, child))
      }
    } else {
      if (!exists) {
        this
      } else {
        IndexedNode<K, V>(newBitmap, size - atIndx!!.size + child.size, update(subNodes, index, child))
      }
    }
  }

  override fun remove(shift: Int, keyHash: Int, key: K): HAMTNode<K, V> {
    val frag = hashFragment(shift, keyHash)
    val bit = toBitmap(frag)
    val index = fromBitmap(bitmap, bit)
    val mask = bitmap
    val exists = mask and bit != 0
    val atIndx = if (exists) subNodes[index] as HAMTNode<K, V> else null
    val child = if (exists) {
      atIndx!!.remove(shift + SIZE, keyHash, key)
    } else {
      EmptyNode.instance<K, V>().remove(shift + SIZE, keyHash, key)
    }
    val removed = exists && child.isEmpty
    val added = !exists && !child.isEmpty
    val newBitmap = if (removed) mask and bit.inv() else if (added) mask or bit else mask
    return if (newBitmap == 0) {
      EmptyNode.instance()
    } else if (removed) {
      if (subNodes.size <= 2 && subNodes[index xor 1] is LeafNode<*, *>) {
        subNodes[index xor 1] as HAMTNode<K, V> // collapse
      } else {
        IndexedNode<K, V>(newBitmap, size - atIndx!!.size, remove(subNodes, index))
      }
    } else if (added) {
      if (subNodes.size >= MAX_INDEX_NODE) {
        expand(frag, child, mask, subNodes)
      } else {
        IndexedNode<K, V>(newBitmap, size + child.size, insert(subNodes, index, child))
      }
    } else {
      if (!exists) {
        this
      } else {
        IndexedNode<K, V>(newBitmap, size - atIndx!!.size + child.size, update(subNodes, index, child))
      }
    }
  }

  private fun expand(frag: Int, child: HAMTNode<K, out V>, mask: Int, subNodes: Array<Any?>): ArrayNode<K, V> {
    var bit = mask
    var count = 0
    var ptr = 0
    val arr = arrayOfNulls<Any>(BUCKET_SIZE)
    for (i in 0 until BUCKET_SIZE) {
      when {
        bit and 1 != 0 -> {
          arr[i] = subNodes[ptr++]
          count++
        }
        i == frag -> {
          arr[i] = child
          count++
        }
        else -> arr[i] = EmptyNode.instance<Any, Any>()
      }
      bit = bit.ushr(1)
    }
    return ArrayNode(count, size + child.size, arr)
  }
}

/**
 * Representation of a HAMT array node.
 */
class ArrayNode<K, V>(private val count: Int,
                      override val size: Int,
                      val subNodes: Array<Any?>) : HAMTNode<K, V>() {

  override val isEmpty = false

  override fun lookup(shift: Int, keyHash: Int, key: K): Option<out V> {
    val frag = hashFragment(shift, keyHash)
    val child = subNodes[frag] as HAMTNode<K, V>
    return child.lookup(shift + SIZE, keyHash, key)
  }

  override fun lookup(shift: Int, keyHash: Int, key: K, defaultValue: V): V {
    val frag = hashFragment(shift, keyHash)
    val child = subNodes[frag] as HAMTNode<K, V>
    return child.lookup(shift + SIZE, keyHash, key, defaultValue)
  }

  override fun put(shift: Int, keyHash: Int, key: K, value: V): HAMTNode<K, V> {
    val hashFragment = hashFragment(shift, keyHash)
    val child = subNodes[hashFragment] as HAMTNode<K, V>
    val newChild = child.put(shift + SIZE, keyHash, key, value)
    return modify(child, newChild, hashFragment)
  }

  override fun remove(shift: Int, keyHash: Int, key: K): HAMTNode<K, V> {
    val hashFragment = hashFragment(shift, keyHash)
    val child = subNodes[hashFragment] as HAMTNode<K, V>
    val newChild = child.remove(shift + SIZE, keyHash, key)
    return modify(child, newChild, hashFragment)
  }

  private fun modify(child: HAMTNode<K, V>, newChild: HAMTNode<K, V>, hashFragment: Int): HAMTNode<K, V> {
    return if (child.isEmpty && !newChild.isEmpty) {
      ArrayNode(count + 1, size + newChild.size, update(subNodes, hashFragment, newChild))
    } else if (!child.isEmpty && newChild.isEmpty) {
      if (count - 1 <= MIN_ARRAY_NODE) {
        pack(hashFragment, subNodes)
      } else {
        ArrayNode(count - 1, size - child.size, update(subNodes, hashFragment, EmptyNode.instance<Any, Any>()))
      }
    } else {
      ArrayNode(count, size - child.size + newChild.size, update(subNodes, hashFragment, newChild))
    }
  }

  private fun pack(idx: Int, elements: Array<Any?>): IndexedNode<K, V> {
    val arr = arrayOfNulls<Any>(count - 1)
    var bitmap = 0
    var size = 0
    var ptr = 0
    for (i in 0 until BUCKET_SIZE) {
      val elem = elements[i] as HAMTNode<K, V>
      if (i != idx && !elem.isEmpty) {
        size += elem.size
        arr[ptr++] = elem
        bitmap = bitmap or (1 shl i)
      }
    }
    return IndexedNode(bitmap, size, arr)
  }
}
