package heraclius.tools.classses_loader

interface ClassesLoader {
    fun load(): List<Class<*>>
}
