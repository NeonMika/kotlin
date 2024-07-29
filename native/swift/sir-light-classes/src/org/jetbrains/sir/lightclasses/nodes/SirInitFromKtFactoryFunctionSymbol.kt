/*
 * Copyright 2010-2024 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.sir.lightclasses.nodes

import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.projectStructure.KaModule
import org.jetbrains.kotlin.analysis.api.symbols.KaFunctionSymbol
import org.jetbrains.kotlin.analysis.api.symbols.KaNamedFunctionSymbol
import org.jetbrains.kotlin.analysis.api.symbols.isTopLevel
import org.jetbrains.kotlin.analysis.api.symbols.name
import org.jetbrains.kotlin.analysis.api.types.symbol
import org.jetbrains.kotlin.sir.*
import org.jetbrains.kotlin.sir.providers.SirSession
import org.jetbrains.kotlin.sir.providers.source.KotlinSourceForFactoryFunction
import org.jetbrains.sir.lightclasses.SirFromKtSymbol
import org.jetbrains.sir.lightclasses.extensions.documentation
import org.jetbrains.sir.lightclasses.extensions.lazyWithSessions
import org.jetbrains.sir.lightclasses.utils.translateParameters

internal fun isFactoryThatShouldBeTranslatedToExtensionInit(kaFunctionSymbol: KaFunctionSymbol, session: KaSession): Boolean {
//    val parent = with (session) { kaFunctionSymbol.containingDeclaration }
    val returnTypeClass = kaFunctionSymbol.returnType.symbol ?: return false
    val classId = returnTypeClass.classId ?: return false

    return !classId.isNestedClass &&
            returnTypeClass.name == kaFunctionSymbol.name &&
            kaFunctionSymbol.isTopLevel &&
            kaFunctionSymbol.callableId?.packageName == classId.packageFqName
}

internal class SirInitFromKtFactoryFunctionSymbol(
    override val ktSymbol: KaNamedFunctionSymbol,
    override val ktModule: KaModule,
    override val sirSession: SirSession
) : SirInit(), SirFromKtSymbol<KaFunctionSymbol> {
    override val origin: SirOrigin
        get() = KotlinSourceForFactoryFunction(ktSymbol)
    override val visibility: SirVisibility
        get() = SirVisibility.PUBLIC
    override val documentation: String? by lazyWithSessions {
        ktSymbol.documentation()
    }
    override lateinit var parent: SirDeclarationParent
    override val kind: SirCallableKind
        get() = SirCallableKind.CLASS_METHOD
    override var body: SirFunctionBody? = null
    override val isFailable: Boolean
        get() = false // FIXME: add test
    override val parameters: List<SirParameter> by lazy {
        translateParameters()
    }
    override val initKind: SirInitializerKind
        get() = SirInitializerKind.CONVENIENCE
    override val isOverride: Boolean
        get() = false // FIXME
}