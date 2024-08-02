/*
 * Copyright 2010-2024 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.build.asm

import kotlinx.metadata.hasAnnotations
import kotlinx.metadata.jvm.KotlinClassMetadata
import org.jetbrains.org.objectweb.asm.AnnotationVisitor
import org.jetbrains.org.objectweb.asm.ClassVisitor
import org.jetbrains.org.objectweb.asm.ClassWriter
import org.jetbrains.org.objectweb.asm.Opcodes
import org.jetbrains.org.objectweb.asm.Opcodes.ASM9

/**
 * ASM JVM class transformer that marks each visited class as @kotlin.Deprecated with the specified [deprecationMessage]
 * Context: https://youtrack.jetbrains.com/issue/KT-70251
 */
class DeprecatingClassTransformer(
    cv: ClassWriter,
    private val deprecationMessage: String,
    private val processedClassCallback: (className: String) -> Unit = {},
) : ClassVisitor(ASM9, cv) {
    override fun visit(
        version: Int, access: Int, name: String, signature: String?, superName: String?,
        interfaces: Array<out String>?,
    ) {
        processedClassCallback(name.replace('/', '.'))
        val deprecatedAccess = access or Opcodes.ACC_DEPRECATED
        super.visit(version, deprecatedAccess, name, signature, superName, interfaces)
        val deprecatedAnnotation = super.visitAnnotation("Lkotlin/Deprecated;", true)
        deprecatedAnnotation.visit("message", deprecationMessage)
        deprecatedAnnotation.visitEnd()
    }

    override fun visitAnnotation(desc: String?, visible: Boolean): AnnotationVisitor? {
        if ("Lkotlin/Metadata;" == desc) {
            return MetadataAnnotationVisitor(super.visitAnnotation(desc, visible))
        }
        return super.visitAnnotation(desc, visible)
    }

    private inner class MetadataAnnotationVisitor(av: AnnotationVisitor) : AnnotationVisitor(ASM9, av) {
        private val values = mutableMapOf<String, Any>()

        override fun visit(name: String?, value: Any?) {
            super.visit(name, value)
            if (name != null && value != null) {
                values[name] = value
            }
        }

        override fun visitArray(name: String?): AnnotationVisitor {
            return object : AnnotationVisitor(ASM9) {
                private val list = mutableListOf<String>()

                override fun visit(name: String?, value: Any?) {
                    if (value != null) {
                        list.add(value as String)
                    }
                }

                override fun visitEnd() {
                    if (name != null) {
                        values[name] = list.toTypedArray()
                    }
                }
            }
        }

        @Suppress("UNCHECKED_CAST")
        override fun visitEnd() {
            super.visitEnd()
            val header = kotlinx.metadata.jvm.Metadata(
                values["k"] as Int,
                values["mv"] as IntArray?,
                values["d1"] as Array<String>?,
                values["d2"] as Array<String>?,
                values["xs"] as String?,
                values["pn"] as String?,
                values["xi"] as Int?
            )

            val metadata = KotlinClassMetadata.readStrict(header)
            if (metadata is KotlinClassMetadata.Class) {
                val kClass = metadata.kmClass
                kClass.hasAnnotations = true
            }
        }
    }
}