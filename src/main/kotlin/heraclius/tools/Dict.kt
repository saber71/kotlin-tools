package heraclius.tools

import java.util.*

// 字典键值对
class Dict {
    private val _dataMap: MutableMap<Symbol<*>, Any> = mutableMapOf()

    // 父级字典
    private val _parents = mutableSetOf<Dict>()

    // 删除的键值对
    private val _removedKey = WeakHashMap<Symbol<*>, Any>()

    // 继承父级字典
    fun extend(parent: Dict): Dict {
        _parents.add(parent)
        return this
    }

    // 删除键值对
    fun remove(key: Symbol<*>): Dict {
        _removedKey[key] = true
        _dataMap.remove(key)
        return this
    }

    // 获取键值对，未找到则返回null
    fun <V> valueOrNull(key: Symbol<V>): V? {
        if (_removedKey.containsKey(key)) return null
        if (!_dataMap.containsKey(key)) {
            for (parent in _parents.reversed()) {
                val value = parent.valueOrNull(key)
                if (value != null) return key.checkType(value)
            }
            return null
        } else {
            @Suppress("UNCHECKED_CAST")
            return this._dataMap[key] as V
        }
    }

    // 获取键值对，未找到则返回默认值
    fun <V> value(key: Symbol<V>, default: V): V {
        val value = valueOrNull(key) ?: return default
        return value
    }

    // 复制本对象
    fun clone(): Dict {
        val result = Dict()
        result._dataMap.putAll(_dataMap)
        result._parents.addAll(_parents)
        result._removedKey.putAll(_removedKey)
        return result
    }

    // 将Dict的所有键值对转换为Map
    fun toMap(): Map<Symbol<*>, Any> {
        val result = mutableMapOf<Symbol<*>, Any>()
        for (parent in _parents) {
            result.putAll(parent.toMap())
        }
        result.putAll(_dataMap)
        for (key in _removedKey.keys) {
            result.remove(key)
        }
        return result
    }

    // 使用hashMap作为本对象的hashcode
    override fun hashCode(): Int {
        return _dataMap.hashCode()
    }

    // 使用hashMap的toString
    override fun toString(): String {
        return toMap().toString()
    }

    // 获取键值对
    operator fun <V> get(key: Symbol<V>): V {
        return this.valueOrNull(key) ?: throw RuntimeException("$key not found")
    }

    // 设置键值对
    operator fun <V> set(key: Symbol<V>, value: V) {
        this._dataMap[key] = key.checkType(value) as Any
        _removedKey.remove(key)
    }

    // 判断键值对是否存在
    operator fun contains(key: Symbol<*>): Boolean {
        if (_removedKey.containsKey(key)) return false
        if (!_dataMap.containsKey(key)) {
            for (parent in _parents.reversed()) {
                if (parent.contains(key)) return true
            }
            return false
        }
        return true
    }

    // 合并两个字典
    operator fun plus(other: Dict): Dict {
        val result = clone()
        for (entry in other.toMap()) {
            result._dataMap[entry.key] = entry.value
            result._removedKey.remove(entry.key)
        }
        return result
    }

    // 剔除掉两个字典的共同部分
    operator fun minus(other: Dict): Dict {
        val result = clone()
        for (key in other.toMap().keys) {
            result.remove(key)
        }
        return result
    }

    // 合并另一个字典至本对象
    operator fun plusAssign(other: Dict) {
        for (entry in other.toMap()) {
            _dataMap[entry.key] = entry.value
            _removedKey.remove(entry.key)
        }
    }

    // 从本对象中剔除掉与另一个字典共同的部分
    operator fun minusAssign(other: Dict) {
        for (key in other._dataMap.keys) {
            remove(key)
        }
    }

    // 判断两个字典的内容是否相等
    override operator fun equals(other: Any?): Boolean {
        if (other !is Dict) return false
        if (_dataMap.hashCode() == other.hashCode()) return true
        val map1 = this.toMap()
        val map2 = other.toMap()
        if (map1.size != map2.size) return false
        for (entry in map1.entries) {
            val otherVal = map2[entry.key]
            if (otherVal != entry.value) return false
        }
        return true
    }
}
