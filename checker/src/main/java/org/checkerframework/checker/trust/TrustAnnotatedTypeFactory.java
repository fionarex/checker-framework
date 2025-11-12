package org.checkerframework.checker.trust;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Set;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.util.Elements;
import org.checkerframework.checker.initialization.qual.UnderInitialization;
import org.checkerframework.checker.trust.qual.Trust;
import org.checkerframework.checker.trust.qual.TrustType;
import org.checkerframework.common.basetype.BaseAnnotatedTypeFactory;
import org.checkerframework.common.basetype.BaseTypeChecker;
import org.checkerframework.framework.type.MostlyNoElementQualifierHierarchy;
import org.checkerframework.framework.type.QualifierHierarchy;
import org.checkerframework.framework.util.DefaultQualifierKindHierarchy;
import org.checkerframework.framework.util.QualifierKind;
import org.checkerframework.framework.util.QualifierKindHierarchy;
import org.checkerframework.javacutil.AnnotationBuilder;
import org.checkerframework.javacutil.AnnotationUtils;

/** Annotated type factory for the Trust Checker. */
public class TrustAnnotatedTypeFactory extends BaseAnnotatedTypeFactory {

  /** AnnotationMirror for {@link Trust}. */
  protected final AnnotationMirror TRUST;

  /** AnnotationMirror for {@link TrustType} */
  protected final AnnotationMirror TRUST_TYPE;

  /**
   * Creates a {@link TrustAnnotatedTypeFactory}.
   *
   * @param checker the trust checker
   */
  @SuppressWarnings("this-escape")
  public TrustAnnotatedTypeFactory(BaseTypeChecker checker) {
    super(checker);
    TRUST = AnnotationBuilder.fromClass(elements, Trust.class);
    TRUST_TYPE = AnnotationBuilder.fromClass(elements, TrustType.class);
    this.postInit();
  }

  /**
   * Returns the supported type qualifiers for the Trust Checker.
   *
   * @return the supported type qualifiers
   */
  @Override
  protected Set<Class<? extends Annotation>> createSupportedTypeQualifiers() {
    return getBundledTypeQualifiers(Trust.class);
  }

  /**
   * Creates qualifier hierarchy
   *
   * @return the TrustQualifierHierarchy, subclass of MostlyNoElementQualifierHierarchy
   */
  @Override
  protected QualifierHierarchy createQualifierHierarchy() {
    return new TrustQualifierHierarchy(getSupportedTypeQualifiers(), elements);
  }

  /** Heavily inspired by fenum checker's approach to handling createQualifierHierarchy(). */
  protected class TrustQualifierHierarchy extends MostlyNoElementQualifierHierarchy {
    /**
     * Qualifier Hierarchy for the Trust Checker
     *
     * @param qualifierClasses the collection of qualifier annotation classes that participate in
     *     the hierarchy
     * @param elements utility for operating on program elements
     */
    public TrustQualifierHierarchy(
        Collection<Class<? extends Annotation>> qualifierClasses, Elements elements) {
      super(qualifierClasses, elements, TrustAnnotatedTypeFactory.this);
    }

    /**
     * Creates the {@link QualifierKindHierarchy} for trust qualifiers. This implementation uses
     * {@link DefaultQualifierKindHierarchy} with {@code @Trust} as the root qualifier.
     *
     * @param qualifierClasses the set of qualifier annotation classes
     * @return a {@link DefaultQualifierKindHierarchy} rooted at {@code @Trust}
     */
    @Override
    protected QualifierKindHierarchy createQualifierKindHierarchy(
        @UnderInitialization TrustQualifierHierarchy this,
        Collection<Class<? extends Annotation>> qualifierClasses) {
      return new DefaultQualifierKindHierarchy(qualifierClasses, Trust.class);
    }

    /**
     * Determines whether one annotation is a subtype of another. In this hierarchy, subtyping is
     * defined strictly by annotation equality.
     *
     * @param subAnno the potential subtype annotation
     * @param subKind the qualifier kind of {@code subAnno}
     * @param superAnno the potential supertype annotation
     * @param superKind the qualifier kind of {@code superAnno}
     * @return {@code true} if the annotations are identical; {@code false} otherwise
     */
    @Override
    protected boolean isSubtypeWithElements(
        AnnotationMirror subAnno,
        QualifierKind subKind,
        AnnotationMirror superAnno,
        QualifierKind superKind) {
      return AnnotationUtils.areSame(subAnno, superAnno);
    }

    /**
     * Computes the least upper bound (LUB) of two annotations. If the annotations are identical,
     * the LUB is that annotation; otherwise, the default {@code @Trust} qualifier is returned.
     *
     * @param a1 the first annotation
     * @param k1 the qualifier kind of {@code a1}
     * @param a2 the second annotation
     * @param k2 the qualifier kind of {@code a2}
     * @param lubKind the qualifier kind representing the LUB
     * @return the LUB annotation
     */
    @Override
    protected AnnotationMirror leastUpperBoundWithElements(
        AnnotationMirror a1,
        QualifierKind k1,
        AnnotationMirror a2,
        QualifierKind k2,
        QualifierKind lubKind) {
      return AnnotationUtils.areSame(a1, a2) ? a1 : TRUST;
    }

    /**
     * Computes the greatest lower bound (GLB) of two annotations. If the annotations are identical,
     * the GLB is that annotation; otherwise, the default {@code @Trust} qualifier is returned.
     *
     * @param a1 the first annotation
     * @param k1 the qualifier kind of {@code a1}
     * @param a2 the second annotation
     * @param k2 the qualifier kind of {@code a2}
     * @param glbKind the qualifier kind representing the GLB
     * @return the GLB annotation
     */
    @Override
    protected AnnotationMirror greatestLowerBoundWithElements(
        AnnotationMirror a1,
        QualifierKind k1,
        AnnotationMirror a2,
        QualifierKind k2,
        QualifierKind glbKind) {
      return AnnotationUtils.areSame(a1, a2) ? a1 : TRUST;
    }
  }
}
