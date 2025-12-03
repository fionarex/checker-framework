package org.checkerframework.checker.trust.qual;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.checkerframework.framework.qual.DefaultQualifierInHierarchy;
import org.checkerframework.framework.qual.SubtypeOf;

/** The Trust annotation is used as the base type in the type hierachy. */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@SubtypeOf({})
@Target({ElementType.TYPE_USE, ElementType.TYPE_PARAMETER})
@DefaultQualifierInHierarchy
public @interface Trust {
  /**
   * The trust level associated with the annotated element.
   *
   * @return the trust level
   */
  int level() default 0;
}
