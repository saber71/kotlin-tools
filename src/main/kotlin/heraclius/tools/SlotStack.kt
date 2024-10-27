package heraclius.tools

open class SlotStack<V> : Iterable<V> {
    private var _list = mutableListOf<Slot<V>>()
    private val _itemMapSlots = mutableMapOf<V, MutableList<Slot<V>>>()

    fun clear() {
        _list.clear()
        _itemMapSlots.clear()
    }

    fun add(item: V) {
        if (_itemMapSlots.contains(item)) throw RuntimeException("item already exists")
        val slot = Slot(item)
        _itemMapSlots[item] = mutableListOf(slot)
        _list.add(slot)
    }

    fun pop() {
        if (_list.isEmpty()) return
        recovery(_list.last().value)
    }

    fun remove(item: V) {
        val slots = _itemMapSlots[item] ?: return
        for (slot in slots) {
            _list.remove(slot)
        }
        _itemMapSlots.remove(item)
    }

    fun top(): V? {
        return _list.lastOrNull()?.value
    }

    fun toTop(item: V) {
        val slots = _itemMapSlots[item] ?: return add(item)
        slots.last().placeholder = true
        val slot = Slot(item)
        slots.add(slot)
        _list.add(slot)
    }

    fun recovery(item: V) {
        val slots = _itemMapSlots[item] ?: return
        if (slots.size == 1) {
            remove(item)
            return
        }
        _list.remove(slots.removeLast())
        slots.last().placeholder = false
    }

    override fun iterator(): Iterator<V> {
        val list = _list.filter { !it.placeholder }.toList()
        return object : Iterator<V> {
            private var _curIndex = 0

            override fun hasNext(): Boolean {
                return _curIndex < list.size
            }

            override fun next(): V {
                return list[_curIndex++].value
            }
        }
    }

    operator fun get(index: Int): V {
        if (index < 0 || index >= _list.size) throw IndexOutOfBoundsException()
        var curIndex = 0
        for (slot in _list) {
            if (slot.placeholder) continue
            if (curIndex == index) return slot.value
            curIndex++
        }
        throw IndexOutOfBoundsException()
    }

    private data class Slot<V>(val value: V, var placeholder: Boolean = false)
}
