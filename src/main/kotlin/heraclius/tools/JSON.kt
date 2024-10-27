package heraclius.tools

import com.google.gson.GsonBuilder

object JSON {
    private val gson = GsonBuilder().create()

    fun <T : Any> parse(jsonString: String, cls: Class<T>): T {
        return gson.fromJson(jsonString, cls)
    }

    fun parse(jsonString: String): Map<String, Any> {
        @Suppress("UNCHECKED_CAST")
        return gson.fromJson(jsonString, Map::class.java) as Map<String, Any>
    }

    fun stringify(obj: Any): String {
        return gson.toJson(obj)
    }
}
