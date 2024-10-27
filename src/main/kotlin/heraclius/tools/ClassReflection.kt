package heraclius.tools

import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KProperty
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.jvm.javaType

object ClassReflection {
    data class Property<V>(val prop: KProperty<V>) {
        @Suppress("UNCHECKED_CAST")
        val type: Class<V> get() = prop.returnType.javaType as Class<V>
        val name: String get() = prop.name
        val isReadonly: Boolean get() = prop !is KMutableProperty

        fun value(inst: V): V {
            prop.isAccessible = true
            return prop.getter.call(inst)
        }

        fun value(inst: Any, value: V): V {
            if (prop is KMutableProperty) {
                prop.isAccessible = true
                prop.setter.call(inst, TypeUtils.to(type, value))
            } else
                throw IllegalArgumentException("Property $name is not mutable")
            return value
        }
    }

    data class Meta<T : Any>(val kClass: KClass<T>) {
        val properties = lazy { kClass.memberProperties.map { Property(it) }.toList() }

        init {
            println(properties.value)
            println(kClass)
        }
    }

    private val kClassMapMeta = WeakHashMap<KClass<*>, Meta<*>>()
    private val KClass<*>.meta get() = kClassMapMeta.getOrPut(this) { Meta(this) }

    fun <T : Any> newInstance(cls: KClass<T>, vararg args: Any): T {
        if (cls.objectInstance != null) return cls.objectInstance!!
        @Suppress("UNCHECKED_CAST")
        if (cls.constructors.size == 1) return cls.java.constructors[0].newInstance(*args) as T
        @Suppress("UNCHECKED_CAST")
        return cls.java.constructors[0].newInstance(*args) as T
    }

    fun <T : Any> newInstance(cls: Class<T>, vararg args: Any): T {
        return newInstance(cls.kotlin, *args)
    }

    fun <T> getPropertyOrNull(inst: Any, name: String): Property<T>? {
        @Suppress("UNCHECKED_CAST")
        return inst::class.meta.properties.value.find { it.name == name } as Property<T>?
    }

    fun <T> getProperty(inst: Any, name: String): Property<T> {
        return getPropertyOrNull<T>(inst, name) ?: throw NoSuchFieldException("No such property $name")
    }

    fun setProperty(inst: Any, name: String, value: Any?) {
        getProperty<Any?>(inst, name).value(inst, value)
    }

    fun setPropertyOrNot(inst: Any, name: String, value: Any?) {
        val property = getPropertyOrNull<Any?>(inst, name) ?: return
        if (!property.isReadonly) property.value(inst, value)
    }

    fun <T : Any> setProperties(inst: T, keyValue: Map<Any, Any>): T {
        for (entry in keyValue) {
            val key = TypeUtils.to(String::class.java, entry.key)
            setProperty(inst, key, entry.value)
        }
        return inst
    }

    fun <T : Any> setProperties(inst: T, dict: Dict): T {
        @Suppress("UNCHECKED_CAST")
        return setProperties(inst, dict.toMap() as Map<Any, Any>)
    }

    fun <T : Any> assign(dst: T, vararg src: Any): T {
        for (item in src) {
            for (srcProperty in item::class.meta.properties.value) {
                setPropertyOrNot(dst, srcProperty.name, srcProperty.value(item))
            }
        }
        return dst
    }

    fun toMap(inst: Any): Map<String, Any> {
        val result = mutableMapOf<String, Any>()
        for (property in inst::class.meta.properties.value) {
            result[property.name] = property.value(inst) as Any
        }
        return result
    }

    fun toDict(inst: Any, symbolStore: Symbol.Store? = null): Dict {
        val result = Dict()
        for (property in inst::class.meta.properties.value) {
            val symbol = Symbol.of(property.name, property.type, symbolStore)
            result[symbol] = property.value(inst)
        }
        return result
    }
}
