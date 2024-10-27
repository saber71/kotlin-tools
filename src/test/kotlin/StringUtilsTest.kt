import heraclius.tools.StringUtils
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class StringUtilsTest {
    @Test
    fun firstUpper() {
        assertEquals(StringUtils.firstUpper("abc"), "Abc")
        assertEquals(StringUtils.firstUpper("a"), "A")
    }

    @Test
    fun firstLower() {
        assertEquals(StringUtils.firstLower("Abc"), "abc")
        assertEquals(StringUtils.firstLower("A"), "a")
    }

    @Test
    fun flatCase() {
        assertEquals(StringUtils.flatCase("HelloWorld"), "helloworld")
        assertEquals(StringUtils.flatCase("-HelloWorld"), "helloworld")
        assertEquals(StringUtils.flatCase("-Hello_-World_"), "helloworld")
    }

    @Test
    fun kebabCase() {
        assertEquals(StringUtils.kebabCase("HelloWorld"), "hello-world")
        assertEquals(StringUtils.kebabCase("-HelloWorld"), "hello-world")
        assertEquals(StringUtils.kebabCase("-Hello_-World_"), "hello-world")
    }

    @Test
    fun underscoreCase() {
        assertEquals(StringUtils.underscoreCase("HelloWorld"), "hello_world")
        assertEquals(StringUtils.underscoreCase("-HelloWorld"), "hello_world")
        assertEquals(StringUtils.underscoreCase("-Hello_-World_"), "hello_world")
    }

    @Test
    fun camelCase() {
        assertEquals(StringUtils.camelCase("HelloWorld"), "helloWorld")
        assertEquals(StringUtils.camelCase("-HelloWorld"), "helloWorld")
        assertEquals(StringUtils.camelCase("-Hello_-World_"), "helloWorld")
    }

    @Test
    fun width() {
        assertEquals(11, StringUtils.width("Hello World"))
        assertEquals(12, StringUtils.width("你好，世界。"))
        assertEquals(13, StringUtils.width("Hello，世界。"))
    }
}
