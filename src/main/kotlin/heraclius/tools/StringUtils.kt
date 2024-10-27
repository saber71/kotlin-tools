package heraclius.tools

object StringUtils {
    fun firstUpper(string: String): String {
        if (string.isEmpty()) return string
        return string.first().uppercase() + string.substring(1)
    }

    fun firstLower(string: String): String {
        if (string.isEmpty()) return string
        return string.first().lowercase() + string.substring(1)
    }

    fun toList(string: String): List<String> {
        return string.toList().map { it.toString() }
    }

    fun flatCase(string: String): String {
        return string.lowercase().replace("-", "").replace("_", "")
    }

    fun camelCase(string: String): String {
        return firstLower(string.split(Regex("[-_]")).filter { it.isNotEmpty() }.joinToString("") { firstUpper(it) })
    }

    fun underscoreCase(string: String): String {
        return string.split("-")
            .joinToString("_")
            .map { if (it.isUpperCase()) "_" + it.lowercase() else it }
            .joinToString("")
            .replace(Regex("__+"), "_")
            .replace(Regex("(_+$)|(^_+)"), "")
    }

    fun kebabCase(string: String): String {
        return string.split("_")
            .joinToString("-")
            .map { if (it.isUpperCase()) "-" + it.lowercase() else it }
            .joinToString("")
            .replace(Regex("--+"), "-")
            .replace(Regex("(-+$)|(^-+)"), "")
    }

    fun width(str: String): Int {
        return str.map { char ->
            when {
                char in '\u1100'..'\uD7FF' || char in '\uF900'..'\uFAFF' || char in '\uFE30'..'\uFE6F' || char in '\uFF00'..'\uFFEF' -> 2
                else -> 1
            }
        }.sum()
    }

    fun align(str: String, width: Int, align: StringAlign): String {
        if (align == StringAlign.LEFT) return str.padEnd(width)
        if (align == StringAlign.RIGHT) return str.padStart(width)
        val diff = width - str.length
        if (diff <= 0) return str
        val left = diff / 2
        val leftStr = if (left > 0) " ".padEnd(left) else ""
        val right = diff - left
        val rightStr = if (right > 0) " ".padEnd(right) else ""
        return leftStr + str + rightStr
    }
}
