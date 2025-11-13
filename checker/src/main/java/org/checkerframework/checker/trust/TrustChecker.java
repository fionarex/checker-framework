package org.checkerframework.checker.trust;

import org.checkerframework.common.basetype.BaseTypeChecker;
import org.checkerframework.framework.source.SupportedLintOptions;
import org.checkerframework.checker.trust.qual.Trust;

/** A type-checker plug-in for the Trust type system. */
@SupportedLintOptions({"debugSpew"})
public class TrustChecker extends BaseTypeChecker {

  /** Constructs a TrustChecker. */
  public TrustChecker() {
    super();
  }
}