package heraclius.tools.classses_loader

import java.io.File

class FileClassesLoader(val directory: File, val packagePrefix: String = directory.name) : ClassesLoader {
    override fun load(): List<Class<*>> {
        return recursiveFind(directory, packagePrefix)
    }

    private fun recursiveFind(directory: File, packagePrefix: String): List<Class<*>> {
        return directory.listFiles()?.asSequence()?.flatMap { file ->
            if (file.isDirectory) {
                recursiveFind(file, "$packagePrefix.${file.name}")
            } else {
                val className = "${packagePrefix}.${file.nameWithoutExtension}".replace('/', '.')
                try {
                    listOf(Class.forName(className, false, Thread.currentThread().contextClassLoader))
                } catch (e: ClassNotFoundException) {
                    emptyList()
                }
            }
        }?.toList() ?: emptyList()
    }
}
