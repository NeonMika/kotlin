/*
 * Copyright 2010-2021 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package kotlin.time

import kotlin.native.internal.GCUnsafeCall
import kotlin.native.internal.escapeAnalysis.Escapes

internal actual inline val durationAssertionsEnabled: Boolean get() = true

@GCUnsafeCall("Kotlin_DurationValue_formatToExactDecimals")
@Escapes(0b100) // The return value is explicitly allocated on the heap.
internal actual external fun formatToExactDecimals(value: Double, decimals: Int): String
