/*
 * Copyright 2010-2024 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package kotlin.js

import kotlin.wasm.internal.ExternalInterfaceType

/**
 * Exception thrown by the JavaScript code.
 * All exceptions thrown by JS code are signalled to Wasm code as `JsException`.
 *
 * [thrownValue] is a value thrown by JavaScript; commonly it's an instance of an `Error` subclass, but it could be also any value
 * [externalStackTrace] is a stacktrace received from the [thrownValue] if it's an instance of an `Error` subclass
 * */
public class JsException(
    public val thrownValue: JsAny,
    message: String = "Some non-error like JavaScript value was thrown from JavaScript side.",
    private val externalStackTrace: ExternalInterfaceType? = null
) : Throwable(message = message) {
    override internal val jsStack: ExternalInterfaceType
        get() = externalStackTrace ?: super.jsStack
}
