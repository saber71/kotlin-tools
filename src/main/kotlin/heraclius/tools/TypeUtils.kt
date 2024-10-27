package heraclius.tools

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*

object TypeUtils {
    data class Converter<To>(
        val toType: Class<To>?, val fn: ((value: Any?) -> To)?, val srcTypes: List<Class<*>?>? = null
    ) {
        fun call(value: Any?): To {
            val result: To?
            if (fn != null) result = fn.invoke(value)
            else {
                val toTypeName = toType?.name ?: "null"
                result = callToConvert(value, "to" + StringUtils.firstUpper(toTypeName))
            }
            return checkType(result, toType)
        }
    }

    private val _typeMapConverter = mutableMapOf<Class<*>?, List<Converter<*>>>()

    init {
        register(Converter(Int::class.java, ::toInt))
        register(Converter(Short::class.java, ::toShort))
        register(Converter(Byte::class.java, ::toByte))
        register(Converter(Long::class.java, ::toLong))
        register(Converter(Float::class.java, ::toFloat))
        register(Converter(Double::class.java, ::toDouble))
        register(Converter(Boolean::class.java, ::toBoolean))
        register(Converter(Char::class.java, ::toChar))
        register(Converter(String::class.java, ::toString))
        register(Converter(List::class.java, ::toList))
        register(Converter(Map::class.java, ::toMap))
        register(Converter(null, ::toNull))
        register(Converter(LocalDate::class.java, ::toLocalDate))
        register(Converter(LocalDateTime::class.java, ::toLocalDateTime))
    }

    fun <T> register(converter: Converter<T>) {
        val cls = converter.toType
        val converters = _typeMapConverter[cls] ?: emptyList()
        _typeMapConverter[cls] = converters + converter
    }

    fun <T> to(cls: Class<T>?, value: Any?): T {
        @Suppress("UNCHECKED_CAST") val converters = (_typeMapConverter[cls]
            ?: throw IllegalArgumentException("No converter found for $cls")) as List<Converter<T>>
        val convertersWithSrcTypes = converters.filter { it.srcTypes != null }
        val srcType = value?.javaClass
        for (converter in convertersWithSrcTypes) {
            if (converter.srcTypes!!.find { it == srcType } != null) {
                return converter.call(value)
            }
        }
        if (srcType != null) {
            for (converter in convertersWithSrcTypes) {
                if (converter.srcTypes!!.find { it?.isAssignableFrom(srcType) == true } != null) {
                    return converter.call(value)
                }
            }
        }
        for (converter in converters) {
            if (converter.srcTypes == null) {
                return converter.call(value)
            }
        }
        throw IllegalArgumentException("No converter found for $cls")
    }

    fun toInt(value: Any?): Int {
        return when (value) {
            is Int -> value
            is Boolean -> if (value) 1 else 0
            is Char -> value.code
            is Number -> value.toInt()
            is String -> value.toIntOrNull() ?: throw IllegalArgumentException("Cannot convert $value to Int")
            else -> checkType(callToConvert(value, "toInt"), Int::class.java)
                ?: throw IllegalArgumentException("Cannot convert $value to Int")
        }
    }

    fun toShort(value: Any?): Short {
        return when (value) {
            is Short -> value
            is Boolean -> if (value) 1 else 0
            is Char -> value.code.toShort()
            is Number -> value.toShort()
            is String -> value.toShortOrNull() ?: throw IllegalArgumentException("Cannot convert $value to Short")
            else -> checkType(callToConvert(value, "toShort"), Short::class.java)
                ?: throw IllegalArgumentException("Cannot convert $value to Short")
        }
    }

    fun toByte(value: Any?): Byte {
        return when (value) {
            is Byte -> value
            is Boolean -> if (value) 1 else 0
            is Char -> value.code.toByte()
            is Number -> value.toByte()
            is String -> value.toByteOrNull() ?: throw IllegalArgumentException("Cannot convert $value to Byte")
            else -> checkType(callToConvert(value, "toByte"), Byte::class.java)
                ?: throw IllegalArgumentException("Cannot convert $value to Byte")
        }
    }

    fun toLong(value: Any?): Long {
        return when (value) {
            is Long -> value
            is Boolean -> if (value) 1 else 0
            is Char -> value.code.toLong()
            is Number -> value.toLong()
            is String -> value.toLongOrNull() ?: throw IllegalArgumentException("Cannot convert $value to Long")
            else -> checkType(callToConvert(value, "toLong"), Long::class.java)
                ?: throw IllegalArgumentException("Cannot convert $value to Long")
        }
    }

    fun toFloat(value: Any?): Float {
        return when (value) {
            is Float -> value
            is Boolean -> if (value) 1f else 0f
            is Char -> value.code.toFloat()
            is Number -> value.toFloat()
            is String -> value.toFloatOrNull() ?: throw IllegalArgumentException("Cannot convert $value to Float")
            else -> checkType(callToConvert(value, "toFloat"), Float::class.java)
                ?: throw IllegalArgumentException("Cannot convert $value to Float")
        }
    }

    fun toDouble(value: Any?): Double {
        return when (value) {
            is Double -> value
            is Boolean -> if (value) 1.0 else 0.0
            is Char -> value.code.toDouble()
            is Number -> value.toDouble()
            is String -> value.toDoubleOrNull() ?: throw IllegalArgumentException("Cannot convert $value to Double")
            else -> checkType(callToConvert(value, "toDouble"), Double::class.java)
                ?: throw IllegalArgumentException("Cannot convert $value to Double")
        }
    }

    fun toBoolean(value: Any?): Boolean {
        return when (value) {
            is Boolean -> value
            is Char -> value != 0.toChar()
            is Number -> value != 0
            is String -> value.toBoolean()
            else -> checkType(callToConvert(value, "toBoolean"), Boolean::class.java) ?: (value != null)
        }
    }

    fun toChar(value: Any?): Char {
        return when (value) {
            is Char -> value
            is Boolean -> if (value) (1).toChar() else (0).toChar()
            is Number -> value.toInt().toChar()
            is String -> if (value.length == 1) value.toCharArray()[0] else throw IllegalArgumentException("Cannot convert $value to Char")
            else -> checkType(callToConvert(value, "toChar"), Char::class.java)
                ?: throw IllegalArgumentException("Cannot convert $value to Char")
        }
    }

    fun toString(value: Any?): String {
        return when (value) {
            is String -> value
            else -> checkType(value?.toString(), String::class.java)
                ?: throw IllegalArgumentException("Cannot convert $value to String")
        }
    }

    fun toList(value: Any?): List<Any?> {
        return when (value) {
            is List<*> -> value.toList()
            is String -> StringUtils.toList(value)
            is Array<*> -> value.toList()
            is Iterable<*> -> value.toList()
            is Enumeration<*> -> value.toList()
            else -> checkType(callToConvert(value, "toList"), List::class.java)
                ?: throw IllegalArgumentException("Cannot convert $value to List")
        }
    }

    fun toMap(value: Any?): Map<Any?, Any?> {
        return when (value) {
            is Map<*, *> -> value.toMap()
            else -> checkType(callToConvert(value, "toMap"), Map::class.java)
                ?: throw IllegalArgumentException("Cannot convert $value to Map")
        }
    }

    fun toNull(value: Any?) {
        return when (value) {
            null -> {}
            else -> checkType(callToConvert(value, "toNull"), null)
                ?: throw IllegalArgumentException("Cannot convert $value to Null")
        }
    }

    fun toLocalDateTime(value: Any?): LocalDateTime {
        return when (value) {
            is LocalDateTime -> value
            is String -> DateUtils.from(value)
            is Long -> DateUtils.from(value)
            is LocalDate -> LocalDateTime.of(value, LocalTime.of(0, 0, 0))
            else -> throw IllegalArgumentException("Cannot convert $value to LocalDateTime")
        }
    }

    fun toLocalDate(value: Any?): LocalDate {
        return when (value) {
            is LocalDate -> value
            is String -> DateUtils.from(value).toLocalDate()
            is Long -> DateUtils.from(value).toLocalDate()
            is LocalDateTime -> value.toLocalDate()
            else -> throw IllegalArgumentException("Cannot convert $value to LocalDate")
        }
    }

    /**
     * 通用方法调用函数
     *
     * 该函数尝试在给定的对象实例上查找并调用指定名称的方法如果找到该方法，则调用它并返回结果；
     * 如果未找到该方法，则返回null
     *
     * @param inst 任何对象实例，表示我们将要在哪个对象上调用方法
     * @param methodName 字符串，表示我们要调用的方法的名称
     * @return 任何类型的结果，取决于调用的方法返回什么；如果方法不存在，则返回null
     */
    fun <T> callToConvert(inst: Any?, methodName: String): T? {
        if (inst == null) return null
        try {
            val method = inst::class.java.getMethod(methodName)
            @Suppress("UNCHECKED_CAST") return method.invoke(inst) as T
        } catch (e: NoSuchMethodException) {
            return null
        }
    }

    fun <T> checkType(value: Any?, targetType: Class<*>?): T {
        if (targetType == null) {
            if (value != null) throw IllegalArgumentException("${value}的类型与${targetType}不一致")
        } else if (value == null) {
            throw IllegalArgumentException("${value}的类型与${targetType}不一致")
        } else {
            val checkResult =
                targetType.isAssignableFrom(javaClass(value))
                        || targetType.isInstance(value)
                        || (value is Int && targetType == Int::class.java)
                        || (value is Short && targetType == Short::class.java)
                        || (value is Byte && targetType == Byte::class.java)
                        || (value is Long && targetType == Long::class.java)
                        || (value is Double && targetType == Double::class.java)
                        || (value is Float && targetType == Float::class.java)
                        || (value is Char && targetType == Char::class.java)
                        || (value is Boolean && targetType == Boolean::class.java)
            if (!checkResult) throw IllegalArgumentException("${value}的类型与${targetType}不一致")
        }
        @Suppress("UNCHECKED_CAST") return value as T
    }

    fun <T : Any> javaClass(inst: T): Class<T> {
        val cls = when (inst) {
            is Int -> Int::class.java
            is Short -> Short::class.java
            is Byte -> Byte::class.java
            is Long -> Long::class.java
            is Float -> Float::class.java
            is Double -> Double::class.java
            is Boolean -> Boolean::class.java
            is Char -> Char::class.java
            else -> inst.javaClass
        }
        @Suppress("UNCHECKED_CAST")
        return cls as Class<T>
    }
}
