/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.fir.types

import org.jetbrains.kotlin.fir.symbols.ConeTypeParameterLookupTag

abstract class ConeTypeParameterType(
    typeArguments: Array<out ConeTypeProjection>,
    attributes: ConeAttributes,
    nullability: ConeNullability
) : ConeLookupTagBasedType(typeArguments, attributes, nullability) {
    abstract override val lookupTag: ConeTypeParameterLookupTag
}
