package heraclius.tools.annotations

@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class MarkProperty(vararg val components: String)
