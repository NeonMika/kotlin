/*
 * Copyright 2010-2023 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.generators.builtins.numbers.primitives

import java.io.File
import kotlin.reflect.KClass

private fun String.shift(): String {
    return this.split(END_LINE).joinToString(separator = END_LINE) { if (it.isEmpty()) it else "    $it" }
}

internal fun file(builtBy: KClass<*>, init: FileBuilder.() -> Unit): FileBuilder {
    val file = FileBuilder(builtBy.qualifiedName!!)
    file.init()
    return file
}

internal interface PrimitiveBuilder {
    fun build(): String

    fun throwIfAlreadyInitialized(arg: Any?, propertyName: String, className: String) {
        if (arg != null) {
            throw AssertionError("Property '$propertyName' for '$className' was already initialized")
        }
    }

    fun throwIfWasNotInitialized(arg: Any?, propertyName: String, className: String) {
        if (arg == null) {
            throw AssertionError("Property '$propertyName' for '$className' wasn't set to its value")
        }
    }

    fun throwNotInitialized(propertyName: String, className: String): Nothing {
        throw AssertionError("Property '$propertyName' for '$className' wasn't initialized to access")
    }
}

internal abstract class AnnotatedAndDocumented {
    protected var doc: String? = null
    val annotations: MutableList<String> = mutableListOf()
    var additionalComments: String? = null

    fun appendDoc(doc: String) {
        if (this.doc == null) {
            this.doc = doc
        } else {
            this.doc += "$END_LINE$doc"
        }
    }

    protected fun StringBuilder.printDocumentationAndAnnotations(forceMultiLineDoc: Boolean = false) {
        if (doc != null) {
            appendLine(doc!!.printAsDoc(forceMultiLineDoc))
        }

        if (additionalComments != null) {
            additionalComments!!.lines().forEach { line ->
                appendLine("// $line")
            }
        }

        if (annotations.isNotEmpty()) {
            appendLine(annotations.joinToString(separator = END_LINE) { "@$it" })
        }
    }

    private fun String.printAsDoc(forceMultiLine: Boolean = false): String {
        if (this.contains(END_LINE) || forceMultiLine) {
            return this.split(END_LINE).joinToString(
                separator = END_LINE, prefix = "/**$END_LINE", postfix = "$END_LINE */"
            ) { if (it.isEmpty()) " *" else " * $it" }
        }
        return "/** $this */"
    }
}

internal class FileBuilder(private val builtBy: String) : PrimitiveBuilder {
    private val suppresses: MutableList<String> = mutableListOf()
    private val imports: MutableList<String> = mutableListOf()
    private val fileComments: MutableList<String> = mutableListOf()
    private val topLevelDeclarations: MutableList<PrimitiveBuilder> = mutableListOf()

    fun suppress(suppress: String) {
        suppresses += suppress
    }

    fun import(newImport: String) {
        imports += newImport
    }

    fun appendFileComment(doc: String) {
        fileComments += doc
    }

    fun klass(init: ClassBuilder.() -> Unit): ClassBuilder {
        val classBuilder = ClassBuilder()
        topLevelDeclarations += classBuilder.apply(init)
        return classBuilder
    }

    fun method(init: MethodBuilder.() -> Unit): MethodBuilder {
        val methodBuilder = MethodBuilder()
        topLevelDeclarations.add(methodBuilder.apply(init))
        return methodBuilder
    }

    override fun build(): String {
        return buildString {
            appendLine(File("license/COPYRIGHT_HEADER.txt").readText())
            appendLine()
            appendLine("// Auto-generated file. DO NOT EDIT!")
            appendLine("// Generated by $builtBy")
            appendLine()

            if (suppresses.isNotEmpty()) {
                appendLine(suppresses.joinToString(separator = ", ", prefix = "@file:Suppress(", postfix = ")") { "\"$it\"" })
                appendLine()
            }

            appendLine("package kotlin")
            appendLine()

            if (imports.isNotEmpty()) {
                appendLine(imports.joinToString(separator = END_LINE) { "import $it" })
                appendLine()
            }

            if (fileComments.isNotEmpty()) {
                appendLine(fileComments.joinToString(separator = END_LINE) { "// $it" })
                appendLine()
            }

            append(topLevelDeclarations.joinToString(separator = END_LINE + END_LINE, postfix = END_LINE) { it.build() })
        }
    }
}

internal class ClassBuilder : AnnotatedAndDocumented(), PrimitiveBuilder {
    var name: String = ""
    var visibility: MethodVisibility = MethodVisibility.PUBLIC
    var expectActual: ExpectActualModifier = ExpectActualModifier.Unspecified
    private var primaryConstructor: PrimaryConstructorBuilder? = PrimaryConstructorBuilder()
    private var secondaryConstructor: SecondaryConstructorBuilder? = null
    private val typeParams: MutableList<String> = mutableListOf()
    private var superTypes: List<String> = emptyList()
    private var companionObject: CompanionObjectBuilder? = null

    private var builders: MutableList<PrimitiveBuilder> = mutableListOf()

    fun primaryConstructor(init: PrimaryConstructorBuilder.() -> Unit): PrimaryConstructorBuilder {
        val builder = PrimaryConstructorBuilder().apply(init)
        primaryConstructor = builder
        return builder

    }

    fun noPrimaryConstructor() {
        primaryConstructor = null
    }

    fun secondaryConstructor(init: SecondaryConstructorBuilder.() -> Unit): SecondaryConstructorBuilder {
        val secondaryConstructorBuilder = SecondaryConstructorBuilder()
        secondaryConstructorBuilder.expectActual = ExpectActualModifier.Inherited(from = ::expectActual)
        secondaryConstructor = secondaryConstructorBuilder.apply(init)
        return secondaryConstructorBuilder
    }

    fun superType(type: String) {
        superTypes += type
    }

    fun typeParam(type: String) {
        typeParams += type
    }

    fun companionObject(init: CompanionObjectBuilder.() -> Unit): CompanionObjectBuilder {
        throwIfAlreadyInitialized(companionObject, "companionObject", "ClassBuilder")
        val companionObjectBuilder = CompanionObjectBuilder()
        companionObjectBuilder.expectActual = ExpectActualModifier.Inherited(from = ::expectActual)
        companionObject = companionObjectBuilder.apply(init)
        builders.add(companionObjectBuilder)
        return companionObjectBuilder
    }

    fun method(init: MethodBuilder.() -> Unit): MethodBuilder {
        val methodBuilder = MethodBuilder()
        methodBuilder.expectActual = ExpectActualModifier.Inherited(from = ::expectActual)
        builders.add(methodBuilder.apply(init))
        return methodBuilder
    }

    fun property(init: PropertyBuilder.() -> Unit): PropertyBuilder {
        val propertyBuilder = PropertyBuilder()
        propertyBuilder.expectActual = ExpectActualModifier.Inherited(from = ::expectActual)
        builders += propertyBuilder.apply(init)
        return propertyBuilder
    }

    fun classBody(text: String): FreeFormBuilder {
        return FreeFormBuilder(text).also { builders += it }
    }

    override fun build(): String {
        fun filterSupertypeConstructors(): List<String> =
            if (expectActual.isExpect) superTypes.map { it.substringBefore('(') } else superTypes

        return buildString {
            this.printDocumentationAndAnnotations()

            append("${visibility.name.lowercase()} ")
            expectActual.modifier?.let { append(it).append(' ') }
            append("class $name")
            if (typeParams.isNotEmpty()) {
                typeParams.joinTo(this, prefix = "<", postfix = ">")
            }
            primaryConstructor?.takeUnless { it.visibility == MethodVisibility.PRIVATE && expectActual.isExpect }?.let {
                append(it.build())
            }
            val supertypes = filterSupertypeConstructors()
            if (supertypes.isNotEmpty()) {
                append(" : ${supertypes.joinToString()}")
            }
            appendLine(" {")

            secondaryConstructor?.let {
                appendLine(it.build().shift())
                appendLine()
            }

            if (builders.isNotEmpty()) {
                appendLine(builders.joinToString(separator = END_LINE + END_LINE) { it.build().shift() })
            }
            append("}")
        }
    }
}

internal class CompanionObjectBuilder : AnnotatedAndDocumented(), PrimitiveBuilder {
    private val properties: MutableList<PropertyBuilder> = mutableListOf()
    var expectActual: ExpectActualModifier = ExpectActualModifier.Unspecified

    fun property(init: PropertyBuilder.() -> Unit): PropertyBuilder {
        val propertyBuilder = PropertyBuilder()
        propertyBuilder.expectActual = ExpectActualModifier.Inherited(from = ::expectActual)
        propertyBuilder.modifier("const")
        properties += propertyBuilder.apply(init)
        return propertyBuilder
    }

    override fun build(): String {
        return buildString {
            printDocumentationAndAnnotations()
            append("public ")
            expectActual.modifier?.let { append(it).append(' ') }
            if (properties.isEmpty()) {
                append("companion object {}")
            } else {
                appendLine("companion object {")
                appendLine(properties.joinToString(separator = END_LINE + END_LINE) { it.build().shift() })
                append("}")
            }
        }
    }
}

internal class PrimaryConstructorBuilder : AnnotatedAndDocumented(), PrimitiveBuilder {
    var visibility: MethodVisibility? = MethodVisibility.PRIVATE
    var expectActual: ExpectActualModifier = ExpectActualModifier.Unspecified
    private var parameters: MutableList<MethodParameterBuilder> = mutableListOf()

    fun parameter(init: MethodParameterBuilder.() -> Unit): MethodParameterBuilder {
        val argBuilder = MethodParameterBuilder()
        parameters.add(argBuilder.apply(init))
        return argBuilder
    }

    override fun build(): String {
        return buildString {
            if (annotations.isNotEmpty() || doc != null) appendLine() else append(' ')
            printDocumentationAndAnnotations()

            visibility?.let { append("${it.name.lowercase()} ") }
            expectActual.modifier?.let { append(it).append(' ') }
            append("constructor")
            append(parameters.joinToString(prefix = "(", postfix = ")") { it.build() })
        }
    }
}

internal class SecondaryConstructorBuilder : AnnotatedAndDocumented(), PrimitiveBuilder {
    var visibility: MethodVisibility = MethodVisibility.PRIVATE
    var expectActual: ExpectActualModifier = ExpectActualModifier.Unspecified
    var body: String? = null
    private val modifiers: MutableList<String> = mutableListOf()
    private val parameters: MutableList<MethodParameterBuilder> = mutableListOf()
    private var argumentsToPrimaryContructor: MutableList<String>? = mutableListOf()

    fun modifier(modifier: String) {
        modifiers += modifier
    }

    fun parameter(init: MethodParameterBuilder.() -> Unit): MethodParameterBuilder {
        val argBuilder = MethodParameterBuilder()
        parameters.add(argBuilder.apply(init))
        return argBuilder
    }

    fun argument(arg: String) {
        argumentsToPrimaryContructor!! += arg
    }

    fun noPrimaryConstructorCall() {
        argumentsToPrimaryContructor = null
    }

    fun primaryConstructorCall(vararg arguments: String) {
        argumentsToPrimaryContructor = arguments.toMutableList()
    }

    fun String.setAsBlockBody() {
        body = " {$END_LINE${this.shift()}$END_LINE}"
    }

    override fun build(): String {
        return buildString {
            printDocumentationAndAnnotations()

            append("${visibility.name.lowercase()} ")
            expectActual.modifier?.let { append(it).append(' ') }
            modifiers.forEach { append(it).append(' ') }
            append("constructor")
            append(parameters.joinToString(prefix = "(", postfix = ")") { it.build() })
            argumentsToPrimaryContructor?.joinTo(this, prefix = " : this(", postfix = ")")
            append(body ?: "")
        }
    }
}

internal class MethodSignatureBuilder(private var expectActual: () -> ExpectActualModifier) : PrimitiveBuilder {
    var isExternal: Boolean = false
    var visibility: MethodVisibility = MethodVisibility.PUBLIC
    var isOverride: Boolean = false
    var isInline: Boolean = false
    var isInfix: Boolean = false
    var isOperator: Boolean = false

    var methodName: String? = null
    private val parameters: MutableList<MethodParameterBuilder> = mutableListOf()
    var returnType: String? = null

    val parameterName: String
        get() = parameters.singleOrNull()?.name ?: throwNotInitialized("name", "MethodParameterBuilder")

    val parameterType: String
        get() = parameters.singleOrNull()?.type ?: throwNotInitialized("type", "MethodParameterBuilder")

    fun parameter(init: MethodParameterBuilder.() -> Unit): MethodParameterBuilder {
        val argBuilder = MethodParameterBuilder()
        parameters += argBuilder.apply(init)
        return argBuilder
    }

    override fun build(): String {
        throwIfWasNotInitialized(methodName, "methodName", "MethodSignatureBuilder")
        throwIfWasNotInitialized(returnType, "returnType", "MethodSignatureBuilder")

        return buildString {
            append("${visibility.name.lowercase()} ")
            expectActual().modifier?.let { append(it).append(' ') }
            if (isExternal) append("external ")
            if (isOverride) append("override ")
            if (isInline) append("inline ")
            if (isInfix) append("infix ")
            if (isOperator) append("operator ")
            append("fun $methodName(${parameters.joinToString { it.build() }}): $returnType")
        }
    }
}

internal enum class MethodVisibility {
    PUBLIC, INTERNAL, PRIVATE
}

internal class MethodParameterBuilder : PrimitiveBuilder {
    var name: String? = null
    var type: String? = null

    override fun build(): String {
        throwIfWasNotInitialized(name, "name", "MethodParameterBuilder")
        throwIfWasNotInitialized(type, "type", "MethodParameterBuilder")
        return "$name: $type"
    }
}

internal class MethodBuilder : AnnotatedAndDocumented(), PrimitiveBuilder {
    private var signature: MethodSignatureBuilder? = null
    private var body: String? = null

    var expectActual: ExpectActualModifier = ExpectActualModifier.Unspecified

    val methodName: String
        get() = signature?.methodName ?: throwNotInitialized("methodName", "MethodSignatureBuilder")

    val returnType: String
        get() = signature?.returnType ?: throwNotInitialized("returnType", "MethodSignatureBuilder")

    val parameterName: String
        get() = signature?.parameterName ?: throwNotInitialized("name", "MethodParameterBuilder")

    val parameterType: String
        get() = signature?.parameterType ?: throwNotInitialized("type", "MethodParameterBuilder")

    fun signature(init: MethodSignatureBuilder.() -> Unit): MethodSignatureBuilder {
        throwIfAlreadyInitialized(signature, "signature", "MethodBuilder")
        val signatureBuilder = MethodSignatureBuilder(::expectActual)
        signature = signatureBuilder.apply(init)
        return signatureBuilder
    }

    fun modifySignature(modify: MethodSignatureBuilder.() -> Unit) {
        throwIfWasNotInitialized(signature, "signature", "MethodBuilder")
        signature!!.apply(modify)
    }

    override fun build(): String {
        throwIfWasNotInitialized(signature, "signature", "MethodBuilder")

        return buildString {
            printDocumentationAndAnnotations()
            append(signature!!.build())
            append(body ?: "")
        }
    }

    fun noBody() { body = null }

    fun String.setAsExpressionBody() {
        body = " =$END_LINE    $this"
    }

    fun String.setAsBlockBody() {
        body = " {$END_LINE${this.shift()}$END_LINE}"
    }
}

internal class PropertyBuilder : AnnotatedAndDocumented(), PrimitiveBuilder {
    var visibility: MethodVisibility? = MethodVisibility.PUBLIC
    var expectActual: ExpectActualModifier = ExpectActualModifier.Unspecified
    var name: String? = null
    var type: String? = null
    var value: String? = null
    var getterBody: String? = null
    private val modifiers: MutableList<String> = mutableListOf()

    fun modifier(modifier: String) {
        modifiers += modifier
    }

    fun String.setAsExpressionGetterBody() {
        getterBody = " = $this"
    }

    fun String.setAsBlockGetterBody() {
        getterBody = " {$END_LINE${this.shift()}$END_LINE}"
    }

    override fun build(): String {
        throwIfWasNotInitialized(name, "name", "PropertyBuilder")
        throwIfWasNotInitialized(type, "type", "PropertyBuilder")

        return buildString {
            printDocumentationAndAnnotations(forceMultiLineDoc = true)
            visibility?.let { append("${it.name.lowercase()} ") }
            expectActual.modifier?.let { append(it).append(' ') }
            modifiers.forEach { append(it).append(' ') }
            append("val $name: $type")
            value?.let { append(" = ").append(value) }
            if (getterBody != null) {
                append("$END_LINE    get()")
                append(getterBody)
            }
        }
    }
}

internal class FreeFormBuilder(val text: String) : PrimitiveBuilder {
    override fun build(): String = text
}

sealed class ExpectActualModifier {
    object Unspecified : ExpectActualModifier()
    object Expect : ExpectActualModifier()
    object Actual : ExpectActualModifier()
    class Inherited(val from: () -> ExpectActualModifier) : ExpectActualModifier()

    private val nested: ExpectActualModifier
        get() = when (this) {
            Expect, Unspecified -> Unspecified
            Actual -> Actual
            is Inherited -> from().nested
        }

    val effective: ExpectActualModifier get() = if (this is Inherited) from().nested else this

    val modifier: String?
        get() = when (effective) {
            Expect -> "expect"
            Actual -> "actual"
            Unspecified -> null
            is Inherited -> error("Effective modifier should be expect/actual/unspecified")
        }

    val isExpect get() = effective == Expect
    val isActual get() = effective == Actual
}