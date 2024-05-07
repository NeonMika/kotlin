/*
 * Copyright 2010-2024 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package kotlin.js

import kotlin.wasm.internal.ExternalInterfaceType

/**
 * Exception thrown by the JavaScript code.
 * All exceptions thrown by JS code are signalled to Wasm code as `JsException`.
 * One can catch such exception in Wasm, but no details of the original exception can be retrieved from it.
 * */
public class JsException(
    public val value: JsAny,
    message: String = "Some non-error like JavaScript value was thrown from JavaScript side.",
    override val jsStack: ExternalInterfaceType = captureStackTrace()
) : Throwable(message = message)
