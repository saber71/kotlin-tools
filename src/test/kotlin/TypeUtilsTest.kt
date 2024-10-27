import heraclius.tools.StringUtils
import heraclius.tools.TypeUtils
import org.junit.jupiter.api.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class TypeUtilsTest {
    @Test
    fun to() {
        TypeUtils.register(
            TypeUtils.Converter(Int::class.java, null, listOf(TestConvertType::class.java))
        )
        TypeUtils.register(
            TypeUtils.Converter(Double::class.java, null, listOf(TestConvertType::class.java))
        )
        assertEquals(TypeUtils.to(Int::class.java, "20"), 20)
        assertEquals(TypeUtils.to(Double::class.java, "20"), 20.0)
        assertEquals(TypeUtils.to(Int::class.java, TestConvertType("20")), 20)
        assertEquals(TypeUtils.to(Double::class.java, TestConvertType("20")), 20.0)
        assertContentEquals(
            TypeUtils.to(List::class.java, TestConvertType("20")), mutableListOf("2", "0")
        )
    }

    data class TestConvertType(val value: String) {
        fun toInt(): Int {
            return value.toInt()
        }

        fun toDouble(): Double {
            return value.toDouble()
        }

        fun toList(): List<Any> {
            return StringUtils.toList(value)
        }
    }
}
