package org.checkerframework.checker.trust.qual;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.checkerframework.framework.qual.SubtypeOf;

/**
 * The TrustReq annotation is used to specify the trust level required by an object or parameter .
 *
 * <p>The Trust element indicates the trust level, with higher numbers representing higher trust.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@SubtypeOf({})
@Target({ElementType.CONSTRUCTOR, ElementType.METHOD, ElementType.FIELD})
public @interface TrustReq {
  /**
   * The trust level. Higher numbers represent higher trust.
   *
   * @return the trust level
   */
  int level() default 0;
}
