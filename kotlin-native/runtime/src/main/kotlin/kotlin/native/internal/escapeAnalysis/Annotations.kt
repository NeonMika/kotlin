/*
 * Copyright 2010-2024 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package kotlin.native.internal.escapeAnalysis

/**
 * Escape analysis annotations.
 *
 * Kotlin/Native uses escape analysis in optimized builds to determine which objects can be allocated on the stack.
 * It involves analysing function bodies to determine what happens to the function parameters.
 * For `external fun` this is impossible, because there are no bodies. So, these functions involving
 * object parameters (or receivers) must be correctly annotated to specify which parameters escape (e.g. get stored
 * in some global) and how parameters relate to each other and to the return value.
 *
 * To mark escaping parameters use [Escapes] or [Escapes.Nothing] annotations.
 * To mark relationships between parameters use [PointsTo] annotation.
 *
 * Every `external fun` in the stdlib with object parameters/receivers must be marked either by [Escapes], [Escapes.Nothing] or [PointsTo].
 * Except functions defined in `kotlinx.cinterop`, `kotlin.concurrent`, `kotlin.native.concurrent` (the list may change in the future)
 *
 * For more details, see `EscapeAnalysis.kt` in the compiler sources.
 */
private object EscapeAnalysisAnnotations // A hack to have a place for file-level documentation

/**
 * Specifies which parameters/receivers of the `external fun` escape (see [EscapeAnalysisAnnotations] for details).
 *
 * ```
 * class C {
 *     @Escapes(0b0101)
 *     external fun Array<Any>.f(p0: Any, p1: Any): Any
 * }
 * ```
 * In this example `f` has 2 parameters and 2 receivers. The bitmask `0b0101` is deciphered as follows:
 * ```
 * 0b0  1  0  1
 *   ^  ^  ^  ^
 *   |  |  |  |
 *   |  |  |  this@C
 *   p1 p0 this@Array<Any>
 * ```
 * So, the dispatch receiver `this@C` and `p0` escape, the extension receiver `this@Array<Any>` and `p1` do not.
 *
 * @param who bitmask of parameters/receivers where set bits indicate escaping
 * @see Escapes.Nothing
 * @see EscapeAnalysisAnnotations
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.BINARY)
internal annotation class Escapes(val who: Int) {

    /**
     * Marks a function where no parameter escapes (see [EscapeAnalysisAnnotations] for details).
     *
     * Equivalent to `@Escapes(0)`.
     *
     * @see Escapes
     * @see EscapeAnalysisAnnotations
     */
    @Target(AnnotationTarget.FUNCTION)
    @Retention(AnnotationRetention.BINARY)
    annotation class Nothing
}

/**
 * Specifies how parameters/receivers and the return value of the `external fun` point to each other (see [EscapeAnalysisAnnotations] for details).
 *
 * There are 4 kinds of `p1` pointing to `p2`:
 * 1. `p1 -> p2`
 * 2. `p1 -> p2.intestines`
 * 3. `p1.intestines -> p2`
 * 4. `p1.intestines -> p2.intestines`
 * Where `intestines` is typically used with arrays to mean "elements of the array".
 *
 * [onWhom] will contain the list of lists of nibbles, where each nibble is a kind (and so has value 0-4).
 * The order in both the external and internal lists are: dispatch receiver, extension receiver, parameters, return value.
 *
 * ```
 * class C {
 *     @PointsTo(0x00000, 0x03400, 0x00000, 0x00000, 0x01020)
 *     external fun Array<Any>.f(p0: Array<Any>, p1: Any): Any
 * }
 * ```
 * In this example `f` has 2 parameters, 2 receivers and 1 return value.
 * A value `0xabcde` is deciphered as:
 * ```
 * 0xa  b  c  d  e
 *   ^  ^  ^  ^  ^
 *   |  |  |  |  |
 *   |  |  |  |  this@C
 *   |  p1 p0 this@Array<Any>
 *   return value
 * ```
 *
 * So, the second `0x03400` means that `Array<Any>` points to `p0` with kind `4` and to `p1` with kind `3`
 * (i.e. some element of the array will be `p1` and another will be some element of `p0`).
 *
 * And the last `0x01020` means that the return value points to `p1` with kind `1` and to `Array<Any>` with kind `2`
 * (i.e. the return value will be `p1` and will also be an element of `Array<Any>`)
 *
 * @param onWhom a list of parameters/receivers + the return value, where each item `i` is a list of nibbles (packed into `Int`) and each nibble
 *               `j` is a value `0-4` representing a kind of parameter/receiver/return `i` pointing to parameter/receiver/return `j`
 * @see EscapeAnalysisAnnotations
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.BINARY)
internal annotation class PointsTo(vararg val onWhom: Int)