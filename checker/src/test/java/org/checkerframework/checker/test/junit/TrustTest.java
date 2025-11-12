package org.checkerframework.checker.test.junit;

import java.io.File;
import java.util.List;
import org.checkerframework.checker.trust.TrustChecker;
import org.checkerframework.framework.test.CheckerFrameworkPerDirectoryTest;
import org.junit.runners.Parameterized.Parameters;

public class TrustTest extends CheckerFrameworkPerDirectoryTest {

  /**
   * Create a TrustTest.
   *
   * @param testFiles the files containing test code, which will be type-checked
   */
  public TrustTest(List<File> testFiles) {
    super(testFiles, TrustChecker.class, "trust");
  }

  @Parameters
  public static String[] getTestDirs() {
    return new String[] {"trust"};
  }
}
