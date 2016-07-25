import java.util.logging.Logger

fun <T: Any> unwrapCompanionClass(ofClass: Class<T>): Class<*> {
    return if (ofClass.enclosingClass != null && ofClass.enclosingClass.kotlin.java == ofClass) {
        ofClass.enclosingClass
    } else {
        ofClass
    }
}

fun <T: Any> logger(forClass: Class<T>): Logger {
    return Logger.getLogger(unwrapCompanionClass(forClass).name)
}

fun <T: Any> T.logger(): Logger {
    return logger(this.javaClass)
}
