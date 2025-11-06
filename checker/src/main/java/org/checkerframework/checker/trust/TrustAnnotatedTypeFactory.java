package org.checkerframework.checker.trust;

import org.checkerframework.common.basetype.BaseAnnotatedTypeFactory;
import org.checkerframework.common.basetype.BaseTypeChecker;

/** Annotated type factory for the Trust Checker. */
public class TrustAnnotatedTypeFactory extends BaseAnnotatedTypeFactory {
  @SuppressWarnings("this-escape")
  public TrustAnnotatedTypeFactory(BaseTypeChecker checker) {
    super(checker);
    postInit();
  }
}
