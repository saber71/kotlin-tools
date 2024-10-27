package heraclius.tools

// 通用管理上下文的对象
open class CommonContext : SlotStack<Dict>() {
    init {
        // 默认添加一个空的字典
        add(Dict())
    }

    // 获取当前上下文对象
    fun current(): Dict {
        return top() ?: throw RuntimeException("current context is empty")
    }

    // 备份当前上下文
    fun backupCurrent(): CommonContext {
        val cur = current()
        val dict = ClassReflection.newInstance(cur::class).extend(cur)
        toTop(dict)
        return this
    }

    // 恢复当前上下文
    fun recoveryCurrent(): CommonContext {
        recovery(current())
        return this
    }
}
