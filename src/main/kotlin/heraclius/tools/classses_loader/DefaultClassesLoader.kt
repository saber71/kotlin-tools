package heraclius.tools.classses_loader

class DefaultClassesLoader(private val _classList: List<Class<*>>) : ClassesLoader {
    override fun load(): List<Class<*>> {
        return _classList
    }
}
