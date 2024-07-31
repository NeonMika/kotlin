// PLATFORM_DEPENDANT_METADATA
// IGNORE_BACKEND: JS_IR
// Reason: java.io.Serializable shouldn't be accessible in JS

package test

import java.io.Serializable

public open class AllBoundsInWhen {
    public open fun <T> foo() where T: Serializable {
        throw UnsupportedOperationException()
    }
}
