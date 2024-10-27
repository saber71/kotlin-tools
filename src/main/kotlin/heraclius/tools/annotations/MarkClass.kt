package heraclius.tools.annotations

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class MarkClass(vararg val components: String)
