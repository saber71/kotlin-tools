import heraclius.tools.ClassReflection
import heraclius.tools.DateUtils
import heraclius.tools.JSON

import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.javaType

data class User(val name: String, private var age: Int, val child: User? = null) {
    fun abc(name: String) {
        println(name)
    }

    fun abc(name: Int) {
        println(name)
    }
}

fun main() {
    println(DateUtils.format(DateUtils.from(1692749400000L)))
    println(DateUtils.timestamp(DateUtils.from(1692749400000L)))
    println(DateUtils.from("2023-08-23 08:10:00"))
    //    println(ClassFilter(ResourceClassesLoader("heraclius")).subClasses(ClassesLoader::class.java).classList)
    val user = ClassReflection.newInstance(User::class, "Heraclius", 18, User("aaa", 1))
    val user2 = User("Byzantine", 1000)
    user.abc("dsa")
    val member = user::class.members.toList()[0]
    if (member is KMutableProperty) {
        println(member.returnType.javaType == Int::class.java)
        ClassReflection.setProperty(user, "age", "20")
    }
    println(ClassReflection.toDict(user))
    println(User::class.memberProperties)
    println(ClassReflection.assign(user, user2))
    println(JSON.stringify(ClassReflection.toMap(user)))
}
