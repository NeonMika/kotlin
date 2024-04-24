/*
 * Copyright 2010-2024 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

// Auto-generated file. DO NOT EDIT!
// Generated by org.jetbrains.kotlin.generators.builtins.arrays.GenerateCommonArrays

package kotlin

import kotlin.internal.ActualizeByJvmBuiltinProvider

/**
 * A generic array of objects. When targeting the JVM, instances of this class are represented as `T[]`.
 * Array instances can be created using the [arrayOf], [arrayOfNulls] and [emptyArray]
 * standard library functions.
 *
 * See [Kotlin language documentation](https://kotlinlang.org/docs/arrays.html)
 * for more information on arrays.
 */
@ActualizeByJvmBuiltinProvider
public expect class Array<T> {
    /**
     * Creates a new array of the specified [size], where each element is calculated by calling the specified
     * [init] function.
     *
     * The function [init] is called for each array element sequentially starting from the first one.
     * It should return the value for an array element given its index.
     *
     * @throws RuntimeException if the specified [size] is negative.
     */
    @Suppress("WRONG_MODIFIER_TARGET")
    public inline constructor(size: Int, init: (Int) -> T)

    /**
     * Returns the array element at the given [index].
     *
     * This method can be called using the index operator:
     * ```
     * value = array[index]
     * ```
     *
     * If the [index] is out of bounds of this array, throws an [IndexOutOfBoundsException] except in Kotlin/JS
     * where the behavior is unspecified.
     */
    public operator fun get(index: Int): T

    /**
     * Sets the array element at the given [index] to the given [value].
     *
     * This method can be called using the index operator:
     * ```
     * array[index] = value
     * ```
     *
     * If the [index] is out of bounds of this array, throws an [IndexOutOfBoundsException] except in Kotlin/JS
     * where the behavior is unspecified.
     */
    public operator fun set(index: Int, value: T): Unit

    /**
     * Returns the number of elements in the array.
     */
    public val size: Int

    /** Creates an [Iterator] for iterating over the elements of the array. */
    public operator fun iterator(): Iterator<T>
}
