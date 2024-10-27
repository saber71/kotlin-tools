package heraclius.tools.annotations

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class MarkMethod(vararg val components: String)
