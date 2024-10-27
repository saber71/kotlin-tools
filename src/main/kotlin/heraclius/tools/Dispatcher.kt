package heraclius.tools

import heraclius.tools.Symbol

/**
 * 事件调度器类，用于管理和调度不同类型的事件
 */
class Dispatcher {
    // 存储事件与其对应发射器的映射
    private val _evtMapEmitter = mutableMapOf<Symbol<*>, Emitter<*>>()

    /**
     * 清空所有事件及其发射器
     */
    fun clear() {
        _evtMapEmitter.clear()
    }

    /**
     * 获取或创建指定事件的发射器
     *
     * @param evt 事件标识
     * @return 与该事件关联的发射器
     */
    fun <T> emitter(evt: Symbol<T>): Emitter<T> {
        @Suppress("UNCHECKED_CAST")
        return _evtMapEmitter.computeIfAbsent(evt) { _ -> Emitter<T>() } as Emitter<T>
    }

    /**
     * 调度指定事件，发送数据
     *
     * @param evt  事件标识
     * @param data 需要发送的数据
     */
    fun <T> dispatch(evt: Symbol<T>, data: T) {
        @Suppress("UNCHECKED_CAST")
        (_evtMapEmitter[evt] as Emitter<T>?)?.emit(data)
    }

    /**
     * 检查是否存在指定事件
     *
     * @param evt 事件标识
     * @return 如果事件存在且有订阅者，则返回true；否则返回false
     */
    fun hasEvent(evt: Symbol<*>): Boolean {
        val emitter = _evtMapEmitter[evt] ?: return false
        return !emitter.isEmpty()
    }
}
