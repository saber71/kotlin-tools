package heraclius.tools.classses_loader

import java.io.File
import java.net.URI
import java.net.URLDecoder
import java.util.jar.JarFile

class ResourceClassesLoader(val packageName: String) : ClassesLoader {
    override fun load(): List<Class<*>> {
        val packagePath = packageName.replace('.', '/')
        val resources = Thread.currentThread().contextClassLoader.getResources(packagePath).toList()

        return resources.mapNotNull { it.toURI() }
            .flatMap { uri ->
                when (uri.scheme) {
                    "file" -> FileClassesLoader(File(uri), packageName).load()
                    "jar" -> JarClassesLoader(
                        JarFile(
                            URLDecoder.decode(
                                URI.create(
                                    uri.toString().substringBefore("!/") + "!"
                                ).toURL().file, Charsets.UTF_8
                            )
                        )
                    ).load()

                    else -> emptyList()
                }
            }
    }
}
