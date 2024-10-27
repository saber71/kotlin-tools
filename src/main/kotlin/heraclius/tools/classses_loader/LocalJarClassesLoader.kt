package heraclius.tools.classses_loader

import java.io.File
import java.net.URLClassLoader
import java.util.jar.JarFile

class LocalJarClassesLoader(val file: File) : ClassesLoader {
    override fun load(): List<Class<*>> {
        val url = file.toURI().toURL()
        val classLoader = URLClassLoader.newInstance(arrayOf(url), Thread.currentThread().contextClassLoader)
        return JarClassesLoader(JarFile(file), classLoader).load()
    }
}
