package heraclius.tools

import java.util.WeakHashMap

/**
 * 符号类，用于标识一个变量或常量。
 *
 * @param name 符号的名称。
 * @param type 符号的值的类型。
 */
class Symbol<T>(val name: String, val type: Class<T>? = null) {
    class Store : WeakHashMap<String, Symbol<*>>()

    companion object {
        private val defaultStore = Store()

        // 获取或创建一个Symbol
        fun <V> of(name: String, type: Class<V>? = null, store: Store? = null): Symbol<V> {
            val store1 = store ?: defaultStore
            var symbol = store1[name]
            @Suppress("UNCHECKED_CAST")
            if (symbol != null) return symbol as Symbol<V>
            symbol = Symbol(name, type)
            store1[name] = symbol
            return symbol
        }

        // 获取一个Symbol
        fun <V> get(name: String?, store: Store? = null): Symbol<V> {
            val store1 = store ?: defaultStore
            @Suppress("UNCHECKED_CAST")
            return (store1[name] ?: throw RuntimeException("Not found symbol by $name")) as Symbol<V>
        }
    }

    override fun toString(): String {
        return name
    }

    fun checkType(value: Any?): T {
        @Suppress("UNCHECKED_CAST")
        if (type == null) return value as T
        return TypeUtils.checkType(value, type)
    }
}
