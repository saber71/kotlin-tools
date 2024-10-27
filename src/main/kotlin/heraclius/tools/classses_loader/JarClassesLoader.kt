package heraclius.tools.classses_loader

import java.util.jar.JarFile

class JarClassesLoader(val jarFile: JarFile, val classLoader: ClassLoader = Thread.currentThread().contextClassLoader) :
    ClassesLoader {
    override fun load(): List<Class<*>> {
        return jarFile.entries().asSequence()
            .filter { it.name.endsWith(".class") }
            .map { it.name.replace('/', '.').removeSuffix(".class") }
            .mapNotNull { className ->
                try {
                    Class.forName(className, false, classLoader)
                } catch (e: ClassNotFoundException) {
                    null
                }
            }
            .toList()
    }
}
