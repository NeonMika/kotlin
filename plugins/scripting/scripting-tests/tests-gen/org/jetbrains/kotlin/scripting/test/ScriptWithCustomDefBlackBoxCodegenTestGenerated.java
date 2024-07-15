/*
 * Copyright 2010-2024 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.scripting.test;

import com.intellij.testFramework.TestDataPath;
import org.jetbrains.kotlin.test.util.KtTestUtil;
import org.jetbrains.kotlin.test.TargetBackend;
import org.jetbrains.kotlin.test.TestMetadata;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.regex.Pattern;

/** This class is generated by {@link org.jetbrains.kotlin.generators.tests.GenerateTestsKt}. DO NOT MODIFY MANUALLY */
@SuppressWarnings("all")
@TestMetadata("plugins/scripting/scripting-tests/testData/codegen/testScripts")
@TestDataPath("$PROJECT_ROOT")
public class ScriptWithCustomDefBlackBoxCodegenTestGenerated extends AbstractScriptWithCustomDefBlackBoxCodegenTest {
  @Test
  public void testAllFilesPresentInTestScripts() {
    KtTestUtil.assertAllTestsPresentByMetadataWithExcluded(this.getClass(), new File("plugins/scripting/scripting-tests/testData/codegen/testScripts"), Pattern.compile("^(.+)\\.kts$"), null, TargetBackend.JVM_IR, true);
  }

  @Test
  @TestMetadata("declarationsOrderExtension.test.kts")
  public void testDeclarationsOrderExtension_test() {
    runTest("plugins/scripting/scripting-tests/testData/codegen/testScripts/declarationsOrderExtension.test.kts");
  }

  @Test
  @TestMetadata("declarationsOrderSingleExpression.test.kts")
  public void testDeclarationsOrderSingleExpression_test() {
    runTest("plugins/scripting/scripting-tests/testData/codegen/testScripts/declarationsOrderSingleExpression.test.kts");
  }

  @Test
  @TestMetadata("declarationsOrderTopLevelProperty.test.kts")
  public void testDeclarationsOrderTopLevelProperty_test() {
    runTest("plugins/scripting/scripting-tests/testData/codegen/testScripts/declarationsOrderTopLevelProperty.test.kts");
  }

  @Test
  @TestMetadata("declarationsOrderWith.test.kts")
  public void testDeclarationsOrderWith_test() {
    runTest("plugins/scripting/scripting-tests/testData/codegen/testScripts/declarationsOrderWith.test.kts");
  }

  @Test
  @TestMetadata("empty.test.kts")
  public void testEmpty_test() {
    runTest("plugins/scripting/scripting-tests/testData/codegen/testScripts/empty.test.kts");
  }

  @Test
  @TestMetadata("params.test.kts")
  public void testParams_test() {
    runTest("plugins/scripting/scripting-tests/testData/codegen/testScripts/params.test.kts");
  }

  @Test
  @TestMetadata("reflect.test.kts")
  public void testReflect_test() {
    runTest("plugins/scripting/scripting-tests/testData/codegen/testScripts/reflect.test.kts");
  }

  @Test
  @TestMetadata("simple.test.kts")
  public void testSimple_test() {
    runTest("plugins/scripting/scripting-tests/testData/codegen/testScripts/simple.test.kts");
  }
}
