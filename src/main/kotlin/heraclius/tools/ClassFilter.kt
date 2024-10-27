package heraclius.tools

import heraclius.tools.classses_loader.ClassesLoader
import heraclius.tools.classses_loader.DefaultClassesLoader
import heraclius.tools.classses_loader.ResourceClassesLoader

class ClassFilter(private val _classesLoader: ClassesLoader) {
    private val _lazyValues = lazy { _classesLoader.load() }
    val values: List<Class<*>> get() = _lazyValues.value

    companion object {
        private val loadClasses = lazy { ClassFilter(ResourceClassesLoader("heraclius")) }

        fun fromClassList(classList: List<Class<*>>): ClassFilter {
            return ClassFilter(DefaultClassesLoader(classList))
        }

        fun loadClasses(): ClassFilter {
            return loadClasses.value
        }
    }

    fun <T : Any> list(): List<Class<T>> {
        @Suppress("UNCHECKED_CAST")
        return values as List<Class<T>>
    }

    fun <T : Any> instances(): List<T> {
        return list<T>().map { ClassReflection.newInstance(it) }
    }

    fun subClasses(superClass: Class<*>): ClassFilter {
        return fromClassList(values.filter { superClass.isAssignableFrom(it) && it != superClass })
    }

    fun annotatedWith(annotation: Class<out Annotation>): ClassFilter {
        return fromClassList(values.filter { it.isAnnotationPresent(annotation) })
    }
}
