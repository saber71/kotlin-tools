import heraclius.tools.DateUtils

fun main() {
    println(DateUtils.format(DateUtils.from(1692749400000L)))
    println(DateUtils.timestamp(DateUtils.from(1692749400000L)))
    println(DateUtils.from("2023-08-23 08:10:00"))
}
