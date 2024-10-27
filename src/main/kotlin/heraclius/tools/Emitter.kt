package heraclius.tools

typealias Listener<T> = (data: T) -> Unit

/**
 * Emitter类用于管理事件监听器的注册和触发。
 * 它支持为特定事件添加监听器，并可以指定监听器只执行一次。
 * 同时，它也支持清除所有监听器以及触发事件。
 *
 * @param T 事件的数据类型
 */
class Emitter<T> {
    private data class Data<T>(val listener: Listener<T>, val once: Boolean)

    // 存储所有注册的监听器数据
    private val dataArray = mutableListOf<Data<T>>()

    /**
     * 注册一个监听器，可指定是否仅执行一次。
     *
     * @param listener 事件监听器，用于处理事件
     * @param once     是否仅执行一次，默认为false
     * @return Emitter<T> 实例，支持链式调用
     */
    fun on(listener: Listener<T>, once: Boolean = false): Emitter<T> {
        dataArray.add(Data(listener, once))
        return this
    }

    /**
     * 注册一个仅执行一次的监听器。
     *
     * @param listener 事件监听器，用于处理事件
     * @return Emitter<T> 实例，支持链式调用
     */
    fun once(listener: Listener<T>): Emitter<T> {
        return this.on(listener, true)
    }

    /**
     * 清除所有已注册的监听器。
     *
     * @return Emitter<T> 实例，支持链式调用
     */
    fun clear(): Emitter<T> {
        dataArray.clear()
        return this
    }

    /**
     * 触发事件，调用所有监听器。
     *
     * @param data 事件数据
     * @return Emitter<T> 实例，支持链式调用
     */
    fun emit(data: T): Emitter<T> {
        for (item in dataArray.slice(0 until dataArray.size)) {
            item.listener.invoke(data)
            if (item.once) {
                dataArray.remove(item)
            }
        }
        return this
    }

    // 判断是否还有监听器
    fun isEmpty(): Boolean {
        return dataArray.isEmpty()
    }
}
